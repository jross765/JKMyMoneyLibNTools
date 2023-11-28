package org.kmymoney.api.read.impl.aux;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMPriceID;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.aux.KMMPrice;
import org.kmymoney.api.read.aux.KMMPricePair;
import org.kmymoney.generated.PRICE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMMPriceImpl implements KMMPrice {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMMPriceImpl.class);

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // -----------------------------------------------------------

    private final PRICE jwsdpPeer;

    private final KMyMoneyFile file;

    // -----------------------------------------------------------
    
    private KMMPricePair parent = null;

    /**
     * The currency-format to use for formatting.<br/>
     */
    private NumberFormat currencyFormat = null;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public KMMPriceImpl(final KMMPricePair parent, final PRICE newPeer, final KMyMoneyFile file) {
	super();
		
	this.parent    = parent;
	this.jwsdpPeer = newPeer;
	this.file      = file;
    }

    // -----------------------------------------------------------
    
    @Override
    public KMMPriceID getId() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return new KMMPriceID(parent.getFromSecCurrStr(),
		              parent.getToCurrStr(),
		              DATE_FORMAT.format(getDate()));
    }

    @Override
    public KMMCurrPair getParentPricePairID() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	return parent.getId();
    }

    @Override
    public KMMPricePair getParentPricePair() {
	return parent;
    }

    // ----------------------------
    
    @Override
    public KMMQualifSecCurrID getFromSecCurrQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	String secCurrID = parent.getFromSecCurrStr();

	KMMQualifSecCurrID result = null;
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
	    result = new KMMQualifSecID(secCurrID);
	} else {
	    result = new KMMQualifCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public KMMQualifSecID getFromSecurityQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	KMMQualifSecCurrID secCurrID = getFromSecCurrQualifId();
	if ( secCurrID.getType() != KMMQualifSecCurrID.Type.SECURITY )
	    throw new InvalidQualifSecCurrTypeException();
	    
	return new KMMQualifSecID(secCurrID);
    }

    @Override
    public KMMQualifCurrID getFromCurrencyQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	KMMQualifSecCurrID secCurrID = getFromSecCurrQualifId();
	if ( secCurrID.getType() != KMMQualifSecCurrID.Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException();

	return new KMMQualifCurrID(secCurrID);
    }

    @Override
    public KMyMoneySecurity getFromSecurity() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifSecID secID = getFromSecurityQualifId();
	
	KMyMoneySecurity cmdty = file.getSecurityByQualifID(secID);
	
	return cmdty;
    }
    
    @Override
    public String getFromCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getFromCurrencyQualifId().getCurrency().getCurrencyCode();
    }

    @Override
    public KMyMoneyCurrency getFromCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifCurrID currID = getFromCurrencyQualifId();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifId(currID);
	
	return curr;
    }
    
    // ----------------------------
    
    @Override
    public KMMQualifCurrID getToCurrencyQualifId() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	String secCurrID = parent.getToCurrStr();

	KMMQualifCurrID result = null;
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
	    throw new InvalidQualifSecCurrTypeException();
	} else {
	    result = new KMMQualifCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public String getToCurrencyCode() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getToCurrencyQualifId().getCode();
    }

    @Override
    public KMyMoneyCurrency getToCurrency() throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
	KMMQualifCurrID currID = getToCurrencyQualifId();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifId(currID);
	
	return curr;
    }

    // ----------------------------
    
    /**
     * @return The currency-format to use for formating.
     * @throws InvalidQualifSecCurrTypeException 
     * @throws InvalidQualifSecCurrIDException 
     */
    private NumberFormat getCurrencyFormat() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	}

//	// the currency may have changed
//	if ( ! getCurrencyQualifId().getType().equals(SecurityCurrID.Type.CURRENCY) )
//	    throw new InvalidSecCurrTypeException();
	    
	Currency currency = Currency.getInstance(getToCurrencyCode());
	currencyFormat.setCurrency(currency);

	return currencyFormat;
    }

    @Override
    public LocalDate getDate() {
	if ( jwsdpPeer.getDate() == null )
	    return null;
	
	XMLGregorianCalendar cal = jwsdpPeer.getDate();
	try {
	    return LocalDate.of(cal.getYear(), cal.getMonth(), cal.getDay());
	} catch (Exception e) {
	    IllegalStateException ex = new IllegalStateException("unparsable date '" + cal + "' in price!");
	    ex.initCause(e);
	    throw ex;
	}
    }

    @Override
    public String getSource() {
	if ( jwsdpPeer.getSource() == null )
	    return null;
	
	return jwsdpPeer.getSource();
    }

    @Override
    public FixedPointNumber getValue() {
	if ( jwsdpPeer.getPrice() == null )
	    return null;
	
	return new FixedPointNumber(jwsdpPeer.getPrice());
    }

    @Override
    public String getValueFormatted() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
	return getCurrencyFormat().format(getValue());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	String result = "KMMPriceImpl [";
	
	try {
	    result += "id='" + getId() + "'";
	} catch (Exception e) {
	    result += "id=" + "ERROR";
	}
	
	try {
	    result += ", from-sec-curr-qualif-id='" + getFromSecCurrQualifId() + "'";
	} catch (Exception e) {
	    result += ", from-sec-curr-qualif-id=" + "ERROR";
	}
	
	try {
	    result += ", to-curr-qualif-id='" + getToCurrencyQualifId() + "'";
	} catch (Exception e) {
	    result += ", to-curr-qualif-id=" + "ERROR";
	}
	
	result += ", date=" + getDate(); 
	result += ", source='" + getSource() + "'"; 
	
	try {
	    result += ", value=" + getValueFormatted() + "]";
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    result += ", value=" + "ERROR" + "]";
	}
	
	return result;
    }

}
