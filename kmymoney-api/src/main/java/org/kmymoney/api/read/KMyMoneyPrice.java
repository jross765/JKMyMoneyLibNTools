package org.kmymoney.api.read;

import java.time.LocalDate;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.numbers.FixedPointNumber;

public interface KMyMoneyPrice extends KMyMoneyPricePairCore {

    // ::TODO: these are sitll Gnucash values
    // Cf. https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gnc-pricedb.h
    public enum Source {
	EDIT_DLG,         // "user:price-editor"
	FQ,               // "Finance::Quote"
	USER_PRICE,       // "user:price"
	XFER_DLG_VAL,     // "user:xfer-dialog"
	SPLIT_REG,        // "user:split-register"
	SPLIT_IMPORT,     // "user:split-import"
	STOCK_SPLIT,      // "user:stock-split"
	STOCK_TRANSACTION,// "user:stock-transaction"
	INVOICE,          // "user:invoice-post"
	TEMP,             // "temporary"
	INVALID,          // "invalid"    
    }
	
    // ---------------------------------------------------------------
    
    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMMPriceID getID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    /**
     * @return
     * @throws InvalidQualifSecCurrIDException
     * @throws InvalidQualifSecCurrTypeException
     */
    KMMCurrPair getParentPricePairID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
	
    /**
     * @return
     */
    KMyMoneyPricePair getParentPricePair();
	
    // ----------------------------

    /**
     * @return
     */
    LocalDate getDate();

    /**
     * @return
     */
    String getSource();

    /**
     * @return
     */
    FixedPointNumber getValue();
    
    /**
     * @return
     * @throws InvalidQualifSecCurrTypeException
     * @throws InvalidQualifSecCurrIDException
     */
    String getValueFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
