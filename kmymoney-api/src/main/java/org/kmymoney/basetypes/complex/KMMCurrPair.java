package org.kmymoney.basetypes.complex;

import java.util.Objects;

public class KMMCurrPair {
    
    private KMMQualifSecCurrID fromSecCurr;
    private KMMQualifCurrID    toCurr;
    
    // ---------------------------------------------------------------

    public KMMCurrPair(KMMQualifSecCurrID fromCurr, KMMQualifCurrID toCurr) {
	this.fromSecCurr = fromCurr;
	this.toCurr = toCurr;
    }
    
    public KMMCurrPair(String fromCurr, String toCurr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	setFromSecCurr(fromCurr);
	setToCurr(toCurr);
    }
    
    // ---------------------------------------------------------------

    public KMMQualifSecCurrID getFromSecCurr() {
        return fromSecCurr;
    }

    public void setFromCurr(KMMQualifSecCurrID fromCurr) {
        this.fromSecCurr = fromCurr;
    }

    public void setFromSecCurr(String fromCurr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( fromCurr.startsWith("E0") ) { // ::MAGIC
	    this.fromSecCurr = new KMMQualifSecID(fromCurr);
	} else {
	    this.fromSecCurr = new KMMQualifCurrID(fromCurr);
	}
    }

    public KMMQualifCurrID getToCurr() {
        return toCurr;
    }

    public void setToCurr(KMMQualifCurrID toCurr) {
        this.toCurr = toCurr;
    }

    public void setToCurr(String toCurr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
        this.toCurr = new KMMQualifCurrID(toCurr);
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
