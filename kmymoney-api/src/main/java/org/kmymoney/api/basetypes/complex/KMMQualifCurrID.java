package org.kmymoney.api.basetypes.complex;

import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifCurrID extends KMMQualifSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifCurrID.class);

    // ---------------------------------------------------------------

    private Currency currency;

    // ---------------------------------------------------------------
    
    public KMMQualifCurrID() {
	super();
	type = Type.CURRENCY;
    }

    public KMMQualifCurrID(Currency curr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	super(Type.CURRENCY, curr.getCurrencyCode());
	
	setType(Type.CURRENCY);
	setCurrency(curr);
    }

    public KMMQualifCurrID(String currStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	super(Type.CURRENCY, currStr);
	
	setType(Type.CURRENCY);
	setCurrency(currStr);
    }

    public KMMQualifCurrID(KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	
	super(Type.CURRENCY, secCurrID.getCode());

	if ( getType() != Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException();

	setType(Type.CURRENCY);
	setCurrency(code);
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) throws InvalidQualifSecCurrIDException {
//        if ( type != Type.CURRENCY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ----------------------------
    
    public Currency getCurrency() throws InvalidQualifSecCurrTypeException {
	if ( type != Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException();
	
        return currency;
    }
    
    public void setCurrency(Currency currency) throws InvalidQualifSecCurrTypeException {
	if ( type != Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException();
	
	if ( currency == null )
	    throw new IllegalArgumentException("Argument currency is null");

	this.currency = currency;
    }

    public void setCurrency(String iso4217CurrCode) throws InvalidQualifSecCurrTypeException {
	if ( iso4217CurrCode == null )
	    throw new IllegalArgumentException("Argument string is null");

	setCurrency(Currency.getInstance(iso4217CurrCode));
    }

    // ---------------------------------------------------------------
    
    public static KMMQualifCurrID parse(String str) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( str == null )
	    throw new IllegalArgumentException("Argument string is null");

	if ( str.equals("") )
	    throw new IllegalArgumentException("Argument string is empty");

	KMMQualifCurrID result = new KMMQualifCurrID();
	
	int posSep = str.indexOf(SEPARATOR);
	// Plausi ::MAGIC
	if ( posSep <= 3 ||
	     posSep >= str.length() - 2 )
	    throw new InvalidQualifSecCurrIDException();
	
	String typeStr = str.substring(0, posSep).trim();
	String currStr = str.substring(posSep + 1, str.length()).trim();
	
	if ( typeStr.equals(Type.CURRENCY.toString()) ) {
	    result.setType(Type.CURRENCY);
	    result.setCode(currStr);
	    result.setCurrency(Currency.getInstance(currStr));
	} else {
	    LOGGER.error("parse: Unknown security/currency type '" + typeStr + "'");
	    throw new InvalidQualifSecCurrTypeException();
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
	KMMQualifCurrID other = (KMMQualifCurrID) obj;
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
