package org.kmymoney.api.read.impl.hlp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSplitID;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a base-class that helps implementing the GnucashAccount
 * -interface with it's extenive number of convenience-methods.<br/>
 */
public abstract class SimpleAccount implements KMyMoneyAccount {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccount.class);

	/**
	 * The file we belong to.
	 */
	private final KMyMoneyFile myFile;

	/**
	 * @param myFile The file we belong to
	 */
	public SimpleAccount(final KMyMoneyFile myFile) {
		super();
		this.myFile = myFile;
	}

	/**
	 * The returned list ist sorted by the natural order of the Transaction-Splits.
	 *
	 * @return all splits
	 * @link GnucashTransaction
	 */
	public List<KMyMoneyTransaction> getTransactions() {
		List<? extends KMyMoneyTransactionSplit> splits = getTransactionSplits();
		List<KMyMoneyTransaction> retval = new ArrayList<KMyMoneyTransaction>(splits.size());

		for (Object element : splits) {
			KMyMoneyTransactionSplit split = (KMyMoneyTransactionSplit) element;
			retval.add(split.getTransaction());
		}

		return retval;
	}

	/**
	 * @return Returns the file.
	 * @link #myFile
	 */
	public KMyMoneyFile getKMyMoneyFile() {
		return myFile;
	}

	/**
	 * @param account the account to test
	 * @return true if this is a child of us or any child's or us.
	 */
	public boolean isChildAccountRecursive(final KMyMoneyAccount account) {

		if (this == account) {
			return true;
		}

		for (Object element : getChildren()) {
			KMyMoneyAccount child = (KMyMoneyAccount) element;
			if (this == child) {
				return true;
			}
			if (child.isChildAccountRecursive(account)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getQualifiedName();
	}

	/**
	 * same as getBalance(new Date()).<br/>
	 * ignores transactions after the current date+time
	 *
	 * @see #getBalance(LocalDate)
	 */
	public FixedPointNumber getBalance() {
		return getBalance(LocalDate.now());
	}

	/**
	 * get name including the name of the parent.accounts.
	 *
	 * @return e.g. "Asset::Barvermögen::Bargeld"
	 * @see KMyMoneyAccount#getQualifiedName()
	 */
	public String getQualifiedName() {
		KMyMoneyAccount acc = getParentAccount();
		
		if ( acc == null || 
		     acc.getId() == getId() ) {
		    KMMComplAcctID parentID = getParentAccountId(); 
		    if ( parentID == null ) {
			return getName();
		    } else if ( parentID.toString().equals("") ||
			        parentID.toString().equals("(unset)") ||
			        parentID.toString().equals("(unknown)") ) {
			return getName();
		    } else {
			return getName();
		    }
		} else {
		    return acc.getQualifiedName() + SEPARATOR + getName();
		}
	}
	
	@Override
	public boolean isRootAccount() {
	    // The following does not work -- endless loop
//	    for ( KMyMoneyAccount acct : getKMyMoneyFile().getRootAccounts() ) {
//		if ( acct.getId().equals(getId()) ) {
//		    return true;
//		}
//	    }
//	    return false;
	    
	    // Instead (and just as good):
	    if ( getId().equals(KMMComplAcctID.get(KMMComplAcctID.Top.ASSET)) ||
		 getId().equals(KMMComplAcctID.get(KMMComplAcctID.Top.LIABILITY)) ||
		 getId().equals(KMMComplAcctID.get(KMMComplAcctID.Top.INCOME)) ||
		 getId().equals(KMMComplAcctID.get(KMMComplAcctID.Top.EXPENSE)) ||
		 getId().equals(KMMComplAcctID.get(KMMComplAcctID.Top.EQUITY)) ) {
		return true;
	    } else {
		    return false;
	    }
	}

	/**
	 * @see KMyMoneyAccount#getParentAccount()
	 */
	public KMyMoneyAccount getParentAccount() {
	    	if ( isRootAccount() )
	    	    return null;
	    
	    	KMMComplAcctID parentID = getParentAccountId();
		if ( parentID == null ) {
		    return null;
		} else if ( parentID.toString().equals("") ||
			    parentID.toString().equals("(unset)") ||
			    parentID.toString().equals("(unknown)") ) {
		    return null;
		}
		
		return getKMyMoneyFile().getAccountById(parentID);
	}

	/**
	 * @see KMyMoneyAccount#getSubAccounts()
	 */
	public Collection<KMyMoneyAccount> getSubAccounts() {
		return getChildren();
	}

	/**
	 * @param date     ignores transactions after the given date
	 * @param currency the currency the result shall be in (use account-currency if null)
	 * @return null if the conversion is not possible
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see #getBalance(LocalDate)
	 */
	public FixedPointNumber getBalance(final LocalDate date, final Currency currency) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		FixedPointNumber retval = getBalance(date);

		if (retval == null) {
			LOGGER.warn("SimpleAccount.getBalance() - error creating balance!");
			return null;
		}

		if (currency == null || retval.equals(new FixedPointNumber())) {
			return retval;
		}

		// is conversion needed?
		if ( getSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
		     if ( getSecCurrID().getCode().equals(currency.getCurrencyCode()) ) {
			 	return retval;
		     }
		}

		ComplexPriceTable priceTab = getKMyMoneyFile().getCurrencyTable();

		if (priceTab == null) {
			LOGGER.warn("SimpleAccount.getBalance() - cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}

		if ( ! priceTab.convertToBaseCurrency(retval, getSecCurrID()) ) {
			LOGGER.warn("SimpleAccount.getBalance() - cannot transfer "
					+ "from our currency '"
					+ getSecCurrID().toString()
					+ "' to the base-currency!");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(retval, new KMMQualifCurrID(currency)) ) {
			LOGGER.warn("SimpleAccount.getBalance() - cannot transfer "
					+ "from base-currenty to given currency '"
					+ currency
					+ "'!");
			return null;
		}

		return retval;
	}

	/**
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see KMyMoneyAccount#getBalanceRecursiveFormatted(LocalDate)
	 */
	public String getBalanceRecursiveFormatted(final LocalDate date) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive(date));
	}

	/**
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see KMyMoneyAccount#getBalanceRecursiveFormatted()
	 */
	public String getBalanceRecursiveFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive());
	}

	/**
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see KMyMoneyAccount#getBalanceRecursive()
	 */
	public FixedPointNumber getBalanceRecursive() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getBalanceRecursive(LocalDate.now());
	}

	/**
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see KMyMoneyAccount#getBalanceRecursive(LocalDate)
	 */
	public FixedPointNumber getBalanceRecursive(final LocalDate date) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getBalanceRecursive(date, getSecCurrID());
	}

	/**
	 * Gets the last transaction-split before the given date.
	 *
	 * @param date if null, the last split of all time is returned
	 * @return the last transaction-split before the given date
	 */
	public KMyMoneyTransactionSplit getLastSplitBeforeRecursive(final LocalDate date) {

		KMyMoneyTransactionSplit lastSplit = null;

		for (Object element : getTransactionSplits()) {
			KMyMoneyTransactionSplit split = (KMyMoneyTransactionSplit) element;
			if ( date == null || 
			     split.getTransaction().getDatePosted().isBefore( date ) ) {
				if ( lastSplit == null || 
				     split.getTransaction().getDatePosted().isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		for ( Iterator iter = getSubAccounts().iterator(); iter.hasNext(); ) {
			KMyMoneyAccount account = (KMyMoneyAccount) iter.next();
			KMyMoneyTransactionSplit split = account.getLastSplitBeforeRecursive(date);
			if ( split != null && 
			     split.getTransaction() != null ) {
				if ( lastSplit == null || 
				     split.getTransaction().getDatePosted().isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		return lastSplit;
	}

	/**
	 * Ignores accounts for wich this conversion is not possible.
	 *
	 * @param date     ignores transactions after the given date
	 * @param curr the currency the result shall be in
	 * @return Gets the balance including all sub-accounts.
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidSecTypeException 
	 * @see GnucashAccount#getBalanceRecursive(Date, Currency)
	 */
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		FixedPointNumber retval = getBalance(date, curr);
	
		if (retval == null) {
			retval = new FixedPointNumber();
		}

		for (Object element : getChildren()) {
			KMyMoneyAccount child = (KMyMoneyAccount) element;
			retval.add(child.getBalanceRecursive(date, curr));
		}

		return retval;
	}

	/**
	 * Ignores accounts for wich this conversion is not possible.
	 *
	 * @param date              ignores transactions after the given date
	 * @param currencyNameSpace the currency the result shall be in
	 * @param currencyName      the currency the result shall be in
	 * @return Gets the balance including all sub-accounts.
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see KMyMoneyAccount#getBalanceRecursive(Date, Currency)
	 */
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

	    // >>>> NO, ABSOLUTE SYMMETRY DOES NOT WORK HERE!
//	    FixedPointNumber retval = getBalance(date, secCurrID);
//
//	    if (retval == null) {
//		retval = new FixedPointNumber();
//	    }
//
//	    for ( KMyMoneyAccount child : getChildren() ) {
//		retval.add(child.getBalanceRecursive(date, secCurrID));
//	    }
//
//	    return retval;
	    // <<<< NO, ABSOLUTE SYMMETRY DOES NOT WORK HERE!
	    
	    if ( secCurrID.getType() == KMMQualifSecCurrID.Type.CURRENCY )
		return getBalanceRecursive(date, new KMMQualifCurrID(secCurrID.getCode()).getCurrency());
	    else
		return getBalance(date, secCurrID); // CAUTION: This assumes that under a stock account,
                                                    // there are no children (which sounds sensible,
                                                    // but there might be special cases)
	}

	/**
	 * @return true if ${@link #hasTransactions()} is true for this
	 * or any sub-accounts
	 */
	public boolean hasTransactionsRecursive() {
		if (this.hasTransactions()) {
			return true;
		}

		for (Object element : getChildren()) {
			KMyMoneyAccount child = (KMyMoneyAccount) element;
			if (child.hasTransactionsRecursive()) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true if ${@link #getTransactionSplits()}.size()>0
	 */
	public boolean hasTransactions() {
		return this.getTransactionSplits().size() > 0;
	}

	/**
	 * @param date              ignores transactions after the given date
	 * @param currencyNameSpace the currency the result shall be in
	 * @param currencyName      the currency the result shall be in
	 * @return null if the conversion is not possible
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 * @see #getBalance(LocalDate)
	 */
	public FixedPointNumber getBalance(final LocalDate date, final KMMQualifSecCurrID secCurrID) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		FixedPointNumber retval = getBalance(date);

		if (retval == null) {
			LOGGER.error("SimpleAccount.getBalance() - error creating balance!");
			return null;
		}

		// is conversion needed?
		if ( getSecCurrID().equals(secCurrID) ) {
			return retval;
		}

		ComplexPriceTable priceTab = getKMyMoneyFile().getCurrencyTable();

		if (priceTab == null) {
			LOGGER.error("SimpleAccount.getBalance() - cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}

		if ( ! priceTab.convertToBaseCurrency(retval, secCurrID) ) {
			Collection<String> currList = getKMyMoneyFile().getCurrencyTable().getCurrencies(getSecCurrID().getType());
			LOGGER.error("SimpleAccount.getBalance() - cannot transfer "
					+ "from our currency '"
					+ getSecCurrID().toString()
					+ "' to the base-currency!"
					+ " \n(we know " + getKMyMoneyFile().getCurrencyTable().getNameSpaces().size()
					+ " currency-namespaces and "
					+ (currList == null ? "no" : "" + currList.size())
					+ " currencies in our namespace)");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(retval, secCurrID) ) {
			LOGGER.error("SimpleAccount.getBalance() - cannot transfer "
					+ "from base-currenty to given currency '"
					+ secCurrID.toString()
					+ "'!");
			return null;
		}

		return retval;
	}

	/**
	 * The currency-format to use for formating.<br/>
	 * Plase access this only via {@link #getCurrencyFormat()}.
	 */
	private static NumberFormat currencyFormat = null;

	/**
	 * @return null if we are no currency but e.g. a fund
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 */
	public Currency getCurrency() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		if ( getSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			return null;
		}

		String kmmCurrID = getSecCurrID().getCode();
		return Currency.getInstance(kmmCurrID);
	}

	/**
	 * @return The currency-format to use for formating.
	 * @throws InvalidQualifSecCurrIDException 
	 */
	public NumberFormat getCurrencyFormat() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		if (currencyFormat == null) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		    // the currency may have changed
		if ( getSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			Currency currency = getCurrency();
			currencyFormat.setCurrency(currency);
		} else {
			currencyFormat = NumberFormat.getNumberInstance();
		}

		return currencyFormat;
	}

	/**
	 * same as {@link #getBalance(LocalDate)}. <br/>
	 * ignores transactions after the current date+time.
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 *
	 * @see #getBalance(LocalDate)
	 */
	public String getBalanceFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalance());
	}

	/**
	 * same as {@link #getBalance(LocalDate)}. <br/>
	 * ignores transactions after the current date+time.
	 * @throws InvalidQualifSecCurrIDException 
	 * @throws InvalidQualifSecCurrTypeException 
	 *
	 * @see #getBalance(LocalDate)
	 */
	public String getBalanceFormatted(final Locale lcl) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(getCurrency());
		return cf.format(getBalance());
	}

	/**
	 * The currency will be the one of this account.
	 *
	 * @see KMyMoneyAccount#getBalance(LocalDate)
	 */
	public FixedPointNumber getBalance(final LocalDate date) {
		return getBalance(date, (Collection<KMyMoneyTransactionSplit>) null);
	}

	/**
	 * The currency will be the one of this account.
	 *
	 * @see KMyMoneyAccount#getBalance(LocalDate, Collection)
	 */
	public FixedPointNumber getBalance(final LocalDate date, Collection<KMyMoneyTransactionSplit> after) {

		FixedPointNumber balance = new FixedPointNumber();

		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			if ( date  != null &&
			     after != null ) {
			  if ( split.getTransaction().getDatePosted().isAfter( date ) ) {
			      after.add(split);
			      continue;
			  }
			}

			// the currency of the quantity is the one of the account
			balance.add(split.getShares());
		}
		
		return balance;
	}

	/**
	 * @see KMyMoneyAccount#getBalance(KMyMoneyTransactionSplit)
	 */
	public FixedPointNumber getBalance(final KMyMoneyTransactionSplit lastIncludesSplit) {

		FixedPointNumber balance = new FixedPointNumber();
		
		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			balance.add(split.getShares());

			if (split == lastIncludesSplit) {
				break;
			}

		}
		
		return balance;
	}

	/**
	 * @see KMyMoneyAccount#getTransactionSplitByID(java.lang.String)
	 */
	public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSplitID id) {
		if (id == null) {
			throw new IllegalArgumentException("null id given!");
		}

		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			if (id.equals(split.getId())) {
				return split;
			}

		}
		
		return null;
	}

	/**
	 * This is an extension to ${@link #compareNamesTo(Object)}
	 * that makes sure that NEVER 2 accounts with different
	 * IDs compare to 0.
	 * Compares our name to o.toString() .<br/>
	 * If both starts with some digits the resulting
	 * ${@link java.lang.Integer} are compared.<br/>
	 * If one starts with a number and the other does not,
	 * the one starting with a number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
	 * ${@link java.lang.String} is done.     *
	 *
	 * @param o the Object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws ClassCastException if the specified object's type prevents it
	 *                            from being compared to this Object.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final KMyMoneyAccount otherAcc) {

		int i = compareNamesTo(otherAcc);
		if (i != 0) {
			return i;
		}

		KMyMoneyAccount other = otherAcc;
		i = other.getId().compareTo(getId());
		if (i != 0) {
		  return i;
		}

		return ("" + hashCode()).compareTo("" + otherAcc.hashCode());

	}

	/**
	 * Compares our name to o.toString() .<br/>
	 * If both starts with some digits the resulting
	 * ${@link java.lang.Integer} are compared.<br/>
	 * If one starts with a number and the other does not,
	 * the one starting with a number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
	 * �{@link java.lang.String} is done.     *
	 *
	 * @param o the Object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object
	 * is less than, equal to, or greater than the specified object.
	 * @throws ClassCastException if the specified object's type prevents it
	 *                            from being compared to this Object.
	 */
	public int compareNamesTo(final Object o) throws ClassCastException {

		// usually compare the qualified name
		String other = o.toString();
		String me = getQualifiedName();

		// if we have the same parent,
		// compare the unqualified name.
		// This enshures that the exception
		// for numbers is used within our parent-
		// account too and not just in the top-
		// level accounts
		if (o instanceof KMyMoneyAccount
				&&
				((KMyMoneyAccount) o).getParentAccountId() != null
				&&
				getParentAccountId() != null
				&&
				((KMyMoneyAccount) o).getParentAccountId().toString().
				equalsIgnoreCase(getParentAccountId().toString())) {
			other = ((KMyMoneyAccount) o).getName();
			me = getName();
		}

		// compare

		Long i0 = startsWithNumber(other);
		Long i1 = startsWithNumber(me);
		if (i0 == null && i1 != null) {
			return 1;
		}
		else if (i1 == null && i0 != null) {
			return -1;
		}
		else if (i0 == null) {
			return me.compareTo(other);
		}
		else if (i1 == null) {
			return me.compareTo(other);
		}
		else if (i1.equals(i0)) {
			return me.compareTo(other);
		}

		return i1.compareTo(i0);
	}

	/**
	 * Helper used in ${@link #compareTo(Object)} to
	 * compare names starting with a number.
	 *
	 * @param s the name
	 * @return the Integer build from the digits the name starts with or null
	 */
	private Long startsWithNumber(final String s) {
		int digitCount = 0;
		for (int i = 0; i < s.length()
				&&
				Character.isDigit(s.charAt(i)); i++) {
			digitCount++;
		}
		if (digitCount == 0) {
			return null;
		}
		return new Long(s.substring(0, digitCount));
	}

	//  ------------------------ support for propertyChangeListeners ------------------

	/**
	 * support for firing PropertyChangeEvents.
	 * (gets initialized only if we really have listeners)
	 */
	private volatile PropertyChangeSupport myPropertyChange = null;

	/**
	 * Returned value may be null if we never had listeners.
	 *
	 * @return Our support for firing PropertyChangeEvents
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return myPropertyChange;
	}

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(
			final PropertyChangeListener listener) {
		if (myPropertyChange == null) {
			myPropertyChange = new PropertyChangeSupport(this);
		}
		myPropertyChange.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property.  The listener
	 * will be invoked only when a call on firePropertyChange names that
	 * specific property.
	 *
	 * @param propertyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(
			final String propertyName,
			final PropertyChangeListener listener) {
		if (myPropertyChange == null) {
			myPropertyChange = new PropertyChangeSupport(this);
		}
		myPropertyChange.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The name of the property that was listened on.
	 * @param listener     The PropertyChangeListener to be removed
	 */
	public final void removePropertyChangeListener(
			final String propertyName,
			final PropertyChangeListener listener) {
		if (myPropertyChange != null) {
			myPropertyChange.removePropertyChangeListener(propertyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		if (myPropertyChange != null) {
			myPropertyChange.removePropertyChangeListener(listener);
		}
	}

}
