package org.kmymoney.api.read.impl.aux;

import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.aux.KMMPrice;
import org.kmymoney.api.read.aux.KMMPricePair;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMPricePairImpl implements KMMPricePair {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMMPricePairImpl.class);

    // -----------------------------------------------------------

    /**
     * The JWSDP-object we are wrapping.
     */
    private final PRICEPAIR jwsdpPeer;

    private final KMyMoneyFile file;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public KMMPricePairImpl(final PRICEPAIR newPeer, final KMyMoneyFile file) {
	super();
		
	this.jwsdpPeer = newPeer;
	this.file      = file;
    }

    // -----------------------------------------------------------
    
    @Override
    public KMMCurrPair getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return new KMMCurrPair(jwsdpPeer.getFrom(), jwsdpPeer.getTo());
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
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
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
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
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
    public Collection<KMMPrice> getPrices() {
	Collection<KMMPrice> result = new ArrayList<KMMPrice>();
	
	for ( PRICE prc : jwsdpPeer.getPRICE() ) {
	    KMMPrice newPrc = new KMMPriceImpl(this, prc, file);
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
    
    public boolean equals(KMMPricePair other) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
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
