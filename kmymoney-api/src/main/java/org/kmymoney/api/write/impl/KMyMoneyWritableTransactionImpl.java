package org.kmymoney.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.SplitNotFoundException;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWSDP-Implmentation of a Transaction that can be changed.
 */
public class KMyMoneyWritableTransactionImpl extends KMyMoneyTransactionImpl 
                                            implements KMyMoneyWritableTransaction 
{

	/**
	 * Our logger for debug- and error-ourput.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableTransactionImpl.class);

	/**
	 * Our helper to implement the GnucashWritableObject-interface.
	 */
	private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(this);

	// -----------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableTransactionImpl(final GncTransaction jwsdpPeer, final KMyMoneyFileImpl file) {
		super(jwsdpPeer, file);

		// repair a broken file
		if ( jwsdpPeer.getTrnDatePosted() == null ) {
			LOGGER.warn("repairing broken transaction " + jwsdpPeer.getTrnId() + " with no date-posted!");
			// we use our own ObjectFactory because: Exception in thread "AWT-EventQueue-0"
			// java.lang.IllegalAccessError: tried to access
			// method org.gnucash.write.jwsdpimpl.GnucashFileImpl.getObjectFactory()
			// Lbiz/wolschon/fileformats/gnucash/jwsdpimpl/generated/ObjectFactory; from
			// class org.gnucash.write.jwsdpimpl
			// .GnucashTransactionWritingImpl
			// ObjectFactory factory = file.getObjectFactory();
			ObjectFactory factory = new ObjectFactory();
			GncTransaction.TrnDatePosted datePosted = factory.createGncTransactionTrnDatePosted();
			datePosted.setTsDate(jwsdpPeer.getTrnDateEntered().getTsDate());
			jwsdpPeer.setTrnDatePosted(datePosted);
		}

	}

	/**
	 * Create a new Transaction and add it to the file.
	 *
	 * @param file the file we belong to
	 */
	public KMyMoneyWritableTransactionImpl(final KMyMoneyWritableFileImpl file) {
		super(createTransaction(file, file.createGUID()), file);
		file.addTransaction(this);
	}

	public KMyMoneyWritableTransactionImpl(final KMyMoneyTransaction trx) {
		super(trx.getJwsdpPeer(), trx.getKMyMoneyFile());

		// ::TODO
		System.err.println("NOT IMPLEMENTED YET");
//	    for ( GnucashTransactionSplit splt : trx.getSplits() ) 
//	    {
//		addSplit(new GnucashTransactionSplitImpl(splt.getJwsdpPeer(), trx));
//	    }
	}

	// -----------------------------------------------------------

	/**
	 * @see KMyMoneyWritableObject#setUserDefinedAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		helper.setUserDefinedAttribute(name, value);
	}

	/**
	 * The gnucash-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	public KMyMoneyWritableFileImpl getWritingFile() {
		return (KMyMoneyWritableFileImpl) getKMyMoneyFile();
	}

	/**
	 * Create a new split for a split found in the jaxb-data.
	 *
	 * @param element the jaxb-data
	 * @return the new split-instance
	 */
	@Override
	protected KMyMoneyTransactionSplitImpl createSplit(final GncTransaction.TrnSplits.TrnSplit element) {
		KMyMoneyWritableTransactionSplitImpl gnucashTransactionSplitWritingImpl = new KMyMoneyWritableTransactionSplitImpl(
				element, this);
		if ( getPropertyChangeSupport() != null ) {
			getPropertyChangeSupport().firePropertyChange("splits", null, getWritingSplits());
		}
		return gnucashTransactionSplitWritingImpl;
	}

	/**
	 * @see KMyMoneyWritableTransaction#createWritingSplit(KMyMoneyAccount)
	 */
	public KMyMoneyWritableTransactionSplit createWritingSplit(final KMyMoneyAccount account) {
		KMyMoneyWritableTransactionSplitImpl splt = new KMyMoneyWritableTransactionSplitImpl(this, account);
		addSplit(splt);
		if ( getPropertyChangeSupport() != null ) {
			getPropertyChangeSupport().firePropertyChange("splits", null, getWritingSplits());
		}
		return splt;
	}

	/**
	 * Creates a new Transaction and add's it to the given gnucash-file Don't modify
	 * the ID of the new transaction!
	 */
	protected static GncTransaction createTransaction(final KMyMoneyWritableFileImpl file, final String newId) {

		ObjectFactory factory = file.getObjectFactory();
		GncTransaction transaction = file.createGncTransaction();

		{
			GncTransaction.TrnId id = factory.createGncTransactionTrnId();
			id.setType(Const.XML_DATA_TYPE_GUID);
			id.setValue(newId);
			transaction.setTrnId(id);
		}

		{
			GncTransaction.TrnDateEntered dateEntered = factory.createGncTransactionTrnDateEntered();
			dateEntered.setTsDate(DATE_ENTERED_FORMAT.format(ZonedDateTime.now()));
			transaction.setTrnDateEntered(dateEntered);
		}

		{
			GncTransaction.TrnDatePosted datePosted = factory.createGncTransactionTrnDatePosted();
			datePosted.setTsDate(DATE_ENTERED_FORMAT.format(ZonedDateTime.now()));
			transaction.setTrnDatePosted(datePosted);
		}

		{
			GncTransaction.TrnCurrency currency = factory.createGncTransactionTrnCurrency();
			currency.setCmdtyId(file.getDefaultCurrencyID());
			currency.setCmdtySpace(CurrencyNameSpace.NAMESPACE_CURRENCY);
			transaction.setTrnCurrency(currency);
		}

		{
			GncTransaction.TrnSplits splits = factory.createGncTransactionTrnSplits();
			transaction.setTrnSplits(splits);
		}

		transaction.setVersion(Const.XML_FORMAT_VERSION);
		transaction.setTrnDescription("-");

		return transaction;
	}

	/**
	 * @param impl the split to remove from this transaction
	 */
	public void remove(final KMyMoneyWritableTransactionSplit impl) {
		getJwsdpPeer().getTrnSplits().getTrnSplit()
				.remove(((KMyMoneyWritableTransactionSplitImpl) impl).getJwsdpPeer());
		getWritingFile().setModified(true);
		if ( mySplits != null ) {
			mySplits.remove(impl);
		}
		KMyMoneyWritableAccountImpl account = (KMyMoneyWritableAccountImpl) impl.getAccount();
		if ( account != null ) {
			account.removeTransactionSplit(impl);
		}

		// there is no count for splits up to now
		// getWritingFile().decrementCountDataFor()

		if ( getPropertyChangeSupport() != null ) {
			getPropertyChangeSupport().firePropertyChange("splits", null, getWritingSplits());
		}
	}

	/**
	 * @throws SplitNotFoundException
	 * @see KMyMoneyWritableTransaction#getWritingFirstSplit()
	 */
	@Override
	public KMyMoneyWritableTransactionSplit getFirstSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getFirstSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritingFirstSplit()
	 */
	public KMyMoneyWritableTransactionSplit getWritingFirstSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getFirstSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritingSecondSplit()
	 */
	@Override
	public KMyMoneyWritableTransactionSplit getSecondSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getSecondSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritingSecondSplit()
	 */
	public KMyMoneyWritableTransactionSplit getWritingSecondSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getSecondSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritingSplitByID(java.lang.String)
	 */
	public KMyMoneyWritableTransactionSplit getWritingSplitByID(final String id) {
		return (KMyMoneyWritableTransactionSplit) super.getSplitByID(id);
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritingSplits()
	 */
	@SuppressWarnings("unchecked")
	public Collection<? extends KMyMoneyWritableTransactionSplit> getWritingSplits() {
		return (Collection<? extends KMyMoneyWritableTransactionSplit>) super.getSplits();
	}

	/**
	 * @param impl the split to add to mySplits
	 */
	protected void addSplit(final KMyMoneyWritableTransactionSplitImpl impl) {
		super.addSplit(impl);
	}

	/**
	 * @see KMyMoneyWritableTransaction#remove()
	 */
	public void remove() {
		getWritingFile().removeTransaction(this);
		Collection<KMyMoneyWritableTransactionSplit> c = new LinkedList<KMyMoneyWritableTransactionSplit>();
		c.addAll(getWritingSplits());
		for ( KMyMoneyWritableTransactionSplit element : c ) {
			element.remove();
		}

	}

	/**
	 * @param id the new currency
	 * @see #setCurrencyNameSpace(String)
	 * @see {@link KMyMoneyTransaction#getCurrencyID()}
	 */
	public void setCurrencyID(final String id) {
		this.getJwsdpPeer().getTrnCurrency().setCmdtyId(id);
	}

	/**
	 * @param id the new namespace
	 * @see {@link KMyMoneyTransaction#getCurrencyNameSpace()}
	 */
	public void setCurrencyNameSpace(final String id) {
		this.getJwsdpPeer().getTrnCurrency().setCmdtySpace(id);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setDateEntered(LocalDateTime)
	 */
	public void setDateEntered(final ZonedDateTime dateEntered) {
		this.entryDate = dateEntered;
		getJwsdpPeer().getTrnDateEntered().setTsDate(DATE_ENTERED_FORMAT.format(dateEntered));
		getWritingFile().setModified(true);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setDatePosted(LocalDateTime)
	 */
	public void setDatePosted(final LocalDate datePosted) {
		this.postDate = ZonedDateTime.of(datePosted, LocalTime.MIN, ZoneId.systemDefault());
		getJwsdpPeer().getTrnDatePosted().setTsDate(DATE_ENTERED_FORMAT.format(this.postDate));
		getWritingFile().setModified(true);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setNotes(java.lang.String)
	 */
	public void setDescription(final String desc) {
		if ( desc == null ) {
			throw new IllegalArgumentException(
					"null description given! Please use the empty string instead of null for an empty description");
		}

		String old = getJwsdpPeer().getTrnDescription();
		getJwsdpPeer().setTrnDescription(desc);
		getWritingFile().setModified(true);

		if ( old == null || !old.equals(desc) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	/**
	 * @see KMyMoneyWritableTransaction#setNumber(java.lang.String)
	 */
	public void setNumber(final String tnum) {
		if ( tnum == null ) {
			throw new IllegalArgumentException(
					"null transaction-number given! Please use the empty string instead of null for an empty "
							+ "description");
		}

		String old = getJwsdpPeer().getTrnNum();
		getJwsdpPeer().setTrnNum(tnum);
		getWritingFile().setModified(true);

		if ( old == null || !old.equals(tnum) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("transactionNumber", old, tnum);
			}
		}
	}

	@Override
	public void setDateEntered(LocalDateTime dateEntered) {
		setDateEntered(dateEntered.atZone(ZoneId.systemDefault()));
	}

}
