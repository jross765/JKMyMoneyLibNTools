package org.kmymoney.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.KMyMoneyTag;
import org.kmymoney.api.read.KMyMoneyTransaction;
import org.kmymoney.api.write.hlp.HasWritableUserDefinedAttributes;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.base.basetypes.simple.KMMSpltID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

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
	 * The KMyMoney file is the top-level class to contain everything.
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
	KMyMoneyWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException;

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getSecondSplit()
	 */
	KMyMoneyWritableTransactionSplit getWritableSecondSplit() throws TransactionSplitNotFoundException;

	/**
	 * @return 
	 * @see KMyMoneyTransaction#getSplitByID(KMMSpltID)
	 */
	KMyMoneyWritableTransactionSplit getWritableSplitByID(KMMSpltID id);

	/**
	 *
	 * @return the first split of this transaction or null.
	 */
	KMyMoneyWritableTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException;

	/**
	 * @return the second split of this transaction or null.
	 */
	KMyMoneyWritableTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException;

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

	KMyMoneyWritableTransactionSplit createWritableSplit(KMyMoneyAccount account, 
														 KMyMoneyPayee pye,
														 Collection<KMyMoneyTag> tagList);

	/**
	 * Removes the given split from this transaction.
	 * 
	 * @param impl the split to remove from this transaction
	 */
	void remove(KMyMoneyWritableTransactionSplit impl);

	/**
	 * remove this transaction.
	 */
	void remove();

}
