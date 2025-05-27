package org.kmymoney.api.read;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.kmymoney.api.read.hlp.HasAddress;
import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

/**
 * Person or entity that is being paid in a transaction.
 * <br>
 * Cf. <a href="https://docs.kde.org/stable5/en/kmymoney/kmymoney/makingmostof.mapping.html#makingmostof.mapping.payees">KMyMoney handbook</a>
 */
public interface KMyMoneyPayee extends KMyMoneyObject,
                                       HasAddress
{
    
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

    // -----------------------------------------------------------------

    List<KMyMoneyTransactionSplit> getTransactionSplits();

    // ----------------------------

    /**
     * @return true if ${@link #getTransactionSplits()}.size()>0
     */
    boolean hasTransactions();

    /**
     * The returned list ist sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     * {@link KMyMoneyTransaction}
     */
    List<KMyMoneyTransaction> getTransactions();

    List<KMyMoneyTransaction> getTransactions(LocalDate fromDate, LocalDate toDate);

    /**
     * @param spltID 
     * @return the identified split or null
     */
    KMyMoneyTransactionSplit getTransactionSplitByID(KMMQualifSpltID spltID);
    
    /**
     * @param splt split to add to this transaction
     */
    void addTransactionSplit(KMyMoneyTransactionSplit splt);
}
