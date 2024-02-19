package org.kmymoney.api.read.impl.hlp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSpltID;
import org.kmymoney.api.basetypes.simple.KMMIDNotSetException;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a base-class that helps implementing the KMyMoneyAccount interface
 * with its extensive number of convenience-methods.<br/>
 */
public abstract class SimpleAccount implements KMyMoneyAccount {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccount.class);

	// ---------------------------------------------------------------

	private final KMyMoneyFile myFile;

	// ----------------------------

	private static NumberFormat currencyFormat = null;

	private volatile PropertyChangeSupport myPtyChg = null;

	// ---------------------------------------------------------------

	public SimpleAccount(final KMyMoneyFile myFile) {
		super();
		this.myFile = myFile;
	}

	// ---------------------------------------------------------------

	/*
	 * The returned list is sorted by the natural order of the Transaction-Splits.
	 */
	public List<KMyMoneyTransaction> getTransactions() {
		List<? extends KMyMoneyTransactionSplit> splits = getTransactionSplits();
		List<KMyMoneyTransaction> retval = new ArrayList<KMyMoneyTransaction>(splits.size());

		for ( Object element : splits ) {
			KMyMoneyTransactionSplit split = (KMyMoneyTransactionSplit) element;
			retval.add(split.getTransaction());
		}

		return retval;
	}

	public KMyMoneyFile getKMyMoneyFile() {
		return myFile;
	}

	public boolean isChildAccountRecursive(final KMyMoneyAccount account) {

		if ( this == account ) {
			return true;
		}

		for ( KMyMoneyAccount child : getChildren() ) {
			if ( this == child ) {
				return true;
			}
			if ( child.isChildAccountRecursive(account) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	/**
	 * Get name including the name of the parent accounts.
	 */
	public String getQualifiedName() {
		KMyMoneyAccount acc = getParentAccount();

		if ( acc == null || 
			 acc.getID() == getID() ) {
			KMMComplAcctID parentID = getParentAccountID();
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
//		if ( acct.getID().equals(getID()) ) {
//		    return true;
//		}
//	    }
//	    return false;

		// Instead (and just as good):
		if ( getID().equals(KMMComplAcctID.get(KMMComplAcctID.Top.ASSET)) || 
			 getID().equals(KMMComplAcctID.get(KMMComplAcctID.Top.LIABILITY)) || 
			 getID().equals(KMMComplAcctID.get(KMMComplAcctID.Top.INCOME)) || 
			 getID().equals(KMMComplAcctID.get(KMMComplAcctID.Top.EXPENSE)) || 
			 getID().equals(KMMComplAcctID.get(KMMComplAcctID.Top.EQUITY)) ) {
			return true;
		} else {
			return false;
		}
	}

	public KMyMoneyAccount getParentAccount() {
		if ( isRootAccount() )
			return null;

		KMMComplAcctID parentID = getParentAccountID();
		if ( parentID == null ) {
			return null;
		} else if ( parentID.toString().equals("") || 
				    parentID.toString().equals("(unset)") || 
				    parentID.toString().equals("(unknown)") ) {
			return null;
		}

		return getKMyMoneyFile().getAccountByID(parentID);
	}

	public Collection<KMyMoneyAccount> getSubAccounts() {
		return getChildren();
	}

	@Override
	public FixedPointNumber getBalance() {
		return getBalance(LocalDate.now());
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date) {
		return getBalance(date, (Collection<KMyMoneyTransactionSplit>) null);
	}

	/**
	 * The currency will be the one of this account.
	 */
	@Override
	public FixedPointNumber getBalance(final LocalDate date, Collection<KMyMoneyTransactionSplit> after) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( KMyMoneyTransactionSplit splt : getTransactionSplits() ) {
			if ( date != null && 
				 after != null ) {
				if ( splt.getTransaction().getDatePosted().isAfter(date) ) {
					after.add(splt);
					continue;
				}
			}
	
			// the currency of the quantity is the one of the account
			balance.add(splt.getShares());
		}
	
		return balance;
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final KMMQualifSecCurrID secCurrID)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		FixedPointNumber retval = getBalance(date);

		if ( retval == null ) {
			LOGGER.error("getBalance: Error creating balance!");
			return null;
		}

		// is conversion needed?
		if ( getQualifSecCurrID().equals(secCurrID) ) {
			return retval;
		}
	
		ComplexPriceTable priceTab = getKMyMoneyFile().getCurrencyTable();

		if ( priceTab == null ) {
			LOGGER.error("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}
	
		if ( ! priceTab.convertToBaseCurrency(retval, secCurrID) ) {
			Collection<String> currList = getKMyMoneyFile().getCurrencyTable()
					.getCurrencies(getQualifSecCurrID().getType());
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ getQualifSecCurrID().toString() + "' to the base-currency!" + " \n(we know "
					+ getKMyMoneyFile().getCurrencyTable().getNameSpaces().size() + " currency-namespaces and "
					+ (currList == null ? "no" : "" + currList.size()) + " currencies in our namespace)");
			return null;
		}
	
		if ( ! priceTab.convertFromBaseCurrency(retval, secCurrID) ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ secCurrID.toString() + "'!");
			return null;
		}
	
		return retval;
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final Currency curr)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		FixedPointNumber retval = getBalance(date);
		
		if ( retval == null ) {
			LOGGER.warn("getBalance: Error creating balance!");
			return null;
		}

		if ( curr == null ||
			 retval.equals(new FixedPointNumber()) ) {
			return retval;
		}

		// is conversion needed?
		if ( getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			if ( getQualifSecCurrID().getCode().equals(curr.getCurrencyCode()) ) {
				return retval;
			}
		}

		ComplexPriceTable priceTab = getKMyMoneyFile().getCurrencyTable();

		if ( priceTab == null ) {
			LOGGER.warn("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}

		if ( ! priceTab.convertToBaseCurrency(retval, getQualifSecCurrID()) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ getQualifSecCurrID().toString() + "' to the base-currency!");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(retval, new KMMQualifCurrID(curr)) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'!");
			return null;
		}

		return retval;
	}

	@Override
	public FixedPointNumber getBalance(final KMyMoneyTransactionSplit lastIncludesSplit) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			balance.add(split.getShares());
	
			if ( split == lastIncludesSplit ) {
				break;
			}
	
		}
	
		return balance;
	}

	public String getBalanceFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalance());
	}

	public String getBalanceFormatted(final Locale lcl) throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	
		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(getCurrency());
		return cf.format(getBalance());
	}

	public FixedPointNumber getBalanceRecursive()
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getBalanceRecursive(LocalDate.now());
	}

	public FixedPointNumber getBalanceRecursive(final LocalDate date)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getBalanceRecursive(date, getQualifSecCurrID());
	}

	public FixedPointNumber getBalanceRecursive(final LocalDate date, final KMMQualifSecCurrID secCurrID)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		// BEGIN OLD IMPL
