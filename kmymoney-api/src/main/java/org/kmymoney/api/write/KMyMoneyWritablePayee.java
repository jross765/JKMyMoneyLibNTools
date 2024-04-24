package org.kmymoney.api.write;

import java.math.BigInteger;

import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.write.hlp.HasWritableAddress;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;

/**
 * Payee that can be modified.
 * 
 * @see KMyMoneyPayee
 */
public interface KMyMoneyWritablePayee extends KMyMoneyPayee,
                                               KMyMoneyWritableObject,
                                               HasWritableAddress
{

    void remove();
   
	// ---------------------------------------------------------------

    void setName(String name);

    void setDefaultAccountID(KMMComplAcctID acctID);
    
    void setEmail(String eml);
    
    void setReference(String ref);
    
    void setNotes(String nts);
    
    // ---------------------------------------------------------------

    void setMatchingEnabled(BigInteger enbl);

    void setMatchKey(String key);

    void setUsingMatchKey(BigInteger key);

    void setMatchIgnoreCase(BigInteger val);

}
