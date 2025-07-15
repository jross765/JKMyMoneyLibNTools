package org.kmymoney.base.basetypes.complex;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.Objects;

import org.kmymoney.base.Const;
import org.kmymoney.base.basetypes.simple.KMMIDNotSetException;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * KMyMoney has no IDs for the price objects (neither on the price-pair level
 * nor on the price level).
 * <br>
 * I cannot understand this -- how can you possibly work with hundreds, thousands 
 * or even tens of thousands of prices without properly identifying them?
 * <br>
 * Anyway: this fact is the reason why we here have pseudo-ID for a price object: 
 * The tuple ( from-currency, to-currency, date ),
 * or, alternatively:
 * The tuple ( (ID of parent price-pair object), date ).
 *
 * @see KMMPricePairID
 */
public class KMMPriceID {
    
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KMMPriceID.class);

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // -----------------------------------------------------------

    protected String fromSecCurr;
    protected String toCurr;
    protected String dateStr;
    
    // ---------------------------------------------------------------

	public KMMPriceID(String fromSecCurr, String toCurr, String dateStr) {
		init();
		
		this.fromSecCurr = fromSecCurr;
		this.toCurr = toCurr;
		this.dateStr = dateStr;
	}

	public KMMPriceID(KMMQualifSecCurrID fromSecCurr, KMMQualifCurrID toCurr, LocalDate date) {
		init();
		
		this.fromSecCurr = fromSecCurr.getCode();
		this.toCurr = toCurr.getCode();
		this.dateStr = DATE_FORMAT.format(date);
	}
    
	public KMMPriceID(KMMPricePairID prcPr, LocalDate date) {
		init();
		
		this.fromSecCurr = prcPr.getFromSecCurr().getCode();
		this.toCurr = prcPr.getToCurr().getCode();
		this.dateStr = DATE_FORMAT.format(date);
	}
    
    // ---------------------------------------------------------------
	
	private void init() {
		// ::EMPTY
	}

	public boolean isSet() {
		if ( fromSecCurr.trim().equals("") ||
			 fromSecCurr.equals("(unset)") ) {
			return false;
		}
		
		if ( toCurr.trim().equals("") ||
			 toCurr.equals("(unset)") ) {
			return false;
		}
			
		if ( dateStr.trim().equals("") ||
			 dateStr.equals("(unset)") ) {
			return false;
		}
				
		return true;
	}

	public void reset() {
		fromSecCurr = "(unset)";
		toCurr      = "(unset)";
		dateStr     = "(unset)";
	}

    // ---------------------------------------------------------------

    public String getFromSecCurr() {
        return fromSecCurr;
    }

    public void setFromSecCurr(String fromSecCurr) {
    	if ( fromSecCurr == null ) {
    		throw new IllegalArgumentException("null security-currency-ID given");
    	}
    	
        this.fromSecCurr = fromSecCurr;
    }

    public void setFromSecCurr(KMMQualifSecCurrID fromSecCurr) {
    	if ( fromSecCurr == null ) {
    		throw new IllegalArgumentException("null security-currency-ID given");
    	}
    	
        setFromSecCurr(fromSecCurr.getCode());
    }

    public void setFromSecID(KMMSecID fromSecID) throws KMMIDNotSetException {
    	if ( fromSecID == null ) {
    		throw new IllegalArgumentException("null security-ID given");
    	}
    	
        setFromSecCurr(fromSecID.get());
    }

    public void setFromCurr(Currency fromCurr) throws KMMIDNotSetException {
    	if ( fromCurr == null ) {
    		throw new IllegalArgumentException("null currency given");
    	}
    	
        setFromSecCurr(fromCurr.getCurrencyCode());
    }

    public void setFromCurr(String fromCurrCode) throws KMMIDNotSetException {
    	if ( fromCurrCode == null ) {
    		throw new IllegalArgumentException("null currency given");
    	}
    	
        setFromCurr(Currency.getInstance(fromCurrCode));
    }

    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String toCurr) {
    	if ( toCurr == null ) {
    		throw new IllegalArgumentException("null currency-ID given");
    	}
    	
        this.toCurr = toCurr;
    }

    public void setToCurr(KMMQualifCurrID toCurr) {
    	if ( toCurr == null ) {
    		throw new IllegalArgumentException("null currency-ID given");
    	}
    	
        setToCurr(toCurr.getCode());
    }

    public void setToCurr(Currency toCurr) throws KMMIDNotSetException {
    	if ( toCurr == null ) {
    		throw new IllegalArgumentException("null currency given");
    	}
    	
        setToCurr(toCurr.getCurrencyCode());
    }

//    public void setToCurr(String toCurrCode) throws KMMIDNotSetException {
//    	if ( toCurrCode == null ) {
//    		throw new IllegalArgumentException("null currency given");
//    	}
//    	
//        setFromCurr(Currency.getInstance(toCurrCode));
//    }

    public String getDateStr() {
        return dateStr;
    }

    public LocalDate getDate() {
    	if ( getDateStr() == null )
    		return null;
	
    	return LocalDate.parse(getDateStr());
    }
    
    public void setDateStr(String dateStr) {
    	if ( dateStr == null ) {
    		throw new IllegalArgumentException("null date given");
    	}
    	
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
	
	public KMMPricePairID getPricePairID() {
		return new KMMPricePairID(getFromSecCurr(), getToCurr());
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
	return "KMMPriceID [fromSecCurr=" + fromSecCurr + 
			              ", toCurr=" + toCurr + 
			             ", dateStr=" + dateStr + "]";
    }

}
