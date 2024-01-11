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
    public KMMComplAcctID getDefaultAccountID();
    
    /**
     * @return
     */
    public KMMAddress getAddress();

    /**
     * @return
     */
    public String getEmail();
    
    /**
     * @return
     */
    public String getReference();
    
    /**
     * @return
     */
    public String getNotes();
    
    // ---------------------------------------------------------------

    /**
     * @return
     */
    public BigInteger getMatchingEnabled();

    /**
     * @return
     */
    public String getMatchKey();

    /**
     * @return
     */
    public BigInteger getUsingMatchKey();

    public BigInteger getMatchIgnoreCase();

}
