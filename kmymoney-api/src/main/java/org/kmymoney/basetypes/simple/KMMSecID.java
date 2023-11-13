package org.kmymoney.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMSecID implements Comparable<Object> {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(KMMSecID.class);

    // E 000 001
    //   6   3
    private final static int STANDARD_LENGTH = 7;
    private final static char PREFIX = 'E';
    private final static int PREFIX_LENGTH = 1;

    protected String kmmID;
    private boolean isSet;

    // -----------------------------------------------------------------

    public KMMSecID() {
	reset();
    }

    public KMMSecID(String kmmID) throws InvalidKMMSecIDException {
	set(kmmID);
    }

    public KMMSecID(long counter) throws InvalidKMMSecIDException {
	set(counter);
    }

    // -----------------------------------------------------------------

    public void reset() {
	kmmID = "";
	isSet = false;
    }

    public String get() throws KMMSecIDNotSetException {
	if (!isSet)
	    throw new KMMSecIDNotSetException();

	return kmmID;
    }

    public boolean isSet() {
	return isSet;
    }

    // -----------------------------------------------------------------

    public void set(KMMSecID value) throws InvalidKMMSecIDException, KMMSecIDNotSetException {
	set(value.get());
    }

    public void set(String kmmID) throws InvalidKMMSecIDException {
	this.kmmID = kmmID;
	standardize();
	validate();
	isSet = true;
    }

    public void set(long counter) throws InvalidKMMSecIDException {
	int coreLength = STANDARD_LENGTH - PREFIX_LENGTH;

	if (counter < 1 || counter > Math.pow(10, coreLength) - 1)
	    throw new InvalidKMMSecIDException();

	String fmtStr = "%0" + coreLength + "d";
	String coreStr = String.format(fmtStr, counter);
	set(PREFIX + coreStr);
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidKMMSecIDException {
	if (kmmID.length() != STANDARD_LENGTH)
	    throw new InvalidKMMSecIDException();

	if (kmmID.charAt(0) != PREFIX)
	    throw new InvalidKMMSecIDException();

	for (int i = PREFIX_LENGTH; i < STANDARD_LENGTH; i++) {
	    if (!Character.isDigit(kmmID.charAt(i))) {
		logger.warn("Char '" + kmmID.charAt(i) + "' is invalid in KMMID '" + kmmID + "'");
		throw new InvalidKMMSecIDException();
	    }
	}
    }

    // -----------------------------------------------------------------

    public String getPrefix() throws KMMSecIDNotSetException {
	if (!isSet)
	    throw new KMMSecIDNotSetException();

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
	KMMSecID other = (KMMSecID) obj;
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
