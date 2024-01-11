package org.kmymoney.api.read;

import java.math.BigInteger;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.read.aux.KMMAddress;

/**
 * Person or entity that is being paid in a transaction.
 * <br>
 * Cf. <a href="https://docs.kde.org/stable5/en/kmymoney/kmymoney/makingmostof.mapping.html#makingmostof.mapping.payees">KMyMoney handbook</a>
 */
public interface KMyMoneyPayee {
    
    /**
     * @return
     */
    KMMPyeID getID();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    KMMComplAcctID getDefaultAccountID();
    
    /**
     * @return
     */
    KMMAddress getAddress();

    /**
     * @return
     */
    String getEmail();
    
    /**
     * @return
     */
    String getReference();
    
    /**
     * @return
     */
    String getNotes();
    
    // ---------------------------------------------------------------

    /**
     * @return
     */
    BigInteger getMatchingEnabled();

    /**
     * @return
     */
    String getMatchKey();

    /**
     * @return
     */
    BigInteger getUsingMatchKey();

    BigInteger getMatchIgnoreCase();

}
