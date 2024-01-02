package org.kmymoney.api.read;

import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;

public interface KMyMoneyPricePair extends KMyMoneyPricePairCore {

    KMMCurrPair getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    Collection<KMyMoneyPrice> getPrices();
	
}
