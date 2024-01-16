package org.kmymoney.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.LinkedList;

import javax.naming.spi.ObjectFactory;

import org.kmymoney.api.basetypes.simple.KMMSpltID;
import org.kmymoney.api.basetypes.simple.KMMTrxID;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.SplitNotFoundException;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
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
	public KMyMoneyWritableTransactionImpl(final TRANSACTION jwsdpPeer, final KMyMoneyFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a new Transaction and add it to the file.
	 *
	 * @param file the file we belong to
	 */
	public KMyMoneyWritableTransactionImpl(final KMyMoneyWritableFileImpl file) {
		super(createTransaction_int(file, file.getNewTransactionID()), file);
		file.addTransaction(this);
	}

	public KMyMoneyWritableTransactionImpl(final KMyMoneyTransaction trx) {
		super(trx.getJwsdpPeer(), trx.getKMyMoneyFile());

		// ::TODO
		System.err.println("NOT IMPLEMENTED YET");
//	    for ( GnucashTransactionSplit splt : trx.getSplits() ) 
//	    {
//		addSplit(new GnucashTransactionSplitImpl(splt.jwsdpPeer, trx));
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
	protected static TRANSACTION createTransaction_int(
			final KMyMoneyWritableFileImpl file, 
			final KMMTrxID newID) {

		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("empty ID given");
		}

		ObjectFactory factory = file.getObjectFactory();
		TRANSACTION jwsdpTrx = file.createTransactionType();

		jwsdpTrx.setId(newID.toString());

		{
			String dateEntered = DATE_ENTERED_FORMAT.format(LocalDateTime.now());
			jwsdpTrx.setEntrydate(dateEntered);
		}

		{
			String datePosted = DATE_ENTERED_FORMAT.format(LocalDateTime.now());
			jwsdpTrx.setPostdate(datePosted);
		}

		{
			jwsdpTrx.setCommodity(file.getDefaultCurrencyID());
		}

		{
			SPLIT splits = factory.createTransactionTrnSplits();
			jwsdpTrx.setTrnSplits(splits);
		}

		jwsdpTrx.setVersion(Const.XML_FORMAT_VERSION);
		jwsdpTrx.setTrnDescription("-");

        file.getRootElement().getTRANSACTIONS().getTRANSACTION().add(jwsdpTrx);
        file.setModified(true);
    
        LOGGER.debug("createTransaction_int: Created new transaction (core): " + jwsdpTrx.getId());
        
        return jwsdpTrx;
	}

	/**
	 * @param splt the split to remove from this transaction
	 */
	public void remove(final KMyMoneyWritableTransactionSplit splt) {
		jwsdpPeer.getSPLITS().getSPLIT()
			.remove(((KMyMoneyWritableTransactionSplitImpl) splt).jwsdpPeer);
		getWritingFile().setModified(true);
		if ( mySplits != null ) {
			mySplits.remove(splt);
		}
		KMyMoneyWritableAccountImpl account = (KMyMoneyWritableAccountImpl) splt.getAccount();
		if ( account != null ) {
			account.removeTransactionSplit(splt);
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
		this.jwsdpPeer.setCommodity(id);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setDateEntered(LocalDateTime)
	 */
	public void setDateEntered(final LocalDate dateEntered) {
		this.entryDate = dateEntered;
		jwsdpPeer.setEntrydate(DATE_ENTERED_FORMAT.format(dateEntered));
		getWritingFile().setModified(true);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setDatePosted(LocalDateTime)
	 */
	public void setDatePosted(final LocalDate datePosted) {
		this.postDate = datePosted;
		jwsdpPeer.setPostdate(DATE_ENTERED_FORMAT.format(datePosted));
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

		String old = jwsdpPeer.getMemo();
		jwsdpPeer.setMemo(desc);
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

		String old = jwsdpPeer.getTrnNum();
		jwsdpPeer.setTrnNum(tnum);
		getWritingFile().setModified(true);

		if ( old == null || !old.equals(tnum) ) {
			if ( getPropertyChangeSupport() != null ) {
				getPropertyChangeSupport().firePropertyChange("transactionNumber", old, tnum);
			}
		}
	}
	
	// ---------------------------------------------------------------
	
	KMMSpltID getNewSplitID() {
		
		int maxSpltNo = 1; // sic, in case there are no splits yet
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			if ( Integer.parseInt( splt.getID().get() ) >= maxSpltNo ) {
				maxSpltNo = Integer.parseInt( splt.getID().get() );
			}
		}
		
		return new KMMSpltID(maxSpltNo);
	}

}
