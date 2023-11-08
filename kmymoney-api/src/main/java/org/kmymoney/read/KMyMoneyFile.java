package org.kmymoney.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.currency.ComplexPriceTable;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.aux.KMMPrice;

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

    @SuppressWarnings("exports")
    KMYMONEYFILE getRootElement();

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

    // public abstract void setFile(File file);

    // public abstract void loadFile(File file) throws Exception;

    /**
     * @param id the unique id of the account to look for
     * @return the account or null if it's not found
     */
    KMyMoneyAccount getAccountById(String id);

    /**
     * @return a read-only collection of all accounts that have no parent
     */
    Collection<? extends KMyMoneyAccount> getRootAccounts();

    /**
     * @param id the unique id of the transaction to look for
     * @return the transaction or null if it's not found
     */
    KMyMoneyTransaction getTransactionById(String id);

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
     * getAccountById first and only call this method if the returned account does
     * not have the right name.
     *
     * @param name the UNQUaLIFIED name to look for
     * @return null if not found
     * @see #getAccountById(String)
     */
    Collection<KMyMoneyAccount> getAccountsByName(String expr);

    Collection<KMyMoneyAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

    KMyMoneyAccount getAccountByNameUniq(String expr, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountById first and only call this method if the returned account does
     * not have the right name.
     *
     * @param name the regular expression of the name to look for
     * @return null if not found
     * @throws org.kmymoney.read.TooManyEntriesFoundException 
     * @throws org.kmymoney.read.NoEntryFoundException 
     * @see #getAccountById(String)
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
     * @see #getAccountById(String)
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
     * @see #getAccountById(String)
     * @see #getAccountByName(String)
     */
    KMyMoneyAccount getAccountByIDorNameEx(String id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    // ----------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyCurrency getCurrencyById(String id);

    KMyMoneyCurrency getCurrencyByQualifId(KMMCurrID currID);

    /**
     * warning: this function has to traverse all securites. If it much faster to
     * try getCustomerById first and only call this method if the returned account
     * does not have the right name.
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getCustomerById(String)
     */
    // Collection<KMyMoneyCurrency> getCurrenciesByName(String name);

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<KMyMoneyCurrency> getCurrencies();

    // ----------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneySecurity getSecurityById(String id);

    KMyMoneySecurity getSecurityByQualifID(KMMSecID secID);

    KMyMoneySecurity getSecurityByQualifID(String qualifID) throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    /**
     * The symbol is usually the ticker, but need not necessarily be so.
     * 
     * @param symb
     * @return
     * @throws InvalidSecCurrIDException
     * @throws InvalidSecCurrTypeException
     */
    KMyMoneySecurity getSecurityBySymbol(String symb) throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    /**
     * By ISIN/CUSIP/SEDOL/WKN...
     * 
     * @param code
     * @return
     * @throws InvalidSecCurrIDException
     * @throws InvalidSecCurrTypeException
     */
    KMyMoneySecurity getSecurityByCode(String code) throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr);
    
    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr, final boolean relaxed);

    public KMyMoneySecurity getSecurityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    Collection<KMyMoneySecurity> getSecurities();

    // ----------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyPayee getPayeeById(String id);

    public Collection<KMyMoneyPayee> getPayeesByName(final String expr);
    
    public Collection<KMyMoneyPayee> getPayeesByName(final String expr, final boolean relaxed);

    public KMyMoneyPayee getPayeesByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<KMyMoneyPayee> getPayees();

    // ----------------------------

    /**
     * @param id id of a price
     * @return the identified price or null
     */
    public KMMPrice getPriceById(String id);

    /**
     * @return all prices defined in the book
     * @link GCshPrice
     */
    public Collection<KMMPrice> getPrices();

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the gnucash-file in EURO
     */
    public FixedPointNumber getLatestPrice(final KMMSecCurrID secCurrID) throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    // ---------------------------------------------------------------
    // Statistics (for test purposes)

    public int getNofEntriesAccountMap();

    public int getNofEntriesTransactionMap();

    public int getNofEntriesTransactionSplitsMap();

}
