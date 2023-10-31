package org.kmymoney.read.aux;

import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrPair;

public interface KMMPricePair {

    KMMCurrPair getId() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    Collection<KMMPrice> getPrices();
	
}
