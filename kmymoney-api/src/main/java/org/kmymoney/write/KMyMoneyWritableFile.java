package org.kmymoney.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.generated.GncV2;
import org.kmymoney.generated.KMYMONEYFILE;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyAccount;
import org.kmymoney.read.KMyMoneyCustomer;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyGenerJob;
import org.kmymoney.read.KMyMoneyVendor;
import org.kmymoney.read.spec.WrongInvoiceTypeException;
import org.kmymoney.write.spec.KMyMoneyWritableCustomerInvoice;
import org.kmymoney.write.spec.KMyMoneyWritableCustomerJob;
import org.kmymoney.write.spec.KMyMoneyWritableJobInvoice;
import org.kmymoney.write.spec.KMyMoneyWritableVendorBill;
import org.kmymoney.write.spec.KMyMoneyWritableVendorJob;
import org.kmymoneyread.impl.aux.WrongOwnerTypeException;

/**
 * Extension of GnucashFile that allows writing. <br/>
 * All the instances for accounts,... it returns can be assumed
 * to implement the respetive *Writable-interfaces.
 *
 * @see KMyMoneyFile
 * @see org.kmymoney.write.impl.KMyMoneyWritableFileImpl
 */
public interface KMyMoneyWritableFile extends KMyMoneyFile, 
                                             KMyMoneyWritableObject 
{

    /**
     * @return true if this file has been modified.
     */
    boolean isModified();

    /**
     * The value is guaranteed not to be bigger then the maximum of the current
     * system-time and the modification-time in the file at the time of the last
     * (full) read or sucessfull write.<br/ It is thus suitable to detect if the
     * file has been modified outside of this library
     * 
     * @return the time in ms (compatible with File.lastModified) of the last
     *         write-operation
     */
    long getLastWriteTime();

    /**
     * @param pB true if this file has been modified.
     * @see {@link #isModified()}
     */
    void setModified(boolean pB);

    /**
     * Write the data to the given file. That file becomes the new file returned by
     * {@link KMyMoneyFile#getKMyMoneyFile()}
     * 
     * @param file the file to write to
     * @throws IOException kn io-poblems
     */
    void writeFile(File file) throws IOException;

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    KMYMONEYFILE getRootElement();

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    KMyMoneyWritablePayee getPayeeByID(String id);

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     */
    Collection<? extends KMyMoneyWritableAccount> getWritableRootAccounts();

    /**
     *
     * @return a read-only collection of all accounts
     */
    Collection<? extends KMyMoneyWritableAccount> getWritableAccounts();

    /**
     * @see KMyMoneyFile#getTransactionByID(String)
     * @return A changable version of the transaction.
     */
    KMyMoneyWritableTransaction getTransactionByID(String id);

    /**
     * @see KMyMoneyFile#getGenerInvoiceByID(String)
     * @param id the id to look for
     * @return A changable version of the invoice.
     */
    KMyMoneyWritableGenerInvoice getGenerInvoiceByID(String id);

    /**
     * @see KMyMoneyFile#getAccountByName(String)
     * @param name the name to look for
     * @return A changable version of the account.
     */
    KMyMoneyWritableAccount getAccountByName(String name);

    /**
     * @param type the type to look for
     * @return A changable version of all accounts of that type.
     */
    Collection<KMyMoneyWritableAccount> getAccountsByType(String type);

    /**
     * @see KMyMoneyFile#getAccountByID(String)
     * @param id the id of the account to fetch
     * @return A changable version of the account or null of not found.
     */
    KMyMoneyWritableAccount getAccountByID(String id);

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     * 
     * @param pCmdtySpace        the namespace (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
	    final int pCmdtyNameFraction, final String pCmdtyName);

    /**
     * @see KMyMoneyFile#getTransactions()
     * @return writable versions of all transactions in the book.
     */
    Collection<? extends KMyMoneyWritableTransaction> getWritableTransactions();

    /**
     * @return a new transaction with no splits that is already added to this file
     */
    KMyMoneyWritableTransaction createWritableTransaction();

    /**
     *
     * @param impl the transaction to remove.
     */
    void removeTransaction(KMyMoneyWritableTransaction impl);

    // ---------------------------------------------------------------

    /**
     * @return a new customer with no values that is already added to this file
     */
    KMyMoneyWritablePayee createWritablePayee();

    // ---------------------------------------------------------------

    /**
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    KMyMoneyWritableAccount createWritableAccount();

    // -----------------------------------------------------------

    /**
     * FOR USE BY EXTENSIONS ONLY
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongOwnerTypeException 
     */
    KMyMoneyWritableCustomerInvoice createWritableCustomerInvoice(
	    final String invoiceNumber, 
	    final KMyMoneyCustomer cust,
	    final KMyMoneyAccount incomeAcct,
	    final KMyMoneyAccount receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException;

    // -----------------------------------------------------------

    /**
     * @param impl the account to remove
     */
    void removeAccount(KMyMoneyWritableAccount impl);

}
