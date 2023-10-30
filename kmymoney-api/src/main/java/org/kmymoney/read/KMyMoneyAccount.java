package org.kmymoney.read;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.currency.ComplexPriceTable;
import org.kmymoney.numbers.FixedPointNumber;

/**
 * An account is a collection of transactions that start or end there. <br>
 * You can compare it's functionality to an abstracted bank account. <br>
 * It has a balance, may have a parent-account(@see #getParentAccount()) and child-accounts(@see #getSubAccounts()) to form
 * a tree. <br>
 * 
 * @see #getParentAccount()
 */
public interface KMyMoneyAccount extends Comparable<KMyMoneyAccount> {

    // For the following types cf.:
    // https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyaccount.h
    //
    /**
     * The current assignment is as follows:
     *
     * - Asset
     *   - Asset
     *   - Checkings
     *   - Savings
     *   - Cash
     *   - Currency
     *   - Investment
     *   - MoneyMarket
     *   - CertificateDep
     *   - AssetLoan
     *   - Stock
     *
     * - Liability
     *   - Liability
     *   - CreditCard
     *   - Loan
     *
     * - Income
     *   - Income
     *
     * - Expense
     *   - Expense
     *
     * - Equity
     *   - Equity
     */
    
    // For the following types cf.:
    // https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h
    /*
     * Checkings,         Standard checking account
     * Savings,              Typical savings account
     * Cash,                 Denotes a shoe-box or pillowcase stuffed with cash
     * CreditCard,           Credit card accounts
     * Loan,                 Loan and mortgage accounts (liability)
     * CertificateDep,       Certificates of Deposit
     * Investment,           Investment account
     * MoneyMarket,          Money Market Account
     * Asset,                Denotes a generic asset account
     * Liability,            Denotes a generic liability account.
     * Currency,             Denotes a currency trading account.
     * Income,               Denotes an income account
     * Expense,              Denotes an expense account
     * AssetLoan,            Denotes a loan (asset of the owner of this object)
     * Stock,                Denotes an security account as sub-account for an investment
     * Equity,               Denotes an equity account e.g. opening/closing balance
    */
    
    // ::MAGIC
    // ::TODO Convert to enum
    public static final int TYPE_CHECKING            = 1;
    public static final int TYPE_SAVINGS             = 2;
    public static final int TYPE_CASH                = 3;
    public static final int TYPE_CREDIT_CARD         = 4;
    public static final int TYPE_LOAN                = 5;
    public static final int TYPE_CERTIFICATE_DEPOSIT = 6;
    public static final int TYPE_INVESTMENT          = 7;
    public static final int TYPE_MONEY_MARKET        = 8;
    public static final int TYPE_ASSET               = 9;
    public static final int TYPE_LIABILITY           = 10;
    public static final int TYPE_CURRENCY            = 11;
    public static final int TYPE_INCOME              = 12;
    public static final int TYPE_EXPENSE             = 13;
    public static final int TYPE_ASSET_LOAN          = 14;
    public static final int TYPE_STOCK               = 15;
    public static final int TYPE_EQUITY              = 16;
    
    public static String SEPARATOR = "::";

    // -----------------------------------------------------------------

    /**
     * @return the unique id for that account (not meaningfull to human users)
     */
    String getId();

    /**
     * @return a user-defined description to acompany the name of the account. Can
     *         encompass many lines.
     */
    String getMemo();

    /**
     * @return the account-number
     */
    String getNumber();

    /**
     * @return user-readable name of this account. Does not contain the name of
     *         parent-accounts
     */
    String getName();

    /**
     * get name including the name of the parent.accounts.
     *
     * @return e.g. "Asset::Barvermögen::Bargeld"
     */
    String getQualifiedName();

    /**
     * @return null if the account is below the root
     */
    String getParentAccountId();

    /**
     * @return the parent-account we are a child of or null if we are a top-level
     *         account
     */
    KMyMoneyAccount getParentAccount();

    /**
     * The returned collection is never null and is sorted by Account-Name.
     *
     * @return all child-accounts
     * @see #getChildren()
     */
    Collection<KMyMoneyAccount> getSubAccounts();

    /**
     * The returned collection is never null and is sorted by Account-Name.
     *
     * @return all child-accounts
     */
    Collection<KMyMoneyAccount> getChildren();

    // ----------------------------

