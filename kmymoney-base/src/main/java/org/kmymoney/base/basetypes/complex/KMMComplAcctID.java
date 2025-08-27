package org.kmymoney.base.basetypes.complex;

import java.util.Objects;

import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of the standard account ID ({@link KMMAcctID}):
 * For some special accounts, KMyMoney does not use the regular prefix-counter-scheme
 * to identify them, but rather a special string.
 * <br>
 * This class encapsulates that logic so that <strong>all</strong> accounts
 * are covered.
 * 
 * @see KMMAcctID
 */
public class KMMComplAcctID implements Comparable<KMMComplAcctID> {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KMMComplAcctID.class);

    // ---------------------------------------------------------------

    public enum Type {
    	STANDARD,
    	SPECIAL,
    	UNSET
    }

    public enum Top {
	
    	// ::MAGIC
    	ASSET     ( SPEC_PREFIX + "Asset" ),
    	LIABILITY ( SPEC_PREFIX + "Liability" ),
    	INCOME    ( SPEC_PREFIX + "Income" ),
    	EXPENSE   ( SPEC_PREFIX + "Expense" ),
    	EQUITY    ( SPEC_PREFIX + "Equity" );

    	// ---

    	private String code = "UNSET";
	
    	// ---
	
    	Top(String code) {
    		this.code = code;
    	}

    	// ---
	
    	public String getCode() {
    		return code;
    	}
	
    	// no typo!
    	public static Top valueOff(String code) {
    		for ( Top type : values() ) {
    			if ( type.getCode().equals(code) ) {
    				return type;
    			}
    		}

    		return null;
    	}
    }

    // -----------------------------------------------------------------

    // ::MAGIC
    public static final String SPEC_PREFIX = "AStd::";

    // -----------------------------------------------------------------

    private Type      type;
    private KMMAcctID stdID;
    private String    specID;

    // -----------------------------------------------------------------

    public KMMComplAcctID() {
    	init();
    	reset();
    	setType(Type.UNSET);
    }

    public KMMComplAcctID(KMMAcctID stdAcctID) {
    	init();
    	reset();
    	setType(Type.STANDARD);
    	setStdID(stdAcctID);
    }

    public KMMComplAcctID(String str) {
    	init();
    	reset();
	
    	if ( str.startsWith(SPEC_PREFIX) ) {
    		setType(Type.SPECIAL);
    		setSpecID(str);
    	} else {
    		setType(Type.STANDARD);
    		setStdID(new KMMAcctID(str));
    	}
    }

    // -----------------------------------------------------------------

    private void init() {
    	stdID = new KMMAcctID();
    }

    public boolean isSet() {
    	if ( type == Type.UNSET )
    		return false;
    	else if ( type == Type.STANDARD )
    		return stdID.isSet();
    	else if ( type == Type.SPECIAL )
    		return ! specID.trim().equals("");
    	
		return true; // Compiler happy
    }

    public void reset() {
    	stdID.reset();
    	specID = "";
    }

    // -----------------------------------------------------------------

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public KMMAcctID getStdID() {
    	if ( type != Type.STANDARD )
    		throw new InvalidKMMComplAcctIDException();
	
    	return stdID;
    }

    public void setStdID(KMMAcctID stdID) {
    	if ( type != Type.STANDARD )
    		throw new InvalidKMMComplAcctIDException();

        this.stdID = stdID;
    }

    public String getSpecID() {
    	if ( type != Type.SPECIAL )
    		throw new InvalidKMMComplAcctIDException();
	
        return specID;
    }

    public void setSpecID(String specID) {
    	if ( type != Type.SPECIAL )
    		throw new InvalidKMMComplAcctIDException();
	
    	if ( ! checkSpecID(specID.trim()) )
    		throw new InvalidKMMComplAcctIDException();
	
        this.specID = specID.trim();
    }

    private boolean checkSpecID(String specID) {
    	if ( specID.equals(Top.ASSET.getCode()) ||
    		 specID.equals(Top.LIABILITY.getCode()) ||
    		 specID.equals(Top.INCOME.getCode()) ||
    		 specID.equals(Top.EXPENSE.getCode()) ||
    		 specID.equals(Top.EQUITY.getCode()) )
    	{
    		return true;
    	} else {
    		return false;
		}
    }

    // -----------------------------------------------------------------

    @Override
    public int compareTo(KMMComplAcctID o) {
		if ( type != o.getType() )
			throw new IllegalArgumentException("Different types");

		if ( o.getType() == Type.UNSET )
			throw new IllegalArgumentException("Type of external object is " + Type.UNSET);

		if ( type == Type.UNSET )
			throw new IllegalStateException("Type of internal object is " + Type.UNSET);

		if ( type == Type.STANDARD ) {
			try {
				return stdID.compareTo(o.getStdID());
			} catch (Exception e) {
				throw new IllegalStateException("Cannot call compareTo (1)");
			}
		} else if ( type == Type.SPECIAL ) {
			try {
				return specID.compareTo(o.getSpecID());
			} catch (Exception e) {
				throw new IllegalStateException("Cannot call compareTo (2)");
			}
		}

		return 0; // Compiler happy
    }

    @Override
    public int hashCode() {
    	return Objects.hash(specID, stdID, type);
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj) {
    		return true;
    	}
    	
    	if (!(obj instanceof KMMComplAcctID)) {
    		return false;
    	}
    	
    	KMMComplAcctID other = (KMMComplAcctID) obj;
    	return Objects.equals(specID, other.specID) && 
    		   Objects.equals(stdID, other.stdID) &&
    		   type == other.type;
    }

    // -----------------------------------------------------------------

    public static KMMComplAcctID get(Top topCode) {
    	return new KMMComplAcctID(topCode.getCode());
    }
    
    // -----------------------------------------------------------------

    @Override
    public String toString() {
    	return toStringShort();
    }
    
    public String toStringShort() {
		if ( type == Type.STANDARD )
			return stdID.toString();
		else if ( type == Type.SPECIAL )
			return specID;
		else if ( type == Type.UNSET )
			return "(unset)";

		return "(unknown)"; // Compiler happy
    }
    
    public String toStringLong() {
    	return "KMMComplAcctID [type=" + type + ", stdID=" + stdID + ", specID=" + specID + "]";
    }

}
