package org.kmymoney.api.read;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.basetypes.complex.KMMPriceID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.read.hlp.KMyMoneyObject;

/**
 * Interface of a top-level class that gives access to a KMyMoney file
 * with all its accounts, transactions, etc.
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
	 * KMyMoney file.
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
	 * @param acctID the unique ID of the account to look for
	 * @return the account or null if it's not found
	 */
	KMyMoneyAccount getAccountByID(KMMComplAcctID acctID);

	KMyMoneyAccount getAccountByID(KMMAcctID acctID);
	
	/**
	 *
	 * @param acctID if null, gives all account that have no parent
	 * @return all accounts with that parent in no particular order
	 */
	List<KMyMoneyAccount> getAccountsByParentID(KMMComplAcctID acctID);

	/**
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountById first and only call this method if the returned account does
	 * not have the right name.
	 * 
	 * @param expr search expression
	 *
	 * @param name the <stronig>unqualified</strong> name to look for
	 * @return null if not found
	 * @see #getAccountByID(KMMComplAcctID)
	 * @see #getAccountsByParentID(KMMComplAcctID)
	 * @see #getAccountsByName(String, boolean, boolean)
	 */
	Collection<KMyMoneyAccount> getAccountsByName(String expr);

	/**
	 * @param expr search expression
	 * @param qualif Whether to search for qualified names of unqualified ones
	 * @param relaxed Whether to ignore upper/lower-case letters or not (true: case-insensitive)
	 * @return the qualified or unqualified name to look for, depending on parameter qualif.
	 */
	Collection<KMyMoneyAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

	/**
	 * @param expr search expression
	 * @param qualif
	 * @return
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
	 */
	KMyMoneyAccount getAccountByNameUniq(String expr, boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountById first and only call this method if the returned account does
	 * not have the right name.
	 *
	 * @param name the regular expression of the name to look for
	 * @return null if not found
	 * @throws org.kmymoney.api.read.TooManyEntriesFoundException
	 * @throws org.kmymoney.api.read.NoEntryFoundException
	 * @see #getAccountByID(KMMComplAcctID)
	 * @see #getAccountsByName(String)
	 */
	KMyMoneyAccount getAccountByNameEx(String name) throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 *
	 * @param acctID the id to look for
	 * @param name   the name to look for if nothing is found for the id
	 * @return null if not found
	 * @throws org.kmymoney.api.read.TooManyEntriesFoundException
	 * @throws org.kmymoney.api.read.NoEntryFoundException
	 */
	KMyMoneyAccount getAccountByIDorName(KMMComplAcctID acctID, String name)
			throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 *
	 * @param acctID the id to look for
	 * @param name   the regular expression of the name to look for if nothing is
	 *               found for the id
	 * @return null if not found
	 * @throws org.kmymoney.api.read.TooManyEntriesFoundException
	 * @throws org.kmymoney.api.read.NoEntryFoundException
	 */
	KMyMoneyAccount getAccountByIDorNameEx(KMMComplAcctID acctID, String name)
			throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * 
	 * @param type
	 * @return
	 * @throws UnknownAccountTypeException
	 */
    Collection<KMyMoneyAccount> getAccountsByType(KMyMoneyAccount.Type type) throws UnknownAccountTypeException;
    
	/**
	 * @param type
	 * @param expr search expression
	 * @param qualif
	 * @param relaxed
	 * @return
	 * @throws UnknownAccountTypeException
	 */
	Collection<KMyMoneyAccount> getAccountsByTypeAndName(KMyMoneyAccount.Type type, String expr, boolean qualif,
			boolean relaxed) throws UnknownAccountTypeException;

	/**
	 * @return all accounts
	 */
	Collection<KMyMoneyAccount> getAccounts();

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
	KMyMoneyAccount getRootAccount() throws UnknownAccountTypeException;

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     * @throws UnknownAccountTypeException 
     */
    Collection<? extends KMyMoneyAccount> getParentlessAccounts() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<KMMComplAcctID> getTopAccountIDs() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<KMyMoneyAccount> getTopAccounts() throws UnknownAccountTypeException;

	// ---------------------------------------------------------------

	/**
	 * @param trxID the unique ID of the transaction to look for
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
	 * @param spltID the unique ID of the transaction split to look for
	 * @return the transaction split or null if it's not found
	 */
	KMyMoneyTransactionSplit getTransactionSplitByID(KMMQualifSpltID spltID);

	/**
	 * @return
	 */
	Collection<KMyMoneyTransactionSplit> getTransactionSplits();

	// ---------------------------------------------------------------

	/**
	 * @param id the unique ID of the customer to look for
	 * @return the customer or null if it's not found
	 */
	KMyMoneyPayee getPayeeByID(KMMPyeID id);

	/**
	 * @param expr search expression
	 * @return
	 */
	Collection<KMyMoneyPayee> getPayeesByName(String expr);

	/**
	 * @param expr search expression
	 * @param relaxed
	 * @return
	 */
	Collection<KMyMoneyPayee> getPayeesByName(String expr, boolean relaxed);

	/**
	 * @param expr search expression
	 * @return
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
	 */
	KMyMoneyPayee getPayeesByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * @return a (possibly read-only) collection of all customers Do not modify the
	 *         returned collection!
	 */
	Collection<KMyMoneyPayee> getPayees();

	// ---------------------------------------------------------------

	/**
	 * @param id the unique ID of the customer to look for
	 * @return the customer or null if it's not found
	 */
	KMyMoneySecurity getSecurityByID(KMMSecID id);

	/**
	 * @param id
	 * @return
	 */
	KMyMoneySecurity getSecurityByID(String id);

	/**
	 * @param secID
	 * @return
	 */
	KMyMoneySecurity getSecurityByQualifID(KMMQualifSecID secID);

	/**
	 * @param qualifID
	 * @return
	 * @throws InvalidQualifSecCurrIDException
	 * @throws InvalidQualifSecCurrTypeException
	 */
	KMyMoneySecurity getSecurityByQualifID(String qualifID)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

	/**
	 * The symbol is usually the ticker, but need not necessarily be so.
	 * 
	 * @param symb
	 * @return
	 * @throws InvalidQualifSecCurrIDException
	 * @throws InvalidQualifSecCurrTypeException
	 */
	KMyMoneySecurity getSecurityBySymbol(String symb)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

	/**
	 * By ISIN/CUSIP/SEDOL/WKN...
	 * 
	 * @param code
	 * @return
	 * @throws InvalidQualifSecCurrIDException
	 * @throws InvalidQualifSecCurrTypeException
	 */
	KMyMoneySecurity getSecurityByCode(String code)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

	/**
	 * @param expr search expression
	 * @return
	 */
	List<KMyMoneySecurity> getSecuritiesByName(String expr);

	/**
	 * @param expr search expression
	 * @param relaxed
	 * @return
	 */
	List<KMyMoneySecurity> getSecuritiesByName(String expr, boolean relaxed);

	/**
	 * @param expr search expression
	 * @return
	 * @throws NoEntryFoundException
	 * @throws TooManyEntriesFoundException
	 */
	KMyMoneySecurity getSecurityByNameUniq(String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException;

	/**
	 * @return
	 */
	Collection<KMyMoneySecurity> getSecurities();

	// ---------------------------------------------------------------

	/**
	 * @param id the unique ID of the customer to look for
	 * @return the customer or null if it's not found
	 */
	KMyMoneyCurrency getCurrencyByID(String id);

	/**
	 * @param currID
	 * @return
	 */
	KMyMoneyCurrency getCurrencyByQualifID(KMMQualifCurrID currID);

	/**
	 * warning: this function has to traverse all securities. If it much faster to
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
	 * @param prcPrID id of a price pair
	 * @return the identified price pair or null
	 */
	KMyMoneyPricePair getPricePairByID(KMMPricePairID prcPrID);

	/**
	 * @return all price pairs defined in the book
	 */
	Collection<KMyMoneyPricePair> getPricePairs();

	// ---------------------------------------------------------------

	/**
	 * @param prcID id of a price
	 * @return the identified price or null
	 */
	KMyMoneyPrice getPriceByID(KMMPriceID prcID);

	/**
	 * @return all prices defined in the book
	 */
	Collection<KMyMoneyPrice> getPrices();

	/**
	 * @param secCurrID
	 * @param pCmdtySpace the name space for pCmdtyId
	 * @param pCmdtyId    the currency-name
	 * @return the latest price-quote in the KMyMoney-file in EURO
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 */
	FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

}
