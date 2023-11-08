package org.kmymoney.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.impl.UnknownRoundingMethodException;
import org.kmymoney.read.impl.UnknownSecurityTypeException;

public interface KMyMoneySecurity {

    String getId();

    KMMSecID getQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

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
    
    KMMCurrID getTradingCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    String getTradingMarket();

    // ------------------------------------------------------------

    Collection<KMMPrice> getQuotes() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;
    
    KMMPrice getYoungestQuote() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;
    
}
