package org.kmymoney.api.basetypes.complex;

import java.util.Objects;

import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.currency.InvalidKMMComplAcctIDException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMComplAcctID implements Comparable<KMMComplAcctID> {

    public enum Type {
	STANDARD,
	SPECIAL,
	UNSET
    }

    // -----------------------------------------------------------------

    private static final Logger logger = LoggerFactory.getLogger(KMMComplAcctID.class);
    
    private static final String SPEC_PREFIX = "AStd::";
    private static final String SPEC_ACCT_ID_1 = SPEC_PREFIX + "Asset";
    private static final String SPEC_ACCT_ID_2 = SPEC_PREFIX + "Liability";
    private static final String SPEC_ACCT_ID_3 = SPEC_PREFIX + "Expense";
    private static final String SPEC_ACCT_ID_4 = SPEC_PREFIX + "Income";
    private static final String SPEC_ACCT_ID_5 = SPEC_PREFIX + "Equity";

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

    public KMMComplAcctID(KMMAcctID stdAcctID) throws InvalidKMMComplAcctIDException {
	init();
	reset();
	setType(Type.STANDARD);
	setStdID(stdAcctID);
    }

    public KMMComplAcctID(String str) throws InvalidKMMComplAcctIDException {
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

    public KMMAcctID getStdID() throws InvalidKMMComplAcctIDException {
	if ( type != Type.STANDARD )
	    throw new InvalidKMMComplAcctIDException();
	
        return stdID;
    }

    public void setStdID(KMMAcctID stdID) throws InvalidKMMComplAcctIDException {
	if ( type != Type.STANDARD )
	    throw new InvalidKMMComplAcctIDException();

        this.stdID = stdID;
    }

    public String getSpecID() throws InvalidKMMComplAcctIDException {
	if ( type != Type.SPECIAL )
	    throw new InvalidKMMComplAcctIDException();
	
        return specID;
    }

    public void setSpecID(String specID) throws InvalidKMMComplAcctIDException {
	if ( type != Type.SPECIAL )
	    throw new InvalidKMMComplAcctIDException();
	
	if ( ! checkSpecID(specID.trim()) )
	    throw new InvalidKMMComplAcctIDException();
	
        this.specID = specID.trim();
    }

    private boolean checkSpecID(String specID) {
	if ( specID.equals(SPEC_ACCT_ID_1) ||
	     specID.equals(SPEC_ACCT_ID_2) ||
	     specID.equals(SPEC_ACCT_ID_3) ||
	     specID.equals(SPEC_ACCT_ID_4) ||
	     specID.equals(SPEC_ACCT_ID_5) )
	{
	    return true;
	}
	else
	{
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
	
	if ( type == Type.STANDARD )
	{
	    try {
		return stdID.compareTo(o.getStdID());
	    } catch (InvalidKMMComplAcctIDException e) {
		throw new IllegalStateException("Cannot call compareTo (1)");
	    }
	}
	else if ( type == Type.SPECIAL )
	{
	    try {
		return specID.compareTo(o.getSpecID());
	    } catch (InvalidKMMComplAcctIDException e) {
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
