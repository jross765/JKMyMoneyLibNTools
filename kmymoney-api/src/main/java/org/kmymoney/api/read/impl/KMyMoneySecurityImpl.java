package org.kmymoney.api.read.impl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.UnknownRoundingMethodException;
import org.kmymoney.api.read.UnknownSecurityTypeException;
import org.kmymoney.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.kmymoney.api.read.impl.hlp.KVPListDoesNotContainKeyException;
import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneySecurityImpl extends KMyMoneyObjectImpl 
								  implements KMyMoneySecurity 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneySecurityImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * the JWSDP-object we are facading.
     */
    protected final SECURITY jwsdpPeer;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneySecurityImpl(
	    final SECURITY peer, 
	    final KMyMoneyFile kmmFile) {
    	super(kmmFile);

    	jwsdpPeer = peer;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public SECURITY getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    @Override
    public KMMSecID getID() {
	return new KMMSecID(jwsdpPeer.getId());
    }

    @Override
    public KMMQualifSecID getQualifID() {
	return new KMMQualifSecID(getID());
    }
    
    @Override
    public String getSymbol() {
	return jwsdpPeer.getSymbol();
    }

    /**
     * {@inheritDoc}
     */
    public String getCode() {
    	try {
    		return getUserDefinedAttribute("kmm-security-id");
    	} catch (KVPListDoesNotContainKeyException exc) {
    		return null;
    	}
    }

    // ---------------------------------------------------------------

    @Override
    public KMMSecCurr.Type getType() throws UnknownSecurityTypeException {
	BigInteger typeVal = getTypeBigInt(); 
	return KMMSecCurrImpl.getType(typeVal.intValue());
    }

    public BigInteger getTypeBigInt() {
	return jwsdpPeer.getType(); 
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
    public KMMQualifCurrID getTradingCurrency() {
	return new KMMQualifCurrID(jwsdpPeer.getTradingCurrency());
    }

    @Override
    public String getTradingMarket() {
	return jwsdpPeer.getTradingMarket();
    }
    
    // ---------------------------------------------------------------

	@Override
	public List<KMyMoneyAccount> getStockAccounts() {
		List<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();
		
		for ( KMyMoneyAccount acct : getKMyMoneyFile().getAccountsByType(KMyMoneyAccount.Type.STOCK) ) {
			KMMQualifSecCurrID secCurrID = acct.getQualifSecCurrID();
			KMMSecID secID = new KMMSecID(secCurrID.getCode());
			if ( this.getID().equals(secID) ) {
				result.add(acct);
			}
		}
		
		return result;
	}

    // ---------------------------------------------------------------

    @Override
    public List<KMyMoneyPrice> getQuotes() {
	List<KMyMoneyPrice> result = new ArrayList<KMyMoneyPrice>();
	
	Collection<KMyMoneyPrice> prices = getKMyMoneyFile().getPrices();
	for ( KMyMoneyPrice price : prices ) {
	    try {
		if ( price.getFromSecCurrQualifID().toString().equals(getQualifID().toString()) ) {
		    result.add(price);
		} 
	    } catch ( Exception exc ) {
		LOGGER.error("getQuotes: Could not check price " + price.toString());
	    }
	}
	
	return result;
    }

    @Override
    public KMyMoneyPrice getYoungestQuote() {
	KMyMoneyPrice result = null;

	LocalDate youngestDate = LocalDate.of(1970, 1, 1); // ::MAGIC
	for ( KMyMoneyPrice price : getQuotes() ) {
	    if ( price.getDate().isAfter(youngestDate) ) {
		result = price;
		youngestDate = price.getDate();
	    }
	}

	return result;
    }

    // ---------------------------------------------------------------

	/**
	 * @param name the name of the user-defined attribute
	 * @return the value or null if not set
	 */
	public String getUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(kvpList, name);
	}

    /**
     * @return all keys that can be used with
     *         ${@link #getUserDefinedAttribute(String)}}.
     */
	public List<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(kvpList);
	}

    // -----------------------------------------------------------------

	@Override
	public int compareTo(final KMyMoneySecurity otherSec) {
		int i = compareToByName(otherSec);
		if ( i != 0 ) {
			return i;
		}

		i = compareToByQualifID(otherSec);
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherSec.hashCode());
	}
	
//	private int compareToByID(final KMyMoneySecurity otherCmdty) {
//		return getID().toString().compareTo(otherCmdty.getID().toString());
//	}

	private int compareToByQualifID(final KMyMoneySecurity otherSec) {
		return getQualifID().toString().compareTo(otherSec.getQualifID().toString());
	}

	private int compareToByName(final KMyMoneySecurity otherSec) {
		return getName().compareTo(otherSec.getName());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	String result = "KMyMoneySecurityImpl ";
	
	result += "[id=" + getID();
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
