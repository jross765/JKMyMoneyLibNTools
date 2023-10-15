package org.kmymoney.write.impl;

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

import org.gnucash.generated.GncAccount;
import org.gnucash.generated.GncBudget;
import org.gnucash.generated.GncCountData;
import org.gnucash.generated.GncTransaction;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.Slot;
import org.kmymoney.Const;
import org.kmymoney.currency.CurrencyNameSpace;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyCustomer;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyGenerInvoice;
import org.kmymoney.read.KMyMoneyGenerInvoiceEntry;
import org.kmymoney.read.KMyMoneyGenerJob;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.KMyMoneyVendor;
import org.kmymoney.read.aux.KMMTaxTable;
import org.kmymoney.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.read.impl.KMyMoneyCustomerImpl;
import org.kmymoney.read.impl.KMyMoneyFileImpl;
import org.kmymoney.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.read.impl.KMyMoneyVendorImpl;
import org.kmymoney.read.impl.spec.KMyMoneyCustomerJobImpl;
import org.kmymoney.read.spec.WrongInvoiceTypeException;
import org.kmymoney.write.KMyMoneyWritableAccount;
import org.kmymoney.write.KMyMoneyWritableCustomer;
import org.kmymoney.write.KMyMoneyWritableFile;
import org.kmymoney.write.KMyMoneyWritableGenerInvoice;
import org.kmymoney.write.KMyMoneyWritableGenerJob;
import org.kmymoney.write.KMyMoneyWritableTransaction;
import org.kmymoney.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.write.KMyMoneyWritableVendor;
import org.kmymoney.write.impl.spec.KMyMoneyWritableCustomerInvoiceImpl;
import org.kmymoney.write.impl.spec.KMyMoneyWritableCustomerJobImpl;
import org.kmymoney.write.impl.spec.KMyMoneyWritableJobInvoiceImpl;
import org.kmymoney.write.impl.spec.KMyMoneyWritableVendorBillImpl;
import org.kmymoney.write.impl.spec.KMyMoneyWritableVendorJobImpl;
import org.kmymoney.write.spec.KMyMoneyWritableCustomerInvoice;
import org.kmymoney.write.spec.KMyMoneyWritableCustomerJob;
import org.kmymoney.write.spec.KMyMoneyWritableJobInvoice;
import org.kmymoney.write.spec.KMyMoneyWritableVendorBill;
import org.kmymoney.write.spec.KMyMoneyWritableVendorJob;
import org.kmymoneyread.impl.aux.KMMTaxTableImpl;
import org.kmymoneyread.impl.aux.WrongOwnerTypeException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Implementation of GnucashWritableFile based on GnucashFileImpl.
 * @see KMyMoneyFileImpl
 */
