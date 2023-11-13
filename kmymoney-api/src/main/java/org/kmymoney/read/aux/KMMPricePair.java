package org.kmymoney.read.aux;

import java.util.Collection;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMCurrPair;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecCurrID;

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
