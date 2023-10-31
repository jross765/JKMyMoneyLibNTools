package org.kmymoney.read.impl;

import org.kmymoney.read.KMMSecCurr;

public class KMMSecCurrImpl {

    // Internal values cf.:
    // https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h
    // 
    // CAUTION: Do *not* change them!
    static final int TYPE_STOCK       = 0;
    static final int TYPE_MUTUAL_FUND = 1;
    static final int TYPE_BOND        = 2;
    static final int TYPE_CURRENCY    = 3;
    static final int TYPE_NONE        = 4;
    
    // ---
    
    // Cf. XYZ
    // ::TODO: Have not found definitions yet
    // The following values are PROBABLY WRONG!
    static final int ROUNDING_METHOD_NEVER = 1;
    static final int ROUNDING_METHOD_FLOOR = 2;
    static final int ROUNDING_METHOD_CEIL = 3;
    static final int ROUNDING_METHOD_TRUNCATE = 4;
    static final int ROUNDING_METHOD_PROMOTE = 5;
    static final int ROUNDING_METHOD_HALF_DOWN = 6;
    static final int ROUNDING_METHOD_HALF_UP = 7;
    static final int ROUNDING_METHOD_ROUND = 8;
    static final int ROUNDING_METHOD_UNKNOWN = 9;
    
    // ---------------------------------------------------------------

    public static KMMSecCurr.Type getType(int typeVal) throws UnknownSecurityTypeException {
	
	if ( typeVal == KMMSecCurrImpl.TYPE_STOCK )
	    return KMMSecCurr.Type.STOCK;
	else if ( typeVal == KMMSecCurrImpl.TYPE_MUTUAL_FUND )
	    return KMMSecCurr.Type.MUTUAL_FUND;
	else if ( typeVal == KMMSecCurrImpl.TYPE_BOND )
	    return KMMSecCurr.Type.BOND;
	else if ( typeVal == KMMSecCurrImpl.TYPE_CURRENCY )
	    return KMMSecCurr.Type.CURRENCY;
	else if ( typeVal == KMMSecCurrImpl.TYPE_NONE )
	    return KMMSecCurr.Type.NONE;
	else
	    throw new UnknownSecurityTypeException();
    }

    public static KMMSecCurr.RoundingMethod getRoundingMethod(int methodVal) throws UnknownRoundingMethodException {
	
	if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_NEVER )
	    return KMMSecCurr.RoundingMethod.NEVER;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_FLOOR )
	    return KMMSecCurr.RoundingMethod.FLOOR;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_CEIL )
	    return KMMSecCurr.RoundingMethod.CEIL;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_TRUNCATE )
	    return KMMSecCurr.RoundingMethod.TRUNCATE;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_PROMOTE )
	    return KMMSecCurr.RoundingMethod.PROMOTE;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_HALF_DOWN )
	    return KMMSecCurr.RoundingMethod.HALF_DOWN;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_HALF_UP )
	    return KMMSecCurr.RoundingMethod.HALF_UP;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_ROUND )
	    return KMMSecCurr.RoundingMethod.ROUND;
	else if ( methodVal == KMMSecCurrImpl.ROUNDING_METHOD_UNKNOWN )
	    return KMMSecCurr.RoundingMethod.UNKNOWN;
	else
	    throw new UnknownRoundingMethodException();
    }

}
