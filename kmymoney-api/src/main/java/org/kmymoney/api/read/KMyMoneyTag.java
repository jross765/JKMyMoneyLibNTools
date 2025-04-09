package org.kmymoney.api.read;

import java.math.BigInteger;
import java.util.Collection;

import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMTagID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

/**
 * Person or entity that is being paid in a transaction.
 * <br>
 * Cf. <a href="https://docs.kde.org/stable5/en/kmymoney/kmymoney/makingmostof.mapping.html#makingmostof.mapping.payees">KMyMoney handbook</a>
 */
public interface KMyMoneyTag extends KMyMoneyObject
{
    
    /**
     * @return
     */
    KMMTagID getID();

    /**
     * @return
     */
    String getName();

    /**
     * @return
     */
    String getColor();

    /**
     * @return
     */
    String getNotes();
    
    /**
     * @return
     */
    boolean isClosed();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    Collection<KMMQualifSpltID> getTransactionSplitIDs();

}
