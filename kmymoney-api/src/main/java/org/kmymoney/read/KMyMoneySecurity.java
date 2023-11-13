package org.kmymoney.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecID;
import org.kmymoney.basetypes.simple.KMMSecID;
import org.kmymoney.read.aux.KMMPrice;

public interface KMyMoneySecurity {

    KMMSecID getId();

    KMMQualifSecID getQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

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

    Collection<KMMPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMMPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
