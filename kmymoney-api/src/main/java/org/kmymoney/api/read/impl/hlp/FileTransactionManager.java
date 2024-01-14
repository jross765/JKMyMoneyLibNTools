package org.kmymoney.api.read.impl.hlp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	protected KMyMoneyFileImpl kmmFile;

	private Map<KMMTrxID, KMyMoneyTransaction>             trxMap;
	private Map<KMMQualifSpltID, KMyMoneyTransactionSplit> trxSpltMap;

	// ---------------------------------------------------------------

	public FileTransactionManager(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
		init(kmmFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final KMYMONEYFILE pRootElement) {
		trxMap = new HashMap<KMMTrxID, KMyMoneyTransaction>();
		trxSpltMap = new HashMap<KMMQualifSpltID, KMyMoneyTransactionSplit>();

		for ( TRANSACTION jwsdpTrx : pRootElement.getTRANSACTIONS().getTRANSACTION() ) {
			try {
				KMyMoneyTransactionImpl trx = createTransaction(jwsdpTrx);
				trxMap.put(trx.getID(), trx);
				for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
					KMMQualifSpltID spltID = new KMMQualifSpltID(trx.getID(), splt.getID());
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
		LOGGER.debug("Generated new transaction: " + trx.getID());
		return trx;
	}

	// ---------------------------------------------------------------

	public void addTransaction(KMyMoneyTransaction trx) throws IllegalArgumentException {
		addTransaction(trx, true);
	}

	public void addTransaction(KMyMoneyTransaction trx, boolean withSplt) throws IllegalArgumentException {
		trxMap.put(trx.getID(), trx);

		if ( withSplt ) {
			for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
				addTransactionSplit(splt, false);
			}
		}

		LOGGER.debug("Added transaction to cache: " + trx.getID());
	}

	public void removeTransaction(KMyMoneyTransaction trx) throws IllegalArgumentException {
		removeTransaction(trx, true);
	}

	public void removeTransaction(KMyMoneyTransaction trx, boolean withSplt) throws IllegalArgumentException {
		if ( withSplt ) {
			for ( KMyMoneyTransactionSplit splt : trx.getSplits() ) {
				removeTransactionSplit(splt, false);
			}
		}

		trxMap.remove(trx.getID());

		LOGGER.debug("Removed transaction from cache: " + trx.getID());
	}

	// ---------------------------------------------------------------

	public void addTransactionSplit(KMyMoneyTransactionSplit splt) throws IllegalArgumentException {
		addTransactionSplit(splt, true);
	}

	public void addTransactionSplit(KMyMoneyTransactionSplit splt, boolean withInvc) throws IllegalArgumentException {
		trxSpltMap.put(splt.getQualifID(), splt);

		if ( withInvc ) {
			addTransaction(splt.getTransaction(), false);
		}
	}

	public void removeTransactionSplit(KMyMoneyTransactionSplit splt) throws IllegalArgumentException {
		removeTransactionSplit(splt, true);
	}

	public void removeTransactionSplit(KMyMoneyTransactionSplit splt, boolean withInvc)
			throws IllegalArgumentException {
		if ( withInvc ) {
			removeTransaction(splt.getTransaction(), false);
		}

		trxSpltMap.remove(splt.getQualifID());
	}

	// ---------------------------------------------------------------

	public KMyMoneyTransaction getTransactionByID(final KMMTrxID trxID) {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyTransaction retval = trxMap.get(trxID);
		if ( retval == null ) {
			LOGGER.warn("No Transaction with ID '" + trxID + "'. We know " + trxMap.size() + " transactions.");
		}

		return retval;
	}

	public Collection<? extends KMyMoneyTransaction> getTransactions() {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		return Collections.unmodifiableCollection(trxMap.values());
	}

	// ---------------------------------------------------------------

	public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSpltID spltID) {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyTransactionSplit retval = trxSpltMap.get(spltID);
		if ( retval == null ) {
			LOGGER.warn(
					"No Transaction-Split with ID '" + spltID + "'. We know " + trxSpltMap.size() + " transactions.");
		}
		return retval;
	}

	public Collection<KMyMoneyTransactionSplit> getTransactionSplits() {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		return Collections.unmodifiableCollection(trxSpltMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTransactionMap() {
		return trxMap.size();
	}

	public int getNofEntriesTransactionSplitMap() {
		return trxSpltMap.size();
	}

}