//	    FixedPointNumber retval = getBalance(date, secCurrID);
//
//	    if (retval == null) {
//		retval = new FixedPointNumber();
//	    }
//
//	    for ( KMyMoneyAccount child : getChildren() ) {
//		retval.add(child.getBalanceRecursive(date, cmdtyCurrID));
//	    }
//
//	    return retval;
		// END OLD IMPL

		if ( secCurrID.getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			return getBalanceRecursive(date, new KMMQualifCurrID(secCurrID.getCode()).getCurrency());
		}
		else {
//			return new FixedPointNumber(999999).negate();
			return getBalance(date, secCurrID); // CAUTION: This assumes that under a stock account,
												// there are no children (which sounds sensible,
												// but there might be special cases)
		}
	}

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {

		FixedPointNumber retval = getBalance(date, curr);

		if ( retval == null ) {
			retval = new FixedPointNumber();
		}

		// CAUTION: As opposed to the sister project JGnucashLib, the following three lines 
		// work for read-branch (KMyMoneyAccountImpl) but *not* for write-branch 
		// (KMyMoneyWritableAccountImpl). Don'nt know why, can't explain it...
//		for ( KMyMoneyAccount child : getChildren() ) {
//			retval.add(child.getBalanceRecursive(date, curr));
//		}

		// So here is another implementation which works for both read- and write-branch:
		for ( KMyMoneyAccount child : getChildrenRecursive() ) {
			retval.add( child.getBalance(date, curr) );
		}

		return retval;
	}

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final KMMSecID secID)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException, KMMIDNotSetException {
		// CAUTION: This assumes that under a stock account,
		// there are no children (which sounds sensible,
		// but there might be special cases)
		return getBalance(date, new KMMQualifSecCurrID(KMMQualifSecCurrID.Type.SECURITY, secID.get())); 
	}

	@Override
	public String getBalanceRecursiveFormatted()
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive());
	}

	@Override
	public String getBalanceRecursiveFormatted(final LocalDate date)
			throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive(date));
	}

	@Override
	public KMyMoneyTransactionSplit getLastSplitBeforeRecursive(final LocalDate date) {
	
		KMyMoneyTransactionSplit lastSplit = null;
	
		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			if ( date == null || 
				 split.getTransaction().getDatePosted()
				 	.isBefore(date) ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		for ( Iterator<KMyMoneyAccount> iter = getSubAccounts().iterator(); iter.hasNext(); ) {
			KMyMoneyAccount account = (KMyMoneyAccount) iter.next();
			KMyMoneyTransactionSplit split = account.getLastSplitBeforeRecursive(date);
			if ( split != null && 
				 split.getTransaction() != null ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		return lastSplit;
	}

	@Override
	public boolean hasTransactions() {
		return this.getTransactionSplits().size() > 0;
	}

	@Override
	public boolean hasTransactionsRecursive() {
		if ( this.hasTransactions() ) {
			return true;
		}

		for ( KMyMoneyAccount child : getChildren() ) {
			if ( child.hasTransactionsRecursive() ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return null if we are no currency but e.g. a fund
	 * @throws InvalidQualifSecCurrIDException
	 * @throws InvalidQualifSecCurrTypeException
	 */
	public Currency getCurrency() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		if ( getQualifSecCurrID().getType() != KMMQualifSecCurrID.Type.CURRENCY ) {
			return null;
		}

		String kmmCurrID = getQualifSecCurrID().getCode();
		return Currency.getInstance(kmmCurrID);
	}

	public NumberFormat getCurrencyFormat() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		// the currency may have changed
		if ( getQualifSecCurrID().getType() == KMMQualifSecCurrID.Type.CURRENCY ) {
			Currency currency = getCurrency();
			currencyFormat.setCurrency(currency);
		} else {
			currencyFormat = NumberFormat.getNumberInstance();
		}

		return currencyFormat;
	}

	public KMyMoneyTransactionSplit getTransactionSplitByID(final KMMQualifSpltID id) {
		if ( id == null ) {
			throw new IllegalArgumentException("null id given!");
		}

		// ::TODO
//		if ( ! id.isSet() ) {
//			throw new IllegalArgumentException("ID not set!");
//		}

		for ( KMyMoneyTransactionSplit split : getTransactionSplits() ) {
			if ( id.equals(split.getID()) ) {
				return split;
			}

		}

		return null;
	}

	/*
	 * This is an extension to ${@link #compareNamesTo(Object)} that makes sure that
	 * NEVER 2 accounts with different IDs compare to 0. Compares our name to
	 * o.toString() .<br/>
	 * If both starts with some digits the resulting ${@link java.lang.Integer} are
	 * compared.<br/>
	 * If one starts with a number and the other does not, the one starting with a
	 * number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
	 * ${@link java.lang.String} is done.
	 */
	@Override
	public int compareTo(final KMyMoneyAccount otherAcc) {

		int i = compareNamesTo(otherAcc);
		if ( i != 0 ) {
			return i;
		}

		KMyMoneyAccount other = otherAcc;
		i = other.getID().toString().compareTo(getID().toString());
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherAcc.hashCode());

	}

	/*
	 * Compares our name to o.toString() .<br/>
	 * If both starts with some digits the resulting ${@link java.lang.Integer} are
	 * compared.<br/>
	 * If one starts with a number and the other does not, the one starting with a
	 * number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
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
		if ( o instanceof KMyMoneyAccount && 
				((KMyMoneyAccount) o).getParentAccountID() != null && 
				getParentAccountID() != null && 
				((KMyMoneyAccount) o).getParentAccountID().toString()
						.equalsIgnoreCase(getParentAccountID().toString()) ) {
			other = ((KMyMoneyAccount) o).getName();
			me = getName();
		}

		// compare

		Long i0 = startsWithNumber(other);
		Long i1 = startsWithNumber(me);
		if ( i0 == null && i1 != null ) {
			return 1;
		} else if ( i1 == null && i0 != null ) {
			return -1;
		} else if ( i0 == null ) {
			return me.compareTo(other);
		} else if ( i1 == null ) {
			return me.compareTo(other);
		} else if ( i1.equals(i0) ) {
			return me.compareTo(other);
		}

		return i1.compareTo(i0);
	}

	/*
	 * Helper used in ${@link #compareTo(Object)} to compare names starting with a
	 * number.
	 */
	private Long startsWithNumber(final String s) {
		int digitCount = 0;
		for ( int i = 0; i < s.length() && Character.isDigit(s.charAt(i)); i++ ) {
			digitCount++;
		}
		if ( digitCount == 0 ) {
			return null;
		}
		return Long.valueOf(s.substring(0, digitCount));
	}

	// ------------------------ support for propertyChangeListeners

	protected PropertyChangeSupport getPropertyChangeSupport() {
		return myPtyChg;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 *
	 * @param propertyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The name of the property that was listened on.
	 * @param listener     The PropertyChangeListener to be removed
	 */
	public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(propertyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(listener);
		}
	}

}
