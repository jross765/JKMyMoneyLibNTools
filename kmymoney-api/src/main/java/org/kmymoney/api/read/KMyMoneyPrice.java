package org.kmymoney.api.read;

import java.time.LocalDate;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMPricePairID;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.hlp.KMyMoneyPricePairCore;

/**
 * A price is an umbrella term comprising:
 * <ul>
 *   <li>A currency's exchange rate</li>
 *   <li>A security's quote</li>
 *   <li>A pseudo-security's price</li> 
 * </ul>
 */
public interface KMyMoneyPrice extends KMyMoneyPricePairCore {

	/*
	 * After superficial code analysis, it seems that the KMyMoney developers 
	 * generally put little emphasis on type safety -- sloppily speaking, 
	 * "everything's a string".
	 * That includes the price source. At present, the author does not even
	 * know precisely whether the strings written by KMyMoney for a price
	 * source are locale-specfic or not (hopefully not).
	 * It seems that we generally *cannot" map from/to an enum. However, 
	 * it might be that we can define a sort of "base enum" for the most
	 * basic/common cases.
	 * Very dissatisfying indeed...     
	 */
    public enum Source {
    	USER        ( "User" ),
    	TRANSACTION ( "Transaction" );
    	
    	// ---
	      
    	private String code = "UNSET";

    	// ---
    	      
    	Source(String code) {
    	    this.code = code;
    	}
    	      
    	// ---
    		
    	public String getCode() {
    	    return code;
    	}
    		
    	// no typo!
    	public static Source valueOff(String code) {
    	    for ( Source src : values() ) {
    	    	if ( src.getCode().equals(code) ) {
    	    		return src;
    	    	}
    	    }
    		    
    	    return null;
    	}
    }
	
    // ---------------------------------------------------------------
    
    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMMPriceID getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMMPricePairID getParentPricePairID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
	
    /**
     * @return
     */
    KMyMoneyPricePair getParentPricePair();
	
    // ----------------------------

    /**
     * @return
     */
    LocalDate getDate();

    String getDateStr();

    /**
     * @return
     */
    Source getSource();

    /**
     * @return
     */
    String getSourceStr();

    /**
     * @return
     */
    FixedPointNumber getValue();
    
    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    String getValueFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
