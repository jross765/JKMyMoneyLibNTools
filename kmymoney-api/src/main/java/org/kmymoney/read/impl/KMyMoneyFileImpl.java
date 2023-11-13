package org.kmymoney.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.Const;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMComplAcctID;
import org.kmymoney.basetypes.complex.KMMPriceID;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecID;
import org.kmymoney.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.currency.ComplexPriceTable;
import org.kmymoney.generated.ACCOUNT;
import org.kmymoney.generated.CURRENCY;
import org.kmymoney.generated.KEYVALUEPAIRS;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.generated.ObjectFactory;
import org.kmymoney.generated.PAIR;
import org.kmymoney.generated.PAYEE;
import org.kmymoney.generated.PRICE;
import org.kmymoney.generated.PRICEPAIR;
import org.kmymoney.generated.PRICES;
import org.kmymoney.generated.SECURITY;
import org.kmymoney.generated.TRANSACTION;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyCurrency;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyObject;
import org.kmymoney.read.KMyMoneyPayee;
import org.kmymoney.read.KMyMoneySecurity;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.NoEntryFoundException;
import org.kmymoney.read.TooManyEntriesFoundException;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.aux.KMMPricePair;
import org.kmymoney.read.impl.aux.KMMPriceImpl;
import org.kmymoney.read.impl.aux.KMMPricePairImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Implementation of KMyMoneyFile that can only
 * read but not modify KMyMoney-Files. <br/>
 * @see KMyMoneyFile
 */
public class KMyMoneyFileImpl implements KMyMoneyFile {

