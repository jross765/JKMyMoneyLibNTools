package org.kmymoney.api.write.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.basetypes.simple.KMMInstID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.CURRENCY;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.hlp.IDManager;
import org.kmymoney.api.write.KMyMoneyWritablePayee;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Implementation of GnucashWritableFile based on GnucashFileImpl.
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

	public KMyMoneyWritableFileImpl(final InputStream is) throws IOException {
		super(is);
	}

	// ---------------------------------------------------------------

	/**
	 * @return true if this file has been modified
	 */
	public boolean isModified() {
		return modified;
	}

	/**
	 * @return the time in ms (compatible with File.lastModified) of the last
	 *         write-operation
	 */
	public long getLastWriteTime() {
		return lastWriteTime;
	}

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
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
	 *
	 * @param type the type to set it for
	 */
	protected void incrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
		for ( Iterator<GncCountData> iter = l.iterator(); iter.hasNext(); ) {
			GncCountData gncCountData = (GncCountData) iter.next();

			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(gncCountData.getValue() + 1);
				setModified(true);
			}
		}
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
	 *
	 * @param type the type to set it for
	 */
	protected void decrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
		for ( Iterator<GncCountData> iter = l.iterator(); iter.hasNext(); ) {
			GncCountData gncCountData = (GncCountData) iter.next();

			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(gncCountData.getValue() - 1);
				setModified(true);
			}
		}
	}

	/**
	 * keep the count-data up to date.
	 *
	 * @param type  the type to set it for
	 * @param count the value
	 */
	protected void setCountDataFor(final String type, final int count) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
		for ( GncCountData gncCountData : l ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(count);
				setModified(true);
			}
		}
	}

	/**
	 * @param file the file to load
	 * @throws IOException on bsic io-problems such as a FileNotFoundException
	 */
	public KMyMoneyWritableFileImpl(final File file) throws IOException {
		super(file);
		setModified(false);
	}

	/**
	 * Used by GnucashTransactionImpl.createTransaction to add a new Transaction to
	 * this file.
	 *
	 * @see KMyMoneyTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
	 */
	protected void addTransaction(final KMyMoneyTransactionImpl impl) {
		incrementCountDataFor("transaction");

		getRootElement().getGncBook().getBookElements().add(impl.getJwsdpPeer());
		setModified(true);
		transactionID2transaction.put(impl.getId(), impl);

	}

	/**
	 * @see {@link KMyMoneyFileImpl#loadFile(java.io.File)}
	 */
	@Override
	protected void loadFile(final File pFile) throws IOException {
		super.loadFile(pFile);
		lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @see KMyMoneyFileImpl#setRootElement(GncV2)
	 */
	@SuppressWarnings("exports")
	@Override
	public void setRootElement(final GncV2 rootElement) {
		super.setRootElement(rootElement);
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
		if ( file.getName().endsWith(".gz") ) {
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
	 * Calculate and set the correct valued for all the following count-data.<br/>
	 * Also check the that only valid elements are in the book-element and that they
	 * have the correct order.
	 */
	private void checkAllCountData() {

		int cntAccount = 0;
		int cntTransaction = 0;
		int cntPayee = 0;
		int cntSecurity = 0;

		for ( ACCOUNT acct : getRootElement().getACCOUNTS().getACCOUNT() ) {
			cntAccount++;
		}
		
		for ( TRANSACTION acct : getRootElement().getTRANSACTIONS().getTRANSACTION() ) {
			cntTransaction++;
		}
		
		for ( PAYEE acct : getRootElement().getPAYEES().getPAYEE() ) {
			cntPayee++;
		}
		
		for ( SECURITY acct : getRootElement().getSECURITIES().getSECURITY() ) {
			cntSecurity++;
		}

		setCountDataFor("account", cntAccount);
		setCountDataFor("transaction", cntTransaction);
		setCountDataFor("gnc:GncVendor", cntPayee);
		setCountDataFor("gnc:GncVendor", cntSecurity);

		// make sure the correct sort-order of the entity-types is obeyed in writing.
		// (we do not enforce this in the xml-schema to allow for reading out of order
		// files)
		java.util.Collections.sort(bookElements, new BookElementsSorter());
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
	protected SPLIT createSplitType() {
		SPLIT retval = getObjectFactory().createSPLIT();
		// incrementCountDataFor();
		return retval;
	}

	/**
	 */
	protected PAYEE createPayeeType() {
		PAYEE retval = getObjectFactory().createPAYEE();
		incrementCountDataFor("gnc:GncVendor");
		return retval;
	}
	
	/**
	 */
	protected SECURITY createSecurityType() {
		SECURITY retval = getObjectFactory().createSECURITY();
		incrementCountDataFor("gnc:GncVendor");
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
	@Override
	protected KMyMoneyAccount createAccount(final ACCOUNT jwsdpAccount) {
		KMyMoneyAccount account = new KMyMoneyWritableAccountImpl(jwsdpAccount, this);
		return account;
	}

	/**
	 * This overridden method creates the writable version of the returned object.
	 *
	 * @param jwsdpCust the jwsdp-object the customer shall wrap
	 * @return the new customer
	 * @see KMyMoneyFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
	 */
	@Override
	protected KMyMoneyVendorImpl createPayee(final PAYEE jwsdpPye) {
		KMyMoneyVendorImpl vend = new KMyMoneyWritableVendorImpl(jwsdpPye, this);
		return vend;
	}

	/**
	 * This overridden method creates the writable version of the returned object.
	 *
	 * @see KMyMoneyFileImpl#createTransaction(GncTransaction)
	 */
	@Override
	protected KMyMoneyTransactionImpl createTransaction(final TRANSACTION jwsdpTrx) {
		KMyMoneyTransactionImpl account = new KMyMoneyWritableTransactionImpl(jwsdpTrx, this);
		return account;
	}

	/**
	 * @see KMyMoneyWritableFile#getTransactionByID(java.lang.String)
	 */
	@Override
	public KMyMoneyWritableTransaction getTransactionByID(final KMMTrxID trxID) {
		return (KMyMoneyWritableTransaction) super.getTransactionByID(trxID);
	}

	/**
	 * @param type the type to look for
	 * @return A changable version of all accounts of that type.
	 * @see {@link KMyMoneyWritableFile#getAccountsByType(String)}
	 */
	public Collection<KMyMoneyWritableAccount> getAccountsByType(final String type) {
		Collection<KMyMoneyWritableAccount> retval = new LinkedList<KMyMoneyWritableAccount>();
		for ( KMyMoneyWritableAccount acct : getWritableAccounts() ) {

			if ( acct.getType() == null ) {
				if ( type == null ) {
					retval.add(acct);
				}
			} else if ( acct.getType().equals(type) ) {
				retval.add(acct);
			}

		}
		return retval;
	}

	/**
	 * @param name the name of the account
	 * @return A changable version of the first account with that name.
	 * @see KMyMoneyFile#getAccountByName(String)
	 */
	@Override
	public KMyMoneyWritableAccount getAccountByName(final String name) {
		return (KMyMoneyWritableAccount) super.getAccountByName(name);
	}

	/**
	 * @param id the unique account-id
	 * @return A changable version of the account or null if not found.
	 * @see KMyMoneyFile#getAccountByID(String)
	 */
	@Override
	public KMyMoneyWritableAccount getAccountByID(final KMMAcctID acctid) {
		return (KMyMoneyWritableAccount) super.getAccountByID(acctid);
	}

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
	public void removeTransaction(final KMyMoneyWritableTransaction impl) {

		Collection<KMyMoneyWritableTransactionSplit> c = new LinkedList<KMyMoneyWritableTransactionSplit>();
		c.addAll(impl.getWritingSplits());
		for ( KMyMoneyWritableTransactionSplit element : c ) {
			element.remove();
		}

		getRootElement().getGncBook().getBookElements().remove(((KMyMoneyWritableTransactionImpl) impl).getJwsdpPeer());
		setModified(true);
		transactionID2transaction.remove(impl.getId());

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
		newQuote.setPriceSource("JGnucashLib");
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
		throw new IllegalStateException("No priceDB in Book in Gnucash-file");
	}

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableTransaction createWritableTransaction() {
		return new KMyMoneyWritableTransactionImpl(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public KMyMoneyWritableTransaction createWritableTransaction(final String id) {
		return new KMyMoneyWritableTransactionImpl(this);
	}

	// ----------------------------

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 * @throws
	 * @see KMyMoneyWritableFile#createWritableTransaction()
	 */
	public KMyMoneyWritableCustomerInvoice createWritableCustomerInvoice(final String number,
			final KMyMoneyCustomer cust, final KMyMoneyAccount incomeAcct, final KMyMoneyAccount receivableAcct,
			final LocalDate openedDate, final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException {
		if ( cust == null ) {
			throw new IllegalArgumentException("null customer given");
		}

		KMyMoneyWritableCustomerInvoice retval = new KMyMoneyWritableCustomerInvoiceImpl(this, number, cust,
				(KMyMoneyAccountImpl) incomeAcct, (KMyMoneyAccountImpl) receivableAcct, openedDate, postDate, dueDate);

		invoiceID2invoice.put(retval.getId(), retval);
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 *
	 * @see KMyMoneyWritableFile#createWritableTransaction()
	 */
	public KMyMoneyWritableVendorBill createWritableVendorBill(final String number, final KMyMoneyVendor vend,
			final KMyMoneyAccount expensesAcct, final KMyMoneyAccount payableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException {
		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
		}

		KMyMoneyWritableVendorBill retval = new KMyMoneyWritableVendorBillImpl(this, number, vend,
				(KMyMoneyAccountImpl) expensesAcct, (KMyMoneyAccountImpl) payableAcct, openedDate, postDate, dueDate);

		invoiceID2invoice.put(retval.getId(), retval);
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 *
	 * @see KMyMoneyWritableFile#createWritableTransaction()
	 */
	public KMyMoneyWritableJobInvoice createWritableJobInvoice(final String number, final KMyMoneyGenerJob job,
			final KMyMoneyAccount incExpAcct, final KMyMoneyAccount recvblPayblAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException {
		if ( job == null ) {
			throw new IllegalArgumentException("null job given");
		}

		KMyMoneyWritableJobInvoice retval = new KMyMoneyWritableJobInvoiceImpl(this, number, job,
				(KMyMoneyAccountImpl) incExpAcct, (KMyMoneyAccountImpl) recvblPayblAcct, openedDate, postDate, dueDate);

		invoiceID2invoice.put(retval.getId(), retval);
		return retval;
	}

	// ----------------------------

	/**
	 * @see KMyMoneyWritableFile#createWritableCustomer()
	 */
	public KMyMoneyWritableCustomer createWritableCustomer() {
		KMyMoneyWritableCustomerImpl cust = new KMyMoneyWritableCustomerImpl(this);
		super.customerID2customer.put(cust.getId(), cust);
		return cust;
	}

	/**
	 * @param impl what to remove
	 */
	public void removeCustomer(final KMyMoneyWritableCustomer impl) {
		customerID2customer.remove(impl.getId());
		getRootElement().getGncBook().getBookElements().remove(((KMyMoneyWritableCustomerImpl) impl).getJwsdpPeer());
		setModified(true);
	}

	// ----------------------------

	/**
	 * @see KMyMoneyWritableFile#createWritableCustomer()
	 */
	public KMyMoneyWritableVendor createWritableVendor() {
		KMyMoneyWritableVendorImpl vend = new KMyMoneyWritableVendorImpl(this);
		super.vendorID2vendor.put(vend.getId(), vend);
		return vend;
	}

	/**
	 * @param impl what to remove
	 */
	public void removeVendor(final KMyMoneyWritableVendor impl) {
		vendorID2vendor.remove(impl.getId());
		getRootElement().getGncBook().getBookElements().remove(((KMyMoneyWritableVendorImpl) impl).getJwsdpPeer());
		setModified(true);
	}

	// ----------------------------

	/**
	 * @see KMyMoneyWritableFile#createWritableAccount()
	 */
	public KMyMoneyWritableAccount createWritableAccount() {
		KMyMoneyWritableAccount acct = new KMyMoneyWritableAccountImpl(this);
		super.accountID2account.put(acct.getId(), acct);
		return acct;
	}

	/**
	 * @param impl what to remove
	 */
	public void removeAccount(final KMyMoneyWritableAccount impl) {
		if ( impl.getTransactionSplits().size() > 0 ) {
			throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
		}

		getRootElement().getGncBook().getBookElements().remove(((KMyMoneyWritableAccountImpl) impl).getJwsdpPeer());
		setModified(true);
		super.accountID2account.remove(impl.getId());
	}

	/**
	 * @return a read-only collection of all accounts that have no parent
	 */
	@SuppressWarnings("unchecked")
	public Collection<? extends KMyMoneyWritableAccount> getWritableRootAccounts() {
		return (Collection<? extends KMyMoneyWritableAccount>) getRootAccounts();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KMyMoneyWritableFile getWritableGnucashFile() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnucash.write.jwsdpimpl.GnucashFileImpl#getRootAccounts()
	 */
	@Override
	public Collection<? extends KMyMoneyAccount> getRootAccounts() {
		// TODO Auto-generated method stub
		Collection<? extends KMyMoneyAccount> rootAccounts = super.getRootAccounts();
		if ( rootAccounts.size() > 1 ) {
			KMyMoneyAccount root = null;
			StringBuilder roots = new StringBuilder();
			for ( KMyMoneyAccount gnucashAccount : rootAccounts ) {
				if ( gnucashAccount == null ) {
					continue;
				}
				if ( gnucashAccount.getType() != null && gnucashAccount.getType().equals(KMyMoneyAccount.TYPE_ROOT) ) {
					root = gnucashAccount;
					continue;
				}
				roots.append(gnucashAccount.getId()).append("=\"").append(gnucashAccount.getName()).append("\" ");
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

	// ---------------------------------------------------------------
	
	@Override
	public KMMInstID getNewInstitutionID() {
		// ::TOIDO
		return null;
	}

	@Override
	public KMMAcctID getNewAccountID() {
		// ::TOIDO
		return null;
	}

	@Override
	public KMMTrxID getNewTransactionID() {
		// ::TOIDO
		return null;
	}

	@Override
	public KMMSpltID getNewSplitID() {
		// ::TOIDO
		return null;
	}

	@Override
	public KMMPyeID getNewPayeeID() {
		// ::TOIDO
		return null;
	}

	@Override
	public KMMSecID getNewSecurityID() {
		// ::TOIDO
		return null;
	}

}
