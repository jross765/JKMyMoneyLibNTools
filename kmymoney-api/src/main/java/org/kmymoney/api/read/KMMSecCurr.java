package org.kmymoney.api.read;

import java.math.BigInteger;

public class KMMSecCurr {

    // Cf. https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h
    public static enum Type {
    	
    	// ::MAGIC
        STOCK       ( 0 ),
        MUTUAL_FUND ( 1 ),
        BOND        ( 2 ),
        CURRENCY    ( 3 ),
        NONE        ( 4 );
    	
    	// ---

    	final public static int UNSET = 999;
    	
    	private BigInteger code = BigInteger.valueOf(UNSET);

    	// ---

    	Type(BigInteger code) {
    	    this.code = code;
    	}

    	Type(int code) {
    	    this.code = BigInteger.valueOf(code);
    	}

    	// ---

    	public BigInteger getCode() {
    	    return code;
    	}

    	// no typo!
    	public static Type valueOff(BigInteger code) {
    	    for (Type type : values()) {
    	    	if (type.getCode().equals(code)) {
    	    		return type;
    			}
    	    }

    	    return null;
    	}
    	
    	// no typo!
    	public static Type valueOff(int code) {
    	    return valueOff(BigInteger.valueOf(code));
    	}
    }
    
    // ----------------------------

    // ::TODO: Could not find mapping to integers yet.
    // Cf.: 
    //  - xxx
    //  - https://lxr.kde.org/ident?_i=RoundingMethod
    //  - https://lxr.kde.org/ident?_i=AlkValue
    //  - https://lxr.kde.org/source/office/kmymoney/kmymoney/mymoney/mymoneysecurity.cpp#0246
    public static enum RoundingMethod {
        NEVER,
        FLOOR,
        CEIL,
        TRUNCATE,
        PROMOTE,
        HALF_DOWN,
        HALF_UP,
        ROUND,
        UNKNOWN
    }

}
