package org.kmymoney.read.aux;

import java.time.LocalDate;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMCurrPair;
import org.kmymoney.basetypes.KMMPriceID;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyCurrency;
import org.kmymoney.read.KMyMoneySecurity;

public interface KMMPrice {

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
    
    KMMPriceID getId() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    KMMCurrPair getParentPricePairID() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
	
    KMMPricePair getParentPricePair();
	
    // ----------------------------
    	
    KMMSecCurrID getFromSecCurrQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    KMMSecID getFromSecurityQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    KMMCurrID getFromCurrencyQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    KMyMoneySecurity getFromSecurity() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    String getFromCurrencyCode() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    KMyMoneyCurrency getFromCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;
    
    // ----------------------------

    KMMCurrID getToCurrencyQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    String getToCurrencyCode() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;

    KMyMoneyCurrency getToCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException;

    // ----------------------------

    LocalDate getDate();

    String getSource();

    FixedPointNumber getValue();
    
    String getValueFormatted() throws InvalidSecCurrTypeException, InvalidSecCurrIDException;
    
}
