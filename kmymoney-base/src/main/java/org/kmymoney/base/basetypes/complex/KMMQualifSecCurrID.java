package org.kmymoney.base.basetypes.complex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifSecCurrID {
	
    public enum Type {
    	CURRENCY,
    	SECURITY,
    	UNSET
    }
    
    // ---------------------------------------------------------------
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifSecCurrID.class);

    public static final String PREFIX_SECURITY = "E0";  // ::MAGIC
    public static final char   SEPARATOR       = ':';

    // ---------------------------------------------------------------

    protected Type    type;
    protected String  code;

    // ---------------------------------------------------------------
    
    public KMMQualifSecCurrID() {
    	this.type = Type.UNSET;
    }

	public KMMQualifSecCurrID(Type type, String code) {

		if ( code == null )
			throw new IllegalArgumentException("Security code is null");

		if ( code.trim().equals("") )
			throw new IllegalArgumentException("Security code is empty");

		this.type = type;
		setCode(code.trim());
	}

    // ---------------------------------------------------------------
	
	public boolean isSet() {
		return ( this.type != Type.UNSET && 
				 ! this.code.trim().equals("") );
	}

	public void reset() {
		this.type = Type.UNSET;
		this.code = "";
	}

    // ---------------------------------------------------------------

    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String secCode) {
    	if ( secCode == null )
    		throw new IllegalArgumentException("Security code is null");

    	if ( secCode.trim().equals("") )
    		throw new IllegalArgumentException("Security code is empty");

        this.code = secCode.trim();
    }
    
    // ---------------------------------------------------------------
    
    public void set(KMMQualifSecCurrID val) {
    	setType(val.getType());
    	setCode(val.getCode());
    }
    
    // ---------------------------------------------------------------
    
	public static KMMQualifSecCurrID parse(String str)
			throws InvalidQualifSecCurrIDException {
		if ( str == null )
			throw new IllegalArgumentException("Argument string is null");

		if ( str.equals("") )
			throw new IllegalArgumentException("Argument string is empty");

		KMMQualifSecCurrID result = new KMMQualifSecCurrID();

		int posSep = str.indexOf(SEPARATOR);
		// Plausi ::MAGIC
		if ( posSep <= 3 || posSep >= str.length() - 2 )
			throw new InvalidQualifSecCurrIDException();

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
			throw new InvalidQualifSecCurrTypeException();
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
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		KMMQualifSecCurrID other = (KMMQualifSecCurrID) obj;
		if ( type != other.type )
			return false;
		if ( code == null ) {
			if ( other.code != null )
				return false;
		} else if ( !code.equals(other.code) )
			return false;
		return true;
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
    	return toStringShort();
    }

    public String toStringShort() {
    	return type.toString() + SEPARATOR + code;
    }

    public String toStringLong() {
    	return "KMMQualifSecCurrID [type=" + type + ", code='" + code + "']";
    }

}
