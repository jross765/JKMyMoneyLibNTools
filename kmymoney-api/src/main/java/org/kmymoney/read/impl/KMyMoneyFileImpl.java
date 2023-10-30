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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.Const;
import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.currency.ComplexPriceTable;
import org.kmymoney.generated.ACCOUNT;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.generated.ObjectFactory;
import org.kmymoney.generated.PAYEE;
import org.kmymoney.generated.PRICE;
import org.kmymoney.generated.PRICEPAIR;
import org.kmymoney.generated.PRICES;
import org.kmymoney.generated.TRANSACTION;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyObject;
import org.kmymoney.read.KMyMoneyPayee;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.NoEntryFoundException;
import org.kmymoney.read.TooManyEntriesFoundException;
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
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final File pFile) throws IOException, InvalidSecCurrIDException, InvalidSecCurrTypeException {
	super();
	loadFile(pFile);
    }

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     * @see #loadFile(File)
     */
    public KMyMoneyFileImpl(final InputStream is) throws IOException, InvalidSecCurrIDException, InvalidSecCurrTypeException {
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
    public Collection<KMyMoneyAccount> getAccountsByParentID(final String id) {
	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	SortedSet<KMyMoneyAccount> retval = new TreeSet<KMyMoneyAccount>();

	for (Object element : accountID2account.values()) {
	    KMyMoneyAccount account = (KMyMoneyAccount) element;

	    String parent = account.getParentAccountId();
	    if (parent == null) {
		if (id == null) {
		    retval.add((KMyMoneyAccount) account);
		}
	    } else {
		if (parent.equals(id)) {
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
		    if ( acct.getQualifiedName().trim().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(acct);
		    }
		} else {
		    if ( acct.getName().trim().toLowerCase().
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
     * @see #getAccountByID(String)
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
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    public KMyMoneyAccount getAccountByIDorName(final String id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	KMyMoneyAccount retval = getAccountByID(id);
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
     * @see #getAccountByID(String)
     * @see #getAccountByName(String)
     */
    public KMyMoneyAccount getAccountByIDorNameEx(final String id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	KMyMoneyAccount retval = getAccountByID(id);
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
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the kmymoney-file in EURO
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     * @see {@link KMyMoneyFile#getLatestPrice(String, String)}
     */
    public FixedPointNumber getLatestPrice(final String secCurrIDStr) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if ( secCurrIDStr.startsWith("E0") ) { // ::MAGIC
	    return getLatestPrice(new KMMSecID(secCurrIDStr));
	} else {
	    return getLatestPrice(new KMMCurrID(secCurrIDStr));
	}
    }

    public FixedPointNumber getLatestPrice(final KMMSecCurrID secCurrID) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
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
    protected Map<String, KMyMoneyAccount> accountID2account;

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
    protected Map<String, KMyMoneyTransactionSplit> transactionSplitID2transactionSplit;

    /**
     * All payees indexed by their unique id-String.
     *
     * @see KMyMoneyPayee
     * @see KMyMoneyPayeeImpl
     */
    protected Map<String, KMyMoneyPayee> payeeID2Payee;

    /**
     * Helper to implement the {@link KMyMoneyObject}-interface without having the
     * same code twice.
     */
    private KMyMoneyObjectImpl myKMyMoneyObject;

    /**
     * @return the underlying JAXB-element
     */
    protected KMYMONEYFILE getRootElement() {
	return rootElement;
    }

    /**
     * Set the new root-element and load all accounts, transactions,... from it.
     *
     * @param pRootElement the new root-element
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     */
    protected void setRootElement(final KMYMONEYFILE pRootElement) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
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

	initPayeeMap(pRootElement);
    }

    private void initAccountMap(final KMYMONEYFILE pRootElement) {
	accountID2account = new HashMap<>();

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
	transactionSplitID2transactionSplit = new HashMap<>();

	for ( TRANSACTION jwsdpTrx : pRootElement.getTRANSACTIONS().getTRANSACTION() ) {
	    try {
		KMyMoneyTransactionImpl trx = createTransaction(jwsdpTrx);
		transactionID2transaction.put(trx.getId(), trx);
		for (KMyMoneyTransactionSplit splt : trx.getSplits()) {
		    transactionSplitID2transactionSplit.put(splt.getId(), splt);
		}
	    } catch (RuntimeException e) {
		LOGGER.error("[RuntimeException] Problem in " + getClass().getName() + ".initTransactionMap: "
			+ "ignoring illegal Transaction-Entry with id=" + jwsdpTrx.getId(), e);
	    }
	} // for

	LOGGER.debug("No. of entries in transaction map: " + transactionID2transaction.size());
    }

    private void initPayeeMap(final KMYMONEYFILE pRootElement) {
	payeeID2Payee = new HashMap<>();

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
     * @return the default-currencyID to use.
     */
    public String getDefaultCurrencyID() {
	// ::TODO
//	KMYMONEYFILE root = getRootElement();
//	if (root == null) {
//	    return "EUR";
//	}
//	for ( ACCOUNT jwsdpAccount : getRootElement().getACCOUNTS().getACCOUNT() ) {
//	    if (jwsdpAccount.getActCommodity() != null
//		    && jwsdpAccount.getActCommodity().getCmdtySpace().equals("ISO4217")) {
//		return jwsdpAccount.getActCommodity().getCmdtyId();
//	    }
//	}
	return "EUR";
    }

    /**
     * @param pRootElement the root-element of the KMyMoney-file
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     */
    private void loadPriceDatabase(final KMYMONEYFILE pRootElement) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	boolean noPriceDB = true;
	PRICES priceDB = pRootElement.getPRICES();

		// getCurrencyTable().clear();
	if ( priceDB.getPRICEPAIR().size() > 0 )
	    noPriceDB = false;

		for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
		    String fromCurr = pricePair.getFrom();
		    String toCurr = pricePair.getTo();
		    
		    // ::TODO: Try to implement Security type
		    KMMSecCurrID.Type nameSpace = null;
		    if ( fromCurr.startsWith("E0") ) // ::MAGIC
			nameSpace = KMMSecCurrID.Type.SECURITY;
		    else
			nameSpace = KMMSecCurrID.Type.CURRENCY;

		    // check if we already have a latest price for this comodity
		    // (=currency, fund, ...)
		    if ( getCurrencyTable().getConversionFactor(nameSpace, fromCurr) != null ) {
			continue;
		    }

		    String baseCurrency = getDefaultCurrencyID();
		    if (fromCurr.equals(baseCurrency)) {
			LOGGER.warn("Ignoring price-quote for " + baseCurrency + " because " + baseCurrency + " is"
				+ "our base-currency.");
			continue;
		    }

		    // get the latest price in the file and insert it into
		    // our currency table
		    FixedPointNumber factor = getLatestPrice(new KMMSecCurrID(nameSpace, fromCurr));

		    if (factor != null) {
			getCurrencyTable().setConversionFactor(nameSpace, fromCurr, factor);
		    } else {
			LOGGER.warn("The KMyMoney-file defines a factor for a commodity '" 
				+ fromCurr + "' but has no commodity for it");
		    }
		}

	if (noPriceDB) {
	    // case: no priceDB in file
	    getCurrencyTable().clear();
	}
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
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     * @see {@link KMyMoneyFile#getLatestPrice(String, String)}
     * @see #getDefaultCurrencyID()
     */
    private FixedPointNumber getLatestPrice(final KMMSecCurrID secCurrID, final int depth) throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	if (secCurrID == null) {
	    throw new IllegalArgumentException("null parameter 'secCurrID' given");
	}
	// System.err.println("depth: " + depth);

	LocalDate latestDate = null;
	FixedPointNumber latestQuote = null;
	FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
	final int maxRecursionDepth = 5;

	PRICES priceDB = getRootElement().getPRICES();
	for ( PRICEPAIR pricePair : priceDB.getPRICEPAIR() ) {
	    String fromCurr = pricePair.getFrom();
	    String toCurr = pricePair.getTo();
	    // System.err.println("pricepair: " + fromCurr + " -> " + toCurr);
		
	    if ( fromCurr == null ) {
		LOGGER.warn("KMyMoney-file contains price-quotes" + " with no from-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    if ( toCurr == null ) {
		LOGGER.warn("KMyMoney-file contains price-quotes" + " with no to-currency: '"
			+ pricePair.toString() + "'");
		continue;
	    }
		
	    for ( PRICE prc : pricePair.getPRICE() ) {
		try {
		    if (prc == null) {
			LOGGER.warn("KMyMoney-file contains null price-quotes" 
				+ " there may be a problem with JWSDP");
			continue;
		    }
		    
		    if (prc.getDate() == null) {
			LOGGER.warn("KMyMoney-file contains price-quotes" + " with no timestamp: '"
				+ pricePair.toString() + "'");
			continue;
		    }
		    
		    if (prc.getPrice() == null) {
			LOGGER.warn("KMyMoney-file contains price-quotes" + " with no price value: '"
				+ pricePair.toString() + "'");
			continue;
		    }

		    if ( ! fromCurr.equals(secCurrID.getCode()) ) {
			continue;
		    }

		    // BEGIN core
		    if ( toCurr.startsWith("E0") ) {
			if (depth > maxRecursionDepth) {
			    LOGGER.warn("ignoring price-quote that is not in an ISO4217-currency but in '" + toCurr + "'");
			    continue;
			}
			factor = getLatestPrice(new KMMSecID(toCurr), depth + 1);
		    } else {
			if ( ! toCurr.equals(getDefaultCurrencyID()) ) {
			    if (depth > maxRecursionDepth) {
				LOGGER.warn("ignoring price-quote that is not in " + getDefaultCurrencyID() + " "
					+ "but in  '" + toCurr + "'");
				continue;
			    }
			    factor = getLatestPrice(new KMMCurrID(toCurr), depth + 1);
			}
		    }
		    // END core

		    XMLGregorianCalendar dateCal = prc.getDate();
		    LocalDate date = LocalDate.of(dateCal.getYear(), dateCal.getMonth(), dateCal.getDay());

		    if (latestDate == null || latestDate.isBefore(date)) {
			latestDate = date;
			latestQuote = new FixedPointNumber(prc.getPrice());
			LOGGER.debug("getLatestPrice(pCmdtyId='" + secCurrID.toString()
				+ "') converted " + latestQuote + " <= " + prc.getPrice());
		    }

		} catch (NumberFormatException e) {
		    LOGGER.error("[NumberFormatException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pCmdtyId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (NullPointerException e) {
		    LOGGER.error("[NullPointerException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pCmdtyId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		} catch (ArithmeticException e) {
		    LOGGER.error("[ArithmeticException] Problem in " + getClass().getName()
			    + ".getLatestPrice(pCmdtyId='" + secCurrID.toString()
			    + "')! Ignoring a bad price-quote '" + pricePair + "'", e);
		}
	    } // for price
	} // for pricepair

	LOGGER.debug(getClass().getName() + ".getLatestPrice(pCmdtyId='"
		+ secCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

	if (latestQuote == null) {
	    return null;
	}

	if (factor == null) {
	    factor = new FixedPointNumber(1);
	}

	return factor.multiply(latestQuote);
    }

    // ----------------------------

    /**
     * @param jwsdpAcct the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyAccount to wrap the given jaxb-object.
     */
    protected KMyMoneyAccountImpl createAccount(final ACCOUNT jwsdpAcct) {
	KMyMoneyAccountImpl acct = new KMyMoneyAccountImpl(jwsdpAcct, this);
	return acct;
    }

    /**
     * @param jwsdpPye the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyPayee to wrap the given JAXB object.
     */
    protected KMyMoneyPayeeImpl createPayee(final PAYEE jwsdpPye) {
	KMyMoneyPayeeImpl pye = new KMyMoneyPayeeImpl(jwsdpPye, this);
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
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     * @see #setRootElement(KMYMONEYFILE)
     */
    protected void loadFile(final File pFile) throws IOException, InvalidSecCurrIDException, InvalidSecCurrTypeException {

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

    protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException, InvalidSecCurrIDException, InvalidSecCurrTypeException {
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
     * @see KMyMoneyFile#getAccountByID(java.lang.String)
     */
    public KMyMoneyAccount getAccountByID(final String id) {
	if (accountID2account == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyAccount retval = accountID2account.get(id);
	if (retval == null) {
	    System.err.println("No Account with id '" + id + "'. We know " + accountID2account.size() + " accounts.");
	}
	return retval;
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getPayeeByID(java.lang.String)
     */
    public KMyMoneyPayee getPayeeByID(final String id) {
	if (payeeID2Payee == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyPayee retval = payeeID2Payee.get(id);
	if (retval == null) {
	    LOGGER.warn("No Payee with id '" + id + "'. We know " + payeeID2Payee.size() + " payees.");
	}
	return retval;
    }

    /**
     * @see KMyMoneyFile#getPayeeByName(java.lang.String)
     */
    public KMyMoneyPayee getPayeeByName(final String name) {

	if (payeeID2Payee == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	for (KMyMoneyPayee payee : getPayees()) {
	    if (payee.getName().equals(name)) {
		return payee;
	    }
	}
	return null;
    }

    /**
     * @see KMyMoneyFile#getPayees()
     */
    public Collection<KMyMoneyPayee> getPayees() {
	return payeeID2Payee.values();
    }

    // ---------------------------------------------------------------

    

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getTransactionByID(java.lang.String)
     */
    public KMyMoneyTransaction getTransactionByID(final String id) {
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
     * @see KMyMoneyFile#getTransactionByID(java.lang.String)
     */
    public KMyMoneyTransactionSplit getTransactionSplitByID(final String id) {
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
