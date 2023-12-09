package org.kmymoney.api.read.aux;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneySecurity;

public interface KMMPricePairCore {

    String getFromSecCurrStr();
    
    String getToCurrStr();
    
    // ----------------------------
    
    KMMQualifSecCurrID getFromSecCurrQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMMQualifSecID getFromSecurityQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMMQualifCurrID getFromCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneySecurity getFromSecurity() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    String getFromCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneyCurrency getFromCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ----------------------------

    KMMQualifCurrID getToCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    String getToCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneyCurrency getToCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
}
