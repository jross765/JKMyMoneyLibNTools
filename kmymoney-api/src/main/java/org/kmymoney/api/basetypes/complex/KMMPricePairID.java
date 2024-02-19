package org.kmymoney.api.basetypes.complex;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMPricePairID {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMPricePairID.class);

    // ---------------------------------------------------------------

    private KMMQualifSecCurrID fromSecCurr;
    private KMMQualifCurrID    toCurr;
    
    // ---------------------------------------------------------------

	public KMMPricePairID(KMMQualifSecCurrID fromSecCurr, KMMQualifCurrID toCurr) {
		if ( fromSecCurr == null ) {
			throw new IllegalArgumentException("null from-security-currency-ID given");
		}

		if ( toCurr == null ) {
			throw new IllegalArgumentException("null to-currency-ID given");
		}
		
		init();
		
		this.fromSecCurr.set(fromSecCurr);
		this.toCurr.set(toCurr);
	}

	public KMMPricePairID(String fromSecCurr, String toCurr)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
		if ( fromSecCurr == null ) {
			throw new IllegalArgumentException("null from-security-currency-ID given");
		}

		if ( toCurr == null ) {
			throw new IllegalArgumentException("null to-currency-ID given");
		}
		
		init();
		
		setFromSecCurr(fromSecCurr);
		setToCurr(toCurr);
	}
    
    // ---------------------------------------------------------------
	
	private void init() {
		fromSecCurr = new KMMQualifSecCurrID();
		toCurr      = new KMMQualifCurrID();
	}

    // ---------------------------------------------------------------

    public KMMQualifSecCurrID getFromSecCurr() {
        return fromSecCurr;
    }

    public void setFromSecCurr(KMMQualifSecCurrID fromSecCurr) {
		if ( fromSecCurr == null ) {
			throw new IllegalArgumentException("null from-security-currency-ID given");
		}

        this.fromSecCurr.set(fromSecCurr);
    }

	public void setFromSecCurr(String fromSecCurr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
		if ( fromSecCurr == null ) {
			throw new IllegalArgumentException("null from-security-currency-ID given");
		}

		if ( fromSecCurr.startsWith(KMMQualifSecCurrID.PREFIX_SECURITY) ) {
			this.fromSecCurr = new KMMQualifSecID(fromSecCurr);
		} else {
			this.fromSecCurr = new KMMQualifCurrID(fromSecCurr);
		}
	}

    public KMMQualifCurrID getToCurr() {
        return toCurr;
    }

    public void setToCurr(KMMQualifCurrID toCurr) {
		if ( toCurr == null ) {
			throw new IllegalArgumentException("null to-currency-ID given");
		}
		
        this.toCurr.set(toCurr);
    }

    public void setToCurr(String toCurr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
		if ( toCurr == null ) {
			throw new IllegalArgumentException("null to-currency-ID given");
		}
		
        this.toCurr = new KMMQualifCurrID(toCurr);
    }

    // ---------------------------------------------------------------
    
	@Override
	public int hashCode() {
		return Objects.hash(fromSecCurr, toCurr);
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( !(obj instanceof KMMPricePairID) ) {
			return false;
		}
		KMMPricePairID other = (KMMPricePairID) obj;
		return Objects.equals(fromSecCurr.toString(), other.getFromSecCurr().toString()) && // <-- important: toString()!
			   Objects.equals(toCurr.toString(),      other.getToCurr().toString());        // <-- here optional: toString()!
	}

    // ---------------------------------------------------------------
    
	@Override
	public String toString() {
		return toStringShort();
	}

	public String toStringShort() {
		return fromSecCurr.getCode() + ";" + toCurr.getCode();
	}

	public String toStringLong() {
		return "KMMPricePairID [fromSecCurr=" + fromSecCurr.toString() + 
				                  ", toCurr=" + toCurr.toString() + "]";
	}

}
