package org.kmymoney.api.write;

import java.math.BigInteger;

import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.write.hlp.HasWritableUserDefinedAttributes;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;

/**
 * Security that can be modified.
 * 
 * @see KMyMoneySecurity
 */
public interface KMyMoneyWritableSecurity extends KMyMoneySecurity,
                                                  KMyMoneyWritableObject,
                                                  HasWritableUserDefinedAttributes
{

	void remove();

	// ---------------------------------------------------------------

	void setSymbol(String symb);

	void setCode(String code);

	// ---------------------------------------------------------------

	void setType(KMMSecCurr.Type type);

	void setName(String name);

	void setPP(BigInteger pp);

	void setRoundingMethod(KMMSecCurr.RoundingMethod meth);

	void setSAF(BigInteger saf);

	void setTradingCurrency(KMMQualifCurrID currID);

	void setTradingMarket(String mkt);

}
