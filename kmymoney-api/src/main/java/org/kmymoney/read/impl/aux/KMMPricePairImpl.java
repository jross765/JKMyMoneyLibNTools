package org.kmymoney.read.impl.aux;

import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMCurrPair;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecID;
import org.kmymoney.generated.PRICE;
import org.kmymoney.generated.PRICEPAIR;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.aux.KMMPricePair;
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
    public KMMCurrPair getId() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
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

    // -----------------------------------------------------------
    
    @Override
    public KMMQualifSecCurrID getFromSecCurr() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	String fromStr = getFromSecCurrStr();
	
	KMMQualifSecCurrID result = null;
	if ( fromStr.startsWith("E0") ) { // ::MAGIC
	    result = new KMMQualifSecID(fromStr);
	} else {
	    result = new KMMQualifCurrID(fromStr);
	}
	
	return result;
    }

    @Override
    public KMMQualifCurrID getToCurr() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return new KMMQualifCurrID(getToCurrStr());
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
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + getId());
	} catch (Exception e) {
	    LOGGER.debug("getPrices: Found " + result.size() + " prices for KMMPricePair " + "ERROR");
	}
	
	return result;
    }

    // -----------------------------------------------------------
    
    public boolean equals(KMMPricePair other) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( ! getFromSecCurr().equals(other.getFromSecCurr()) )
	    return false;

	if ( ! getToCurr().equals(other.getToCurr()) )
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
	    result += "from-sec-curr=" + getFromSecCurr();
	} catch (Exception e) {
	    result += "from-sec-curr=" + "ERROR";
	}

	try {
	    result += ", to-curr=" + getToCurr();
	} catch (Exception e) {
	    result += ", to-curr=" + "ERROR";
	}
	
	result += "]";
	
	return result;
    }

}
