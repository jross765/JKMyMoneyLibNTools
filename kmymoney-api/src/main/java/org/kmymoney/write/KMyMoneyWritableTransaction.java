package org.kmymoney.write;

import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyObject;
import org.kmymoney.read.KMyMoneyTransaction;
import org.kmymoney.read.impl.SplitNotFoundException;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
 */
public interface KMyMoneyWritableTransaction extends KMyMoneyTransaction {

    /**
     * @param id the new currency
     * @see #setCurrencyNameSpace(String)
     * @see {@link KMyMoneyTransaction#getCurrencyID()}
     */
    void setCurrencyID(final String id);

    /**
     * @param id the new namespace
     * @see {@link KMyMoneyTransaction#getCurrencyNameSpace()}
     */
    void setCurrencyNameSpace(final String id);

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    KMyMoneyWritableFile getWritingFile();

    /**
     * @param dateEntered the day (time is ignored) that this transaction has been
     *                    entered into the system
     * @see {@link #setDatePosted(LocalDateTime)}
     */
    void setDateEntered(final LocalDateTime dateEntered); // sic, not LocalDate

    /**
     * @param datePosted the day (time is ignored) that the money was transfered
     * @see {@link #setDateEntered(LocalDateTime)}
     */
    void setDatePosted(final LocalDate datePosted);

    void setDescription(final String desc);

    void setNumber(String string);

    /**
     * @see KMyMoneyTransaction#getFirstSplit()
     */
    KMyMoneyWritableTransactionSplit getWritingFirstSplit() throws SplitNotFoundException;

    /**
     * @see KMyMoneyTransaction#getSecondSplit()
     */
    KMyMoneyWritableTransactionSplit getWritingSecondSplit() throws SplitNotFoundException;

    /**
     * @see KMyMoneyTransaction#getSplitByID(String)
     */
    KMyMoneyWritableTransactionSplit getWritingSplitByID(String id);

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
     * @see KMyMoneyTransaction#getSplits()
     */
    Collection<? extends KMyMoneyWritableTransactionSplit> getWritingSplits();

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     */
    KMyMoneyWritableTransactionSplit createWritingSplit(KMyMoneyAccount account);

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
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be
     * invoked only when a call on firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     */
    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);

}
