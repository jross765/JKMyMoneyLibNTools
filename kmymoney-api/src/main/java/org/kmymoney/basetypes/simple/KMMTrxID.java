package org.kmymoney.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMTrxID implements Comparable<Object> {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMTrxID.class);

    // T 000 000 000 000 000 001
    //   1   1   1   9   6   3
    //   8   5   2
    private final static int STANDARD_LENGTH = 19;
    private final static char PREFIX = 'T';
    private final static int PREFIX_LENGTH = 1;

    protected String KMMTrxID;
    private boolean isSet;

    // -----------------------------------------------------------------

    public KMMTrxID() {
	reset();
    }

    public KMMTrxID(String KMMTrxID) throws InvalidKMMTrxIDException {
	set(KMMTrxID);
    }

    public KMMTrxID(long counter) throws InvalidKMMTrxIDException {
	set(counter);
    }

    // -----------------------------------------------------------------

    public void reset() {
	KMMTrxID = "";
	isSet = false;
    }

    public String get() throws KMMTrxIDNotSetException {
	if (!isSet)
	    throw new KMMTrxIDNotSetException();

	return KMMTrxID;
    }

    public boolean isSet() {
	return isSet;
    }

    // -----------------------------------------------------------------

    public void set(KMMTrxID value) throws InvalidKMMTrxIDException, KMMTrxIDNotSetException {
	set(value.get());
    }

    public void set(String KMMTrxID) throws InvalidKMMTrxIDException {
	this.KMMTrxID = KMMTrxID;
	standardize();
	validate();
	isSet = true;
    }

    public void set(long counter) throws InvalidKMMTrxIDException {
	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

	if (counter < 1 || counter > Math.pow(10, coreLength) - 1)
	    throw new InvalidKMMTrxIDException();

	String fmtStr = "%0" + coreLength + "d";
	String coreStr = String.format(fmtStr, counter);
	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMTrxIDException {
	if (KMMTrxID.length() != STANDARD_LENGTH)
	    throw new InvalidKMMTrxIDException();

	if (KMMTrxID.charAt(0) != PREFIX)
	    throw new InvalidKMMTrxIDException();

	for (int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++) {
	    if (!Character.isDigit(KMMTrxID.charAt(i))) {
		logger.warn("Char '" + KMMTrxID.charAt(i) + "' is invalid in KMMTrxID '" + KMMTrxID + "'");
		throw new InvalidKMMTrxIDException();
	    }
	}
    }

    // -----------------------------------------------------------------

    public String getPrefix() throws KMMTrxIDNotSetException {
	if (!isSet)
	    throw new KMMTrxIDNotSetException();

	return KMMTrxID.substring(0, PREFIX_LENGTH);
    }

    public void standardize() {
	KMMTrxID = KMMTrxID.trim().toUpperCase();
    }

    // -----------------------------------------------------------------

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (isSet ? 1231 : 1237);
	result = prime * result + ((KMMTrxID == null) ? 0 : KMMTrxID.hashCode());
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
	KMMTrxID other = (KMMTrxID) obj;
	if (isSet != other.isSet)
	    return false;
	if (KMMTrxID == null) {
	    if (other.KMMTrxID != null)
		return false;
	} else if (!KMMTrxID.equals(other.KMMTrxID))
	    return false;
	return true;
    }

    @Override
    public int compareTo(Object o) {
	return KMMTrxID.compareTo(o.toString());
    }

    @Override
    public String toString() {
	if (isSet)
	    return KMMTrxID;
	else
	    return "(unset)";
    }
}
