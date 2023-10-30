package org.kmymoney.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.currency.ComplexPriceTable;
import org.kmymoney.numbers.FixedPointNumber;

/**
 * Interface of a top-level class<br/>
 * that gives access to a gnucash-file <br/>
 * with all it's transactions and accounts,... <br/>
 * <br/>
 */
public interface KMyMoneyFile extends KMyMoneyObject {

    /**
     *
     * @return the file on disk we are managing
     */
    File getFile();

    /**
     * The Currency-Table gets initialized with the latest prices found in the
     * gnucash-file.
     * 
     * @return Returns the currencyTable.
     */
    ComplexPriceTable getCurrencyTable();

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "ISO4217" .
     * 
     * @return the default-currencyID to use.
     */
    String getDefaultCurrencyID();

    // ---------------------------------------------------------------

    FixedPointNumber getLatestPrice(final KMMSecCurrID secCurrID) throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    // public abstract void setFile(File file);

    // public abstract void loadFile(File file) throws Exception;

    /**
     * @param id the unique id of the account to look for
     * @return the account or null if it's not found
     */
    KMyMoneyAccount getAccountByID(String id);

    /**
     * @return a read-only collection of all accounts that have no parent
     */
    Collection<? extends KMyMoneyAccount> getRootAccounts();

    /**
     * @param id the unique id of the transaction to look for
     * @return the transaction or null if it's not found
     */
    KMyMoneyTransaction getTransactionByID(String id);

    /**
     * @return a (possibly read-only) collection of all transactions Do not modify
     *         the returned collection!
     */
    Collection<? extends KMyMoneyTransaction> getTransactions();

    /**
     * @return all accounts
     */
    Collection<KMyMoneyAccount> getAccounts();

    /**
     *
     * @param id if null, gives all account that have no parent
     * @return all accounts with that parent in no particular order
     */
    Collection<KMyMoneyAccount> getAccountsByParentID(String id);

    // ---------------------------------------------------------------

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param name the UNQUaLIFIED name to look for
     * @return null if not found
     * @see #getAccountByID(String)
     */
    Collection<KMyMoneyAccount> getAccountsByName(String expr);

    Collection<KMyMoneyAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

    KMyMoneyAccount getAccountByNameUniq(String expr, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param name the regular expression of the name to look for
     * @return null if not found
     * @throws org.kmymoney.read.TooManyEntriesFoundException 
     * @throws org.kmymoney.read.NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    KMyMoneyAccount getAccountByNameEx(String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws org.kmymoney.read.TooManyEntriesFoundException 
     * @throws org.kmymoney.read.NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    KMyMoneyAccount getAccountByIDorName(String id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws org.kmymoney.read.TooManyEntriesFoundException 
     * @throws org.kmymoney.read.NoEntryFoundException 
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    KMyMoneyAccount getAccountByIDorNameEx(String id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    // ----------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyPayee getPayeeByID(String id);

    /**
     * warning: this function has to traverse all customers. If it much faster to
     * try getCustomerByID first and only call this method if the returned account
     * does not have the right name.
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getCustomerByID(String)
     */
    KMyMoneyPayee getPayeeByName(String name);

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<KMyMoneyPayee> getPayees();

    // ---------------------------------------------------------------
    // Statistics (for test purposes)

    public int getNofEntriesAccountMap();

    public int getNofEntriesTransactionMap();

    public int getNofEntriesTransactionSplitsMap();

}