    /**
     * @return the type-string for this account.
     * @see #TYPE_ASSET
     * @see #TYPE_INCOME
     * @see #TYPE_LIABILITY
     * @see #TYPE_PAYABLE
     * @see #TYPE_RECEIVABLE there are other types too
     */
    BigInteger getType();

    KMMSecCurrID getSecCurrID() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * The returned list ist sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     * @link GnucashTransactionSplit
     */
    List<? extends KMyMoneyTransactionSplit> getTransactionSplits();

    /**
     * The returned list ist sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     * @link GnucashTransaction
     */
    List<KMyMoneyTransaction> getTransactions();

    /**
     * @param split split to add to this transaction
     */
    void addTransactionSplit(KMyMoneyTransactionSplit split);

    /**
     * same as getBalance(new Date()).<br/>
     * ignores transactions after the current date+time<br/>
     * Be aware that the result is in the currency of this account!
     *
     * @return the balance
     */
    FixedPointNumber getBalance();

    /**
     * same as getBalanceRecursive(new Date()).<br/>
     * ignores transactions after the current date+time<br/>
     * Be aware that the result is in the currency of this account!
     *
     * @return the balance including sub-accounts
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    FixedPointNumber getBalanceRecursive() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * @return true if ${@link #hasTransactions()} is true for this or any
     *         sub-accounts
     */
    boolean hasTransactionsRecursive();

    /**
     * @return true if ${@link #getTransactionSplits()}.size()>0
     */
    boolean hasTransactions();

    /**
     * Ignores accounts for which this conversion is not possible.
     *
     * @param date     ignores transactions after the given date
     * @param currency the currency the result shall be in
     * @return Gets the balance including all sub-accounts.
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     * @see KMyMoneyAccount#getBalanceRecursive(LocalDate)
     */
    FixedPointNumber getBalanceRecursive(final LocalDate date, KMMSecCurrID secCurrID) throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * same as getBalanceRecursive(new Date()). ignores transactions after the
     * current date+time
     *
     * @return the balance including sub-accounts formatted using the current locale
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    String getBalanceRecursiveFormatted() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * same as getBalance(new Date()). ignores transactions after the current
     * date+time
     *
     * @return the balance formatted using the current locale
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    String getBalanceFormatted() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * same as getBalance(new Date()). ignores transactions after the current
     * date+time
     *
     * @param lcl the locale to use (does not affect the currency)
     * @return the balance formatted using the given locale
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    String getBalanceFormatted(Locale lcl) throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * Be aware that the result is in the currency of this account!
     *
     * @param date if non-null transactions after this date are ignored in the
     *             calculation
     * @return the balance formatted using the current locale
     */
    FixedPointNumber getBalance(LocalDate date);

    /**
     * Be aware that the result is in the currency of this account!
     *
     * @param date  if non-null transactions after this date are ignored in the
     *              calculation
     * @param after splits that are after date are added here.
     * @return the balance formatted using the current locale
     */
    FixedPointNumber getBalance(final LocalDate date, final Collection<KMyMoneyTransactionSplit> after);

    /**
     * Gets the balance including all sub-accounts.
     *
     * @param date if non-null transactions after this date are ignored in the
     *             calculation
     * @return the balance including all sub-accounts
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    FixedPointNumber getBalanceRecursive(LocalDate date) throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * Gets the last transaction-split before the given date.
     *
     * @param date if null, the last split of all time is returned
     * @return the last transaction-split before the given date
     */
    KMyMoneyTransactionSplit getLastSplitBeforeRecursive(LocalDate date);

    /**
     * Gets the balance including all sub-accounts.
     *
     * @param date if non-null transactions after this date are ignored in the
     *             calculation
     * @return the balance including all sub-accounts
     * @throws InvalidSecCurrIDException 
     * @throws InvalidSecCurrTypeException 
     */
    String getBalanceRecursiveFormatted(LocalDate date) throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    /**
     * @param lastIncludesSplit last split to be included
     * @return the balance up to and including the given split
     */
    FixedPointNumber getBalance(KMyMoneyTransactionSplit lastIncludesSplit);

    /**
     * @param id the split-id to look for
     * @return the identified split or null
     */
    KMyMoneyTransactionSplit getTransactionSplitByID(String id);

    /**
     * @param account the account to test
     * @return true if this is a child of us or any child's or us.
     */
    boolean isChildAccountRecursive(KMyMoneyAccount account);

    FixedPointNumber getBalance(LocalDate date, KMMSecCurrID secCurrID) throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

}
