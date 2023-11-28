package org.kmymoney.api.basetypes.complex;

import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifSecID extends KMMQualifSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifSecID.class);

    // ---------------------------------------------------------------

    private KMMSecID secID;

    // ---------------------------------------------------------------
    
    public KMMQualifSecID() {
	super();
	type = Type.SECURITY;
    }

    public KMMQualifSecID(KMMSecID secID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	super(Type.SECURITY, secID.toString());

	setType(Type.SECURITY);
	setSecID(secID);
    }

    public KMMQualifSecID(String secIDStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	super(Type.SECURITY, secIDStr);

	setType(Type.SECURITY);
	setSecID(secIDStr);
    }

    public KMMQualifSecID(KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

	super(Type.SECURITY, secCurrID.getCode());

	if ( getType() != Type.SECURITY )
	    throw new InvalidQualifSecCurrTypeException();

	setType(Type.SECURITY);
	setSecID(code);
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) throws InvalidQualifSecCurrIDException {
//        if ( type != Type.SECURITY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ---------------------------------------------------------------
    
    public KMMSecID getSecID() throws InvalidQualifSecCurrTypeException {
  	if ( type != Type.SECURITY )
	    throw new InvalidQualifSecCurrTypeException();
	
        return secID;
    }

    public void setSecID(KMMSecID secID) throws InvalidQualifSecCurrTypeException {
  	if ( type != Type.SECURITY )
	    throw new InvalidQualifSecCurrTypeException();
	
	if ( secID == null )
	    throw new IllegalArgumentException("Argument currency is null");

        this.secID = secID;
    }

    public void setSecID(String secIDStr) {
  	if ( secIDStr == null )
	    throw new IllegalArgumentException("Argument string is null");

	setSecID(new KMMSecID(secIDStr));
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((secID == null) ? 0 : secID.hashCode());
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
	KMMQualifSecID other = (KMMQualifSecID) obj;
	if (type != other.type)
	    return false;
	if (secID == null) {
	    if (other.secID != null)
		return false;
	} else if (!secID.equals(other.secID))
	    return false;
	return true;
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	if (type != Type.SECURITY)
	    return "ERROR";

	String result = Type.SECURITY.toString() + 
			SEPARATOR + 
			secID.toString();

	return result;
    }

}
