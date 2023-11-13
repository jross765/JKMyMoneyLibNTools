package org.kmymoney.basetypes.complex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifSecID extends KMMQualifSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifSecID.class);

    // ---------------------------------------------------------------

    // ::EMPTY

    // ---------------------------------------------------------------
    
    public KMMQualifSecID() {
	super();
	type = Type.SECURITY;
    }

    public KMMQualifSecID(String secIDStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	super(Type.SECURITY, secIDStr);
    }

    public KMMQualifSecID(KMMQualifSecCurrID cmdtyID) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {	
	super(Type.SECURITY, cmdtyID.getCode());
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) throws InvalidQualifSecCurrIDException {
//        if ( type != Type.SECURITY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ---------------------------------------------------------------
    
    public static KMMQualifSecID parse(String str) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( str == null )
	    throw new IllegalArgumentException("Argument string is null");

	if ( str.equals("") )
	    throw new IllegalArgumentException("Argument string is empty");

	KMMQualifSecID result = new KMMQualifSecID();
	
	int posSep = str.indexOf(SEPARATOR);
	// Plausi ::MAGIC
	if ( posSep <= 3 ||
	     posSep >= str.length() - 2 )
	    throw new InvalidQualifSecCurrIDException();
	
	String typeStr = str.substring(0, posSep).trim();
	String secCodeStr = str.substring(posSep + 1, str.length()).trim();
	
	if ( typeStr.equals(Type.SECURITY.toString()) ) {
	    result.setType(Type.SECURITY);
	    result.setCode(secCodeStr);
	} else {
	    LOGGER.error("parse: Unknown security/currency type '" + typeStr + "'");
	    throw new InvalidQualifSecCurrTypeException();
	}
	
	return result;
    }
    
    // ---------------------------------------------------------------

    // ::EMPTY

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	if (type != Type.SECURITY)
	    return "ERROR";

	String result = super.toString();

	return result;
    }

}
