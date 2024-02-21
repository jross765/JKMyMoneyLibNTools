package org.kmymoney.api.write;

import java.math.BigInteger;

import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.write.aux.KMMWritableAddress;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;

/**
 * Payee that can be modified.
 * 
 * @see KMyMoneyPayee
 */
public interface KMyMoneyWritablePayee extends KMyMoneyPayee,
                                               KMyMoneyWritableObject 
{

    void remove();
   
    // ---------------------------------------------------------------

    KMMWritableAddress getWritableAddress();
    
    KMMWritableAddress createWritableAddress();
    
	void remove(KMMWritableAddress impl);

	// ---------------------------------------------------------------

    void setName(String name);

    void setDefaultAccountID(KMMComplAcctID acctID);
    
    void setEmail(String eml);
    
    void setReference(String ref);
    
    void setAddress(KMMAddress adr);

    void setNotes(String nts);
    
    // ---------------------------------------------------------------

    void setMatchingEnabled(BigInteger enbl);

    void setMatchKey(String key);

    void setUsingMatchKey(BigInteger key);

    void setMatchIgnoreCase(BigInteger val);

}
