package org.kmymoney.read.aux;

import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMCurrPair;
import org.kmymoney.basetypes.KMMSecCurrID;

public interface KMMPricePair {

    KMMCurrPair getId() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    String getFromSecCurrStr();
    
    String getToCurrStr();
    
    // ----------------------------
    
    KMMSecCurrID getFromSecCurr() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    KMMCurrID getToCurr() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    Collection<KMMPrice> getPrices();
	
}
