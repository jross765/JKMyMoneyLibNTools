package org.kmymoney.api.basetypes.complex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.kmymoney.api.Const;
import org.kmymoney.api.read.KMyMoneyPricePair;

/**
 * KMyMoney has no IDs for the price objects (neither on the price-pair level
 * nor on the price level).
 * <br>
 * I cannot understand this -- how can you possibly work with hundreds, thousands 
 * or even tens of thousands of prices without properly identifying them?
 * <br>
 * Anyway: this fact is the reason why we here have a price object pseudo-ID: 
 * The tuple ( from-currency, to-currency, date ).
 */
public class KMMPriceID {
    
    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // -----------------------------------------------------------

    private String fromSecCurr;
    private String toCurr;
    private String dateStr;
    
    // ---------------------------------------------------------------

	public KMMPriceID(String fromSecCurr, String toCurr, String dateStr) {
		this.fromSecCurr = fromSecCurr;
		this.toCurr = toCurr;
		this.dateStr = dateStr;
	}

	public KMMPriceID(KMyMoneyPricePair prcPair, String dateStr) {
		this.fromSecCurr = prcPair.getFromSecCurrStr();
		this.toCurr = prcPair.getToCurrStr();
		this.dateStr = dateStr;
	}
    
    // ---------------------------------------------------------------

    public String getFromSecCurr() {
        return fromSecCurr;
    }

    public void setFromSecCurr(String fromSecCurr) {
        this.fromSecCurr = fromSecCurr;
    }

    public void setFromSecCurr(KMMQualifSecCurrID fromSecCurr) {
        setFromSecCurr(fromSecCurr.getCode());
    }

    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }

    public void setToCurr(KMMQualifCurrID toCurr) {
        setToCurr(toCurr.getCode());
    }

    public String getDateStr() {
        return dateStr;
    }

    public LocalDate getDate() {
    	if ( getDateStr() == null )
    		return null;
	
    	return LocalDate.parse(getDateStr());
    }
    
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setDate(LocalDate date) {
    	if ( date == null ) {
    		throw new IllegalArgumentException("null date given");
    	}
    	
    	setDateStr(DATE_FORMAT.format(date));
    }
    
    // ----------------------------

	public void set(KMMPriceID id) {
		setFromSecCurr(id.getFromSecCurr());
		setToCurr(id.getToCurr());
		setDateStr(id.getDateStr());
	}
        
    // ---------------------------------------------------------------

    @Override
    public int hashCode() {
	return Objects.hash(dateStr, fromSecCurr, toCurr);
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
	       Objects.equals(fromSecCurr, other.fromSecCurr) && 
	       Objects.equals(toCurr, other.toCurr);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	return toStringShort();
    }
        
    public String toStringShort() {
	return fromSecCurr + ";" + toCurr + ";" + dateStr;
    }
        
    public String toStringLong() {
	return "KMMPriceID [fromSecCurr=" + fromSecCurr + ", toCurr=" + toCurr + ", dateStr=" + dateStr + "]";
    }

}
