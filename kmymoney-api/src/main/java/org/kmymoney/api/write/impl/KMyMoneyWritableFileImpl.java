package org.kmymoney.api.write.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.basetypes.simple.KMMInstID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.SPLITS;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyPayeeImpl;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePayee;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.hlp.IDManager;
import org.kmymoney.api.write.impl.hlp.WritingContentHandler;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Implementation of KMyMoneyWritableFile based on GnucashFileImpl.
 * @see KMyMoneyFileImpl
 */
public class KMyMoneyWritableFileImpl extends KMyMoneyFileImpl 
                                      implements KMyMoneyWritableFile,
                                                 IDManager
{
	// ::MAGIC
	private static final int HEX = 16;
	private static final String CODEPAGE = "UTF-8";

	// ---------------------------------------------------------------

	/**
	 * true if this file has been modified.
	 */
	private boolean modified = false;

	/**
	 * @see {@link #getLastWriteTime()}
	 */
	private long lastWriteTime = 0;

	// ---------------------------------------------------------------

	/**
	 * @param file the file to load
	 * @throws IOException                   on bsic io-problems such as a
	 *                                       FileNotFoundException
	 * @throws InvalidCmdtyCurrIDException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public KMyMoneyWritableFileImpl(final File file)
			throws IOException {
		super(file);
		setModified(false);

		acctMgr = new org.kmymoney.api.write.impl.hlp.FileAccountManager(this);
		trxMgr  = new org.kmymoney.api.write.impl.hlp.FileTransactionManager(this);

		pyeMgr  = new org.kmymoney.api.write.impl.hlp.FilePayeeManager(this);

		secMgr  = new org.kmymoney.api.write.impl.hlp.FileSecurityManager(this);
		// ::TODO
		// prcMgr  = new org.kmymoney.api.write.impl.hlp.FilePriceManager(this);
	}

	public KMyMoneyWritableFileImpl(final InputStream is)
			throws IOException {
		super(is);

		acctMgr = new org.kmymoney.api.write.impl.hlp.FileAccountManager(this);
		trxMgr  = new org.kmymoney.api.write.impl.hlp.FileTransactionManager(this);

		pyeMgr  = new org.kmymoney.api.write.impl.hlp.FilePayeeManager(this);

		secMgr  = new org.kmymoney.api.write.impl.hlp.FileSecurityManager(this);
		// ::TODO
		// prcMgr  = new org.kmymoney.api.write.impl.hlp.FilePriceManager(this);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------
	
	// ::TODO

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * @param pModified true if this file has been modified false after save, load
	 *                  or undo of changes
	 */
	public void setModified(final boolean pModified) {
		// boolean old = this.modified;
		modified = pModified;
		// if (propertyChange != null)
		// propertyChange.firePropertyChange("modified", old, pModified);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isModified() {
		return modified;
	}

	// ---------------------------------------------------------------

	/**
	 * @see {@link KMyMoneyFileImpl#loadFile(java.io.File)}
	 */
	@Override
	protected void loadFile(final File pFile) throws IOException {
		super.loadFile(pFile);
		lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @see KMyMoneyWritableFile#writeFile(java.io.File)
	 */
	public void writeFile(final File file) throws IOException {

		if ( file == null ) {
			throw new IllegalArgumentException("null not allowed for field this file");
		}

		if ( file.exists() ) {
			throw new IllegalArgumentException("Given file '" + file.getAbsolutePath() + "' does exist!");
		}

		checkAllCountData();

		setFile(file);

		OutputStream out = new FileOutputStream(file);
		out = new BufferedOutputStream(out);
		if ( file.getName().endsWith(".gz") ||
             file.getName().endsWith(".kmy") ) {
			out = new GZIPOutputStream(out);
		}

		Writer writer = new NamespaceAdderWriter(new OutputStreamWriter(out, CODEPAGE));
		try {
			JAXBContext context = getJAXBContext();
			Marshaller marsh = context.createMarshaller();

			// marsh.marshal(getRootElement(), writer);
			// marsh.marshal(getRootElement(), new PrintWriter( System.out ) );
			marsh.marshal(getRootElement(), new WritingContentHandler(writer));

			setModified(false);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			writer.close();
		}

		out.close();

		lastWriteTime = Math.max(file.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @return the time in ms (compatible with File.lastModified) of the last
	 *         write-operation
	 */
	@Override
	public long getLastWriteTime() {
		return lastWriteTime;
	}

	// ---------------------------------------------------------------

	/**
	 * keep the count-data up to date.
	 *
	 * @param type  the type to set it for
	 * @param val the value
	 */
	protected void setCountDataFor(final String type, final int val) {
	
		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}
	
		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty type given");
		}

		if ( val < 0 ) {
			throw new IllegalArgumentException("val < 0 given");
		}
	
		if ( type.trim().equals("account")  ) {
			getRootElement().getACCOUNTS().setCount(BigInteger.valueOf(val));
			setModified(true);
			return;
		} else if ( type.trim().equals("transaction")  ) {
			getRootElement().getTRANSACTIONS().setCount(BigInteger.valueOf(val));
			setModified(true);
			return;
		} else if ( type.trim().equals("payee")  ) {
			getRootElement().getPAYEES().setCount(BigInteger.valueOf(val));
			setModified(true);
			return;
		} else if ( type.trim().equals("security")  ) {
			getRootElement().getSECURITIES().setCount(BigInteger.valueOf(val));
			setModified(true);
			return;
		} else if ( type.trim().equals("pricepair")  ) {
			getRootElement().getPRICES().setCount(BigInteger.valueOf(val));
			setModified(true);
			return;
		} else {
			throw new IllegalArgumentException("Unknown type '" + type + "'");
		}
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive.
	 *
	 * @param type the type to set it for
	 */
	protected void incrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty type given");
		}
		
		incrementCountDataForCore(type, 1);
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive.
	 *
	 * @param type the type to set it for
	 */
	protected void decrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty type given");
		}
		
		incrementCountDataForCore(type, -1);
	}

	private void incrementCountDataForCore(final String type, final int delta) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty type given");
		}

		int currCnt = getCountDataFor(type);
		setCountDataFor(type, currCnt + delta);
	}

	/**
	 * Calculate and set the correct values for all the following count-data.<br/>
	 * Also check the that only valid elements are in the book-element and that they
	 * have the correct order.
	 */
	private void checkAllCountData() {

		int cntAccount = 0;
		int cntTransaction = 0;
		int cntPayee = 0;
		int cntSecurity = 0;
		int cntPricePair = 0;

		for ( ACCOUNT acct : getRootElement().getACCOUNTS().getACCOUNT() ) {
			cntAccount++;
		}
		
		for ( TRANSACTION trx : getRootElement().getTRANSACTIONS().getTRANSACTION() ) {
			cntTransaction++;
		}
		
		for ( PAYEE pye : getRootElement().getPAYEES().getPAYEE() ) {
			cntPayee++;
		}
		
		for ( SECURITY sec : getRootElement().getSECURITIES().getSECURITY() ) {
			cntSecurity++;
		}

		for ( PRICEPAIR prc : getRootElement().getPRICES().getPRICEPAIR() ) {
			cntPricePair++;
		}

		setCountDataFor("account", cntAccount);
		setCountDataFor("transaction", cntTransaction);
		setCountDataFor("payee", cntPayee);
		setCountDataFor("security", cntSecurity);
		setCountDataFor("pricepair", cntPricePair);

		// make sure the correct sort-order of the entity-types is obeyed in writing.
		// (we do not enforce this in the xml-schema to allow for reading out of order
		// files)
		// java.util.Collections.sort(getRootElement(), new BookElementsSorter());
	}

	// ---------------------------------------------------------------

	/**
	 * Used by KMyMoneyTransactionImpl.createTransaction to add a new Transaction to
	 * this file.
	 *
	 * @see KMyMoneyTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
	 */
	protected void addTransaction(final KMyMoneyTransactionImpl trx) {
		incrementCountDataFor("transaction");

		getRootElement().getTRANSACTIONS().getTRANSACTION().add(trx.getJwsdpPeer());
		setModified(true);
		super.trxMgr.addTransaction(trx);
	}

	/**
	 * @see KMyMoneyFileImpl#setRootElement(GncV2)
	 */
	@SuppressWarnings("exports")
	@Override
	public void setRootElement(final KMYMONEYFILE rootElement) {
		super.setRootElement(rootElement);
	}

	/**
	 * @return the underlying JAXB-element
	 * @see KMyMoneyWritableFile#getRootElement()
	 */
	@SuppressWarnings("exports")
	@Override
	public KMYMONEYFILE getRootElement() {
		return super.getRootElement();
	}
	
	// ---------------------------------------------------------------

	/**
	 */
	protected ACCOUNT createAccountType() {
		ACCOUNT retval = getObjectFactory().createACCOUNT();
		incrementCountDataFor("account");
		return retval;
	}

	/**
	 */
	protected TRANSACTION createTransactionType() {
		TRANSACTION retval = getObjectFactory().createTRANSACTION();
		incrementCountDataFor("transaction");
		return retval;
	}

	/**
	 */
	protected SPLITS createSplitsType() {
		SPLITS retval = getObjectFactory().createSPLITS();
		// incrementCountDataFor("splits");
		return retval;
	}

	/**
	 */
	protected SPLIT createSplitType() {
		SPLIT retval = getObjectFactory().createSPLIT();
		// incrementCountDataFor("split");
		return retval;
	}

	/**
	 */
	protected PAYEE createPayeeType() {
		PAYEE retval = getObjectFactory().createPAYEE();
		incrementCountDataFor("payee");
		return retval;
	}
	
	/**
	 */
	protected SECURITY createSecurityType() {
		SECURITY retval = getObjectFactory().createSECURITY();
		incrementCountDataFor("security");
		return retval;
	}
	
	/**
	 */
	protected PRICEPAIR createPricePairType() {
		PRICEPAIR retval = getObjectFactory().createPRICEPAIR();
		incrementCountDataFor("pricepair");
		return retval;
	}
	
	/**
	 */
	protected PRICE createPriceType() {
		PRICE retval = getObjectFactory().createPRICE();
		// incrementCountDataFor("price");
		return retval;
	}
	
	// ---------------------------------------------------------------

	/**
	 * This overridden method creates the writable version of the returned object.
	 *
	 * @see KMyMoneyFileImpl#createAccount(GncAccount)
	 */
	protected KMyMoneyAccountImpl createAccount(final ACCOUNT jwsdpAcct) {
		KMyMoneyAccountImpl account = new KMyMoneyWritableAccountImpl(jwsdpAcct, this);
		return account;
	}

	/**
	 * This overridden method creates the writable version of the returned object.
	 *
	 * @see KMyMoneyFileImpl#createTransaction(GncTransaction)
	 */
	protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
		KMyMoneyTransactionImpl account = new KMyMoneyWritableTransactionImpl(jwsdpTrx, this);
		return account;
	}
	
	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyWritableFile#getWritableTransactions()
	 */
	@SuppressWarnings("unchecked")
	public Collection<? extends KMyMoneyWritableTransaction> getWritableTransactions() {
		return (Collection<? extends KMyMoneyWritableTransaction>) getTransactions();
	}

	/**
	 * @param impl what to remove
	 */
	public void removeTransaction(final KMyMoneyWritableTransaction trx) {

		Collection<KMyMoneyWritableTransactionSplit> c = new LinkedList<KMyMoneyWritableTransactionSplit>();
		c.addAll(trx.getWritableSplits());
		for ( KMyMoneyWritableTransactionSplit element : c ) {
			element.remove();
		}

		super.trxMgr.removeTransaction(trx);
		getRootElement().getTRANSACTIONS().getTRANSACTION().remove(((KMyMoneyWritableTransactionImpl) trx).getJwsdpPeer());
		setModified(true);

	}

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
	public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
			final int pCmdtyNameFraction, final String pCmdtyName) {

		if ( conversionFactor == null ) {
			throw new IllegalArgumentException("null conversionFactor given");
		}
		if ( pCmdtySpace == null ) {
			throw new IllegalArgumentException("null comodity-space given");
		}
		if ( pCmdtyId == null ) {
			throw new IllegalArgumentException("null comodity-id given");
		}
		if ( pCmdtyName == null ) {
			throw new IllegalArgumentException("null comodity-name given");
		}
		
		/*
		 * ::TODO
		if ( getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyId) == null ) {

			CURRENCY newCurrency = getObjectFactory().createGncV2GncBookGncCommodity();
			newCurrency.setCmdtyFraction(pCmdtyNameFraction);
			newCurrency.setCmdtySpace(pCmdtySpace);
			newCurrency.setCmdtyId(pCmdtyId);
			newCurrency.setCmdtyName(pCmdtyName);
			newCurrency.setVersion(Const.XML_FORMAT_VERSION);
			getRootElement().getGncBook().getBookElements().add(newCurrency);
			incrementCountDataFor("commodity");
		}
		// add price-quote
		CURRENCY currency = new GncV2.GncBook.GncPricedb.Price.PriceCommodity();
		currency.setCmdtySpace(pCmdtySpace);
		currency.setCmdtyId(pCmdtyId);

		CURRENCY baseCurrency = getObjectFactory()
				.createGncV2GncBookGncPricedbPricePriceCurrency();
		baseCurrency.setCmdtySpace(CurrencyNameSpace.NAMESPACE_CURRENCY);
		baseCurrency.setCmdtyId(getDefaultCurrencyID());

		PRICE newQuote = getObjectFactory().createGncV2GncBookGncPricedbPrice();
		newQuote.setPriceSource("JKMyMoneyLib");
		newQuote.setPriceId(getObjectFactory().createGncV2GncBookGncPricedbPricePriceId());
		newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
		newQuote.getPriceId().setValue(createGUID());
		newQuote.setPriceCommodity(currency);
		newQuote.setPriceCurrency(baseCurrency);
		newQuote.setPriceTime(getObjectFactory().createGncV2GncBookGncPricedbPricePriceTime());
		newQuote.getPriceTime().setTsDate(PRICE_QUOTE_DATE_FORMAT.format(new Date()));
		newQuote.setPriceType("last");
		newQuote.setPriceValue(conversionFactor.toKMyMoneyString());

		List<Object> bookElements = getRootElement().getBookElements();
		for ( Object element : bookElements ) {
			if ( element instanceof GncV2.GncBook.GncPricedb ) {
				GncV2.GncBook.GncPricedb prices = (GncV2.GncBook.GncPricedb) element;
				prices.getPrice().add(newQuote);
				getCurrencyTable().setConversionFactor(pCmdtySpace, pCmdtyId, conversionFactor);
				return;
			}
		}
		throw new IllegalStateException("No priceDB in Book in KMyMoney file");
		*/
	}

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableTransaction createWritableTransaction() {
		KMyMoneyWritableTransactionImpl trx = new KMyMoneyWritableTransactionImpl(this);
		super.trxMgr.addTransaction(trx);
		return trx;
	}

	// ----------------------------

	/**
	 * @param pye what to remove
	 */
	@Override
	public void removePayee(final KMyMoneyWritablePayee pye) {
		super.pyeMgr.removePayee(pye);
		getRootElement().getPAYEES().getPAYEE().remove(((KMyMoneyWritablePayeeImpl) pye).getJwsdpPeer());
		setModified(true);
	}

	// ----------------------------

	/**
	 * @see KMyMoneyWritableFile#createWritableAccount()
	 */
	public KMyMoneyWritableAccount createWritableAccount() {
		KMyMoneyWritableAccountImpl acct = new KMyMoneyWritableAccountImpl(this);
		super.acctMgr.addAccount(acct);
		return acct;
	}

	/**
	 * @param acct what to remove
	 */
	public void removeAccount(final KMyMoneyWritableAccount acct) {
		if ( acct.getTransactionSplits().size() > 0 ) {
			throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
		}

		getRootElement().getACCOUNTS().getACCOUNT().remove(((KMyMoneyWritableAccountImpl) acct).getJwsdpPeer());
		setModified(true);
		super.acctMgr.removeAccount(acct);
	}

	/**
	 * @return a read-only collection of all accounts that have no parent
	 * @throws UnknownAccountTypeException 
	 */
	@SuppressWarnings("unchecked")
	public Collection<? extends KMyMoneyWritableAccount> getWritableParentlessAccounts() throws UnknownAccountTypeException {
		return (Collection<? extends KMyMoneyWritableAccount>) getParentlessAccounts();
	}

	/**
	 * @return a read-only collection of all accounts
	 */
	public Collection<KMyMoneyWritableAccount> getWritableAccounts() {
		TreeSet<KMyMoneyWritableAccount> retval = new TreeSet<KMyMoneyWritableAccount>();
		for ( KMyMoneyAccount account : getAccounts() ) {
			retval.add((KMyMoneyWritableAccount) account);
		}
		return retval;
	}

	@Override
	public Collection<? extends KMyMoneyWritableAccount> getWritableRootAccounts() {
		List<KMyMoneyWritableAccount> result = new ArrayList<KMyMoneyWritableAccount>();
		
		KMyMoneyWritableAccount acct1 = getWritableAccountByID(KMMComplAcctID.get(KMMComplAcctID.Top.ASSET));
		result.add(acct1);
		
		KMyMoneyWritableAccount acct2 = getWritableAccountByID(KMMComplAcctID.get(KMMComplAcctID.Top.LIABILITY));
		result.add(acct2);
		
		KMyMoneyWritableAccount acct3 = getWritableAccountByID(KMMComplAcctID.get(KMMComplAcctID.Top.INCOME));
		result.add(acct3);
		
		KMyMoneyWritableAccount acct4 = getWritableAccountByID(KMMComplAcctID.get(KMMComplAcctID.Top.EXPENSE));
		result.add(acct4);
		
		KMyMoneyWritableAccount acct5 = getWritableAccountByID(KMMComplAcctID.get(KMMComplAcctID.Top.EQUITY));
		result.add(acct5);
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kmm.write.jwsdpimpl.KMyMoneyFileImpl#getRootAccounts()
	 */
	@Override
	public Collection<? extends KMyMoneyAccount> getParentlessAccounts() throws UnknownAccountTypeException {
		// TODO Auto-generated method stub
		Collection<? extends KMyMoneyAccount> rootAccounts = super.getParentlessAccounts();
		if ( rootAccounts.size() > 1 ) {
			KMyMoneyAccount root = null;
			StringBuilder roots = new StringBuilder();
			for ( KMyMoneyAccount kmmAccount : rootAccounts ) {
				if ( kmmAccount == null ) {
					continue;
				}
				if ( kmmAccount.getParentAccountID() == null ) {
					root = kmmAccount;
					continue;
				}
				roots.append(kmmAccount.getID()).append("=\"").append(kmmAccount.getName()).append("\" ");
			}
			LOGGER.warn("File has more then one root-account! Attaching excess accounts to root-account: "
					+ roots.toString());
			LinkedList<KMyMoneyAccount> rootAccounts2 = new LinkedList<KMyMoneyAccount>();
			rootAccounts2.add(root);
			for ( KMyMoneyAccount kmmAcct : rootAccounts ) {
				if ( kmmAcct == null ) {
					continue;
				}
				if ( kmmAcct == root ) {
					continue;
				}
				((KMyMoneyWritableAccount) kmmAcct).setParentAccount(root);

			}
			rootAccounts = rootAccounts2;
		}
		return rootAccounts;
	}

	@Override
	public KMyMoneyWritableFile getWritableKMyMoneyFile() {
		return this;
	}
	
	// ---------------------------------------------------------------

	@Override
	public KMyMoneyWritableAccount getWritableAccountByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<KMyMoneyWritableAccount> getWritableAccountsByType(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public KMyMoneyWritableAccount getWritableAccountByID(final KMMComplAcctID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}

		// ::TODO
//		if ( ! acctID.isSet() ) {
//			throw new IllegalArgumentException("account ID is not set");
//		}

		try {
			KMyMoneyAccount acct = super.getAccountByID(acctID);
			return new KMyMoneyWritableAccountImpl((KMyMoneyAccountImpl) acct, true);
		} catch (Exception exc) {
			LOGGER.error(
					"getWritableAccountByID: Could not instantiate writable account object from read-only account object (ID: "
							+ acctID + ")");
			throw new RuntimeException(
					"Could not instantiate writable account object from read-only account object (ID: " + acctID + ")");
		}
	}

	@Override
	public KMyMoneyWritableTransaction getWritableTransactionByID(KMMTrxID trxID) {
		if ( trxID == null ) {
			throw new IllegalArgumentException("null transaction ID given");
		}

		if ( ! trxID.isSet() ) {
			throw new IllegalArgumentException("transaction ID is not set");
		}

		KMyMoneyTransaction trx = super.getTransactionByID(trxID);
		return new KMyMoneyWritableTransactionImpl((KMyMoneyTransactionImpl) trx);
	}

	@Override
	public KMyMoneyWritableTransactionSplit getWritableTransactionSplitByID(KMMQualifSpltID spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("null transaction split ID given");
		}

		if ( ! spltID.isSet() ) {
			throw new IllegalArgumentException("transaction split ID is not set");
		}

		KMyMoneyTransactionSplit splt = super.getTransactionSplitByID(spltID);
		return new KMyMoneyWritableTransactionSplitImpl((KMyMoneyTransactionSplitImpl) splt);
	}

	@Override
	public KMyMoneyWritablePayee getWritablePayeeByID(KMMPyeID pyeID) {
		if ( pyeID == null ) {
			throw new IllegalArgumentException("null payee ID given");
		}

		if ( ! pyeID.isSet() ) {
			throw new IllegalArgumentException("payee ID is not set");
		}

		KMyMoneyPayee trx = super.getPayeeByID(pyeID);
		return new KMyMoneyWritablePayeeImpl((KMyMoneyPayeeImpl) trx);
	}

	/**
	 * This overridden method creates the writable version of the returned object.
	 *
	 * @param jwsdpCust the jwsdp-object the customer shall wrap
	 * @return the new customer
	 * @see KMyMoneyFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
	 */
	@Override
	public KMyMoneyWritablePayee createWritablePayee() {
		KMyMoneyWritablePayeeImpl pye = new KMyMoneyWritablePayeeImpl(this);
		super.pyeMgr.addPayee(pye);
		return pye;
	}

	// ---------------------------------------------------------------

	@Override
	public KMyMoneyWritableSecurity getWritableSecurityByID(KMMSecID secID) {
		if ( secID == null ) {
			throw new IllegalArgumentException("null security ID given");
		}

		if ( ! secID.isSet() ) {
			throw new IllegalArgumentException("security ID is not set");
		}

		KMyMoneySecurity sec = super.getSecurityByID(secID);
		return new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
	}

	@Override
	public KMyMoneyWritableSecurity getWritableSecurityByQualifID(KMMQualifSecID qualifID) {
		if ( qualifID == null ) {
			throw new IllegalArgumentException("null security ID given");
		}

		// ::TODO
//		if ( ! qualifID.isSet() ) {
//			throw new IllegalArgumentException("security ID is not set");
//		}

		KMyMoneySecurity sec = super.getSecurityByQualifID(qualifID);
		return new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
	}

	@Override
	public KMyMoneyWritableSecurity getWritableSecurityBySymbol(final String symb) {
		KMyMoneySecurity sec = super.getSecurityBySymbol(symb);
		return new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
	}

	@Override
	public Collection<KMyMoneyWritableSecurity> getWritableSecuritiesByName(final String expr) {
		Collection<KMyMoneyWritableSecurity> result = new ArrayList<KMyMoneyWritableSecurity>();

		for ( KMyMoneySecurity sec : super.getSecuritiesByName(expr) ) {
			KMyMoneyWritableSecurity newSec = new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
			result.add(newSec);
		}

		return result;
    }

	@Override
	public Collection<KMyMoneyWritableSecurity> getWritableSecuritiesByName(final String expr, final boolean relaxed) {
		Collection<KMyMoneyWritableSecurity> result = new ArrayList<KMyMoneyWritableSecurity>();

		for ( KMyMoneySecurity sec : super.getSecuritiesByName(expr, relaxed) ) {
			KMyMoneyWritableSecurity newSec = new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
			result.add(newSec);
		}

		return result;
	}
    
	@Override
	public KMyMoneyWritableSecurity getWritableSecurityByNameUniq(final String expr) 
			throws NoEntryFoundException, TooManyEntriesFoundException {
		KMyMoneySecurity sec = super.getSecurityByNameUniq(expr);
		return new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
	}
    
	@Override
	public Collection<KMyMoneyWritableSecurity> getWritableSecurities() {
		Collection<KMyMoneyWritableSecurity> result = new ArrayList<KMyMoneyWritableSecurity>();

		for ( KMyMoneySecurity sec : super.getSecurities() ) {
			KMyMoneyWritableSecurity newSec = new KMyMoneyWritableSecurityImpl((KMyMoneySecurityImpl) sec);
			result.add(newSec);
		}

		return result;
	}

	@Override
	public KMyMoneyWritableSecurity createWritableSecurity() {
		KMyMoneyWritableSecurityImpl sec = new KMyMoneyWritableSecurityImpl(this);
		super.secMgr.addSecurity(sec);
		return sec;
	}

	@Override
	public void removeSecurity(KMyMoneyWritableSecurity sec) {
		super.secMgr.removeSecurity(sec);
		getRootElement().getSECURITIES().getSECURITY().remove(((KMyMoneyWritableSecurityImpl) sec).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public KMyMoneyWritablePricePair getWritablePricePairByID(KMMCurrPair prcPairID) {
		if ( prcPairID == null ) {
			throw new IllegalArgumentException("null price pair ID given");
		}

		// ::TODO
//		if ( ! prcPairID.isSet() ) {
//			throw new IllegalArgumentException("price ID is not set");
//		}

		KMyMoneyPricePair prcPair = super.getPricePairByID(prcPairID);
		return new KMyMoneyWritablePricePairImpl((KMyMoneyPricePairImpl) prcPair);
	}

	@Override
	public Collection<KMyMoneyWritablePricePair> getWritablePricePairs() {
		// TODO Auto-generated method stub
		return null;
	}

	// ----------------------------

	@Override
	public KMyMoneyWritablePricePair createWritablePricePair() {
		KMyMoneyWritablePricePairImpl prc = new KMyMoneyWritablePricePairImpl(this);
		super.prcMgr.addPricePair(prc);
		return prc;
	}

	@Override
	public void removePricePair(KMyMoneyWritablePricePair prcPair) {
		// 1) remove avatar in price manager
		super.prcMgr.removePricePair(prcPair);
		
		// 2) remove price pair, if no prices left
		for ( PRICEPAIR jwsdpPrcPair : getRootElement().getPRICES().getPRICEPAIR() ) {
			if ( jwsdpPrcPair.getFrom().equals(prcPair.getFromCurrencyCode()) &&
				 jwsdpPrcPair.getTo().equals(prcPair.getToCurrencyCode()) ) {
				if ( jwsdpPrcPair.getPRICE().size() == 0 ) {
					// CAUTION concurrency ::CHECK
					getRootElement().getPRICES().getPRICEPAIR().remove(jwsdpPrcPair);
					break;
				}
			}
		}
		
		// 4) set 'modified' flag
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public KMyMoneyWritablePrice getWritablePriceByID(KMMPriceID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("null price ID given");
		}

		// ::TODO
//		if ( ! prcID.isSet() ) {
//			throw new IllegalArgumentException("price ID is not set");
//		}

		KMyMoneyPrice prc = super.getPriceByID(prcID);
		return new KMyMoneyWritablePriceImpl((KMyMoneyPriceImpl) prc);
	}

	@Override
	public Collection<KMyMoneyWritablePrice> getWritablePrices() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ----------------------------

	@Override
	public KMyMoneyWritablePrice createWritablePrice(KMyMoneyPricePairImpl prcPair) {
		KMyMoneyWritablePriceImpl prc = new KMyMoneyWritablePriceImpl(prcPair, this);
		super.prcMgr.addPrice(prc);
		return prc;
	}

	@Override
	public void removePrice(KMyMoneyWritablePrice prc) {
		// 1) remove avatar in price manager
		super.prcMgr.removePrice(prc);
		
		// 2) remove price
		KMyMoneyPricePair prcPair = prc.getParentPricePair();
		
		for ( PRICEPAIR jwsdpPrcPair : getRootElement().getPRICES().getPRICEPAIR() ) {
			if ( jwsdpPrcPair.getFrom().equals(prcPair.getFromCurrencyCode()) &&
				 jwsdpPrcPair.getTo().equals(prcPair.getToCurrencyCode()) ) {
				// CAUTION concurrency ::CHECK
				jwsdpPrcPair.getPRICE().remove(((KMyMoneyWritablePriceImpl) prc).getJwsdpPeer());
				break;
			}
		}
		
		// 3) remove price pair, if no prices left
		for ( PRICEPAIR jwsdpPrcPair : getRootElement().getPRICES().getPRICEPAIR() ) {
			if ( jwsdpPrcPair.getFrom().equals(prcPair.getFromCurrencyCode()) &&
				 jwsdpPrcPair.getTo().equals(prcPair.getToCurrencyCode()) ) {
				if ( jwsdpPrcPair.getPRICE().size() == 0 ) {
					// CAUTION concurrency ::CHECK
					getRootElement().getPRICES().getPRICEPAIR().remove(jwsdpPrcPair);
					break;
				}
			}
		}
		
		// 4) set 'modified' flag
		setModified(true);
	}

	// ---------------------------------------------------------------
	
	@Override
	public KMMInstID getNewInstitutionID() {
		// ::TODO
		return null;
	}

	@Override
	public KMMAcctID getNewAccountID() {
		int counter = 0;
		
		for ( KMyMoneyAccount trx : getAccounts() ) {
			try {
				if ( trx.getID().getType() == KMMComplAcctID.Type.STANDARD ) {
					String coreID = trx.getID().getStdID().get().substring(1);
					if ( Integer.parseInt(coreID) > counter ) {
						counter = Integer.parseInt(coreID);
					}
				}
			} catch (Exception e) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		counter++;
		
		return new KMMAcctID(counter);
	}

	@Override
	public KMMTrxID getNewTransactionID() {
		int counter = 0;
		
		for ( KMyMoneyTransaction trx : getTransactions() ) {
			try {
				String coreID = trx.getID().get().substring(1);
				if ( Integer.parseInt(coreID) > counter ) {
					counter = Integer.parseInt(coreID);
				}
			} catch (Exception e) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		counter++;
		
		return new KMMTrxID(counter);
	}

	@Override
	public KMMSpltID getNewSplitID() {
		int counter = 0;
		
		for ( KMyMoneyTransactionSplit splt : getTransactionSplits() ) {
			try {
				String coreID = splt.getID().get().substring(1);
				if ( Integer.parseInt(coreID) > counter ) {
					counter = Integer.parseInt(coreID);
				}
			} catch (Exception e) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		counter++;
		
		return new KMMSpltID(counter);
	}

	@Override
	public KMMPyeID getNewPayeeID() {
		int counter = 0;
		
		for ( KMyMoneyPayee pye : getPayees() ) {
			try {
				String coreID = pye.getID().get().substring(1);
				if ( Integer.parseInt(coreID) > counter ) {
					counter = Integer.parseInt(coreID);
				}
			} catch (Exception e) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		counter++;
		
		return new KMMPyeID(counter);
	}

	@Override
	public KMMSecID getNewSecurityID() {
		int counter = 0;
		
		for ( KMyMoneySecurity sec : getSecurities() ) {
			try {
				String coreID = sec.getID().get().substring(1);
				if ( Integer.parseInt(coreID) > counter ) {
					counter = Integer.parseInt(coreID);
				}
			} catch (Exception e) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		counter++;
		
		return new KMMSecID(counter);
	}

}
