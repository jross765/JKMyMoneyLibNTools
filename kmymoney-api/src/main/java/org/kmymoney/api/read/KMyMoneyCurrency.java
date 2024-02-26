package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.List;

import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;

/**
 * A KMyMoney currency is just that, i.e. it satisfies the standard definition.
 * <br>
 * Cf. <a href="https://docs.kde.org/stable5/en/kmymoney/kmymoney/details.currencies.html">KMyMoney handbook</a>
 * <br>
 * Cf. <a href="https://en.wikipedia.org/wiki/Currency">Wikipedia</a>
 */
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

    List<KMyMoneyPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMyMoneyPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
