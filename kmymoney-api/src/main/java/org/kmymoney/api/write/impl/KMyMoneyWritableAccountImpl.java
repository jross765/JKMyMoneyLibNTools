package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.KEYVALUEPAIRS;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.hlp.KVPListDoesNotContainKeyException;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecID;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.kmymoney.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneyAccountImpl to allow writing instead of
 * read-only access.
 */
public class KMyMoneyWritableAccountImpl extends KMyMoneyAccountImpl 
                                         implements KMyMoneyWritableAccount 
{
	/**
	 * Our logger for debug- and error-output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableAccountImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the KMyMoneyWritableObject-interface.
     */
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

	/**
	 * Used by ${@link #getBalance()} to cache the result.
	 */
	private FixedPointNumber myBalanceCached = null;

	/**
	 * Used by ${@link #getBalance()} to cache the result.
	 */
	private PropertyChangeListener myBalanceCachedInvalidtor = null;

    // ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer 
	 * @param file 
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableAccountImpl(final ACCOUNT jwsdpPeer, final KMyMoneyFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * @param file 
	 */
	public KMyMoneyWritableAccountImpl(final KMyMoneyWritableFileImpl file) {
		super(createAccount_int(file, file.getNewAccountID()), file);
	}
	
	public KMyMoneyWritableAccountImpl(final KMyMoneyAccountImpl acct, final boolean addSplits) {
		super(acct.getJwsdpPeer(), acct.getKMyMoneyFile());

		if (addSplits) {
		    for ( KMyMoneyTransactionSplit splt : ((KMyMoneyFileImpl) acct.getKMyMoneyFile()).getTransactionSplits_readAfresh() ) {
		    	if ( ! acct.isRootAccount() &&
		    		 splt.getAccountID().equals(acct.getID()) ) {
		    		super.addTransactionSplit(splt);
			    // NO:
//				    addTransactionSplit(new KMyMoneyTransactionSplitImpl(splt.getJwsdpPeer(), splt.getTransaction(), 
//		                                false, false));
		    	}
		    }
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @param file
	 * @return
	 */
	private static ACCOUNT createAccount_int(
			final KMyMoneyWritableFileImpl file, 
			final KMMAcctID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}

		ACCOUNT jwsdpAcct = file.createAccountType();
		
		jwsdpAcct.setId(newID.toString());
		jwsdpAcct.setType(KMyMoneyAccount.Type.ASSET.getCodeBig());
		jwsdpAcct.setName("UNNAMED");
		jwsdpAcct.setDescription("no description yet");
		jwsdpAcct.setCurrency(file.getDefaultCurrencyID());

		file.getRootElement().getACCOUNTS().getACCOUNT().add(jwsdpAcct);
		file.setModified(true);
		return jwsdpAcct;
	}

	/**
	 * Remove this account from the sytem.<br/>
	 * Throws IllegalStateException if this account has splits or childres.
	 */
	public void remove() {
		if ( getTransactionSplits().size() > 0 ) {
			throw new IllegalStateException("Cannot remove account while it contains transaction-splits!");
		}
		if ( this.getChildren().size() > 0 ) {
			throw new IllegalStateException("Cannot remove account while it contains child-accounts!");
		}

		getWritableKMyMoneyFile().getRootElement().getACCOUNTS().getACCOUNT().remove(jwsdpPeer);
		getWritableKMyMoneyFile().removeAccount(this);
	}

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyAccount#addTransactionSplit(KMyMoneyTransactionSplit)
	 */
	@Override
	public void addTransactionSplit(final KMyMoneyTransactionSplit split) {
		super.addTransactionSplit(split);

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, getTransactionSplits());
		}
	}

	/**
	 * @param impl the split to remove
	 */
	protected void removeTransactionSplit(final KMyMoneyWritableTransactionSplit impl) {
		List<KMyMoneyTransactionSplit> transactionSplits = getTransactionSplits();
		transactionSplits.remove(impl);

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, transactionSplits);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyWritableAccount#setName(java.lang.String)
	 */
	public void setName(final String name) {
		if ( name == null || name.trim().length() == 0 ) {
			throw new IllegalArgumentException("null or empty name given!");
		}

		String oldName = getName();
		if ( oldName == name ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setName(name);
		setIsModified();
		
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("name", oldName, name);
		}
	}

	// ::TODO
//	/**
//	 * @see KMyMoneyWritableAccount#setAccountCode(java.lang.String)
//	 */
//	public void setInstitutionID(final KMMInstID instID) {
//		if ( instID == null ) {
//			throw new IllegalArgumentException("null institution-ID given!");
//		}
//
//		if ( ! instID.isSet() ) {
//			throw new IllegalArgumentException("unset institution-ID given!");
//		}
//
//		String oldInstID = getInstitution();
//		if ( oldInstID == instID.toString() ) {
//			return; // nothing has changed
//		}
//		this.jwsdpPeer.setInstitution(instID.toString());
//		setIsModified();
//		// <<insert code to react further to this change here
//		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
//		if ( propertyChangeFirer != null ) {
//			propertyChangeFirer.firePropertyChange("code", oldInstID, instID);
//		}
//	}

	// ----------------------------

	@Override
	public void setQualifSecCurrID(final KMMQualifSecCurrID secCurrID) {
		if ( secCurrID == null ) {
			throw new IllegalArgumentException("null security/currency ID given!");
		}

		// ::TODO
//		if ( ! secCurrID.isSet() ) {
//			throw new IllegalArgumentException("unset security/currency ID given!");
//		}

		KMMQualifSecCurrID oldCurrId = getQualifSecCurrID();
		if ( oldCurrId == secCurrID ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setCurrency(secCurrID.getCode());
		setIsModified();
		
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyID", oldCurrId, secCurrID.getCode());
		}
	}
	
	@Override
	public void setSecID(final KMMSecID secID) {
		setQualifSecCurrID(new KMMQualifSecID(secID));
	}
	
	@Override
	public void setCurrency(final Currency curr) {
		setQualifSecCurrID(new KMMQualifCurrID(curr));
	}

	@Override
	public void setCurrency(final String currCode) {
		setCurrency(Currency.getInstance(currCode));
	}
	
	// ----------------------------

	/**
	 * set getWritableFile().setModified(true).
	 */
	protected void setIsModified() {
		KMyMoneyWritableFile writableFile = getWritableKMyMoneyFile();
		writableFile.setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * same as getBalance(new Date()).<br/>
	 * ignores transactions after the current date+time<br/>
	 * This implementation caches the result.<br/>
	 * We assume that time does never move backwards
	 *
	 * @see #getBalance(LocalDate)
	 */
	@Override
	public FixedPointNumber getBalance() {

		if ( myBalanceCached != null ) {
			return myBalanceCached;
		}

		List<KMyMoneyTransactionSplit> after = new ArrayList<KMyMoneyTransactionSplit>();
		FixedPointNumber balance = getBalance(LocalDate.now(), after);

		if ( after.isEmpty() ) {
			myBalanceCached = balance;

			// add a listener to keep the cache up to date
			if ( myBalanceCachedInvalidtor != null ) {
				myBalanceCachedInvalidtor = new PropertyChangeListener() {
					private final Collection<KMyMoneyTransactionSplit> splitsWeAreAddedTo = new HashSet<KMyMoneyTransactionSplit>();

					public void propertyChange(final PropertyChangeEvent evt) {
						myBalanceCached = null;

						// we don't handle the case of removing an account
						// because that happens seldomly enough

						if ( evt.getPropertyName().equals("account") && 
							 evt.getSource() instanceof KMyMoneyWritableTransactionSplit ) {
							KMyMoneyWritableTransactionSplit splitw = (KMyMoneyWritableTransactionSplit) evt.getSource();
							if ( splitw.getAccount() != KMyMoneyWritableAccountImpl.this ) {
								helper.removePropertyChangeListener("account", this);
								helper.removePropertyChangeListener("shares", this);
								helper.removePropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.remove(splitw);
							}

						}
						
						if ( evt.getPropertyName().equals("transactionSplits") ) {
							Collection<KMyMoneyTransactionSplit> splits = (Collection<KMyMoneyTransactionSplit>) evt.getNewValue();
							for ( KMyMoneyTransactionSplit split : splits ) {
								if ( ! (split instanceof KMyMoneyWritableTransactionSplit) || 
									 splitsWeAreAddedTo.contains(split) ) {
									continue;
								}
								KMyMoneyWritableTransactionSplit splitw = (KMyMoneyWritableTransactionSplit) split;
								helper.addPropertyChangeListener("account", this);
								helper.addPropertyChangeListener("shares", this);
								helper.addPropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.add(splitw);
							}
						}
					}
				};
				
				helper.addPropertyChangeListener("currencyID", myBalanceCachedInvalidtor);
				helper.addPropertyChangeListener("currencyNameSpace", myBalanceCachedInvalidtor);
				helper.addPropertyChangeListener("transactionSplits", myBalanceCachedInvalidtor);
			}
		}

		return balance;
	}

	/**
	 * Get the sum of all transaction-splits affecting this account in the given
	 * time-frame.
	 *
	 * @param from when to start, inclusive
	 * @param to   when to stop, exlusive.
	 * @return the sum of all transaction-splits affecting this account in the given
	 *         time-frame.
	 */
	public FixedPointNumber getBalanceChange(final LocalDate from, final LocalDate to) {
		FixedPointNumber retval = new FixedPointNumber();
	
		for ( KMyMoneyTransactionSplit splt : getTransactionSplits() ) {
			LocalDate whenHappened = splt.getTransaction().getDatePosted();
			
			if ( !whenHappened.isBefore(to) ) {
				continue;
			}
			
			if ( whenHappened.isBefore(from) ) {
				continue;
			}
			
			retval = retval.add(splt.getShares());
		}
		return retval;
	}
	
	// ---------------------------------------------------------------

	/**
	 * @see KMyMoneyWritableAccount#setName(java.lang.String)
	 */
	public void setMemo(final String descr) {
		if ( descr == null ) {
			throw new IllegalArgumentException("null or empty description given!");
		}

		String oldDescr = getMemo();
		if ( oldDescr == descr ) {
			return; // nothing has changed
		}
		jwsdpPeer.setDescription(descr);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("description", oldDescr, descr);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setInvcType(java.lang.String)
	 */
	public void setType(final KMyMoneyAccount.Type type) {
		setTypeInt(type.getCodeBig());
	}

	public void setTypeInt(final BigInteger typeInt) {
		if ( typeInt == null ) {
			throw new IllegalArgumentException("null type given!");
		}

		if ( typeInt.intValue() <= 0 ) {
			throw new IllegalArgumentException("type <= 0 given!");
		}

		BigInteger oldType = getTypeBigInt();
		if ( oldType == typeInt ) {
			return; // nothing has changed
		}
		jwsdpPeer.setType(typeInt);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("type", oldType, typeInt);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setParentAccount(KMyMoneyAccount)
	 */
	public void setParentAccountID(final KMMComplAcctID prntAcctID) {
		if ( prntAcctID == null ) {
			setParentAccount(null);
			return;
		}

		// ::TODO
//		if ( ! prntAcctID.isSet() ) {
//			throw new IllegalArgumentException("unset account ID given!");
//		}

		setParentAccount(getKMyMoneyFile().getAccountByID(prntAcctID));
	}

	/**
	 * @see KMyMoneyWritableAccount#setParentAccount(KMyMoneyAccount)
	 */
	public void setParentAccount(final KMyMoneyAccount prntAcct) {

		if ( prntAcct == null ) {
			this.jwsdpPeer.setParentaccount(null);
			return;
		}

		if ( prntAcct == this ) {
			throw new IllegalArgumentException("I cannot be my own parent!");
		}

		// check if newparent is a child-account recursively
		if ( isChildAccountRecursive(prntAcct) ) {
			throw new IllegalArgumentException("I cannot be my own (grand-)parent!");
		}

		KMyMoneyAccount oldPrntAcct = getParentAccount();
		jwsdpPeer.setParentaccount(prntAcct.getID().toString());
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("parentAccount", oldPrntAcct, prntAcct);
		}
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

	// -------------------------------------------------------

	@Override
	public void addUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null ) {
			ObjectFactory fact = getKMyMoneyFile().getObjectFactory();
			KEYVALUEPAIRS newKVPs = fact.createKEYVALUEPAIRS();
			jwsdpPeer.setKEYVALUEPAIRS(newKVPs);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getKEYVALUEPAIRS(), getWritableKMyMoneyFile(), 
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

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyWritableAccountImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", type=");
	try {
	    buffer.append(getType());
	} catch (UnknownAccountTypeException e) {
	    buffer.append("ERROR");
	}
	
	buffer.append(", qualif-name='");
	buffer.append(getQualifiedName() + "'");
	
	buffer.append(", security/currency='");
	try {
	    buffer.append(getQualifSecCurrID() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR");
	}
	
	buffer.append("]");
	
	return buffer.toString();
    }

}
