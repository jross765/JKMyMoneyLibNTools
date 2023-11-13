package org.kmymoney.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMAcctID implements Comparable<Object> {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMAcctID.class);

    // A 000 001
    //   6   3
    private final static int STANDARD_LENGTH = 7;
    private final static char PREFIX = 'A';
    private final static int PREFIX_LENGTH = 1;

    protected String kmmID;
    private boolean isSet;

    // -----------------------------------------------------------------

    public KMMAcctID() {
	reset();
    }

    public KMMAcctID(String KMMAccID) throws InvalidKMMAcctIDException {
	set(KMMAccID);
    }

    public KMMAcctID(long counter) throws InvalidKMMAcctIDException {
	set(counter);
    }

    // -----------------------------------------------------------------

    public void reset() {
	kmmID = "";
	isSet = false;
    }

    public String get() throws KMMAcctIDNotSetException {
	if (!isSet)
	    throw new KMMAcctIDNotSetException();

	return kmmID;
    }

    public boolean isSet() {
	return isSet;
    }

    // -----------------------------------------------------------------

    public void set(KMMAcctID value) throws InvalidKMMAcctIDException, KMMAcctIDNotSetException {
	set(value.get());
    }

    public void set(String kmmID) throws InvalidKMMAcctIDException {
	this.kmmID = kmmID;
	standardize();
	validate();
	isSet = true;
    }

    public void set(long counter) throws InvalidKMMAcctIDException {
	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

	if ( counter < 1 || 
	     counter > Math.pow(10, coreLength) - 1 )
	    throw new InvalidKMMAcctIDException("Cannot generate KMM account ID from long " + counter + ": range error");

	String fmtStr = "%0" + coreLength + "d";
	String coreStr = String.format(fmtStr, counter);
	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMAcctIDException {
	if (kmmID.length() != STANDARD_LENGTH)
	    throw new InvalidKMMAcctIDException("No valid KMM account ID string: '" + kmmID + "': wrong string length");

	if (kmmID.charAt(0) != PREFIX)
	    throw new InvalidKMMAcctIDException("No valid KMM account ID string: '" + kmmID + "': wrong prefix");

	for (int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++) {
	    if (!Character.isDigit(kmmID.charAt(i))) {
		logger.error("Char '" + kmmID.charAt(i) + "' is invalid in KMMAcctID '" + kmmID + "'");
		throw new InvalidKMMAcctIDException("No valid KMM account ID string: '" + kmmID + "': wrong character at pos " + i);
	    }
	}
    }

    // -----------------------------------------------------------------

    public String getPrefix() throws KMMAcctIDNotSetException {
	if (!isSet)
	    throw new KMMAcctIDNotSetException();

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
	KMMAcctID other = (KMMAcctID) obj;
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

    @Override
    public String toString() {
	if (isSet)
	    return kmmID;
	else
	    return "(unset)";
    }
}
