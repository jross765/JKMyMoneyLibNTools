package org.kmymoney.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPricePair;
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

	KMyMoneyWritablePricePair getWritableParentPricePair();
	
    // ----------------------------

    void setParentPricePairID(KMMPricePairID prcPrID);
	
    void setParentPricePair(KMyMoneyPricePair prcPr);
	
    // ----------------------------

    void setDate(LocalDate date);

    void setDateTime(LocalDateTime dateTime);

    void setSource(Source src);

    void setSourceStr(String src);

    void setValue(FixedPointNumber val);

}
