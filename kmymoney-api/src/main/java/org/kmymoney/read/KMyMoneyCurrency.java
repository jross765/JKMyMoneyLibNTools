package org.kmymoney.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.read.aux.KMMPrice;

public interface KMyMoneyCurrency {

    String getId();

    KMMQualifCurrID getQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    String getSymbol();

    // ------------------------------------------------------------

    KMMSecCurr.Type getType() throws UnknownSecurityTypeException;
    
    String getName();
    
    BigInteger getPP();
    
    KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException;
    
    BigInteger getSAF();
    
    BigInteger getSCF();
    
    // ------------------------------------------------------------

    Collection<KMMPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMMPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
