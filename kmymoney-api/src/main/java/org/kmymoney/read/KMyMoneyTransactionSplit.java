package org.kmymoney.read;

import java.util.Locale;

import org.kmymoney.numbers.FixedPointNumber;

/**
 * This denotes a single addition or removal of some
 * value from one account in a transaction made up of
 * multiple such splits.
 */
public interface KMyMoneyTransactionSplit extends Comparable<KMyMoneyTransactionSplit> {

    // For the following states cf.:
    // https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h

    // ::MAGIC
    // ::TODO Convert to enum
    public static final int STATE_UNKNOWN        = -1;
    public static final int STATE_NOT_RECONCILED = 0;
    public static final int STATE_CLEARED        = 1;
    public static final int STATE_RECONCILES     = 2;
    public static final int STATE_FROZEN         = 3;
    
    // ---------------------------------------------------------------
    
    /**
     *
     * @return the unique-id to identify this object with across name- and hirarchy-changes
     */
    String getId();

    /**
     *
     * @return the id of the account we transfer from/to.
     */
    String getAccountID();

    /**
     * This may be null if an account-id is specified in
     * the gnucash-file that does not belong to an account.
     * @return the account of the account we transfer from/to.
     */
    KMyMoneyAccount getAccount();

    /**
     * @return the transaction this is a split of.
     */
    KMyMoneyTransaction getTransaction();


    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    FixedPointNumber getValue();

    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    String getValueFormatted();
    /**
     * The value is in the currency of the transaction!
     * @param locale the locale to use
     * @return the value-transfer this represents
     */
    String getValueFormatted(Locale locale);
    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    String getValueFormattedForHTML();
    /**
     * The value is in the currency of the transaction!
     * @param locale the locale to use
     * @return the value-transfer this represents
     */
    String getValueFormattedForHTML(Locale locale);

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     */
    FixedPointNumber getAccountBalance();

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     */
    String getAccountBalanceFormatted();

    /**
     * @see KMyMoneyAccount#getBalanceFormatted()
     */
    String getAccountBalanceFormatted(Locale locale);

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    FixedPointNumber getShares();

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    String getSharesFormatted();

    /**
     * The quantity is in the currency of the account!
     * @param locale the locale to use
     * @return the number of items added to the account
     */
    String getSharesFormatted(Locale locale);

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    String getSharesFormattedForHTML();

    /**
     * The quantity is in the currency of the account!
     * @param locale the locale to use
     * @return the number of items added to the account
     */
    String getSharesFormattedForHTML(Locale locale);

    /**
     * @return the user-defined description for this object
     *         (may contain multiple lines and non-ascii-characters)
     */
    String getMemo();

      /**
     * Get the type of association this split has with
     * an invoice's lot.
     * @return null, or one of the ACTION_xyz values defined
     */
    String getAction();

}
