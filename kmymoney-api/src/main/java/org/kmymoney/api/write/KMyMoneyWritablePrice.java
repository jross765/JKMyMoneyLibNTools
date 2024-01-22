package org.kmymoney.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.write.hlp.KMyMoneyWritablePricePairCore;

/**
 * Price that can be modified.
 * 
 * @see KMyMoneyPrice
 */
public interface KMyMoneyWritablePrice extends KMyMoneyPrice, 
                                               KMyMoneyWritablePricePairCore,
                                               KMyMoneyWritableObject
{

    void setDate(LocalDate date);

    void setDateTime(LocalDateTime dateTime);

    void setSource(Source src);

    void setValue(FixedPointNumber val);

}