public class KMyMoneyWritableFileImpl extends KMyMoneyFileImpl 
                                     implements KMyMoneyWritableFile 
{
    // ::MAGIC
    private static final int    HEX      = 16;
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
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type the type to set it for
     */
    protected void incrementCountDataFor(final String type) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
		gncCountData.setValue(gncCountData.getValue() + 1);
		setModified(true);
	    }
	}
    }

    /**
     * Keep the count-data up to date. The count-data is re-calculated on the fly
     * before writing but we like to keep our internal model up-to-date just to be
     * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type the type to set it for
     */
    protected void decrementCountDataFor(final String type) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
		gncCountData.setValue(gncCountData.getValue() - 1);
		setModified(true);
	    }
	}
    }

    /**
     * keep the count-data up to date.
     * <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type  the type to set it for
     * @param count the value
     */
    protected void setCountDataFor(final String type, final int count) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (GncCountData gncCountData : l) {
	    if (type.equals(gncCountData.getCdType())) {
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
     * @return all TaxTables defined in the book
     * @see {@link KMMTaxTable}
     */
    @Override
    public Collection<KMMTaxTable> getTaxTables() {
	if (taxTablesById == null) {

	    taxTablesById = new HashMap<String, KMMTaxTable>();
	    List<Object> bookElements = this.getRootElement().getGncBook().getBookElements();
	    for (Object bookElement : bookElements) {
		if (bookElement instanceof GncV2.GncBook.GncGncTaxTable) {
		    GncV2.GncBook.GncGncTaxTable jwsdpPeer = (GncV2.GncBook.GncGncTaxTable) bookElement;
		    KMMTaxTableImpl gnucashTaxTable = new KMMTaxTableImpl(jwsdpPeer, this);
		    taxTablesById.put(gnucashTaxTable.getId(), gnucashTaxTable);
		}
	    }
	}

	return taxTablesById.values();
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

	if (file == null) {
	    throw new IllegalArgumentException("null not allowed for field this file");
	}

	if (file.exists()) {
	    throw new IllegalArgumentException("Given file '" + file.getAbsolutePath() + "' does exist!");
	}

	checkAllCountData();

	setFile(file);

	OutputStream out = new FileOutputStream(file);
	out = new BufferedOutputStream(out);
	if (file.getName().endsWith(".gz")) {
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
	}
	finally {
	    writer.close();
	}
	
	out.close();
	
	lastWriteTime = Math.max(file.lastModified(), System.currentTimeMillis());
    }

    /**
     * Calculate and set the correct valued for all the following count-data.<br/>
     * Also check the that only valid elements are in the book-element and that they
     * have the correct order.
     * <p>
     * <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     */
    private void checkAllCountData() {

	int cntCommodity = 0;
	int cntAccount = 0;
	int cntTransaction = 0;
	int cntCustomer = 0;
	int cntVendor = 0;
	int cntJob = 0;
	int cntTaxTable = 0;
	int cntInvoice = 0;
	int cntIncEntry = 0;
	
	/**
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link GncTemplateTransactions} {@link GncGncInvoice} {@link GncGncEntry}
	 * {@link GncGncJob} {@link GncGncTaxTable} {@link GncCommodity}
	 * {@link GncGncCustomer} {@link GncSchedxaction} {@link GncBudget}
	 * {@link GncAccount} {@link GncPricedb} {@link GncTransaction}
	 */
	List<Object> bookElements = getRootElement().getGncBook().getBookElements();
	for (Object element : bookElements) {
	    if (element instanceof GncV2.GncBook.GncCommodity) {
		cntCommodity++;
	    } else if (element instanceof GncAccount) {
		cntAccount++;
	    } else if (element instanceof GncTransaction) {
		cntTransaction++;
	    } else if (element instanceof GncV2.GncBook.GncGncCustomer) {
		cntCustomer++;
	    } else if (element instanceof GncV2.GncBook.GncGncVendor) {
		cntVendor++;
	    } else if (element instanceof GncV2.GncBook.GncGncJob) {
		cntJob++;
	    } else if (element instanceof GncV2.GncBook.GncGncTaxTable) {
		cntTaxTable++;
	    } else if (element instanceof GncV2.GncBook.GncGncInvoice) {
		cntInvoice++;
	    } else if (element instanceof GncV2.GncBook.GncGncEntry) {
		cntIncEntry++;
	    } else if (element instanceof GncV2.GncBook.GncTemplateTransactions) {
		// ::TODO
	    } else if (element instanceof GncV2.GncBook.GncSchedxaction) {
		// ::TODO
	    } else if (element instanceof GncBudget) {
		// ::TODO
	    } else if (element instanceof GncV2.GncBook.GncPricedb) {
		// ::TODO
	    } else if (element instanceof GncV2.GncBook.GncGncEmployee) {
		// ::TODO
	    } else if (element instanceof GncV2.GncBook.GncGncBillTerm) {
		// ::TODO
	    } else {
		throw new IllegalStateException("Unecpected element in GNC:Book found! <" + element.toString() + ">");
	    }
	}

	setCountDataFor("commodity", cntCommodity);
	setCountDataFor("account", cntAccount);
	setCountDataFor("transaction", cntTransaction);
	setCountDataFor("gnc:GncCustomer", cntCustomer);
	setCountDataFor("gnc:GncVendor", cntVendor);
	setCountDataFor("gnc:GncJob", cntJob);
	setCountDataFor("gnc:GncTaxTable", cntTaxTable);
	setCountDataFor("gnc:GncInvoice", cntInvoice);
	setCountDataFor("gnc:GncEntry", cntIncEntry);
	
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
    public GncV2 getRootElement() {
	return super.getRootElement();
    }

    /**
     * create a GUID for a new element. (guids are globally unique and not tied to a
     * specific kind of entity)
     * 
     * ::TODO: Change implementation: use Apache commons UUID class.
     *
     * @return the new gnucash-guid
     */
    public String createGUID() {

	int len = "74e492edf60d6a28b6c1d01cc410c058".length();

	StringBuffer sb = new StringBuffer(Long.toHexString(System.currentTimeMillis()));

	while (sb.length() < len) {
	    sb.append(Integer.toHexString((int) (Math.random() * HEX)).charAt(0));
	}

	return sb.toString();
    }

    /**
     */
    protected GncTransaction createGncTransaction() {
	GncTransaction retval = getObjectFactory().createGncTransaction();
	incrementCountDataFor("transaction");
	return retval;
    }

    /**
     */
    protected GncTransaction.TrnSplits.TrnSplit createGncTransactionTypeTrnSplitsTypeTrnSplitType() {
	GncTransaction.TrnSplits.TrnSplit retval = getObjectFactory().createGncTransactionTrnSplitsTrnSplit();
	// incrementCountDataFor();
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncInvoice createGncGncInvoiceType() {
	GncV2.GncBook.GncGncInvoice retval = getObjectFactory().createGncV2GncBookGncGncInvoice();
	incrementCountDataFor("gnc:GncInvoice");
	return retval;
    }

    /**
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncEntry createGncGncEntryType() {
	GncV2.GncBook.GncGncEntry retval = getObjectFactory().createGncV2GncBookGncGncEntry();
	incrementCountDataFor("gnc:GncEntry");
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncCustomer createGncGncCustomerType() {
	GncV2.GncBook.GncGncCustomer retval = getObjectFactory().createGncV2GncBookGncGncCustomer();
	incrementCountDataFor("gnc:GncCustomer");
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncVendor createGncGncVendorType() {
	GncV2.GncBook.GncGncVendor retval = getObjectFactory().createGncV2GncBookGncGncVendor();
	incrementCountDataFor("gnc:GncVendor");
	return retval;
    }

    /**
     * @return the jaxb-job
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncJob createGncGncJobType() {
	GncV2.GncBook.GncGncJob retval = getObjectFactory().createGncV2GncBookGncGncJob();
	incrementCountDataFor("gnc:GncJob");
	return retval;
    }

    /**
     * @see KMyMoneyFile#getCustomerByID(java.lang.String)
     */
    @Override
    public KMyMoneyWritableCustomer getCustomerByID(final String arg0) {
	return (KMyMoneyWritableCustomer) super.getCustomerByID(arg0);
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see KMyMoneyFileImpl#createAccount(GncAccount)
     */
    @Override
    protected KMyMoneyAccount createAccount(final GncAccount jwsdpAccount) {
	KMyMoneyAccount account = new KMyMoneyWritableAccountImpl(jwsdpAccount, this);
	return account;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see KMyMoneyFileImpl#createGenerInvoice(GncV2.GncBook.GncGncInvoice)
     */
    @Override
    protected KMyMoneyGenerInvoice createGenerInvoice(final GncV2.GncBook.GncGncInvoice jwsdpInvoice) {
	KMyMoneyGenerInvoice invoice = new KMyMoneyWritableGenerInvoiceImpl(jwsdpInvoice, this);
	return invoice;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @param jwsdpInvcEntr the xml-object to represent in the entry.
     * @return a new invoice-entry, already registred with this file.
     * @see KMyMoneyFileImpl#createGenerInvoiceEntry(GncV2.GncBook.GncGncEntry)
     */
    @Override
    protected KMyMoneyGenerInvoiceEntry createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	KMyMoneyGenerInvoiceEntry entry = new KMyMoneyWritableGenerInvoiceEntryImpl(jwsdpInvcEntr, this);
	return entry;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see KMyMoneyFileImpl#createGenerJob(GncV2.GncBook.GncGncJob)
     */
    @Override
    protected KMyMoneyCustomerJobImpl createGenerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	KMyMoneyCustomerJobImpl job = new KMyMoneyWritableCustomerJobImpl(jwsdpJob, this);
	return job;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see KMyMoneyFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    @Override
    protected KMyMoneyCustomerImpl createCustomer(final GncV2.GncBook.GncGncCustomer jwsdpCust) {
	KMyMoneyCustomerImpl cust = new KMyMoneyWritableCustomerImpl(jwsdpCust, this);
	return cust;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see KMyMoneyFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    @Override
    protected KMyMoneyVendorImpl createVendor(final GncV2.GncBook.GncGncVendor jwsdpVend) {
	KMyMoneyVendorImpl vend = new KMyMoneyWritableVendorImpl(jwsdpVend, this);
	return vend;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see KMyMoneyFileImpl#createTransaction(GncTransaction)
     */
    @Override
    protected KMyMoneyTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
	KMyMoneyTransactionImpl account = new KMyMoneyWritableTransactionImpl(jwsdpTrx, this);
	return account;
    }

    /**
     * @see KMyMoneyWritableFile#getTransactionByID(java.lang.String)
     */
    @Override
    public KMyMoneyWritableTransaction getTransactionByID(final String id) {
	return (KMyMoneyWritableTransaction) super.getTransactionByID(id);
    }

    /**
     * @param jobID the id of the job to fetch
     * @return A changable version of the job or null of not found.
     * @see KMyMoneyFile#getGenerJobByID(String)
     * @see KMyMoneyWritableFile#getGenerJobByID(String)
     */
    @Override
    public KMyMoneyWritableGenerJob getGenerJobByID(final String jobID) {
	return (KMyMoneyWritableGenerJob) super.getGenerJobByID(jobID);
    }

    /**
     * @see KMyMoneyWritableFile#getWritableJobs()
     */
    public Collection<KMyMoneyWritableGenerJob> getWritableJobs() {

	Collection<KMyMoneyGenerJob> jobs = getGenerJobs();
	if (jobs == null) {
	    throw new IllegalStateException("getJobs() returned null");
	}
	Collection<KMyMoneyWritableGenerJob> retval = new ArrayList<KMyMoneyWritableGenerJob>(jobs.size());
	for (KMyMoneyGenerJob job : jobs) {
	    retval.add((KMyMoneyWritableGenerJob) job);
	}
	return retval;
    }

    /**
     * @param id the unique invoice-id
     * @return A changable version of the Invoice or null if not found.
     * @see KMyMoneyFile#getGenerInvoiceByID(String)
     */
    @Override
    public KMyMoneyWritableGenerInvoice getGenerInvoiceByID(final String id) {
	return (KMyMoneyWritableGenerInvoice) super.getGenerInvoiceByID(id);
    }

    /**
     * @param type the type to look for
     * @return A changable version of all accounts of that type.
     * @see {@link KMyMoneyWritableFile#getAccountsByType(String)}
     */
    public Collection<KMyMoneyWritableAccount> getAccountsByType(final String type) {
	Collection<KMyMoneyWritableAccount> retval = new LinkedList<KMyMoneyWritableAccount>();
	for (KMyMoneyWritableAccount acct : getWritableAccounts()) {

	    if (acct.getType() == null) {
		if (type == null) {
		    retval.add(acct);
		}
	    } else if (acct.getType().equals(type)) {
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
    public KMyMoneyWritableAccount getAccountByID(final String id) {
	return (KMyMoneyWritableAccount) super.getAccountByID(id);
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
	for (KMyMoneyWritableTransactionSplit element : c) {
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

	if (conversionFactor == null) {
	    throw new IllegalArgumentException("null conversionFactor given");
	}
	if (pCmdtySpace == null) {
	    throw new IllegalArgumentException("null comodity-space given");
	}
	if (pCmdtyId == null) {
	    throw new IllegalArgumentException("null comodity-id given");
	}
	if (pCmdtyName == null) {
	    throw new IllegalArgumentException("null comodity-name given");
	}
	if (getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyId) == null) {

	    GncV2.GncBook.GncCommodity newCurrency = getObjectFactory().createGncV2GncBookGncCommodity();
	    newCurrency.setCmdtyFraction(pCmdtyNameFraction);
	    newCurrency.setCmdtySpace(pCmdtySpace);
	    newCurrency.setCmdtyId(pCmdtyId);
	    newCurrency.setCmdtyName(pCmdtyName);
	    newCurrency.setVersion(Const.XML_FORMAT_VERSION);
	    getRootElement().getGncBook().getBookElements().add(newCurrency);
	    incrementCountDataFor("commodity");
	}
	// add price-quote
	GncV2.GncBook.GncPricedb.Price.PriceCommodity currency = new GncV2.GncBook.GncPricedb.Price.PriceCommodity();
	currency.setCmdtySpace(pCmdtySpace);
	currency.setCmdtyId(pCmdtyId);

	GncV2.GncBook.GncPricedb.Price.PriceCurrency baseCurrency = getObjectFactory()
		.createGncV2GncBookGncPricedbPricePriceCurrency();
	baseCurrency.setCmdtySpace(CurrencyNameSpace.NAMESPACE_CURRENCY);
	baseCurrency.setCmdtyId(getDefaultCurrencyID());

	GncV2.GncBook.GncPricedb.Price newQuote = getObjectFactory().createGncV2GncBookGncPricedbPrice();
	newQuote.setPriceSource("JGnucashLib");
	newQuote.setPriceId(getObjectFactory().createGncV2GncBookGncPricedbPricePriceId());
	newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
	newQuote.getPriceId().setValue(createGUID());
	newQuote.setPriceCommodity(currency);
	newQuote.setPriceCurrency(baseCurrency);
	newQuote.setPriceTime(getObjectFactory().createGncV2GncBookGncPricedbPricePriceTime());
	newQuote.getPriceTime().setTsDate(PRICE_QUOTE_DATE_FORMAT.format(new Date()));
	newQuote.setPriceType("last");
	newQuote.setPriceValue(conversionFactor.toGnucashString());

	List<Object> bookElements = getRootElement().getGncBook().getBookElements();
	for (Object element : bookElements) {
	    if (element instanceof GncV2.GncBook.GncPricedb) {
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
    public KMyMoneyWritableCustomerInvoice createWritableCustomerInvoice(
	    final String number, 
	    final KMyMoneyCustomer cust,
	    final KMyMoneyAccount incomeAcct,
	    final KMyMoneyAccount receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	KMyMoneyWritableCustomerInvoice retval = 
		new KMyMoneyWritableCustomerInvoiceImpl(
			this, 
			number, cust,
			(KMyMoneyAccountImpl) incomeAcct, 
			(KMyMoneyAccountImpl) receivableAcct, 
			openedDate, postDate, dueDate);

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
    public KMyMoneyWritableVendorBill createWritableVendorBill(
	    final String number, 
	    final KMyMoneyVendor vend,
	    final KMyMoneyAccount expensesAcct,
	    final KMyMoneyAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	KMyMoneyWritableVendorBill retval = 
		new KMyMoneyWritableVendorBillImpl(
			this, 
			number, vend,
			(KMyMoneyAccountImpl) expensesAcct, 
			(KMyMoneyAccountImpl) payableAcct, 
			openedDate, postDate, dueDate);

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
    public KMyMoneyWritableJobInvoice createWritableJobInvoice(
	    final String number, 
	    final KMyMoneyGenerJob job,
	    final KMyMoneyAccount incExpAcct,
	    final KMyMoneyAccount recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException {
	if (job == null) {
	    throw new IllegalArgumentException("null job given");
	}

	KMyMoneyWritableJobInvoice retval = 
		new KMyMoneyWritableJobInvoiceImpl(
			this, 
			number, job,
			(KMyMoneyAccountImpl) incExpAcct, 
			(KMyMoneyAccountImpl) recvblPayblAcct, 
			openedDate, postDate, dueDate);

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
     * @see KMyMoneyWritableFile#createWritableCustomerJob(KMyMoneyCustomer)
     */
    public KMyMoneyWritableCustomerJob createWritableCustomerJob(
	    final KMyMoneyCustomer cust, 
	    final String number,
	    final String name) {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	KMyMoneyWritableCustomerJobImpl job = new KMyMoneyWritableCustomerJobImpl(this, cust, number, name);
	super.jobID2job.put(job.getId(), job);
	return job;
    }

    /**
     * @see KMyMoneyWritableFile#createWritableCustomerJob(KMyMoneyCustomer)
     */
    public KMyMoneyWritableVendorJob createWritableVendorJob(
	    final KMyMoneyVendor vend, 
	    final String number,
	    final String name) {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	KMyMoneyWritableVendorJobImpl job = new KMyMoneyWritableVendorJobImpl(this, vend, number, name);
	super.jobID2job.put(job.getId(), job);
	return job;
    }

    /**
     * @param impl what to remove
     */
    public void removeJob(final KMyMoneyWritableGenerJob impl) {
	jobID2job.remove(impl.getId());
	getRootElement().getGncBook().getBookElements().remove(((KMyMoneyWritableCustomerJobImpl) impl).getJwsdpPeer());
	setModified(true);
    }

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
	if (impl.getTransactionSplits().size() > 0) {
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
	for (KMyMoneyAccount account : getAccounts()) {
	    retval.add((KMyMoneyWritableAccount) account);
	}
	return retval;
    }

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    public KMyMoneyWritableGenerJob getJobByNumber(final String jnr) {
	if (jobID2job == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	for (KMyMoneyGenerJob gnucashJob : jobID2job.values()) {
	    KMyMoneyWritableGenerJob job = (KMyMoneyWritableGenerJob) gnucashJob;
	    if (job.getNumber().equals(jnr)) {
		return job;
	    }
	}
	return null;

    }

    /**
     * @param impl an invoice to remove
     */
    public void removeInvoice(final KMyMoneyWritableGenerInvoiceImpl impl) {

	if (impl.getPayingTransactions().size() > 0) {
	    throw new IllegalArgumentException("cannot remove this invoice! It has payments!");
	}

	KMyMoneyTransaction postTransaction = impl.getPostTransaction();
	if (postTransaction != null) {
	    ((KMyMoneyWritableTransaction) postTransaction).remove();
	}

	invoiceID2invoice.remove(impl.getId());
	getRootElement().getGncBook().getBookElements().remove(impl.getJwsdpPeer());
	this.decrementCountDataFor("gnc:GncInvoice");
	setModified(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KMyMoneyWritableFile getWritableGnucashFile() {
	return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserDefinedAttribute(final String aName, final String aValue) {
	List<Slot> slots = getRootElement().getGncBook().getBookSlots().getSlot();
	for (Slot slot : slots) {
	    if (slot.getSlotKey().equals(aName)) {
		slot.getSlotValue().getContent().clear();
		slot.getSlotValue().getContent().add(aValue);
		return;
	    }
	}
	// create new slot
	Slot newSlot = getObjectFactory().createSlot();
	newSlot.setSlotKey(aName);
	newSlot.setSlotValue(getObjectFactory().createSlotValue());
	newSlot.getSlotValue().getContent().add(aValue);
	newSlot.getSlotValue().setType("string");
	getRootElement().getGncBook().getBookSlots().getSlot().add(newSlot);
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
	if (rootAccounts.size() > 1) {
	    KMyMoneyAccount root = null;
	    StringBuilder roots = new StringBuilder();
	    for (KMyMoneyAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if (gnucashAccount.getType() != null && 
	            gnucashAccount.getType().equals(KMyMoneyAccount.TYPE_ROOT)) {
		    root = gnucashAccount;
		    continue;
		}
		roots.append(gnucashAccount.getId()).append("=\"").append(gnucashAccount.getName()).append("\" ");
	    }
	    LOGGER.warn("File has more then one root-account! Attaching excess accounts to root-account: "
		    + roots.toString());
	    LinkedList<KMyMoneyAccount> rootAccounts2 = new LinkedList<KMyMoneyAccount>();
	    rootAccounts2.add(root);
	    for (KMyMoneyAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if (gnucashAccount == root) {
		    continue;
		}
		((KMyMoneyWritableAccount) gnucashAccount).setParentAccount(root);

	    }
	    rootAccounts = rootAccounts2;
	}
	return rootAccounts;
    }

}
