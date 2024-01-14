package org.kmymoney.api.write;

import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.write.hlp.KMyMoneyWritablePricePairCore;

/**
 * Price pair that can be modified.
 * 
 * @see KMyMoneyPricePair
 */
public interface KMyMoneyWritablePricePair extends KMyMoneyPrice, 
                                                   KMyMoneyWritablePricePairCore
{

	// ::EMPTY

}
