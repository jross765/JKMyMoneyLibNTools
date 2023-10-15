package org.kmymoney.read;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.kmymoney.currency.CurrencyNameSpace;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.numbers.FixedPointNumber;

/**
 * It is comparable and sorts primarily on the date the transaction happened
 * and secondarily on the date it was entered.
 */
public interface KMyMoneyTransaction extends Comparable<KMyMoneyTransaction> {

    @SuppressWarnings("exports")
    KMYMONEYFILE.TRANSACTIONS.TRANSACTION getJwsdpPeer();

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    KMyMoneyFile getKMyMoneyFile();

    // ----------------------------------------------------------------

    /**
     *
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    String getId();

    /**
     * @return the user-defined description for this object (may contain multiple
     *         lines and non-ascii-characters)
     */
    String getMemo();

    /**
     *
     * @return the date the transaction was entered into the system
     */
    LocalDateTime getEntryDate();

    /**
     *
     * @return the date the transaction happened
     */
    LocalDateTime getDatePosted();

    /**
     *
     * @return date the transaction happened
     */
    String getDatePostedFormatted();

    // ----------------------------------------------------------------

    /**
     * Do not modify the returned collection!
     * 
     * @return all splits of this transaction.
     */
    List<KMyMoneyTransactionSplit> getSplits();

    /**
     * Get a split of this transaction it's id.
     * 
     * @param id the id to look for
     * @return null if not found
     */
    KMyMoneyTransactionSplit getSplitByID(String id);

    /**
     *
     * @return the first split of this transaction or null.
     * @throws SplitNotFoundException
     */
    KMyMoneyTransactionSplit getFirstSplit() throws SplitNotFoundException;

    /**
     * @return the second split of this transaction or null.
     * @throws SplitNotFoundException
     */
    KMyMoneyTransactionSplit getSecondSplit() throws SplitNotFoundException;

    /**
     *
     * @return the number of splits in this transaction.
     */
    int getSplitsCount();

    /**
     *
     * @return true if the sum of all splits adds up to zero.
     */
    boolean isBalanced();

    /**
     * @return "ISO4217" for a currency "FUND" or a fond,...
     * @see {@link CurrencyNameSpace#NAMESPACE_CURRENCY}
     * @see {@link KMyMoneyAccount#CURRENCY_NAMESPACE_FUND}
     */
    String getCurrencyNameSpace();

    /**
     * The name of the currency in the given namespace e.g. "EUR" for euro in
     * namespace "ISO4217"= {@link CurrencyNameSpace#NAMESPACE_CURRENCY}
     * 
     * @see {@link #getCurrencyNameSpace()}
     */
    String getCurrencyID();

    /**
     * The result is in the currency of the transaction.<br/>
     * if the transaction is unbalanced, get sum of all split-values.
     * 
     * @return the sum of all splits
     * @see #isBalanced()
     */
    FixedPointNumber getBalance();

    /**
     * The result is in the currency of the transaction.
     * 
     * @see KMyMoneyTransaction#getBalance()
     */
    String getBalanceFormatted();

    /**
     * The result is in the currency of the transaction.
     * 
     * @see KMyMoneyTransaction#getBalance()
     */
    String getBalanceFormatted(Locale loc);

    /**
     * The result is in the currency of the transaction.<br/>
     * if the transaction is unbalanced, get the missing split-value to balance it.
     * 
     * @return the sum of all splits
     * @see #isBalanced()
     */
    FixedPointNumber getNegatedBalance();

    /**
     * The result is in the currency of the transaction.
     * 
     * @see KMyMoneyTransaction#getNegatedBalance()
     */
    String getNegatedBalanceFormatted();

    /**
     * The result is in the currency of the transaction.
     * 
     * @see KMyMoneyTransaction#getNegatedBalance()
     */
    String getNegatedBalanceFormatted(Locale loc);
}
