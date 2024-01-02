package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMSecID;

public interface KMyMoneySecurity {

    KMMSecID getID();

    KMMQualifSecID getQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    String getSymbol();

    /**
     * ISIN, CUSIP, SEDOL, WKN...
     * @return
     */
    String getCode();

    // ------------------------------------------------------------

    KMMSecCurr.Type getType() throws UnknownSecurityTypeException;
    
    String getName();
    
    BigInteger getPP();
    
    KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException;
    
    BigInteger getSAF();
    
    KMMQualifCurrID getTradingCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    String getTradingMarket();

    // ------------------------------------------------------------

    Collection<KMyMoneyPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMyMoneyPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
