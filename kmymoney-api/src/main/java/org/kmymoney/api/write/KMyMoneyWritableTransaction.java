package org.kmymoney.api.write;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.read.SplitNotFoundException;
import org.kmymoney.api.write.hlp.HasWritableUserDefinedAttributes;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties: "description" and
 * "splits".
 */
public interface KMyMoneyWritableTransaction extends KMyMoneyTransaction,
                                                     KMyMoneyWritableObject,
                                                     HasWritableUserDefinedAttributes
{

	/**
	 * @param id the new currency
	 * @see #setCurrencyNameSpace(String)
	 * @see {@link KMyMoneyTransaction#getCurrencyID()}
	 */
	void setCurrencyID(String id);

	/**
	 * @param id the new namespace
	 * @see {@link KMyMoneyTransaction#getCurrencyNameSpace()}
	 */
	void setCurrencyNameSpace(String id);

	/**
	 * The gnucash-file is the top-level class to contain everything.
	 * 
	 * @return the file we are associated with
	 */
	KMyMoneyWritableFile getWritableFile();

	/**
	 * @param dateEntered the day (time is ignored) that this transaction has been
	 *                    entered into the system
	 * @see {@link #setDatePosted(LocalDateTime)}
	 */
	void setDateEntered(LocalDate dateEntered);

	/**
	 * @param datePosted the day (time is ignored) that the money was transfered
	 * @see {@link #setDateEntered(LocalDateTime)}
	 */
	void setDatePosted(LocalDate datePosted);

	void setDescription(String desc);

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getFirstSplit()
	 */
	KMyMoneyWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException;

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getSecondSplit()
	 */
	KMyMoneyWritableTransactionSplit getWritableSecondSplit() throws SplitNotFoundException;

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getSplitByID(String)
	 */
	KMyMoneyWritableTransactionSplit getWritableSplitByID(String id);

	/**
	 *
	 * @return the first split of this transaction or null.
	 */
	KMyMoneyWritableTransactionSplit getFirstSplit() throws SplitNotFoundException;

	/**
	 * @return the second split of this transaction or null.
	 */
	KMyMoneyWritableTransactionSplit getSecondSplit() throws SplitNotFoundException;

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getSplits()
	 */
	Collection<? extends KMyMoneyWritableTransactionSplit> getWritableSplits();

	/**
	 * Create a new split, already atached to this transaction.
	 * 
	 * @param account the account for the new split
	 * @return a new split, already atached to this transaction
	 */
	KMyMoneyWritableTransactionSplit createWritableSplit(KMyMoneyAccount account);

	/**
	 * Also removes the split from it's account.
	 * 
	 * @param impl the split to remove from this transaction
	 */
	void remove(KMyMoneyWritableTransactionSplit impl);

	/**
	 * remove this transaction.
	 */
	void remove();

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
	 * @param propertyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The name of the property that was listened on.
	 * @param listener     The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	void removePropertyChangeListener(PropertyChangeListener listener);

}
