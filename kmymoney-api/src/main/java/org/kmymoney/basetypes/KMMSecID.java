package org.kmymoney.basetypes;

import org.kmymoney.basetypes.KMMSecCurrID.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMSecID extends KMMSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMSecID.class);

    // ---------------------------------------------------------------

    // ::EMPTY

    // ---------------------------------------------------------------
    
    public KMMSecID() {
	super();
	type = Type.SECURITY;
    }

    public KMMSecID(String secIDStr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	super(Type.SECURITY, secIDStr);
    }

    public KMMSecID(KMMSecCurrID cmdtyID) throws InvalidSecCurrTypeException, InvalidSecCurrIDException {	
	super(Type.SECURITY, cmdtyID.getCode());
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) throws InvalidSecCurrIDException {
//        if ( type != Type.SECURITY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ---------------------------------------------------------------
    
    public static KMMSecID parse(String str) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if ( str == null )
	    throw new IllegalArgumentException("Argument string is null");

	if ( str.equals("") )
	    throw new IllegalArgumentException("Argument string is empty");

	KMMSecID result = new KMMSecID();
	
	int posSep = str.indexOf(SEPARATOR);
	// Plausi ::MAGIC
	if ( posSep <= 3 ||
	     posSep >= str.length() - 2 )
	    throw new InvalidSecCurrIDException();
	
	String typeStr = str.substring(0, posSep).trim();
	String secCodeStr = str.substring(posSep + 1, str.length()).trim();
	
	if ( typeStr.equals(Type.SECURITY.toString()) ) {
	    result.setType(Type.SECURITY);
	    result.setCode(secCodeStr);
	} else {
	    LOGGER.error("parse: Unknown security/currency type '" + typeStr + "'");
	    throw new InvalidSecCurrTypeException();
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
