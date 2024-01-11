package org.kmymoney.api.write;

import java.math.BigInteger;

import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneySecurity;

/**
 * Security that can be modified.
 * 
 * @see KMyMoneySecurity
 */
public interface KMyMoneyWritableSecurity extends KMyMoneySecurity
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
