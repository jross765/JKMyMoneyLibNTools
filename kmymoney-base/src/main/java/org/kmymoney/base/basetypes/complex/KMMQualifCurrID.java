package org.kmymoney.base.basetypes.complex;

import java.util.Currency;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMQualifCurrID extends KMMQualifSecCurrID {

	private static final Logger LOGGER = LoggerFactory.getLogger(KMMQualifCurrID.class);

	// ---------------------------------------------------------------

	private Currency curr;

	// ---------------------------------------------------------------

	public KMMQualifCurrID() {
		super();
		
		init();
		
		type = Type.CURRENCY;
	}

	public KMMQualifCurrID(Currency curr) {
		super(Type.CURRENCY, curr.getCurrencyCode());

		init();
		
		setType(Type.CURRENCY);
		setCurrency(curr);
	}

	public KMMQualifCurrID(String currStr) {
		super(Type.CURRENCY, currStr);

		init();
		
		setType(Type.CURRENCY);
		setCurrency(currStr);
	}

	public KMMQualifCurrID(KMMQualifSecCurrID secCurrID)
			{
		super(Type.CURRENCY, secCurrID.getCode());

		if ( getType() != Type.CURRENCY )
			throw new InvalidQualifSecCurrTypeException();

		init();
		
		setType(Type.CURRENCY);
		setCurrency(code);
	}

	// ---------------------------------------------------------------
	
	private void init() {
		curr = Currency.getInstance(Locale.getDefault());
	}

	// ---------------------------------------------------------------

	@Override
	public void setType(Type type) {
//        if ( type != Type.CURRENCY )
//            throw new InvalidCmdtyCurrIDException();

		super.setType(type);
	}

    // ---------------------------------------------------------------

	public Currency getCurrency() {
		if ( type != Type.CURRENCY )
			throw new InvalidQualifSecCurrTypeException();

		return curr;
	}

	public void setCurrency(Currency curr) {
		if ( type != Type.CURRENCY )
			throw new InvalidQualifSecCurrTypeException();

		if ( curr == null )
			throw new IllegalArgumentException("Argument currency is null");

		setCode(curr.getCurrencyCode());
		this.curr = curr;
	}

	public void setCurrency(String iso4217CurrCode) {
		if ( iso4217CurrCode == null )
			throw new IllegalArgumentException("Argument string is null");

		setCurrency(Currency.getInstance(iso4217CurrCode));
	}

    // ---------------------------------------------------------------
    
    public void set(KMMQualifCurrID val) {
    	super.set(val);
    	setCurrency(val.getCurrency());
    }
    
	// ---------------------------------------------------------------

	public static KMMQualifCurrID parse(String str)
			throws InvalidQualifSecCurrIDException {
		if ( str == null )
			throw new IllegalArgumentException("Argument string is null");

		if ( str.equals("") )
			throw new IllegalArgumentException("Argument string is empty");

		KMMQualifCurrID result = new KMMQualifCurrID();

		int posSep = str.indexOf(SEPARATOR);
		// Plausi ::MAGIC
		if ( posSep <= 3 || posSep >= str.length() - 2 )
			throw new InvalidQualifSecCurrIDException();

		String typeStr = str.substring(0, posSep).trim();
		String currStr = str.substring(posSep + 1, str.length()).trim();

		if ( typeStr.equals(Type.CURRENCY.toString()) ) {
			result.setType(Type.CURRENCY);
			result.setCode(currStr);
			result.setCurrency(Currency.getInstance(currStr));
		} else {
			LOGGER.error("parse: Unknown security/currency type '" + typeStr + "'");
			throw new InvalidQualifSecCurrTypeException();
		}

		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((curr == null) ? 0 : curr.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		KMMQualifCurrID other = (KMMQualifCurrID) obj;
		if ( type != other.type )
			return false;
		if ( curr == null ) {
			if ( other.curr != null )
				return false;
		} else if ( !curr.equals(other.curr) )
			return false;
		return true;
	}

	// ---------------------------------------------------------------

    @Override
    public String toString() {
    	return toStringShort();
    }
    
	public String toStringShort() {
		if ( type != Type.CURRENCY )
			return "ERROR";

		String result = Type.CURRENCY.toString() + 
						SEPARATOR + 
						curr.getCurrencyCode();

		return result;
	}

	public String toStringLong() {
		if ( type != Type.CURRENCY )
			return "ERROR";

    	return "KMMQualifCurrID [type=" + type + ", curr=" + curr.toString() + "]";
	}

}
