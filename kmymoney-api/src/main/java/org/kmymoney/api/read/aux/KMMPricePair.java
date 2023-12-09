package org.kmymoney.api.read.aux;

import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;

public interface KMMPricePair extends KMMPricePairCore {

    KMMCurrPair getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    Collection<KMMPrice> getPrices();
	
}
