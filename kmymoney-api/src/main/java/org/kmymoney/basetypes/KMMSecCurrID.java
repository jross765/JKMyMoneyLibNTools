package org.kmymoney.basetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMSecCurrID {
    
    public enum Type {
	CURRENCY,
	SECURITY,
	UNSET
    }
    
    // ---------------------------------------------------------------
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMSecCurrID.class);

    public static final char SEPARATOR = ':';

    // ---------------------------------------------------------------

    protected Type    type;
    protected String  code;

    // ---------------------------------------------------------------
    
    public KMMSecCurrID() {
	this.type = Type.UNSET;
    }

    public KMMSecCurrID(Type type, String code) throws InvalidSecCurrTypeException {
	
	if ( code == null )
	    throw new IllegalArgumentException("Security code is null");

	if ( code.trim().equals("") )
	    throw new IllegalArgumentException("Security code is empty");

	this.type = type;
	setCode(code.trim());
    }

    // ---------------------------------------------------------------

    public Type getType() {
        return type;
    }
    
    public void setType(Type type) throws InvalidSecCurrIDException {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String secCode) throws InvalidSecCurrTypeException {
	if ( secCode == null )
	    throw new IllegalArgumentException("Security code is null");

	if ( secCode.trim().equals("") )
	    throw new IllegalArgumentException("Security code is empty");

        this.code = secCode.trim();
    }
    
    // ---------------------------------------------------------------
    
    public static KMMSecCurrID parse(String str) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if ( str == null )
	    throw new IllegalArgumentException("Argument string is null");

	if ( str.equals("") )
	    throw new IllegalArgumentException("Argument string is empty");

	KMMSecCurrID result = new KMMSecCurrID();
	
	int posSep = str.indexOf(SEPARATOR);
	// Plausi ::MAGIC
	if ( posSep <= 3 ||
	     posSep >= str.length() - 2 )
	    throw new InvalidSecCurrIDException();
	
	String typeStr = str.substring(0, posSep).trim();
	String codeStr = str.substring(posSep + 1, str.length()).trim();
	
	if ( typeStr.equals(Type.CURRENCY.toString()) ) {
	    result.setType(Type.CURRENCY);
	    result.setCode(codeStr);
	} else if ( typeStr.equals(Type.SECURITY.toString()) ) {
	    result.setType(Type.SECURITY);
	    result.setCode(codeStr);
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
	result = prime * result + ((code == null) ? 0 : code.hashCode());
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
	KMMSecCurrID other = (KMMSecCurrID) obj;
	if (type != other.type)
	    return false;
	if (code == null) {
	    if (other.code != null)
		return false;
	} else if (!code.equals(other.code))
	    return false;
	return true;
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	return type.toString() + SEPARATOR + code;
    }

}
