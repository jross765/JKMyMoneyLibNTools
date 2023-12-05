package org.kmymoney.api.read.impl.hlp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kmymoney.api.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

    // ---------------------------------------------------------------

    private KMyMoneyFileImpl kmmFile;

    private Map<KMMTrxID, KMyMoneyTransaction>              trxMap;
    private Map<KMMQualifSplitID, KMyMoneyTransactionSplit> trxSpltMap;

    // ---------------------------------------------------------------

    public FileTransactionManager(KMyMoneyFileImpl kmmFile) {
	this.kmmFile = kmmFile;
	init(kmmFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final KMYMONEYFILE pRootElement) {
	trxMap     = new HashMap<KMMTrxID, KMyMoneyTransaction>();
	trxSpltMap = new HashMap<KMMQualifSplitID, KMyMoneyTransactionSplit>();

	for ( TRANSACTION jwsdpTrx : pRootElement.getTRANSACTIONS().getTRANSACTION() ) {
	    try {
		KMyMoneyTransactionImpl trx = createTransaction(jwsdpTrx);
		trxMap.put(trx.getId(), trx);
		for (KMyMoneyTransactionSplit splt : trx.getSplits()) {
		    KMMQualifSplitID spltID = new KMMQualifSplitID(trx.getId(), splt.getId());
		    trxSpltMap.put(spltID, splt);
		}
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Transaction-Entry with id=" + jwsdpTrx.getId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in transaction map: " + trxMap.size());
    }

    /**
     * @param jwsdpTrx the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyTransaction to wrap the given jaxb-object.
     */
    protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
	KMyMoneyTransactionImpl trx = new KMyMoneyTransactionImpl(jwsdpTrx, kmmFile);
	return trx;
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    public KMyMoneyTransaction getTransactionById(final KMMTrxID trxID) {
	if (trxMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyTransaction retval = trxMap.get(trxID);
	if (retval == null) {
	    LOGGER.warn("No Transaction with ID '" + trxID + "'. We know " + trxMap.size() + " transactions.");
	}
	
	return retval;
    }

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSplitID spltID) {
	if (trxSpltMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyTransactionSplit retval = trxSpltMap.get(spltID);
	if (retval == null) {
	    LOGGER.warn("No Transaction-Split with ID '" + spltID + "'. We know " + trxSpltMap.size() + " transactions.");
	}
	return retval;
    }

    /**
     * @see KMyMoneyFile#getTransactions()
     */
    public Collection<? extends KMyMoneyTransaction> getTransactions() {
	if (trxMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(trxMap.values());
    }

    // ---------------------------------------------------------------

    public int getNofEntriesTransactionMap() {
	return trxMap.size();
    }

    public int getNofEntriesTransactionSplitMap() {
	return trxSpltMap.size();
    }

}