    protected static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyFileImpl.class);

    /**
     * my CurrencyTable.
     */
    private final ComplexPriceTable currencyTable = new ComplexPriceTable();

    private static final String PADDING_TEMPLATE = "000000";

    // ---------------------------------------------------------------

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final File pFile) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	super();
	loadFile(pFile);
    }

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final InputStream is) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	super();
	loadInputStream(is);
    }

    // ---------------------------------------------------------------

    /**
     * @return Returns the currencyTable.
     * @link #currencyTable
     */
    public ComplexPriceTable getCurrencyTable() {
	return currencyTable;
    }

    /**
     * @return a read-only collection of all accounts
     */
    public Collection<KMyMoneyAccount> getAccounts() {
	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	return Collections.unmodifiableCollection(new TreeSet<>(accountID2account.values()));
    }

    // ---------------------------------------------------------------

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     */
    public Collection<? extends KMyMoneyAccount> getRootAccounts() {
	try {
	    Collection<KMyMoneyAccount> retval = new TreeSet<KMyMoneyAccount>();

	    for (KMyMoneyAccount account : getAccounts()) {
		if (account.getParentAccountId() == null) {
		    retval.add(account);
		}

	    }

	    return retval;
	} catch (RuntimeException e) {
	    LOGGER.error("Problem getting all root-account", e);
	    throw e;
	} catch (Throwable e) {
	    LOGGER.error("SERIOUS Problem getting all root-account", e);
	    return new LinkedList<KMyMoneyAccount>();
	}
    }

    /**
     * @param id if null, gives all account that have no parent
     * @return the sorted collection of children of that account
     */
    public Collection<KMyMoneyAccount> getAccountsByParentID(final KMMComplAcctID id) {
	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	SortedSet<KMyMoneyAccount> retval = new TreeSet<KMyMoneyAccount>();

	for (Object element : accountID2account.values()) {
	    KMyMoneyAccount account = (KMyMoneyAccount) element;

	    KMMComplAcctID parentID = account.getParentAccountId();
	    if (parentID == null) {
		if (id == null) {
		    retval.add((KMyMoneyAccount) account);
		}
	    } else {
		if (parentID.equals(id)) {
		    retval.add((KMyMoneyAccount) account);
		}
	    }
	}

	return retval;
    }

    @Override
    public Collection<KMyMoneyAccount> getAccountsByName(final String name) {
	return getAccountsByName(name, true, true);
    }
    
    /**
     * @see KMyMoneyFile#getAccountsByName(java.lang.String)
     */
    @Override
    public Collection<KMyMoneyAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {

	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();
	
	for ( KMyMoneyAccount acct : accountID2account.values() ) {
	    if ( relaxed ) {
		if ( qualif ) {
		    if ( acct.getQualifiedName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(acct);
		    }
		} else {
		    if ( acct.getName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(acct);
		    }
		}
	    } else {
		if ( qualif ) {
		    if ( acct.getQualifiedName().equals(expr) ) {
			result.add(acct);
		    }
		} else {
		    if ( acct.getName().equals(expr) ) {
			result.add(acct);
		    }
		}
	    }
	}

	return result;
    }

    @Override
    public KMyMoneyAccount getAccountByNameUniq(final String name, final boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<KMyMoneyAccount> acctList = getAccountsByName(name, qualif, false);
	if ( acctList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( acctList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return acctList.iterator().next();
    }
    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param nameRegEx the regular expression of the name to look for
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountById(String)
     * @see #getAccountByName(String)
     */
    public KMyMoneyAccount getAccountByNameEx(final String nameRegEx) throws NoEntryFoundException, TooManyEntriesFoundException {

	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyAccount foundAccount = getAccountByNameUniq(nameRegEx, true);
	if (foundAccount != null) {
	    return foundAccount;
	}
	Pattern pattern = Pattern.compile(nameRegEx);

	for (KMyMoneyAccount account : accountID2account.values()) {
	    Matcher matcher = pattern.matcher(account.getName());
	    if (matcher.matches()) {
		return account;
	    }
	}

	return null;
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountById(String)
     * @see #getAccountByName(String)
     */
    public KMyMoneyAccount getAccountByIDorName(final KMMComplAcctID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	KMyMoneyAccount retval = getAccountById(id);
	if (retval == null) {
	    retval = getAccountByNameUniq(name, true);
	}

	return retval;
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountById(String)
     * @see #getAccountByName(String)
     */
    public KMyMoneyAccount getAccountByIDorNameEx(final KMMComplAcctID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	KMyMoneyAccount retval = getAccountById(id);
	if (retval == null) {
	    retval = getAccountByNameEx(name);
	}

	return retval;
    }

    // ---------------------------------------------------------------

    /**
     * @see #getKMyMoneyFile()
     */
    private File file;

    /**
     * {@inheritDoc}
     */
    public KMMPrice getPriceById(String id) {
        if (priceById == null) {
            getPrices();
        }
        
        return priceById.get(id);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<KMMPrice> getPrices() {
        if (priceById == null) {
            priceById = new HashMap<KMMPriceID, KMMPrice>();

            PRICES priceDB = getRootElement().getPRICES();
            List<PRICEPAIR> prices = priceDB.getPRICEPAIR();
            for ( PRICEPAIR jwsdpPricePair  : prices ) {
        	String fromCurr = jwsdpPricePair.getFrom();
        	String toCurr = jwsdpPricePair.getTo();
        	KMMPricePair pricePair = new KMMPricePairImpl(jwsdpPricePair, this);
        	for ( PRICE jwsdpPrice : jwsdpPricePair.getPRICE() ) {
        	    XMLGregorianCalendar cal = jwsdpPrice.getDate();
        	    LocalDate date = LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
        	    String dateStr = date.toString();
        	    KMMPriceID priceID = new KMMPriceID(fromCurr, toCurr, dateStr);
        	    KMMPriceImpl price = new KMMPriceImpl(pricePair, jwsdpPrice, this);
        	    priceById.put(priceID, price);
        	}
            }
        } 

        return priceById.values();
    }

    public FixedPointNumber getLatestPrice(final String secCurrIDStr) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if ( secCurrIDStr.startsWith("E0") ) { // ::MAGIC
	    return getLatestPrice(new KMMQualifSecID(secCurrIDStr));
	} else {
	    return getLatestPrice(new KMMQualifCurrID(secCurrIDStr));
	}
    }

    /**
     * {@inheritDoc}
     */
    public FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return getLatestPrice(secCurrID, 0);
    }

    /**
     * the top-level Element of the kmymoney-files parsed and checked for validity by
     * JAXB.
     */
    private KMYMONEYFILE rootElement;

    /**
     * All accounts indexed by their unique id-String.
     *
     * @see KMyMoneyAccount
     * @see KMyMoneyAccountImpl
     */
    protected Map<KMMComplAcctID, KMyMoneyAccount> accountID2account;

    /**
     * All transactions indexed by their unique id-String.
     *
     * @see KMyMoneyTransaction
     * @see KMyMoneyTransactionImpl
     */
    protected Map<String, KMyMoneyTransaction> transactionID2transaction;

    /**
     * All transaction-splits indexed by their unique id-String.
     *
     * @see KMyMoneyTransactionSplit
     * @see KMyMoneyTransactionSplitImpl
     */
    protected Map<KMMQualifSplitID, KMyMoneyTransactionSplit> transactionSplitID2transactionSplit;

    /**
     * All currencies indexed by their unique id-String.
     *
     * @see KMyMoneyCurrency
     * @see KMyMoneyCurrencyImpl
     */
    protected Map<String, KMyMoneyCurrency> currID2Curr;

    /**
     * All securities indexed by their unique id-String.
     *
     * @see KMyMoneySecurity
     * @see KMyMoneySecurityImpl
     */
    protected Map<String, KMyMoneySecurity> secID2Sec;
    protected Map<String, String> secSymbID2SecID;
    protected Map<String, String> secCodeID2SecID;

    /**
     * All payees indexed by their unique id-String.
     *
     * @see KMyMoneyPayee
     * @see KMyMoneyPayeeImpl
     */
    protected Map<String, KMyMoneyPayee> payeeID2Payee;

    protected Map<KMMPriceID, KMMPrice> priceById = null;

    /**
     * Helper to implement the {@link KMyMoneyObject}-interface without having the
     * same code twice.
     */
    private KMyMoneyObjectImpl myKMyMoneyObject;

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    public KMYMONEYFILE getRootElement() {
	return rootElement;
    }

    /**
     * Set the new root-element and load all accounts, transactions,... from it.
     *
     * @param pRootElement the new root-element
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     */
    protected void setRootElement(final KMYMONEYFILE pRootElement) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if (pRootElement == null) {
	    throw new IllegalArgumentException("null not allowed for field this.rootElement");
	}
	rootElement = pRootElement;

	// fill prices

	loadPriceDatabase(pRootElement);

	// fill maps
	initAccountMap(pRootElement);

	// transactions refer to invoices, therefore they must be loaded after
	// them
	initTransactionMap(pRootElement);

	initCurrencyMap(pRootElement);

	initSecurityMap(pRootElement);

	initPayeeMap(pRootElement);
    }

    private void initAccountMap(final KMYMONEYFILE pRootElement) {
	accountID2account = new HashMap<KMMComplAcctID, KMyMoneyAccount>();

	for ( ACCOUNT jwsdpAcct : pRootElement.getACCOUNTS().getACCOUNT() ) {
	    try {
		KMyMoneyAccount acct = createAccount(jwsdpAcct);
		accountID2account.put(acct.getId(), acct);
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initAccountMap: "
			+ "ignoring illegal Account-Entry with id=" + jwsdpAcct.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in account map: " + accountID2account.size());
    }

    private void initTransactionMap(final KMYMONEYFILE pRootElement) {
	transactionID2transaction = new HashMap<>();
	transactionSplitID2transactionSplit = new HashMap<KMMQualifSplitID, KMyMoneyTransactionSplit>();

	for ( TRANSACTION jwsdpTrx : pRootElement.getTRANSACTIONS().getTRANSACTION() ) {
	    try {
		KMyMoneyTransactionImpl trx = createTransaction(jwsdpTrx);
		transactionID2transaction.put(trx.getId(), trx);
		for (KMyMoneyTransactionSplit splt : trx.getSplits()) {
		    KMMQualifSplitID spltID = new KMMQualifSplitID(trx.getId(), splt.getId());
		    transactionSplitID2transactionSplit.put(spltID, splt);
		}
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initTransactionMap: "
			+ "ignoring illegal Transaction-Entry with id=" + jwsdpTrx.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in transaction map: " + transactionID2transaction.size());
    }

    private void initCurrencyMap(final KMYMONEYFILE pRootElement) {
	currID2Curr = new HashMap<String, KMyMoneyCurrency>();

	for ( CURRENCY jwsdpCurr : pRootElement.getCURRENCIES().getCURRENCY() ) {
	    try {
		KMyMoneyCurrencyImpl curr = createCurrency(jwsdpCurr);
		currID2Curr.put(jwsdpCurr.getId(), curr);
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initCurrencyMap: "
			+ "ignoring illegal Currency-Entry with id=" + jwsdpCurr.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in currency map: " + currID2Curr.size());
    }

    private void initSecurityMap(final KMYMONEYFILE pRootElement) {
	secID2Sec = new HashMap<String, KMyMoneySecurity>();
	secSymbID2SecID = new HashMap<String, String>();
	secCodeID2SecID = new HashMap<String, String>();

	for ( SECURITY jwsdpSec : pRootElement.getSECURITIES().getSECURITY() ) {
	    try {
		KMyMoneySecurityImpl sec = createSecurity(jwsdpSec);
		secID2Sec.put(jwsdpSec.getId(), sec);
		secSymbID2SecID.put(sec.getSymbol(), jwsdpSec.getId());
		secCodeID2SecID.put(sec.getCode(), jwsdpSec.getId());
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initSecurityMap: "
			+ "ignoring illegal Security-Entry with id=" + jwsdpSec.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in security map: " + secID2Sec.size());
    }

    private void initPayeeMap(final KMYMONEYFILE pRootElement) {
	payeeID2Payee = new HashMap<String, KMyMoneyPayee>();

	for ( PAYEE jwsdpPye : pRootElement.getPAYEES().getPAYEE() ) {
	    try {
		KMyMoneyPayeeImpl pye = createPayee(jwsdpPye);
		payeeID2Payee.put(pye.getId(), pye);
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initPayeeMap: "
			+ "ignoring illegal Payee-Entry with id=" + jwsdpPye.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in payee map: " + payeeID2Payee.size());
    }

    

    // ---------------------------------------------------------------

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "ISO4217" .
     *
     * @return the default-currency to use.
     */
    public String getDefaultCurrencyID() {
	KMYMONEYFILE root = getRootElement();
	if (root == null) {
	    return Const.DEFAULT_CURRENCY;
	}
	
	KEYVALUEPAIRS kvpList = root.getKEYVALUEPAIRS();
	if (kvpList == null) {
	    return Const.DEFAULT_CURRENCY;
	}
	
	for ( PAIR kvp : kvpList.getPAIR() ) {
	    if ( kvp.getKey().equals("kmm-baseCurrency") ) { // ::MAGIC
		return kvp.getValue();
	    }
	}
	
	// not found
	return Const.DEFAULT_CURRENCY;
    }

    /**
     * @param pRootElement the root-element of the KMyMoney-file
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     */
    private void loadPriceDatabase(final KMYMONEYFILE pRootElement) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	boolean noPriceDB = true;
	
	PRICES priceDB = pRootElement.getPRICES();
	if ( priceDB.getPRICEPAIR().size() > 0 )
	    noPriceDB = false;

	loadPriceDatabaseCore(priceDB);

	if ( noPriceDB ) {
	    // no price DB in file
	    getCurrencyTable().clear();
	}
    }

    private void loadPriceDatabaseCore(PRICES priceDB) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
//  	getCurrencyTable().clear();
//  	getCurrencyTable().setConversionFactor(KMMSecCurrID.Type.CURRENCY, 
//  		                               getDefaultCurrencyID(), 
//  		                               new FixedPointNumber(1));

	String baseCurrency = getDefaultCurrencyID();
	
	for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
	    String fromSecCurr = pricePair.getFrom();
	    // String toCurr      = pricePair.getTo();
	    
	    // ::TODO: Try to implement Security type
	    KMMQualifSecCurrID.Type nameSpace = null;
	    if ( fromSecCurr.startsWith("E0") ) // ::MAGIC
		nameSpace = KMMQualifSecCurrID.Type.SECURITY;
	    else
		nameSpace = KMMQualifSecCurrID.Type.CURRENCY;

	    // Check if we already have a latest price for this security
	    // (= currency, fund, ...)
	    if ( getCurrencyTable().getConversionFactor(nameSpace, fromSecCurr) != null ) {
		continue;
	    }

	    if ( fromSecCurr.equals(baseCurrency) ) {
		LOGGER.warn("Ignoring price-quote for " + baseCurrency 
		    + " because " + baseCurrency + " is our base-currency.");
		continue;
	    }

	    // get the latest price in the file and insert it into
	    // our currency table
	    FixedPointNumber factor = getLatestPrice(new KMMQualifSecCurrID(nameSpace, fromSecCurr));

	    if ( factor != null ) {
		getCurrencyTable().setConversionFactor(nameSpace, fromSecCurr, factor);
	    } else {
		LOGGER.warn("The KMyMoney file defines a factor for a security '" 
			+ fromSecCurr + "' but has no security for it");
	    }
	} // for pricePair
    }

    /**
     * @see {@link #getLatestPrice(String, String)}
     */
    protected static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @param depth       used for recursion. Allways call with '0' for aborting
     *                    recursive quotes (quotes to other then the base- currency)
     *                    we abort if the depth reached 6.
     * @return the latest price-quote in the kmymoney-file in the default-currency
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see {@link KMyMoneyFile#getLatestPrice(String, String)}
     * @see #getDefaultCurrencyID()
     */
    private FixedPointNumber getLatestPrice(final KMMQualifSecCurrID secCurrID, final int depth) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if (secCurrID == null) {
	    throw new IllegalArgumentException("null parameter 'secCurrID' given");
	}
	// System.err.println("depth: " + depth);

	LocalDate latestDate = null;
	FixedPointNumber latestQuote = null;
	FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
	final int maxRecursionDepth = 5; // ::MAGIC

	PRICES priceDB = getRootElement().getPRICES();
	for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
	    String fromSecCurr = pricePair.getFrom();
	    String toCurr      = pricePair.getTo();
	    // System.err.println("pricepair: " + fromCurr + " -> " + toCurr);
		
	    if ( fromSecCurr == null ) {
		LOGGER.warn("KMyMoney-file contains price-quotes without from-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    if ( toCurr == null ) {
		LOGGER.warn("KMyMoney file contains price-quotes without to-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    for ( PRICE prc : pricePair.getPRICE() ) {
		    if (prc == null) {
			LOGGER.warn("KMyMoney file contains null price-quotes - there may be a problem with JWSDP");
			continue;
		    }
		    
		try {
		    if (prc.getDate() == null) {
			LOGGER.warn("KMyMoney file contains price-quotes without date: '"
				+ pricePair.toString() + "'");
			continue;
		    }
		    
		    if (prc.getPrice() == null) {
			LOGGER.warn("KMyMoney file contains price-quotes without price value: '"
				+ pricePair.toString() + "'");
			continue;
		    }

		    if ( ! fromSecCurr.equals(secCurrID.getCode()) ) {
			continue;
		    }

		    // BEGIN core
		    if ( toCurr.startsWith("E0") ) {
		      // is security
			if ( depth > maxRecursionDepth ) {
			    LOGGER.warn("Ignoring price-quote that is not in an ISO4217-currency"
				    + " but in '" + toCurr + "'");
			    continue;
			}
			factor = getLatestPrice(new KMMQualifSecID(toCurr), depth + 1);
		    } else {
		      // is currency
			if ( ! toCurr.equals(getDefaultCurrencyID()) ) {
			    if ( depth > maxRecursionDepth ) {
				LOGGER.warn("Ignoring price-quote that is not in " + getDefaultCurrencyID()
					+ " but in '" + toCurr + "'");
				continue;
			    }
			    factor = getLatestPrice(new KMMQualifCurrID(toCurr), depth + 1);
			}
		    }
		    // END core

		    XMLGregorianCalendar dateCal = prc.getDate();
		    LocalDate date = LocalDate.of(dateCal.getYear(), dateCal.getMonth(), dateCal.getDay());

		    if (latestDate == null || latestDate.isBefore(date)) {
			latestDate = date;
			latestQuote = new FixedPointNumber(prc.getPrice());
			LOGGER.debug("getLatestPrice(pSecCurrId='" + secCurrID.toString()
				+ "') converted " + latestQuote + " <= " + prc.getPrice());
		    }

		} catch (NumberFormatException e) {
		    LOGGER.error("[NumberFormatException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (NullPointerException e) {
		    LOGGER.error("[NullPointerException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (ArithmeticException e) {
		    LOGGER.error("[ArithmeticException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pSecCurrId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		}
	    } // for price
	} // for pricepair

	LOGGER.debug(getClass().getName() + ".getLatestPrice(pSecCurrId='"
		+ secCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

	if (latestQuote == null) {
	    return null;
	}

	if (factor == null) {
	    factor = new FixedPointNumber(1);
	}

	return factor.multiply(latestQuote);
    }

    // ---------------------------------------------------------------

    /**
     * @param jwsdpAcct the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyAccount to wrap the given jaxb-object.
     */
    protected KMyMoneyAccountImpl createAccount(final ACCOUNT jwsdpAcct) {
	KMyMoneyAccountImpl acct = new KMyMoneyAccountImpl(jwsdpAcct, this);
	return acct;
    }

    /**
     * @param jwsdpCurr the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyCurrency to wrap the given JAXB object.
     */
    protected KMyMoneyCurrencyImpl createCurrency(final CURRENCY jwsdpCurr) {
	KMyMoneyCurrencyImpl curr = new KMyMoneyCurrencyImpl(jwsdpCurr, this);
	return curr;
    }

    /**
     * @param jwsdpSec the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneySecurity to wrap the given JAXB object.
     */
    protected KMyMoneySecurityImpl createSecurity(final SECURITY jwsdpSec) {
	KMyMoneySecurityImpl sec = new KMyMoneySecurityImpl(jwsdpSec, this);
	return sec;
    }

    /**
     * @param jwsdpPye the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyPayee to wrap the given JAXB object.
     */
    protected KMyMoneyPayeeImpl createPayee(final PAYEE jwsdpPye) {
	KMyMoneyPayeeImpl pye = new KMyMoneyPayeeImpl(jwsdpPye);
	return pye;
    }

    /**
     * @param jwsdpTrx the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyTransaction to wrap the given jaxb-object.
     */
    protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
	KMyMoneyTransactionImpl trx = new KMyMoneyTransactionImpl(jwsdpTrx, this);
	return trx;
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
	return file;
    }

    /**
     * Internal method, just sets this.file .
     *
     * @param pFile the file loaded
     */
    protected void setFile(final File pFile) {
	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}
	file = pFile;
    }

    /**
     * loads the file and calls setRootElement.
     *
     * @param pFile the file to read
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     * @see #setRootElement(KMYMONEYFILE)
     */
    protected void loadFile(final File pFile) throws IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {

	long start = System.currentTimeMillis();

	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}

	if (!pFile.exists()) {
	    throw new IllegalArgumentException("Given file '" + pFile.getAbsolutePath() + "' does not exist!");
	}

	setFile(pFile);

	InputStream in = new FileInputStream(pFile);
	if ( pFile.getName().endsWith(".gz") ||
	     pFile.getName().endsWith(".kmy") ) {
	    in = new BufferedInputStream(in);
	    in = new GZIPInputStream(in);
	} else {
	    // determine if it's gzipped by the magic bytes
	    byte[] magic = new byte[2];
	    in.read(magic);
	    in.close();

	    in = new FileInputStream(pFile);
	    in = new BufferedInputStream(in);
	    if (magic[0] == 31 && magic[1] == -117) {
		in = new GZIPInputStream(in);
	    }
	}

	loadInputStream(in);

	long end = System.currentTimeMillis();
	LOGGER.info("KMyMoneyFileImpl.loadFile took " + (end - start) + " ms (total) ");

    }

    protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException, InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	long start = System.currentTimeMillis();

	NamespaceRemovererReader reader = new NamespaceRemovererReader(new InputStreamReader(in, "utf-8"));
	try {

	    JAXBContext myContext = getJAXBContext();
	    if ( myContext == null ) {
		// ::TODO: make it fatal
		LOGGER.error("loadInputStream: JAXB context cannot be found/generated");
		throw new IOException("JAXB context cannot be found/generated");
	    }
	    Unmarshaller unmarshaller = myContext.createUnmarshaller();

	    KMYMONEYFILE obj = (KMYMONEYFILE) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
	    long start2 = System.currentTimeMillis();
	    setRootElement(obj);
	    long end = System.currentTimeMillis();
	    LOGGER.info("KMyMoneyFileImpl.loadFileInputStream took " + (end - start) + " ms (total) " + (start2 - start)
		    + " ms (jaxb-loading)" + (end - start2) + " ms (building facades)");

	} catch (JAXBException e) {
	    LOGGER.error(e.getMessage(), e);
	    throw new IllegalStateException(e);
	} finally {
	    reader.close();
	}
    }

    /**
     * @see #getObjectFactory()
     */
    private volatile ObjectFactory myJAXBFactory;

    /**
     * @return the jaxb object-factory used to create new peer-objects to extend
     *         this
     */
    @SuppressWarnings("exports")
    public ObjectFactory getObjectFactory() {
	if (myJAXBFactory == null) {
	    myJAXBFactory = new ObjectFactory();
	}
	return myJAXBFactory;
    }

    /**
     * @see #getJAXBContext()
     */
    private volatile JAXBContext myJAXBContext;

    /**
     * @return the JAXB-context
     */
    protected JAXBContext getJAXBContext() {
	if (myJAXBContext == null) {
	    try {
		myJAXBContext = JAXBContext.newInstance("org.kmymoney.generated", this.getClass().getClassLoader());
	    } catch (JAXBException e) {
		LOGGER.error(e.getMessage(), e);
	    }
	}
	return myJAXBContext;
    }

    /**
     * @return the number of transactions
     */
    protected BigInteger getTransactionCount() {
	return getRootElement().getTRANSACTIONS().getCount();
    }

    /**
     * @see KMyMoneyFile#getAccountById(java.lang.String)
     */
    public KMyMoneyAccount getAccountById(final KMMComplAcctID id) {
	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyAccount retval = accountID2account.get(id);
	if (retval == null) {
	    System.err.println("No Account with ID '" + id + "'. We know " + accountID2account.size() + " accounts.");
	}
	
	return retval;
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneyCurrency getCurrencyById(String id) {
	if (currID2Curr == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyCurrency retval = currID2Curr.get(id);
	if (retval == null) {
	    LOGGER.warn("No Currency with ID '" + id + "'. We know " + currID2Curr.size() + " currencies.");
	}
	
	return retval;
    }

    @Override
    public KMyMoneyCurrency getCurrencyByQualifId(KMMQualifCurrID currID) {
	return getCurrencyById(currID.getCode());
    }

    @Override
    public Collection<KMyMoneyCurrency> getCurrencies() {
	return currID2Curr.values();
    }

    // ---------------------------------------------------------------

    @Override
    public KMyMoneySecurity getSecurityById(final String id) {
	if (secID2Sec == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneySecurity retval = secID2Sec.get(id);
	if (retval == null) {
	    LOGGER.warn("No Security with ID '" + id + "'. We know " + secID2Sec.size() + " securities.");
	}
	
	return retval;
    }

    @Override
    public KMyMoneySecurity getSecurityByQualifID(final KMMQualifSecID secID) {
	return getSecurityById(secID.getCode());
    }

    @Override
    public KMyMoneySecurity getSecurityByQualifID(final String qualifID) throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	if (qualifID == null) {
	    throw new IllegalStateException("null string given");
	}

	if (qualifID.trim().equals("")) {
	    throw new IllegalStateException("Search string is empty");
	}

	KMMQualifSecID secID = KMMQualifSecID.parse(qualifID);
	return getSecurityByQualifID(secID);
    }

    @Override
    public KMyMoneySecurity getSecurityBySymbol(final String symb) {
	if ( secID2Sec == null ) {
	    throw new IllegalStateException("no root-element loaded");
	}

	if ( secSymbID2SecID.size() != secID2Sec.size() ) {
	    // ::CHECK
	    // CAUTION: Don't throw an exception, at least not in all cases,
	    // because this is not necessarily an error: Only if the KMyMoney
	    // file does not contain quotes for foreign currencies (i.e. currency-
	    // commodities but only security-commodities is this an error.
	    // throw new IllegalStateException("Sizes of root elements are not equal");
	    LOGGER.debug("getSecurityBySymbol: Sizes of root elements are not equal.");
	}
	
	String qualifIDStr = secSymbID2SecID.get(symb);
	if (qualifIDStr == null) {
	    LOGGER.warn("No Security with symbol '" + symb + "'. We know " + secSymbID2SecID.size() + " securities in map 2.");
	}
	
	KMyMoneySecurity retval = secID2Sec.get(qualifIDStr);
	if (retval == null) {
	    LOGGER.warn("No Security with qualified ID '" + qualifIDStr + "'. We know " + secID2Sec.size() + " securities in map 1.");
	}
	
	return retval;
    }

    @Override
    public KMyMoneySecurity getSecurityByCode(final String code) {
	if ( secID2Sec == null ) {
	    throw new IllegalStateException("no root-element loaded");
	}

	if ( secCodeID2SecID.size() != secID2Sec.size() ) {
	    // ::CHECK
	    // CAUTION: Don't throw an exception, at least not in all cases,
	    // because this is not necessarily an error: Only if the KMyMoney
	    // file does not contain quotes for foreign currencies (i.e. currency-
	    // commodities but only security-commodities is this an error.
	    // throw new IllegalStateException("Sizes of root elements are not equal");
	    LOGGER.debug("getSecurityBySymbol: Sizes of root elements are not equal.");
	}
	
	String qualifIDStr = secCodeID2SecID.get(code);
	if (qualifIDStr == null) {
	    LOGGER.warn("No Security with symbol '" + code + "'. We know " + secCodeID2SecID.size() + " securities in map 2.");
	}
	
	KMyMoneySecurity retval = secID2Sec.get(qualifIDStr);
	if (retval == null) {
	    LOGGER.warn("No Security with qualified ID '" + qualifIDStr + "'. We know " + secID2Sec.size() + " securities in map 1.");
	}
	
	return retval;
    }

    @Override
    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr) {
	return getSecuritiesByName(expr, true);
    }

    @Override
    public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr, final boolean relaxed) {
	if (secID2Sec == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	Collection<KMyMoneySecurity> result = new ArrayList<KMyMoneySecurity>();

	for ( KMyMoneySecurity sec : getSecurities() ) {
	    if ( sec.getName() != null ) // yes, that can actually happen! 
	    {
		if ( relaxed ) {
		    if ( sec.getName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(sec);
		    }
		} else {
		    if ( sec.getName().equals(expr) ) {
			result.add(sec);
		    }
		}
	    }
	}
	
	return result;
    }

    @Override
    public KMyMoneySecurity getSecurityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<KMyMoneySecurity> cmdtyList = getSecuritiesByName(expr, false);
	if ( cmdtyList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( cmdtyList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return cmdtyList.iterator().next();
    }
    
    @Override
    public Collection<KMyMoneySecurity> getSecurities() {
	return secID2Sec.values();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getPayeeByID(java.lang.String)
     */
    @Override
    public KMyMoneyPayee getPayeeById(final String id) {
	if (payeeID2Payee == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyPayee retval = payeeID2Payee.get(id);
	if (retval == null) {
	    LOGGER.warn("No Payee with ID '" + id + "'. We know " + payeeID2Payee.size() + " payees.");
	}
	
	return retval;
    }

    @Override
    public Collection<KMyMoneyPayee> getPayeesByName(String expr) {
	return getPayeesByName(expr, true);
    }

    @Override
    public Collection<KMyMoneyPayee> getPayeesByName(String expr, boolean relaxed) {
	if (payeeID2Payee == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	Collection<KMyMoneyPayee> result = new ArrayList<KMyMoneyPayee>();

	for ( KMyMoneyPayee pye : getPayees() ) {
	    if ( pye.getName() != null ) 
	    {
		if ( relaxed ) {
		    if ( pye.getName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(pye);
		    }
		} else {
		    if ( pye.getName().equals(expr) ) {
			result.add(pye);
		    }
		}
	    }
	}
	
	return result;
    }

    @Override
    public KMyMoneyPayee getPayeesByNameUniq(String expr)
	    throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<KMyMoneyPayee> cmdtyList = getPayeesByName(expr, false);
	if ( cmdtyList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( cmdtyList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return cmdtyList.iterator().next();
    }

    /**
     * @see KMyMoneyFile#getPayees()
     */
    public Collection<KMyMoneyPayee> getPayees() {
	return payeeID2Payee.values();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    public KMyMoneyTransaction getTransactionById(final String id) {
	if (transactionID2transaction == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyTransaction retval = transactionID2transaction.get(id);
	if (retval == null) {
	    LOGGER.warn("No Transaction with id '" + id + "'. We know " + transactionID2transaction.size()
		    + " transactions.");
	}
	return retval;
    }

    /**
     * @see KMyMoneyFile#getTransactionById(java.lang.String)
     */
    public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSplitID id) {
	if (transactionSplitID2transactionSplit == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyTransactionSplit retval = transactionSplitID2transactionSplit.get(id);
	if (retval == null) {
	    LOGGER.warn("No Transaction-Split with id '" + id + "'. We know "
		    + transactionSplitID2transactionSplit.size() + " transactions.");
	}
	return retval;
    }

    /**
     * @see KMyMoneyFile#getTransactions()
     */
    public Collection<? extends KMyMoneyTransaction> getTransactions() {
	if (transactionID2transaction == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(transactionID2transaction.values());
    }

    /**
     * replaces ':' in tag-names and attribute-names by '_' .
     */
    public static class NamespaceRemovererReader extends Reader {

	/**
	 * How much we have reat.
	 */
	private long position = 0;

	/**
	 * @return How much we have reat.
	 */
	public long getPosition() {
	    return position;
	}

	/**
	 * @param pInput what to read from.
	 */
	public NamespaceRemovererReader(final Reader pInput) {
	    super();
	    input = pInput;
	}

	/**
	 * @return What to read from.
	 */
	public Reader getInput() {
	    return input;
	}

	/**
	 * @param newInput What to read from.
	 */
	public void setInput(final Reader newInput) {
	    if (newInput == null) {
		throw new IllegalArgumentException("null not allowed for field this.input");
	    }

	    input = newInput;
	}

	/**
	 * What to read from.
	 */
	private Reader input;

	/**
	 * true if we are in a quotation and thus shall not remove any namespaces.
	 */
	private boolean isInQuotation = false;

	/**
	 * true if we are in a quotation and thus shall remove any namespaces.
	 */
	private boolean isInTag = false;

	/**
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
	    input.close();
	}

	/**
	 * For debugging.
	 */
	public char[] debugLastTeat = new char[255];

	/**
	 * For debugging.
	 */
	public int debugLastReatLength = -1;

	/**
	 * Log the last chunk of bytes reat for debugging-purposes.
	 *
	 * @param cbuf the data
	 * @param off  where to start in cbuf
	 * @param reat how much
	 */
	private void logReatBytes(final char[] cbuf, final int off, final int reat) {
	    debugLastReatLength = Math.min(debugLastTeat.length, reat);
	    try {
		System.arraycopy(cbuf, off, debugLastTeat, 0, debugLastTeat.length);
	    } catch (Exception e) {
		e.printStackTrace();
		LOGGER.debug("debugLastReatLength=" + debugLastReatLength + "\n" + "off=" + off + "\n" + "reat=" + reat
			+ "\n" + "cbuf.length=" + cbuf.length + "\n" + "debugLastTeat.length=" + debugLastTeat.length
			+ "\n");
	    }
	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {

	    int reat = input.read(cbuf, off, len);

	    logReatBytes(cbuf, off, reat);

	    for (int i = off; i < off + reat; i++) {
		position++;

		if (isInTag && (cbuf[i] == '"' || cbuf[i] == '\'')) {
		    toggleIsInQuotation();
		} else if (cbuf[i] == '<' && !isInQuotation) {
		    isInTag = true;
		} else if (cbuf[i] == '>' && !isInQuotation) {
		    isInTag = false;
		} else if (cbuf[i] == ':' && isInTag && !isInQuotation) {
		    cbuf[i] = '_';
		}

	    }

	    return reat;
	}

	/**
	 *
	 */
	private void toggleIsInQuotation() {
	    if (isInQuotation) {
		isInQuotation = false;
	    } else {
		isInQuotation = true;
	    }
	}
    }

    /**
     * replaces &#164; by the euro-sign .
     */
    public static class EuroConverterReader extends Reader {

	/**
	 * This is "&#164;".length .
	 */
	private static final int REPLACESTRINGLENGTH = 5;

	/**
	 * @param pInput Where to read from.
	 */
	public EuroConverterReader(final Reader pInput) {
	    super();
	    input = pInput;
	}

	/**
	 * @return Where to read from.
	 */
	public Reader getInput() {
	    return input;
	}

	/**
	 * @param newInput Where to read from.
	 */
	public void setInput(Reader newInput) {
	    if (newInput == null) {
		throw new IllegalArgumentException("null not allowed for field this.input");
	    }

	    input = newInput;
	}

	/**
	 * Where to read from.
	 */
	private Reader input;

	/**
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
	    input.close();

	}

	/**
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {

	    int reat = input.read(cbuf, off, len);

	    // this does not work if the euro-sign is wrapped around the
	    // edge of 2 read-call buffers

	    int state = 0;

	    for (int i = off; i < off + reat; i++) {

		switch (state) {

		case 0: {
		    if (cbuf[i] == '&') {
			state++;
		    }
		    break;
		}

		case 1: {
		    if (cbuf[i] == '#') {
			state++;
		    } else {
			state = 0;
		    }
		    break;
		}

		case 2: {
		    if (cbuf[i] == '1') {
			state++;
		    } else {
			state = 0;
		    }
		    break;
		}

		case REPLACESTRINGLENGTH - 2: {
		    if (cbuf[i] == '6') {
			state++;
		    } else {
			state = 0;
		    }
		    break;
		}

		case REPLACESTRINGLENGTH - 1: {
		    if (cbuf[i] == '4') {
			state++;
		    } else {
			state = 0;
		    }
		    break;
		}
		case REPLACESTRINGLENGTH: {
		    if (cbuf[i] == ';') {
			// found it!!!
			cbuf[i - REPLACESTRINGLENGTH] = '�';
			if (i != reat - 1) {
			    System.arraycopy(cbuf, (i + 1), cbuf, (i - (REPLACESTRINGLENGTH - 1)), (reat - i - 1));
			}
			int reat2 = input.read(cbuf, reat - REPLACESTRINGLENGTH, REPLACESTRINGLENGTH);
			if (reat2 != REPLACESTRINGLENGTH) {
			    reat -= (REPLACESTRINGLENGTH - reat2);
			}
			i -= (REPLACESTRINGLENGTH - 1);
			state = 0;
		    } else {
			state = 0;
		    }
		    break;
		}

		default:
		}

	    }
	    return reat;
	}

	;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KMyMoneyFile getKMyMoneyFile() {
	return this;
    }

    // ---------------------------------------------------------------
    // Statistics (for test purposes)

    public int getNofEntriesAccountMap() {
	return accountID2account.size();
    }

    public int getNofEntriesTransactionMap() {
	return transactionID2transaction.size();
    }

    public int getNofEntriesTransactionSplitsMap() {
	return transactionSplitID2transactionSplit.size();
    }

    public int getNofEntriesPayeeMap() {
	return payeeID2Payee.size();
    }

}
