package org.kmymoney.api.read.aux;

import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;

public interface KMMPricePair {

    KMMCurrPair getId() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    String getFromSecCurrStr();
    
    String getToCurrStr();
    
    // ----------------------------
    
    KMMQualifSecCurrID getFromSecCurr() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    KMMQualifCurrID getToCurr() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ---------------------------------------------------------------
    
    Collection<KMMPrice> getPrices();
	
}
