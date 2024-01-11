package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMSecID;

/**
 * In KMyMoney lingo, "security" is an umbrella term for
 * <ul>
 *   <li>(Real) Securities (shares, bonds, funds, etc.)</li>
 *   <li>Possibly other assets that can be mapped to this KMyMoney entity as pseudo-securities, 
 *   such as crypto-currencies, physical precious metals, etc.</li>
 * </ul>
 * <br>
 * Cf. <a href="https://docs.kde.org/stable5/en/kmymoney/kmymoney/details.investments.ledger.html#idm2349">KMyMoney handbook</a>
 */
public interface KMyMoneySecurity {

    /**
     * @return
     */
    KMMSecID getID();

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifSecID getQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     */
    String getSymbol();

    /**
     * ISIN, CUSIP, SEDOL, WKN...
     * @return
     */
    String getCode();

    // ------------------------------------------------------------

    /**
     * @return
     * @throws UnknownSecurityTypeException
     */
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
    
    KMMQualifCurrID getTradingCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    /**
     * @return
     */
    String getTradingMarket();

    // ------------------------------------------------------------

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    Collection<KMyMoneyPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMyMoneyPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
