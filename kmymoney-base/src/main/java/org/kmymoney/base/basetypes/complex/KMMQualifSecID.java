package org.kmymoney.base.basetypes.complex;

import org.kmymoney.base.basetypes.simple.KMMIDNotSetException;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifSecID extends KMMQualifSecCurrID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifSecID.class);

    // ---------------------------------------------------------------

    private KMMSecID secID;

    // ---------------------------------------------------------------
    
	public KMMQualifSecID() {
		super();
		
		init();
		
		type = Type.SECURITY;
	}

	public KMMQualifSecID(KMMSecID secID) {
		super(Type.SECURITY, secID.toString());

		init();
		
		setType(Type.SECURITY);
		setSecID(secID);
	}

	public KMMQualifSecID(String secIDStr) {
		super(Type.SECURITY, secIDStr);

		init();
		
		setType(Type.SECURITY);
		try {
			setSecID(secIDStr);
		} catch (KMMIDNotSetException e) {
			LOGGER.error("Security ID cannot be set from '" + secIDStr + "'");
			throw new IllegalArgumentException("Security ID cannot be set from '" + secIDStr + "'");
		}
	}

	public KMMQualifSecID(KMMQualifSecCurrID secCurrID)
			{
		super(Type.SECURITY, secCurrID.getCode());

		if ( getType() != Type.SECURITY )
			throw new InvalidQualifSecCurrTypeException();

		init();
		
		setType(Type.SECURITY);
		try {
			setSecID(code);
		} catch (KMMIDNotSetException e) {
			LOGGER.debug("KMMQualifSecID: Could not set Security-ID from '" + code + "'");
			throw new IllegalArgumentException("KMMQualifSecID: Could not set Security-ID from '" + code + "'");
		}
	}

	// ---------------------------------------------------------------
	
	private void init() {
		secID = new KMMSecID();
	}

    // ---------------------------------------------------------------

    @Override
    public void setType(Type type) {
//        if ( type != Type.SECURITY )
//            throw new InvalidCmdtyCurrIDException();

        super.setType(type);
    }
    
    // ---------------------------------------------------------------
    
    public KMMSecID getSecID() {
    	if ( type != Type.SECURITY )
    		throw new InvalidQualifSecCurrTypeException();
	
        return secID;
    }

	public void setSecID(KMMSecID secID) {
		if ( type != Type.SECURITY )
			throw new InvalidQualifSecCurrTypeException();

		if ( secID == null )
			throw new IllegalArgumentException("Argument currency is null");

		this.secID = secID;
	}

	public void setSecID(String secIDStr) throws KMMIDNotSetException {
		if ( secIDStr == null )
			throw new IllegalArgumentException("Argument string is null");

		setSecID(new KMMSecID(secIDStr));
		setCode(secID.get());
	}

    // ---------------------------------------------------------------
    
    public void set(KMMQualifSecID val) {
    	super.set(val);
    	setSecID(val.getSecID());
    }
    
    // ---------------------------------------------------------------

	public static KMMQualifSecID parse(String str)
			throws InvalidQualifSecCurrIDException {
		if ( str == null )
			throw new IllegalArgumentException("Argument string is null");

		if ( str.equals("") )
			throw new IllegalArgumentException("Argument string is empty");

		KMMQualifSecID result = new KMMQualifSecID();

		int posSep = str.indexOf(SEPARATOR);
		// Plausi ::MAGIC
		if ( posSep <= 3 || posSep >= str.length() - 2 )
			throw new InvalidQualifSecCurrIDException();

		String typeStr = str.substring(0, posSep).trim();
		String secCodeStr = str.substring(posSep + 1, str.length()).trim();

		if ( typeStr.equals(Type.SECURITY.toString()) ) {
			result.setType(Type.SECURITY);
			try {
				result.setSecID(secCodeStr);
			} catch (KMMIDNotSetException e) {
				LOGGER.error("parse: Cannot set KMMSecID with code '" + secCodeStr + "'");
				throw new InvalidQualifSecCurrIDException();
			}
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
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		KMMQualifSecID other = (KMMQualifSecID) obj;
		if ( type != other.type )
			return false;
		if ( secID == null ) {
			if ( other.secID != null )
				return false;
		} else if ( !secID.equals(other.secID) )
			return false;
		return true;
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
    	return toStringShort();
    }
    
	public String toStringShort() {
		if ( type != Type.SECURITY )
			return "ERROR";

		String result = Type.SECURITY.toString() + 
						SEPARATOR + 
						secID.toString();

		return result;
	}

	public String toStringLong() {
		if ( type != Type.SECURITY )
			return "ERROR";

    	return "KMMQualifSecID [type=" + type + ", secID='" + secID.toString() + "']";
	}

}
