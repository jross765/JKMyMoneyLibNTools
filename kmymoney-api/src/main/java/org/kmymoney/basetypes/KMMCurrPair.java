package org.kmymoney.basetypes;

import java.util.Objects;

public class KMMCurrPair {
    
    private KMMSecCurrID fromSecCurr;
    private KMMCurrID    toCurr;
    
    // ---------------------------------------------------------------

    public KMMCurrPair(KMMSecCurrID fromCurr, KMMCurrID toCurr) {
	this.fromSecCurr = fromCurr;
	this.toCurr = toCurr;
    }
    
    public KMMCurrPair(String fromCurr, String toCurr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	setFromSecCurr(fromCurr);
	setToCurr(toCurr);
    }
    
    // ---------------------------------------------------------------

    public KMMSecCurrID getFromSecCurr() {
        return fromSecCurr;
    }

    public void setFromCurr(KMMSecCurrID fromCurr) {
        this.fromSecCurr = fromCurr;
    }

    public void setFromSecCurr(String fromCurr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if ( fromCurr.startsWith("E0") ) { // ::MAGIC
	    this.fromSecCurr = new KMMSecID(fromCurr);
	} else {
	    this.fromSecCurr = new KMMCurrID(fromCurr);
	}
    }

    public KMMCurrID getToCurr() {
        return toCurr;
    }

    public void setToCurr(KMMCurrID toCurr) {
        this.toCurr = toCurr;
    }

    public void setToCurr(String toCurr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
        this.toCurr = new KMMCurrID(toCurr);
    }

    // ---------------------------------------------------------------
    
    @Override
    public int hashCode() {
	return Objects.hash(fromSecCurr, toCurr);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof KMMCurrPair)) {
	    return false;
	}
	KMMCurrPair other = (KMMCurrPair) obj;
	return Objects.equals(fromSecCurr, other.fromSecCurr) && Objects.equals(toCurr, other.toCurr);
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	return toStringShort();
    }

    public String toStringShort() {
	return fromSecCurr + ";" + toCurr;
    }

    public String toStringLong() {
	return "KMMCurrPair [fromCurr=" + fromSecCurr + ", toCurr=" + toCurr + "]";
    }

}
