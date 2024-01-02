package org.kmymoney.api.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.numbers.FixedPointNumber;

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

    /**
     * @param id the unique id of the account to look for
     * @return the account or null if it's not found
     */
    KMyMoneyAccount getAccountByID(KMMComplAcctID id);

    /**
    *
    * @param id if null, gives all account that have no parent
    * @return all accounts with that parent in no particular order
    */
   Collection<KMyMoneyAccount> getAccountsByParentID(KMMComplAcctID id);

   /**
    * warning: this function has to traverse all accounts. If it much faster to try
    * getAccountById first and only call this method if the returned account does
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
    * getAccountById first and only call this method if the returned account does
    * not have the right name.
    *
    * @param name the regular expression of the name to look for
    * @return null if not found
    * @throws org.kmymoney.api.read.TooManyEntriesFoundException 
    * @throws org.kmymoney.api.read.NoEntryFoundException 
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
    * @throws org.kmymoney.api.read.TooManyEntriesFoundException 
    * @throws org.kmymoney.api.read.NoEntryFoundException 
    * @see #getAccountByID(String)
    * @see #getAccountByName(String)
    */
   KMyMoneyAccount getAccountByIDorName(KMMComplAcctID id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

   /**
    * First try to fetch the account by id, then fall back to traversing all
    * accounts to get if by it's name.
    *
    * @param id   the id to look for
    * @param name the regular expression of the name to look for if nothing is
    *             found for the id
    * @return null if not found
    * @throws org.kmymoney.api.read.TooManyEntriesFoundException 
    * @throws org.kmymoney.api.read.NoEntryFoundException 
    * @see #getAccountByID(String)
    * @see #getAccountByName(String)
    */
   KMyMoneyAccount getAccountByIDorNameEx(KMMComplAcctID id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;
   
   Collection<KMyMoneyAccount> getAccountsByTypeAndName(KMyMoneyAccount.Type type, String expr, 
	                                                boolean qualif, boolean relaxed) throws UnknownAccountTypeException;

   /**
    * @return all accounts
    */
   Collection<KMyMoneyAccount> getAccounts();

   /**
    * @return a read-only collection of all accounts that have no parent (the
    *         result is sorted)
    */
   Collection<? extends KMyMoneyAccount> getParentlessAccounts();

   Collection<KMMComplAcctID> getTopAccountIDs();

   Collection<KMyMoneyAccount> getTopAccounts();

   // ---------------------------------------------------------------

    /**
     * @param trxID the unique id of the transaction to look for
     * @return the transaction or null if it's not found
     */
    KMyMoneyTransaction getTransactionByID(KMMTrxID trxID);

    /**
     * @return a (possibly read-only) collection of all transactions Do not modify
     *         the returned collection!
     */
    Collection<? extends KMyMoneyTransaction> getTransactions();

    // ---------------------------------------------------------------

    /**
     * @param spltID the unique id of the transaction split to look for
     * @return the transaction split or null if it's not found
     */
    KMyMoneyTransactionSplit getTransactionSplitByID(KMMQualifSpltID spltID);

    Collection<KMyMoneyTransactionSplit> getTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyPayee getPayeeByID(KMMPyeID id);

    Collection<KMyMoneyPayee> getPayeesByName(final String expr);
    
    Collection<KMyMoneyPayee> getPayeesByName(final String expr, final boolean relaxed);

    KMyMoneyPayee getPayeesByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<KMyMoneyPayee> getPayees();

    // ---------------------------------------------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneySecurity getSecurityByID(KMMSecID id);

    KMyMoneySecurity getSecurityByID(String id);

    KMyMoneySecurity getSecurityByQualifID(KMMQualifSecID secID);

    KMyMoneySecurity getSecurityByQualifID(String qualifID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    /**
     * The symbol is usually the ticker, but need not necessarily be so.
     * 
     * @param symb
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMyMoneySecurity getSecurityBySymbol(String symb) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    /**
     * By ISIN/CUSIP/SEDOL/WKN...
     * 
     * @param code
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMyMoneySecurity getSecurityByCode(String code) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    Collection<KMyMoneySecurity> getSecuritiesByName(final String expr);
    
    Collection<KMyMoneySecurity> getSecuritiesByName(final String expr, final boolean relaxed);

    KMyMoneySecurity getSecurityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    Collection<KMyMoneySecurity> getSecurities();

    // ---------------------------------------------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyCurrency getCurrencyByID(String id);

    KMyMoneyCurrency getCurrencyByQualifID(KMMQualifCurrID currID);

    /**
     * warning: this function has to traverse all securites. If it much faster to
     * try getCustomerById first and only call this method if the returned account
     * does not have the right name.
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getCustomerByID(String)
     */
    // Collection<KMyMoneyCurrency> getCurrenciesByName(String name);

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<KMyMoneyCurrency> getCurrencies();

    // ---------------------------------------------------------------

    /**
     * @param prcID id of a price
     * @return the identified price or null
     */
    KMyMoneyPrice getPriceByID(KMMPriceID prcID);

    /**
     * @return all prices defined in the book
     * @link GCshPrice
     */
    Collection<KMyMoneyPrice> getPrices();

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the gnucash-file in EURO
     */
    FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

}
