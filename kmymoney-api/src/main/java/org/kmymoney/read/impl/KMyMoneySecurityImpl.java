package org.kmymoney.read.impl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.generated.PAIR;
import org.kmymoney.generated.SECURITY;
import org.kmymoney.read.KMMSecCurr;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneySecurity;
import org.kmymoney.read.aux.KMMPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneySecurityImpl implements KMyMoneySecurity {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneySecurityImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * the JWSDP-object we are facading.
     */
    private SECURITY jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final KMyMoneyFile file;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneySecurityImpl(
	    final SECURITY peer, 
	    final KMyMoneyFile gncFile) {

	jwsdpPeer = peer;
	file = gncFile;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public SECURITY getJwsdpPeer() {
	return jwsdpPeer;
    }

    public KMyMoneyFile getKMyMoneyFile() {
	return file;
    }

    // ---------------------------------------------------------------

    @Override
    public String getId() {
	return jwsdpPeer.getId();
    }

    @Override
    public KMMSecID getQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	return new KMMSecID(getId());
    }
    
    @Override
    public String getSymbol() {
	return jwsdpPeer.getSymbol();
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
	String result = "";
	
	for ( PAIR kvp : jwsdpPeer.getKEYVALUEPAIRS().getPAIR() ) {
	    if ( kvp.getKey().equals("kmm-security-id") )
	    result += kvp.getValue();
	}
	
	return result;
    }

    // ---------------------------------------------------------------

    @Override
    public KMMSecCurr.Type getType() throws UnknownSecurityTypeException {
	BigInteger typeVal = jwsdpPeer.getType(); 
	return KMMSecCurrImpl.getType(typeVal.intValue());
    }

    @Override
    public String getName() {
	return jwsdpPeer.getName();
    }

    @Override
    public BigInteger getPP() {
	return jwsdpPeer.getPp();
    }

    @Override
    public KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException {
	BigInteger methodVal = jwsdpPeer.getRoundingMethod(); 
	return KMMSecCurrImpl.getRoundingMethod(methodVal.intValue());
    }

    @Override
    public BigInteger getSAF() {
	return jwsdpPeer.getSaf();
    }

    @Override
    public KMMCurrID getTradingCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	return new KMMCurrID(jwsdpPeer.getTradingCurrency());
    }

    @Override
    public String getTradingMarket() {
	return jwsdpPeer.getTradingMarket();
    }
    
    // ---------------------------------------------------------------

    @Override
    public Collection<KMMPrice> getQuotes() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	Collection<KMMPrice> result = new ArrayList<KMMPrice>();
	
	Collection<KMMPrice> prices = getKMyMoneyFile().getPrices();
	for ( KMMPrice price : prices ) {
	    try {
		if ( price.getFromSecCurrQualifId().toString().equals(getQualifId().toString()) ) {
		    result.add(price);
		} 
	    } catch ( Exception exc ) {
		LOGGER.error("getQuotes: Could not check price " + price.toString());
	    }
	}
	
	return result;
    }

    @Override
    public KMMPrice getYoungestQuote() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	KMMPrice result = null;

	LocalDate youngestDate = LocalDate.of(1970, 1, 1); // ::MAGIC
	for ( KMMPrice price : getQuotes() ) {
	    if ( price.getDate().isAfter(youngestDate) ) {
		result = price;
		youngestDate = price.getDate();
	    }
	}

	return result;
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "KMyMoneySecurityImpl ";
	
	result += "[id=" + getId();
	result += ", symbol='" + getSymbol() + "'";
	
	try {
	    result += ", type=" + getType();
	} catch (UnknownSecurityTypeException e) {
	    result += ", type=" + "ERROR";
	}
	
	result += ", name='" + getName() + "'";
	result += ", pp=" + getPP();
	
	try {
	    result += ", rounding-method=" + getRoundingMethod();
	} catch (UnknownRoundingMethodException e) {
	    result += ", rounding-method=" + "ERROR";
	}
	
	result += ", saf=" + getSAF();
	
	try {
	    result += ", trading-curr=" + getTradingCurrency();
	} catch (Exception e) {
	    result += ", trading-curr=" + "ERROR";
	}
	
	result += ", trading-mkt='" + getTradingMarket() + "'";
	
	result += "]";
	
	return result;
    }

}
