package org.kmymoney.api.write;

import java.math.BigInteger;

import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;

/**
 * Currency that can be modified.
 * 
 * @see KMyMoneyCurrency
 */
public interface KMyMoneyWritableCurrency extends KMyMoneyCurrency,
                                                  KMyMoneyWritableObject 
{

	// CAUTION: No! 
    // void remove();
   
	// ---------------------------------------------------------------

    void setSymbol(String symb);

    void setName(String name);
    
    void setPP(BigInteger pp);
    
    void setRoundingMethod(KMMSecCurr.RoundingMethod mthd);
    
    void setSAF(BigInteger saf);
    
    void getSCF(BigInteger scf);
    
}
