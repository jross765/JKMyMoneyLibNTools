package org.kmymoney.api.read.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMPricePairID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyPricePairImpl implements KMyMoneyPricePair {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyPricePairImpl.class);

    // -----------------------------------------------------------

    /**
     * The JWSDP-object we are wrapping.
     */
    protected final PRICEPAIR jwsdpPeer;

    protected final KMyMoneyFile file;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public KMyMoneyPricePairImpl(final PRICEPAIR newPeer, final KMyMoneyFile file) {
	super();
		
	this.jwsdpPeer = newPeer;
	this.file      = file;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public PRICEPAIR getJwsdpPeer() {
	return jwsdpPeer;
    }

    public KMyMoneyFile getKMyMoneyFile() {
	return file;
    }

    // -----------------------------------------------------------
    
    @Override
    public KMMPricePairID getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
    	if ( jwsdpPeer.getFrom() == null ||
    		 jwsdpPeer.getTo() == null ) {
    		throw new IllegalStateException("from-sec-curr and/or to-curr of JWSDP peer is/are null");
    	}
    	
    	return new KMMPricePairID(jwsdpPeer.getFrom(), jwsdpPeer.getTo());
    }

    // -----------------------------------------------------------
    
    @Override
    public String getFromSecCurrStr() {
	return jwsdpPeer.getFrom();
    }

    @Override
    public String getToCurrStr() {
	return jwsdpPeer.getTo();
    }

    // ---------------------------------------------------------------
    
    @Override
    public KMMQualifSecCurrID getFromSecCurrQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	String secCurrID = getFromSecCurrStr();

	KMMQualifSecCurrID result = null;
	if ( secCurrID.startsWith(KMMQualifSecCurrID.PREFIX_SECURITY) ) {
	    result = new KMMQualifSecID(secCurrID);
	} else {
	    result = new KMMQualifCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public KMMQualifSecID getFromSecurityQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	KMMQualifSecCurrID secCurrID = getFromSecCurrQualifID();
	if ( secCurrID.getType() != KMMQualifSecCurrID.Type.SECURITY )
	    throw new InvalidQualifSecCurrTypeException();
	    
	return new KMMQualifSecID(secCurrID);
    }

    @Override
    public KMMQualifCurrID getFromCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	KMMQualifSecCurrID secCurrID = getFromSecCurrQualifID();
	if ( secCurrID.getType() != KMMQualifSecCurrID.Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException();

	return new KMMQualifCurrID(secCurrID);
    }

    @Override
    public KMyMoneySecurity getFromSecurity() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifSecID secID = getFromSecurityQualifID();
	
	KMyMoneySecurity cmdty = file.getSecurityByQualifID(secID);
	
	return cmdty;
    }
    
    @Override
    public String getFromCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getFromCurrencyQualifID().getCurrency().getCurrencyCode();
    }

    @Override
    public KMyMoneyCurrency getFromCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifCurrID currID = getFromCurrencyQualifID();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifID(currID);
	
	return curr;
    }
    
    // ----------------------------
    
    @Override
    public KMMQualifCurrID getToCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	String secCurrID = getToCurrStr();

	KMMQualifCurrID result = null;
	if ( secCurrID.startsWith(KMMQualifSecCurrID.PREFIX_SECURITY) ) {
	    throw new InvalidQualifSecCurrTypeException();
	} else {
	    result = new KMMQualifCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public String getToCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getToCurrencyQualifID().getCode();
    }

    @Override
    public KMyMoneyCurrency getToCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifCurrID currID = getToCurrencyQualifID();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifID(currID);
	
	return curr;
    }

    // -----------------------------------------------------------
    
    @Override
    public Collection<KMyMoneyPrice> getPrices() {
	Collection<KMyMoneyPrice> result = new ArrayList<KMyMoneyPrice>();
	
	for ( PRICE prc : jwsdpPeer.getPRICE() ) {
	    KMyMoneyPrice newPrc = new KMyMoneyPriceImpl(this, prc, file);
	    result.add(newPrc);
	}
	
	try {
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + getID());
	} catch (Exception e) {
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + "ERROR");
	}
	
	return result;
    }

    // -----------------------------------------------------------
    
    public boolean equals(KMyMoneyPricePair other) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( ! getFromSecCurrQualifID().toString().equals(other.getFromSecCurrQualifID().toString()) )
	    return false;

	if ( ! getToCurrencyQualifID().toString().equals(other.getToCurrencyQualifID().toString()) )
	    return false;
	
	return true;
    }
    
    // -----------------------------------------------------------
    
    @Override
    public String toString() {
	return toStringShort();
    }

    public String toStringShort() {
	return getFromSecCurrStr() + ";" + getToCurrStr();
    }

    public String toStringLong() {
	String result = "KMMPricePairImpl [";
	
	try {
	    result += "from-sec-curr=" + getFromSecCurrQualifID();
	} catch (Exception e) {
	    result += "from-sec-curr=" + "ERROR";
	}

	try {
	    result += ", to-curr=" + getToCurrencyQualifID();
	} catch (Exception e) {
	    result += ", to-curr=" + "ERROR";
	}
	
	result += "]";
	
	return result;
    }

}
