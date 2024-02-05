package org.kmymoney.api.write;

import java.beans.PropertyChangeListener;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransactionSplit;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.read.IllegalTransactionSplitActionException;

/**
 * Transaction-Split that can be modified<br/>
 * For propertyChange we support the properties "value", "shares"
 * "description",  "splitAction" and "accountID".
 */
public interface KMyMoneyWritableTransactionSplit extends KMyMoneyTransactionSplit,
                                                          KMyMoneyWritableObject
{

	/**
	 * @return the transaction this is a split of.
	 */
	KMyMoneyWritableTransaction getTransaction();

	/**
	 * Remove this split from the system.
	 */
	void remove();

	/**
	 * Does not convert the quantity to another currency if the new account has
	 * another one then the old!
	 * 
	 * @param acctID the new account to give this money to/take it from.
	 */
	void setAccountID(KMMComplAcctID acctID);

	/**
	 * Does not convert the quantity to another currency if the new account has
	 * another one then the old!
	 * 
	 * @param acct the new account to give this money to/take it from.
	 */
	void setAccount(KMyMoneyAccount acct);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setShares(FixedPointNumber)}.
	 * 
	 * @param n the new quantity (in the currency of the account)
	 */
	void setShares(String n);

	/**
	 * Same as ${@link #setShares(String)}.
	 * 
	 * @param n the new quantity (in the currency of the account)
	 */
	void setSharesFormattedForHTML(String n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setShares(FixedPointNumber)}.
	 * 
	 * @param n the new quantity (in the currency of the account)
	 */
	void setShares(FixedPointNumber n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * 
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValue(String n);

	/**
	 * Same as ${@link #setValue(String)}.
	 * 
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValueFormattedForHTML(String n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * 
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValue(FixedPointNumber n);

	/**
	 * Set the description-text.
	 * 
	 * @param desc the new description
	 */
	void setDescription(String desc);

	/**
	 * Set the type of association this split has with an invoice's lot.
	 * 
	 * @param action null, or one of the ACTION_xyz values defined
	 * @throws IllegalTransactionSplitActionException
	 */
	void setAction(Action act) throws IllegalTransactionSplitActionException;

	/**
	 * Set the type of association this split has with an invoice's lot.
	 * 
	 * @param action null, or one of the ACTION_xyz values defined
	 * @throws IllegalTransactionSplitActionException
	 */
	// void setActionStr(String actStr) throws IllegalTransactionSplitActionException;

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 *
	 * @param ptyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	void addPropertyChangeListener(String ptyName, PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The name of the property that was listened on.
	 * @param listener     The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	void removePropertyChangeListener(String ptyName, PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	void removePropertyChangeListener(PropertyChangeListener listener);
}
