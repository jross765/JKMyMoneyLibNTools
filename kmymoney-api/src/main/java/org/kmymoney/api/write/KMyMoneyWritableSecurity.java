package org.kmymoney.api.write;

import java.math.BigInteger;
import java.util.List;

import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.write.hlp.HasWritableUserDefinedAttributes;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;

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

	// ------------------------------------------------------------
    
    List<KMyMoneyWritableAccount> getWritableStockAccounts();

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
