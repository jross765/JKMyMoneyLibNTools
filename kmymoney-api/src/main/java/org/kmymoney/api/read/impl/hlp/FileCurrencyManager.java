package org.kmymoney.api.read.impl.hlp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.generated.CURRENCY;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.impl.KMyMoneyCurrencyImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCurrencyManager {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCurrencyManager.class);

    // ---------------------------------------------------------------

    private KMyMoneyFileImpl kmmFile;

    private Map<String, KMyMoneyCurrency> currID2Curr;

    // ---------------------------------------------------------------

    public FileCurrencyManager(KMyMoneyFileImpl kmmFile) {
	this.kmmFile = kmmFile;
	init(kmmFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final KMYMONEYFILE pRootElement) {
	currID2Curr = new HashMap<String, KMyMoneyCurrency>();

	for ( CURRENCY jwsdpCurr : pRootElement.getCURRENCIES().getCURRENCY() ) {
	    try {
		KMyMoneyCurrencyImpl curr = createCurrency(jwsdpCurr);
		currID2Curr.put(jwsdpCurr.getId(), curr);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Currency-Entry with id=" + jwsdpCurr.getId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in currency map: " + currID2Curr.size());
    }

    /**
     * @param jwsdpCurr the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyCurrency to wrap the given JAXB object.
     */
    protected KMyMoneyCurrencyImpl createCurrency(final CURRENCY jwsdpCurr) {
	KMyMoneyCurrencyImpl curr = new KMyMoneyCurrencyImpl(jwsdpCurr, kmmFile);
	return curr;
    }

    // ---------------------------------------------------------------

    public KMyMoneyCurrency getCurrencyById(String currID) {
	if (currID2Curr == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyCurrency retval = currID2Curr.get(currID);
	if (retval == null) {
	    LOGGER.warn("No Currency with ID '" + currID + "'. We know " + currID2Curr.size() + " currencies.");
	}
	
	return retval;
    }

    public KMyMoneyCurrency getCurrencyByQualifId(KMMQualifCurrID currID) {
	return getCurrencyById(currID.getCode());
    }

    public Collection<KMyMoneyCurrency> getCurrencies() {
	return currID2Curr.values();
    }

    // ---------------------------------------------------------------
    
    public int getNofEntriesCurrencyMap() {
	return currID2Curr.size();
    }
    
}
