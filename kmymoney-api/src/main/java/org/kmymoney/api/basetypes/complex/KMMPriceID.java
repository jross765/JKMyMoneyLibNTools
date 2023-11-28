package org.kmymoney.api.basetypes.complex;

import java.util.Objects;

/**
 * KMyMoney has no IDs for the price objects (neither on the price-pair level
 * nor on the price level).
 * 
 * I cannot understand this -- how can you possibly work with hundreds, thousands 
 * or even tens of thousands of prices without properly identifying them?
 * 
 * Anyway: this fact is the reason why we here have a price object pseudo-ID: 
 * The tuple ( from-currency, to-currency, date ).
 */
public class KMMPriceID {
    
    private String fromCurr;
    private String toCurr;
    private String dateStr;
    
    // ---------------------------------------------------------------

    public KMMPriceID(String fromCurr, String toCurr, String dateStr) {
	this.fromCurr = fromCurr;
	this.toCurr = toCurr;
	this.dateStr = dateStr;
    }
    
    // ---------------------------------------------------------------

    public String getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }

    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    // ---------------------------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(dateStr, fromCurr, toCurr);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof KMMPriceID)) {
	    return false;
	}
	KMMPriceID other = (KMMPriceID) obj;
	return Objects.equals(dateStr, other.dateStr) && 
	       Objects.equals(fromCurr, other.fromCurr) && 
	       Objects.equals(toCurr, other.toCurr);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	return toStringShort();
    }
        
    public String toStringShort() {
	return fromCurr + ";" + toCurr + ";" + dateStr;
    }
        
    public String toStringLong() {
	return "KMMPriceID [fromCurr=" + fromCurr + ", toCurr=" + toCurr + ", dateStr=" + dateStr + "]";
    }
        
}
