package org.kmymoney.api.write.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.api.generated.KEYVALUEPAIRS;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.SPLIT;
import org.kmymoney.api.generated.SPLITS;
import org.kmymoney.api.generated.TRANSACTION;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.SplitNotFoundException;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionImpl;
import org.kmymoney.api.read.impl.KMyMoneyTransactionSplitImpl;
import org.kmymoney.api.read.impl.hlp.KVPListDoesNotContainKeyException;
import org.kmymoney.api.write.KMyMoneyWritableTransaction;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.kmymoney.base.basetypes.simple.KMMIDNotSetException;
import org.kmymoney.base.basetypes.simple.KMMSpltID;
import org.kmymoney.base.basetypes.simple.KMMTrxID;
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
	 * Our helper to implement the KMyMoneyWritableObject-interface.
	 */
	private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

	// -----------------------------------------------------------

	/**
	 * @param kmmFile      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableTransactionImpl(final TRANSACTION jwsdpPeer, final KMyMoneyFileImpl kmmFile) {
		super(jwsdpPeer, kmmFile);
	}

	/**
	 * Create a new Transaction and add it to the file.
	 *
	 * @param file the file we belong to
	 */
	public KMyMoneyWritableTransactionImpl(final KMyMoneyWritableFileImpl file) {
		super(createTransaction_int(file, file.getNewTransactionID()), file);
		// file.addTransaction(this); NO! Redundant
	}

	public KMyMoneyWritableTransactionImpl(final KMyMoneyTransactionImpl trx) {
		super(trx.getJwsdpPeer(), trx.getKMyMoneyFile());

		// ::TODO / ::CHECK
		// System.err.println("NOT IMPLEMENTED YET");
//	    for ( KMyMoeneyTransactionSplit splt : trx.getSplits() ) 
//	    {
//		addSplit(new KMyMoneyTransactionSplitImpl(splt.jwsdpPeer, trx));
//	    }
	}

	// -----------------------------------------------------------

	/**
	 * The gnucash-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	public KMyMoneyWritableFileImpl getWritableFile() {
		return (KMyMoneyWritableFileImpl) getKMyMoneyFile();
	}

	/**
	 * Create a new split for a split found in the jaxb-data.
	 *
	 * @param splt the jaxb-data
	 * @return the new split-instance
	 */
	@Override
	protected KMyMoneyTransactionSplitImpl createSplit(
			final SPLIT splt,
		    final boolean addToAcct) {
		KMyMoneyWritableTransactionSplitImpl kmmTrxSplt = 
				new KMyMoneyWritableTransactionSplitImpl(splt, this,
						                                 addToAcct);
		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
		}
		return kmmTrxSplt;
	}

	/**
	 * @see KMyMoneyWritableTransaction#createWritableSplit(KMyMoneyAccount)
	 */
	public KMyMoneyWritableTransactionSplit createWritableSplit(final KMyMoneyAccount acct) {
		KMyMoneyWritableTransactionSplitImpl splt = new KMyMoneyWritableTransactionSplitImpl(this, acct);
		addSplit(splt);
		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
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
			throw new IllegalArgumentException("unset ID given");
		}

		// ObjectFactory fact = file.getObjectFactory();
		TRANSACTION jwsdpTrx = file.createTransactionType();

		jwsdpTrx.setId(newID.toString());

		{
			String dateEntered = DATE_ENTERED_FORMAT.format(LocalDateTime.now());
			jwsdpTrx.setEntrydate(dateEntered);
		}

		try {
			LocalDateTime dateTime = LocalDateTime.now();
	        // https://stackoverflow.com/questions/835889/java-util-date-to-xmlgregoriancalendar
			// https://simplesolution.dev/java-convert-localdate-to-calendar/
	        GregorianCalendar cal = new GregorianCalendar();
	        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
	        Instant instant = zonedDateTime.toInstant();
	        Date date = Date.from(instant);
	        cal.setTime(date);
	        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			jwsdpTrx.setPostdate(xmlCal);
		} catch ( DatatypeConfigurationException exc ) {
			throw new DateMappingException();
		}

		{
			jwsdpTrx.setCommodity(file.getDefaultCurrencyID());
		}

		{
			SPLITS splits = file.createSplitsType();
			
//			{
//				SPLIT splt = file.createSplitType();
//				splits.getSPLIT().add(splt);
//			}

			jwsdpTrx.setSPLITS(splits);
		}

		jwsdpTrx.setMemo("-");

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
			.remove(((KMyMoneyWritableTransactionSplitImpl) splt).getJwsdpPeer());
		getWritableFile().setModified(true);
		if ( mySplits != null ) {
			mySplits.remove(splt);
		}
		KMyMoneyWritableAccountImpl account = (KMyMoneyWritableAccountImpl) splt.getAccount();
		if ( account != null ) {
			account.removeTransactionSplit(splt);
		}

		// there is no count for splits up to now
		// getWritableFile().decrementCountDataFor()

		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
		}
	}

	/**
	 * @throws SplitNotFoundException
	 * @see KMyMoneyWritableTransaction#getWritableFirstSplit()
	 */
	@Override
	public KMyMoneyWritableTransactionSplit getFirstSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getFirstSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritableFirstSplit()
	 */
	public KMyMoneyWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getFirstSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritableSecondSplit()
	 */
	@Override
	public KMyMoneyWritableTransactionSplit getSecondSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getSecondSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritableSecondSplit()
	 */
	public KMyMoneyWritableTransactionSplit getWritableSecondSplit() throws SplitNotFoundException {
		return (KMyMoneyWritableTransactionSplit) super.getSecondSplit();
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritableSplitByID(java.lang.String)
	 */
	public KMyMoneyWritableTransactionSplit getWritableSplitByID(final String id) {
		return (KMyMoneyWritableTransactionSplit) super.getSplitByID(id);
	}

	/**
	 * @see KMyMoneyWritableTransaction#getWritableSplits()
	 */
	@SuppressWarnings("unchecked")
	public Collection<? extends KMyMoneyWritableTransactionSplit> getWritableSplits() {
		List<KMyMoneyWritableTransactionSplit> result = new ArrayList<KMyMoneyWritableTransactionSplit>();
		
		for ( KMyMoneyTransactionSplit split : super.getSplits() ) {
			KMyMoneyWritableTransactionSplit newSplit = new KMyMoneyWritableTransactionSplitImpl((KMyMoneyTransactionSplitImpl) split);
		    result.add(newSplit);
		}

		return result;
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
		getWritableFile().removeTransaction(this);
		Collection<KMyMoneyWritableTransactionSplit> c = new LinkedList<KMyMoneyWritableTransactionSplit>();
		c.addAll(getWritableSplits());
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
		getWritableFile().setModified(true);
	}

	/**
	 * @see KMyMoneyWritableTransaction#setDatePosted(LocalDateTime)
	 */
	public void setDatePosted(final LocalDate datePosted) {
		this.postDate = datePosted;
		
		try {
	        // https://stackoverflow.com/questions/835889/java-util-date-to-xmlgregoriancalendar
			// https://simplesolution.dev/java-convert-localdate-to-calendar/
	        GregorianCalendar cal = new GregorianCalendar();
	        ZonedDateTime zonedDateTime = datePosted.atStartOfDay(ZoneId.systemDefault());
	        Instant instant = zonedDateTime.toInstant();
	        Date date = Date.from(instant);
	        cal.setTime(date);
	        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			jwsdpPeer.setPostdate(xmlCal);
		} catch ( DatatypeConfigurationException exc ) {
			throw new DateMappingException();
		}

		getWritableFile().setModified(true);
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
		getWritableFile().setModified(true);

		if ( old == null || !old.equals(desc) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	// ---------------------------------------------------------------
	
	@Override
	public void setCurrencyNameSpace(String id) {
		// TODO Auto-generated method stub
		
	}

	// ---------------------------------------------------------------
	
	KMMSpltID getNewSplitID() {
		
		int maxSpltNo = 0;
		for ( KMyMoneyTransactionSplit splt : getSplits() ) {
			try {
				String coreID = splt.getID().get().substring(1);
				if ( Integer.parseInt(coreID) >= maxSpltNo ) {
					maxSpltNo = Integer.parseInt(coreID);
				}
			} catch ( KMMIDNotSetException exc ) {
				throw new CannotGenerateKMMIDException();
			}
		}
		
		return new KMMSpltID(maxSpltNo + 1);
	}

	// ---------------------------------------------------------------

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	@Override
	public KMyMoneyWritableFileImpl getKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
	}
	
	// ---------------------------------------------------------------

	@Override
	public void addUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null ) {
			ObjectFactory fact = getKMyMoneyFile().getObjectFactory();
			KEYVALUEPAIRS newKVPs = fact.createKEYVALUEPAIRS();
			jwsdpPeer.setKEYVALUEPAIRS(newKVPs);
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getKEYVALUEPAIRS(), getWritableKMyMoneyFile(), 
			                             name, value);
	}

	@Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null ) {
			throw new KVPListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getKEYVALUEPAIRS(), getWritableKMyMoneyFile(), 
											name);
	}

	@Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null ) {
			throw new KVPListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getKEYVALUEPAIRS(), getWritableKMyMoneyFile(), 
			                             name, value);
	}

	// ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyWritableTransactionImpl [");

	buffer.append("id=");
	buffer.append(getID());

	// ::TODO: That only works in simple cases --
	// need a more generic approach
	buffer.append(", amount=");
	try {
	    buffer.append(getFirstSplit().getValueFormatted());
	} catch (SplitNotFoundException e) {
	    buffer.append("ERROR");
	}

	buffer.append(", description='");
	buffer.append(getMemo() + "'");

	buffer.append(", #splits=");
	buffer.append(getSplitsCount());

	buffer.append(", post-date=");
	try {
	    buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDatePosted().toString());
	}

	buffer.append(", entry-date=");
	try {
	    buffer.append(getEntryDate().format(DATE_ENTERED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getEntryDate().toString());
	}

	buffer.append("]");

	return buffer.toString();
    }


}
