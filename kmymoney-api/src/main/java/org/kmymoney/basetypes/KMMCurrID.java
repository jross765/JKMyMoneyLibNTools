package org.kmymoney.basetypes;

import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMCurrID extends KMMSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMCurrID.class);

    // ---------------------------------------------------------------

    private Currency currency;

    // ---------------------------------------------------------------
    
    public KMMCurrID() {
	super();
	type = Type.CURRENCY;
    }

    public KMMCurrID(Currency curr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {

	super(Type.CURRENCY, curr.getCurrencyCode());
	
	setType(Type.CURRENCY);
	setCurrency(curr);
    }

    public KMMCurrID(String currStr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {

	super(Type.CURRENCY, currStr);
	
	setType(Type.CURRENCY);
	setCurrency(currStr);
    }

    public KMMCurrID(KMMSecCurrID cmdtyCurrID) throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	
	super(Type.CURRENCY, cmdtyCurrID.getCode());

	if ( getType() != Type.CURRENCY )
	    throw new InvalidSecCurrTypeException();

	setType(Type.CURRENCY);
	setCurrency(code);
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) throws InvalidSecCurrIDException {
//        if ( type != Type.CURRENCY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ----------------------------
    
    public Currency getCurrency() throws InvalidSecCurrTypeException {
	if ( type != Type.CURRENCY )
	    throw new InvalidSecCurrTypeException();
	
        return currency;
    }
    
    public void setCurrency(Currency currency) throws InvalidSecCurrTypeException {
	if ( type != Type.CURRENCY )
	    throw new InvalidSecCurrTypeException();
	
	if ( currency == null )
	    throw new IllegalArgumentException("Argument currency is null");

	this.currency = currency;
    }

    public void setCurrency(String iso4217CurrCode) throws InvalidSecCurrTypeException {
	if ( iso4217CurrCode == null )
	    throw new IllegalArgumentException("Argument string is null");

	setCurrency(Currency.getInstance(iso4217CurrCode));
    }

    // ---------------------------------------------------------------
    
    public static KMMCurrID parse(String str) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if ( str == null )
	    throw new IllegalArgumentException("Argument string is null");

	if ( str.equals("") )
	    throw new IllegalArgumentException("Argument string is empty");

	KMMCurrID result = new KMMCurrID();
	
	int posSep = str.indexOf(SEPARATOR);
	// Plausi ::MAGIC
	if ( posSep <= 3 ||
	     posSep >= str.length() - 2 )
	    throw new InvalidSecCurrIDException();
	
	String typeStr = str.substring(0, posSep).trim();
	String currStr = str.substring(posSep + 1, str.length()).trim();
	
	if ( typeStr.equals(Type.CURRENCY.toString()) ) {
	    result.setType(Type.CURRENCY);
	    result.setCode(currStr);
	    result.setCurrency(Currency.getInstance(currStr));
	} else {
	    LOGGER.error("parse: Unknown security/currency type '" + typeStr + "'");
	    throw new InvalidSecCurrTypeException();
	}
	
	return result;
    }
    
    // ---------------------------------------------------------------

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((currency == null) ? 0 : currency.hashCode());
	result = prime * result + ((type == null) ? 0 : type.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	KMMCurrID other = (KMMCurrID) obj;
	if (type != other.type)
	    return false;
	if (currency == null) {
	    if (other.currency != null)
		return false;
	} else if (!currency.equals(other.currency))
	    return false;
	return true;
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	if (type != Type.CURRENCY)
	    return "ERROR";

	String result = Type.CURRENCY.toString() + 
		        SEPARATOR + 
		        currency.getCurrencyCode();

	return result;
    }

}
