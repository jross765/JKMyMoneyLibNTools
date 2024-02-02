package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.basetypes.simple.KMMInstID;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneyAccountImpl to allow writing instead of
 * read-only access.<br/>
 */
public class KMyMoneyWritableAccountImpl extends KMyMoneyAccountImpl 
                                         implements KMyMoneyWritableAccount 
{

	/**
	 * Our logger for debug- and error-ourput.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableAccountImpl.class);

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
	
	public KMyMoneyWritableAccountImpl(final KMyMoneyAccountImpl acct) {
		super(acct.getJwsdpPeer(), acct.getKMyMoneyFile());
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
			throw new IllegalArgumentException("empty ID given");
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
			throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
		}
		if ( this.getChildren().size() > 0 ) {
			throw new IllegalStateException("cannot remove account while it contains child-accounts!");
		}

		getWritableKMyMoneyFile().getRootElement().getACCOUNTS().getACCOUNT().remove(jwsdpPeer);
		getWritableKMyMoneyFile().removeAccount(this);
	}

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	public KMyMoneyWritableFile getWritableKMyMoneyFile() {
		return (KMyMoneyWritableFileImpl) getKMyMoneyFile();
	}

	/**
	 * @see KMyMoneyAccount#addTransactionSplit(KMyMoneyTransactionSplit)
	 */
	@Override
	public void addTransactionSplit(final KMyMoneyTransactionSplit split) {
		super.addTransactionSplit(split);

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, getTransactionSplits());
		}
	}

	/**
	 * @param impl the split to remove
	 */
	protected void removeTransactionSplit(final KMyMoneyWritableTransactionSplit impl) {
		List transactionSplits = getTransactionSplits();
		transactionSplits.remove(impl);

		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("transactionSplits", null, transactionSplits);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setName(java.lang.String)
	 */
	public void setName(final String name) {
		if ( name == null || name.trim().length() == 0 ) {
			throw new IllegalArgumentException("null or empty name given!");
		}

		String oldName = jwsdpPeer.getName();
		if ( oldName == name ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setName(name);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("name", oldName, name);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setAccountCode(java.lang.String)
	 */
	public void setInstitutionID(final KMMInstID instID) {
		if ( instID == null ) {
			throw new IllegalArgumentException("null institution-ID given!");
		}

		if ( ! instID.isSet() ) {
			throw new IllegalArgumentException("unset institution-ID given!");
		}

		String oldInstID = jwsdpPeer.getInstitution();
		if ( oldInstID == instID.toString() ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setInstitution(instID.toString());
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("code", oldInstID, instID);
		}
	}

	/**
	 * @param currencyID the new currency
	 * @see #setCurrencyNameSpace(String)
	 * @see {@link KMyMoneyAccount#getCurrencyID()}
	 */
	public void setCurrencyID(final String currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("null or empty currencyID given!");
		}

		String oldCurrId = jwsdpPeer.getCurrency();
		if ( oldCurrId == currID ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setCurrency(currID);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyID", oldCurrId, currID);
		}
	}

	/**
	 * set getWritableFile().setModified(true).
	 */
	protected void setIsModified() {
		KMyMoneyWritableFile writableFile = getWritableKMyMoneyFile();
		writableFile.setModified(true);
	}

	/**
	 * Used by ${@link #getBalance()} to cache the result.
	 */
	private FixedPointNumber myBalanceCached = null;

	/**
	 * Used by ${@link #getBalance()} to cache the result.
	 */
	private PropertyChangeListener myBalanceCachedInvalidtor = null;

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

		Collection<KMyMoneyTransactionSplit> after = new LinkedList<KMyMoneyTransactionSplit>();
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
						// because that happenes seldomly enough

						if ( evt.getPropertyName().equals("account")
								&& evt.getSource() instanceof KMyMoneyWritableTransactionSplit ) {
							KMyMoneyWritableTransactionSplit splitw = (KMyMoneyWritableTransactionSplit) evt
									.getSource();
							if ( splitw.getAccount() != KMyMoneyWritableAccountImpl.this ) {
								splitw.removePropertyChangeListener("account", this);
								splitw.removePropertyChangeListener("quantity", this);
								splitw.getTransaction().removePropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.remove(splitw);

							}

						}
						if ( evt.getPropertyName().equals("transactionSplits") ) {
							Collection<KMyMoneyTransactionSplit> splits = (Collection<KMyMoneyTransactionSplit>) evt
									.getNewValue();
							for ( KMyMoneyTransactionSplit split : splits ) {
								if ( !(split instanceof KMyMoneyWritableTransactionSplit)
										|| splitsWeAreAddedTo.contains(split) ) {
									continue;
								}
								KMyMoneyWritableTransactionSplit splitw = (KMyMoneyWritableTransactionSplit) split;
								splitw.addPropertyChangeListener("account", this);
								splitw.addPropertyChangeListener("quantity", this);
								splitw.getTransaction().addPropertyChangeListener("datePosted", this);
								splitsWeAreAddedTo.add(splitw);
							}
						}
					}
				};
				addPropertyChangeListener("currencyID", myBalanceCachedInvalidtor);
				addPropertyChangeListener("currencyNameSpace", myBalanceCachedInvalidtor);
				addPropertyChangeListener("transactionSplits", myBalanceCachedInvalidtor);
			}
		}

		return balance;
	}

	/**
	 * @see KMyMoneyWritableAccount#setName(java.lang.String)
	 */
	public void setDescription(final String descr) {
		if ( descr == null ) {
			throw new IllegalArgumentException("null or empty description given!");
		}

		String oldDescr = jwsdpPeer.getDescription();
		if ( oldDescr == descr ) {
			return; // nothing has changed
		}
		jwsdpPeer.setDescription(descr);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
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

		BigInteger oldTypeInt = jwsdpPeer.getType();
		if ( oldTypeInt == typeInt ) {
			return; // nothing has changed
		}
		jwsdpPeer.setType(typeInt);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("type", oldTypeInt, typeInt);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setParentAccount(KMyMoneyAccount)
	 */
	public void setParentAccountId(final KMMComplAcctID prntAcctID) {
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

		KMyMoneyAccount oldPrntAcct = null;
		jwsdpPeer.setParentaccount(prntAcct.getID().toString());
		setIsModified();

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("parentAccount", oldPrntAcct, prntAcct);
		}
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

		for ( Object element : getTransactionSplits() ) {
			KMyMoneyTransactionSplit splt = (KMyMoneyTransactionSplit) element;
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

	// -------------------------------------------------------

	/**
	 * @param name  the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(kvpList, getWritableKMyMoneyFile(), 
			                             name, value);
	}

}
