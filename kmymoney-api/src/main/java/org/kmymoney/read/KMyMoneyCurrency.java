package org.kmymoney.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.read.aux.KMMPrice;

public interface KMyMoneyCurrency {

    String getId();

    KMMCurrID getQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    String getSymbol();

    // ------------------------------------------------------------

    KMMSecCurr.Type getType() throws UnknownSecurityTypeException;
    
    String getName();
    
    BigInteger getPP();
    
    KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException;
    
    BigInteger getSAF();
    
    BigInteger getSCF();
    
    // ------------------------------------------------------------

    Collection<KMMPrice> getQuotes() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;
    
    KMMPrice getYoungestQuote() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;
    
}
