package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.naming.spi.ObjectFactory;

import org.kmymoney.api.basetypes.simple.KMMAcctID;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableTransactionSplit;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneyAccountImpl to allow writing instead of
 * read-only access.<br/>
 * Supported properties for the propertyChangeListeners:
 * <ul>
 * <li>name</li>
 * <li>code</li>
 * <li>currencyID</li>
 * <li>currencyNameSpace</li>
 * <li>description</li>
 * <li>type</li>
 * <li>parentAccount</li>
 * <li>transactionSplits (not giving the old value of the list)</li>
 * </ul>
 */
public class KMyMoneyWritableAccountImpl extends KMyMoneyAccountImpl 
                                         implements KMyMoneyWritableAccount 
{

	/**
	 * Our logger for debug- and error-ourput.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableAccountImpl.class);

	/**
	 * Our helper to implement the KMyMoneyWritableObject-interface.
	 */
	private KMyMoneyWritableObjectImpl helper;

	/**
	 * {@inheritDoc}
	 *
	 * @see KMyMoneyWritableObject#setUserDefinedAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( helper == null ) {
			helper = new KMyMoneyWritableObjectImpl(super.helper);
		}
		LOGGER.debug("KMyMoneyAccountWritingImpl[account-id=" + getId() + " name=" + getName()
				+ "].setUserDefinedAttribute(name=" + name + ", value=" + value + ")");
		helper.setUserDefinedAttribute(name, value);
	}

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
	
	public KMyMoneyWritableAccountImpl(KMyMoneyAccountImpl acct) {
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
		// left unset account.setActCode();
		jwsdpAcct.setActSecurityScu(100); // x,yz
		jwsdpAcct.setActDescription("no description yet");
		// left unset account.setActLots();
		jwsdpAcct.setActName("UNNAMED");
		// left unset account.setActNonStandardScu();
		// left unset account.setActParent())
		jwsdpAcct.setActType(KMyMoneyAccount.TYPE_BANK);

		jwsdpAcct.setVersion(Const.XML_FORMAT_VERSION);

		{
			GncAccount.ActSecurity currency = factory.createGncAccountActSecurity();
			currency.setCmdtyId(file.getDefaultCurrencyID());
			currency.setCmdtySpace(CurrencyNameSpace.NAMESPACE_CURRENCY);
			jwsdpAcct.setActSecurity(currency);
		}

		{
			GncAccount.ActId guid = factory.createGncAccountActId();
			guid.setType(Const.XML_DATA_TYPE_GUID);
			guid.setValue(accountguid);
			jwsdpAcct.setActId(guid);
		}

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
	public void setAccountCode(final String code) {
		if ( code == null || code.trim().length() == 0 ) {
			throw new IllegalArgumentException("null or empty code given!");
		}

		String oldCode = jwsdpPeer.getActCode();
		if ( oldCode == code ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.setActCode(code);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("code", oldCode, code);
		}
	}

	/**
	 * @param currencyID the new currency
	 * @see #setCurrencyNameSpace(String)
	 * @see {@link KMyMoneyAccount#getCurrencyID()}
	 */
	public void setCurrencyID(final String currencyID) {
		if ( currencyID == null ) {
			throw new IllegalArgumentException("null or empty currencyID given!");
		}

		String oldCurrencyId = jwsdpPeer.getActSecurity().getCmdtyId();
		if ( oldCurrencyId == currencyID ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.getActSecurity().setCmdtyId(currencyID);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyID", oldCurrencyId, currencyID);
		}
	}

	/**
	 * @param currNameSpace the new namespace
	 * @see {@link KMyMoneyAccount#getCurrencyNameSpace()}
	 */
	public void setCurrencyNameSpace(final String currNameSpace) {
		if ( currNameSpace == null ) {
			throw new IllegalArgumentException("null or empty currencyNameSpace given!");
		}

		String oldCurrNameSpace = jwsdpPeer.getSecurity().getCmdtySpace();
		if ( oldCurrNameSpace == currNameSpace ) {
			return; // nothing has changed
		}
		this.jwsdpPeer.getSecurity().setCmdtySpace(currNameSpace);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("currencyNameSpace", oldCurrNameSpace, currNameSpace);
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

		String oldDescr = jwsdpPeer.getActDescription();
		if ( oldDescr == descr ) {
			return; // nothing has changed
		}
		jwsdpPeer.setActDescription(descr);
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
	public void setType(final String type) {
		if ( type == null ) {
			throw new IllegalArgumentException("null type given!");
		}

		String oldType = jwsdpPeer.getDescription();
		if ( oldType == type ) {
			return; // nothing has changed
		}
		jwsdpPeer.setType(type);
		setIsModified();
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("type", oldType, type);
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setParentAccount(KMyMoneyAccount)
	 */
	public void setParentAccountId(final String newParent) {
		if ( newParent == null || newParent.trim().length() == 0 ) {
			setParentAccount(null);
		} else {
			setParentAccount(getKMyMoneyFile().getAccountByID(newParent));
		}
	}

	/**
	 * @see KMyMoneyWritableAccount#setParentAccount(KMyMoneyAccount)
	 */
	public void setParentAccount(final KMyMoneyAccount prntAcct) {

		if ( prntAcct == null ) {
			this.jwsdpPeer.setParent(null);
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
		GncAccount.ActParent parent = jwsdpPeer.getParent();
		if ( parent == null ) {
			parent = ((KMyMoneyWritableFileImpl) getWritableKMyMoneyFile()).getObjectFactory()
					.createGncAccountActParent();
			parent.setType(Const.XML_DATA_TYPE_GUID);
			parent.setValue(prntAcct.getId());
			jwsdpPeer.setActParent(parent);

		} else {
			oldPrntAcct = getParentAccount();
			parent.setValue(prntAcct.getId());
		}
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
			KMyMoneyTransactionSplit split = (KMyMoneyTransactionSplit) element;
			LocalDateTime whenHappened = split.getTransaction().getDatePosted().toLocalDateTime();
			if ( !whenHappened.isBefore(to.atStartOfDay()) ) {
				continue;
			}
			if ( whenHappened.isBefore(from.atStartOfDay()) ) {
				continue;
			}
			retval = retval.add(split.getShares());
		}
		return retval;
	}

	// -------------------------------------------------------

}
