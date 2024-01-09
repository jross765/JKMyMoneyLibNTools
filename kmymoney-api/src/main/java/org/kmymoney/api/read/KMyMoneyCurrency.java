package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;

public interface KMyMoneyCurrency {

    /**
     * @return
     */
    String getID();

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifCurrID getQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     */
    String getSymbol();

    // ------------------------------------------------------------

    /**
     * @return
     * @throws UnknownSecurityTypeException
     */
    KMMSecCurr.Type getType() throws UnknownSecurityTypeException;
    
    /**
     * @return
     */
    String getName();
    
    /**
     * @return
     */
    BigInteger getPP();
    
    /**
     * @return
     * @throws UnknownRoundingMethodException
     */
    KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException;
    
    /**
     * @return
     */
    BigInteger getSAF();
    
    /**
     * @return
     */
    BigInteger getSCF();
    
    // ------------------------------------------------------------

    Collection<KMyMoneyPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMyMoneyPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
