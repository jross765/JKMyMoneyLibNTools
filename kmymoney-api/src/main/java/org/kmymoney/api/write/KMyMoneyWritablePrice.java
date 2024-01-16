package org.kmymoney.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.write.hlp.KMyMoneyWritablePricePairCore;

/**
 * Price that can be modified.
 * 
 * @see KMyMoneyPrice
 */
public interface KMyMoneyWritablePrice extends KMyMoneyPrice, 
                                               KMyMoneyWritablePricePairCore
{

    void setFromSecCurrQualifID(KMMQualifSecCurrID qualifID);

    void setFromSecurityQualifID(KMMSecID qualifID);

    void setFromCurrencyQualifID(KMMQualifCurrID qualifID);

    void setFromSecurity(KMyMoneySecurity sec);

    void setFromCurrencyCode(String code);

    void setFromCurrency(KMyMoneyCurrency curr);
    
    // ----------------------------

    void setToCurrencyQualifID(KMMQualifSecCurrID qualifID) throws InvalidQualifSecCurrTypeException;

    void setToCurrencyQualifID(KMMQualifCurrID qualifID);

    void setToCurrencyCode(String code);

    void setToCurrency(KMyMoneyCurrency curr) throws InvalidQualifSecCurrTypeException;

    // ----------------------------

    void setDate(LocalDate date);

    void setDateTime(LocalDateTime dateTime);

    void setSource(Source src);

    void setValue(FixedPointNumber val);

}
