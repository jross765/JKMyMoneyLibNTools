package org.kmymoney.api.read.hlp;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;

/**
 * Auxiliary interface that defines methods that -- for practical reasons,
 * and this is not an error -- <strong>both</strong> the interfaces of 
 * prices and price pair are being derived from.
 * 
 * @see KMyMoneyPrice
 * @see KMyMoneyPricePair
 */
public interface KMyMoneyPricePairCore {

    /**
     * @return
     */
    String getFromSecCurrStr();
    
    /**
     * @return
     */
    String getToCurrStr();
    
    // ----------------------------
    
    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifSecCurrID getFromSecCurrQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifSecID getFromSecurityQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifCurrID getFromCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMyMoneySecurity getFromSecurity() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    String getFromCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMyMoneyCurrency getFromCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ----------------------------

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    KMMQualifCurrID getToCurrencyQualifID() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    String getToCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMyMoneyCurrency getToCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
}
