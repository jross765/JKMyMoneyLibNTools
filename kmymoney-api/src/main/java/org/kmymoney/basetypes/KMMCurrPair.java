package org.kmymoney.basetypes;

import java.util.Objects;

public class KMMCurrPair {
    
    private KMMCurrID fromCurr;
    private KMMCurrID toCurr;
    
    // ---------------------------------------------------------------

    public KMMCurrPair(KMMCurrID fromCurr, KMMCurrID toCurr) {
	this.fromCurr = fromCurr;
	this.toCurr = toCurr;
    }
    
    public KMMCurrPair(String fromCurr, String toCurr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	this.fromCurr = new KMMCurrID(fromCurr);
	this.toCurr = new KMMCurrID(toCurr);
    }
    
    // ---------------------------------------------------------------

    public KMMCurrID getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(KMMCurrID fromCurr) {
        this.fromCurr = fromCurr;
    }

    public void setFromCurr(String fromCurr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
        this.fromCurr = new KMMCurrID(fromCurr);
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
	return Objects.hash(fromCurr, toCurr);
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
	return Objects.equals(fromCurr, other.fromCurr) && Objects.equals(toCurr, other.toCurr);
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	return "KMMCurrPair [fromCurr=" + fromCurr + ", toCurr=" + toCurr + "]";
    }

}
