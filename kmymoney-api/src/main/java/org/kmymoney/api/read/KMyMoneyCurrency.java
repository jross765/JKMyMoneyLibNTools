package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;

public interface KMyMoneyCurrency {

    String getID();

    KMMQualifCurrID getQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    String getSymbol();

    // ------------------------------------------------------------

    KMMSecCurr.Type getType() throws UnknownSecurityTypeException;
    
    String getName();
    
    BigInteger getPP();
    
    KMMSecCurr.RoundingMethod getRoundingMethod() throws UnknownRoundingMethodException;
    
    BigInteger getSAF();
    
    BigInteger getSCF();
    
    // ------------------------------------------------------------

    Collection<KMyMoneyPrice> getQuotes() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
    KMyMoneyPrice getYoungestQuote() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
