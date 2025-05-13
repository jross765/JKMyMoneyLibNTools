package org.kmymoney.api.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMPriceID;
import org.kmymoney.base.basetypes.complex.KMMPricePairID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.basetypes.simple.KMMInstID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of KMyMoneyFile that allows writing. <br/>
 * All the instances for accounts,... it returns can be assumed
 * to implement the respetive *Writable-interfaces.
 *
 * @see KMyMoneyFile
 * @see org.kmymoney.write.impl.KMyMoneyWritableFileImpl
 */
public interface KMyMoneyWritableFile extends KMyMoneyFile, 
                                              KMyMoneyWritableObject
                                              // HasWritableUserDefinedAttributes
{
	public enum CompressMode {
		COMPRESS,
		DO_NOT_COMPRESS,
		GUESS_FROM_FILENAME
	}
	
	// ---------------------------------------------------------------

	/**
	 * @return true if this file has been modified.
	 */
	boolean isModified();

	/**
	 * The value is guaranteed not to be bigger then the maximum of the current
	 * system-time and the modification-time in the file at the time of the last
	 * (full) read or sucessfull write.<br/ It is thus suitable to detect if the
	 * file has been modified outside of this library
	 * 
	 * @return the time in ms (compatible with File.lastModified) of the last
	 *         write-operation
	 */
	long getLastWriteTime();

	/**
	 * @param pB true if this file has been modified.
	 * @see {@link #isModified()}
	 */
	void setModified(boolean pB);

	/**
	 * Write the data to the given file. That file becomes the new file returned by
	 * {@link KMyMoneyFile#getKMyMoneyFile()}
	 * 
	 * @param file the file to write to
	 * @throws IOException kn io-poblems
	 */
	void writeFile(File file) throws IOException;

	void writeFile(File file, CompressMode compMode) throws IOException;

	/**
	 * @return the underlying JAXB-element
	 */
	@SuppressWarnings("exports")
	KMYMONEYFILE getRootElement();

	// ---------------------------------------------------------------

	/**
	 * @param id the unique id of the institution to look for
	 * @return the customer or null if it's not found
	 */
	KMyMoneyWritableInstitution getWritableInstitutionByID(KMMInstID instID);

	// ----------------------------

	/**
	 * @return a new institution with no values that is already added to this file
	 */
	KMyMoneyWritableInstitution createWritableInstitution(String name);

	/**
	 *
	 * @param inst the transaction to remove.
	 */
	void removeInstitution(KMyMoneyWritableInstitution inst);

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyFile#getAccountByName(String)
	 * @param name the name to look for
	 * @return A changable version of the account.
	 */
	Collection<KMyMoneyWritableAccount> getWritableAccountsByName(String name);

	/**
	 * @param type the type to look for
	 * @return A changable version of all accounts of that type.
	 */
	Collection<KMyMoneyWritableAccount> getWritableAccountsByType(KMyMoneyAccount.Type type);

	Collection<KMyMoneyWritableAccount> getWritableAccountsByTypeAndName(KMyMoneyAccount.Type type, String expr, 
																		 boolean qualif, boolean relaxed);
	
	/**
	 * @see KMyMoneyFile#getAccountByID(String)
	 * @param id the id of the account to fetch
	 * @return A changable version of the account or null of not found.
	 */
	KMyMoneyWritableAccount getWritableAccountByID(KMMComplAcctID acctID);

	KMyMoneyWritableAccount getWritableAccountByID(KMMAcctID acctID);

	/**
	 *
	 * @return a read-only collection of all accounts
	 */
	Collection<? extends KMyMoneyWritableAccount> getWritableAccounts();

	/**
	 *
	 * @return a read-only collection of all accounts that have no parent
	 */
	Collection<? extends KMyMoneyWritableAccount> getWritableRootAccounts();

	// ----------------------------

	/**
	 * @return a new account that is already added to this file as a top-level
	 *         account
	 */
	KMyMoneyWritableAccount createWritableAccount();

	/**
	 * @param acct the account to remove
	 */
	void removeAccount(KMyMoneyWritableAccount acct);

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyFile#getTransactionByID(String)
	 * @return A changable version of the transaction.
	 */
	KMyMoneyWritableTransaction getWritableTransactionByID(KMMTrxID trxID);
	
	/**
	 * @see KMyMoneyFile#getTransactions()
	 * @return writable versions of all transactions in the book.
	 */
	Collection<? extends KMyMoneyWritableTransaction> getWritableTransactions();

	// ----------------------------

	/**
	 * @return a new transaction with no splits that is already added to this file
	 */
	KMyMoneyWritableTransaction createWritableTransaction();

	/**
	 *
	 * @param trx the transaction to remove.
	 */
	void removeTransaction(KMyMoneyWritableTransaction trx);

	// ---------------------------------------------------------------

	/**
	 * 
	 * @param spltID
	 * @return
	 */
	KMyMoneyWritableTransactionSplit getWritableTransactionSplitByID(KMMQualifSpltID spltID);
	
	// ::TODO
	
	// ---------------------------------------------------------------

	/**
	 * @param id the unique id of the payee to look for
	 * @return the customer or null if it's not found
	 */
	KMyMoneyWritablePayee getWritablePayeeByID(KMMPyeID pyeID);

	// ----------------------------

	/**
	 * @return a new payee with no values that is already added to this file
	 */
	KMyMoneyWritablePayee createWritablePayee(String name);

	/**
	 *
	 * @param pye the transaction to remove.
	 */
	void removePayee(KMyMoneyWritablePayee pye);

	// ---------------------------------------------------------------

	KMyMoneyWritableCurrency getWritableCurrencyByID(String currCode);
	
	KMyMoneyWritableCurrency getWritableCurrencyByQualifID(KMMQualifCurrID qualifID);
	
//	List<KMyMoneyWritableCurrency> getWritableCurrencyByName(String expr);
//
//    List<KMyMoneyWritableCurrency> getWritableCurenciesByName(String expr, boolean relaxed);
//    
//    KMyMoneyWritableCurrency getWritableCurrencyByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
	Collection<KMyMoneyWritableCurrency> getWritableCurrencies();

	// ----------------------------

	/**
	 * @param currID ISO Currency code. 
	 * @param name   Currency name
	 * @return a new transaction with no splits that is already added to this file
	 */
	KMyMoneyWritableCurrency createWritableCurrency(String currID, String name);

	/**
	 * Add a new currency.<br/>
	 * If the currency already exists, add a new price-quote for it.
	 * 
	 * @param pCmdtySpace        the namespace (e.g. "GOODS" or "CURRENCY")
	 * @param pCmdtyId           the currency-name
	 * @param conversionFactor   the conversion-factor from the base-currency (EUR).
	 * @param pCmdtyNameFraction number of decimal-places after the comma
	 * @param pCmdtyName         common name of the new currency
	 */
	public void addCurrency(
			String pCmdtySpace,
			String pCmdtyId,
			FixedPointNumber conversionFactor,
			int pCmdtyNameFraction,
			String pCmdtyName);

	/**
	 *
	 * @param curr the transaction to remove.
	 */
	// void removeCurrency(KMyMoneyWritableCurrency curr);

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyFile#getTransactionByID(String)
	 * @return A changable version of the transaction.
	 */
	KMyMoneyWritableSecurity getWritableSecurityByID(KMMSecID secID);
	
	KMyMoneyWritableSecurity getWritableSecurityByQualifID(KMMQualifSecID qualifID);
	
	KMyMoneyWritableSecurity getWritableSecurityBySymbol(String symb);

	KMyMoneyWritableSecurity getWritableSecurityByCode(String code);

	List<KMyMoneyWritableSecurity> getWritableSecuritiesByName(String expr);

    List<KMyMoneyWritableSecurity> getWritableSecuritiesByName(String expr, boolean relaxed);
    
    KMyMoneyWritableSecurity getWritableSecurityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
	/**
	 * @param type the type to look for
	 * @return A changable version of all accounts of that type.
	 */
	Collection<KMyMoneyWritableSecurity> getWritableSecuritiesByType(KMMSecCurr.Type type);

	Collection<KMyMoneyWritableSecurity> getWritableSecuritiesByTypeAndName(KMMSecCurr.Type type, String expr, 
																			boolean relaxed);
	
	/**
	 * @see KMyMoneyFile#getTransactions()
	 * @return writable versions of all transactions in the book.
	 */
	Collection<KMyMoneyWritableSecurity> getWritableSecurities();

	// ----------------------------

	/**
	 * @param type  Security type
	 * @param code  Security code (<strong>not</strong> the internal technical ID,
	 *              but the business ID, such as ISIN, CUSIP, etc. 
	 *              A ticker will also work, but it is <strong>not</strong> recommended,
	 *              as tickers typically are not unique, and there is a separate field
	 *              for it. 
	 * @param name  Security name
	 * @return a new transaction with no splits that is already added to this file
	 */
	KMyMoneyWritableSecurity createWritableSecurity(KMMSecCurr.Type type, String code, String name);

	/**
	 *
	 * @param sec the transaction to remove.
	 */
	void removeSecurity(KMyMoneyWritableSecurity sec);

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyFile#getPriceByID(KMMPriceID)
	 * @return A changeable version of the transaction.
	 */
	KMyMoneyWritablePricePair getWritablePricePairByID(KMMPricePairID prcPrID);
	
	/**
	 * @see KMyMoneyFile#getPricePairs()
	 * @return writable versions of all prices in the book.
	 */
	Collection<KMyMoneyWritablePricePair> getWritablePricePairs();

	// ----------------------------

	/**
	 * @return a new price pair with no splits that is already added to this file
	 */
	KMyMoneyWritablePricePair createWritablePricePair(KMMQualifSecCurrID fromSecCurrID,
													  KMMQualifCurrID toCurrID);

	/**
	 * @return a new price pair with no splits that is already added to this file
	 */
	KMyMoneyWritablePricePair createWritablePricePair(KMMPricePairID prcPrID);

	/**
	 *
	 * @param prcPr 
	 */
	void removePricePair(KMyMoneyWritablePricePair prcPr);

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyFile#getPriceByID(KMMPriceID)
	 * @return A changeable version of the transaction.
	 */
	KMyMoneyWritablePrice getWritablePriceByID(KMMPriceID prcID);
	
	KMyMoneyWritablePrice getWritablePriceBySecIDDate(KMMSecID secID, LocalDate date);
	
	KMyMoneyWritablePrice getWritablePriceByQualifSecIDDate(KMMQualifSecID secID, LocalDate date);
	
	KMyMoneyWritablePrice getWritablePriceByCurrDate(Currency curr, LocalDate date);
	
	KMyMoneyWritablePrice getWritablePriceByQualifCurrIDDate(KMMQualifCurrID currID, LocalDate date);
	
	KMyMoneyWritablePrice getWritablePriceByQualifSecCurrIDDate(KMMQualifSecCurrID secCurrID, LocalDate date);
	
    // ---------------------------------------------------------------
	
	/**
	 * @see KMyMoneyFile#getPrices()
	 * @return writable versions of all prices in the book.
	 */
	Collection<KMyMoneyWritablePrice> getWritablePrices();

	// ----------------------------

	/**
	 * @param prcPr 
	 * @return a new price with no splits that is already added to this file
	 */
	KMyMoneyWritablePrice createWritablePrice(KMyMoneyPricePairImpl prcPr, LocalDate date);

	/**
	 *
	 * @param sec the transaction to remove.
	 */
	void removePrice(KMyMoneyWritablePrice prc);

}
