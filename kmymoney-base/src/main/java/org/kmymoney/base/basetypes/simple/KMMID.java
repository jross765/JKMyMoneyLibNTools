package org.kmymoney.base.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class defining a technical entry ID for all KMyMoney entities
 * except currencies and prices.
 * (essentially a counter with an entity-specific prefix letter, 
 * primarily defined for type safety).
 */
public abstract class KMMID implements Comparable<Object> {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMID.class);

    // X 000 001
    //   6   3
    protected final static int STANDARD_LENGTH = 7;
    protected final static char PREFIX = 'X';
    protected final static int PREFIX_LENGTH = 1;

    // -----------------------------------------------------------------

    protected String kmmID;
    protected boolean isSet;

    // -----------------------------------------------------------------

    public KMMID() {
    	reset();
    }

    public KMMID(String idStr) throws InvalidKMMIDException {
    	set(idStr);
    }

    public KMMID(long counter) throws InvalidKMMIDException {
    	set(counter);
    }

    // -----------------------------------------------------------------

    public void reset() {
    	kmmID = "";
    	isSet = false;
    }

    public String get() throws KMMIDNotSetException {
    	if (!isSet)
    		throw new KMMIDNotSetException();

    	return kmmID;
    }

    public boolean isSet() {
    	return isSet;
    }

    // -----------------------------------------------------------------

    public void set(KMMID value) throws KMMIDNotSetException {
    	set(value.get());
    }

    public void set(String kmmID) throws InvalidKMMIDException {
    	this.kmmID = kmmID;
    	standardize();
    	validate();
    	isSet = true;
    }

    public void set(long counter) throws InvalidKMMIDException {
    	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

    	if ( counter < 1 || 
    		 counter > Math.pow(10, coreLength) - 1 )
    		throw new InvalidKMMIDException("Cannot generate KMM ID from long " + counter + ": range error");

    	String fmtStr = "%0" + coreLength + "d";
    	String coreStr = String.format(fmtStr, counter);
    	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMIDException {
    	if ( kmmID.length() != STANDARD_LENGTH )
    		throw new InvalidKMMIDException("No valid KMM ID string: '" + kmmID + "': wrong string length");

    	if ( kmmID.charAt(0) != PREFIX )
    		throw new InvalidKMMIDException("No valid KMM ID string: '" + kmmID + "': wrong prefix");

    	for ( int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++ ) {
    		if ( ! Character.isDigit(kmmID.charAt(i)) ) {
    			logger.error("Char '" + kmmID.charAt(i) + "' is invalid in KMMID '" + kmmID + "'");
    			throw new InvalidKMMIDException("No valid KMM ID string: '" + kmmID + "': wrong character at pos " + i);
    		}
    	}
    }

    // -----------------------------------------------------------------

    public String getPrefix() throws KMMIDNotSetException {
    	if (!isSet)
    		throw new KMMIDNotSetException();

    	return kmmID.substring(0, PREFIX_LENGTH);
    }

    public void standardize() {
    	kmmID = kmmID.trim().toUpperCase();
    }

    // -----------------------------------------------------------------

    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result + (isSet ? 1231 : 1237);
    	result = prime * result + ((kmmID == null) ? 0 : kmmID.hashCode());
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
    	KMMID other = (KMMID) obj;
    	if (isSet != other.isSet)
    		return false;
    	if (kmmID == null) {
    		if (other.kmmID != null)
    			return false;
    	} else if (!kmmID.equals(other.kmmID))
    		return false;
    	return true;
    }

    @Override
    public int compareTo(Object o) {
    	return kmmID.compareTo(o.toString());
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
    	if (isSet)
    		return kmmID;
    	else
    		return "(unset)";
    }
}
