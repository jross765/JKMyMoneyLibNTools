package org.kmymoney.read.aux;

import java.time.LocalDate;

import org.kmymoney.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.basetypes.complex.KMMCurrPair;
import org.kmymoney.basetypes.complex.KMMPriceID;
import org.kmymoney.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.basetypes.complex.KMMQualifSecID;
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
    
    KMMPriceID getId() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    KMMCurrPair getParentPricePairID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
	
    KMMPricePair getParentPricePair();
	
    // ----------------------------
    	
    KMMQualifSecCurrID getFromSecCurrQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMMQualifSecID getFromSecurityQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMMQualifCurrID getFromCurrencyQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneySecurity getFromSecurity() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    String getFromCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneyCurrency getFromCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;
    
    // ----------------------------

    KMMQualifCurrID getToCurrencyQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    String getToCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;

    KMyMoneyCurrency getToCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException;

    // ----------------------------

    LocalDate getDate();

    String getSource();

    FixedPointNumber getValue();
    
    String getValueFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException;
    
}
