package org.kmymoney.read.impl.aux;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;

import javax.xml.datatype.XMLGregorianCalendar;

import org.kmymoney.Const;
import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.basetypes.KMMCurrPair;
import org.kmymoney.basetypes.KMMPriceID;
import org.kmymoney.basetypes.KMMSecCurrID;
import org.kmymoney.basetypes.KMMSecID;
import org.kmymoney.generated.PRICE;
import org.kmymoney.numbers.FixedPointNumber;
import org.kmymoney.read.KMyMoneyCurrency;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneySecurity;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.aux.KMMPricePair;
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
    public KMMPriceID getId() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	return new KMMPriceID(parent.getFromSecCurrStr(),
		              parent.getToCurrStr(),
		              DATE_FORMAT.format(getDate()));
    }

    @Override
    public KMMCurrPair getParentPricePairID() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	return parent.getId();
    }

    @Override
    public KMMPricePair getParentPricePair() {
	return parent;
    }

    // ----------------------------
    
    @Override
    public KMMSecCurrID getFromSecCurrQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	String secCurrID = parent.getFromSecCurrStr();

	KMMSecCurrID result = null;
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
	    result = new KMMSecID(secCurrID);
	} else {
	    result = new KMMCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public KMMSecID getFromSecurityQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	KMMSecCurrID secCurrID = getFromSecCurrQualifId();
	if ( secCurrID.getType() != KMMSecCurrID.Type.SECURITY )
	    throw new InvalidSecCurrTypeException();
	    
	return new KMMSecID(secCurrID);
    }

    @Override
    public KMMCurrID getFromCurrencyQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	KMMSecCurrID secCurrID = getFromSecCurrQualifId();
	if ( secCurrID.getType() != KMMSecCurrID.Type.CURRENCY )
	    throw new InvalidSecCurrTypeException();

	return new KMMCurrID(secCurrID);
    }

    @Override
    public KMyMoneySecurity getFromSecurity() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	KMMSecID secID = getFromSecurityQualifId();
	
	KMyMoneySecurity cmdty = file.getSecurityByQualifID(secID);
	
	return cmdty;
    }
    
    @Override
    public String getFromCurrencyCode() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	return getFromCurrencyQualifId().getCurrency().getCurrencyCode();
    }

    @Override
    public KMyMoneyCurrency getFromCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	KMMCurrID currID = getFromCurrencyQualifId();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifId(currID);
	
	return curr;
    }
    
    // ----------------------------
    
    @Override
    public KMMCurrID getToCurrencyQualifId() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	String secCurrID = parent.getToCurrStr();

	KMMCurrID result = null;
	if ( secCurrID.startsWith("E0") ) { // ::MAGIC
	    throw new InvalidSecCurrTypeException();
	} else {
	    result = new KMMCurrID(secCurrID);
	}
	    
	return result;
    }

    @Override
    public String getToCurrencyCode() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	return getToCurrencyQualifId().getCode();
    }

    @Override
    public KMyMoneyCurrency getToCurrency() throws InvalidSecCurrIDException, InvalidSecCurrTypeException {
	KMMCurrID currID = getToCurrencyQualifId();
	
	KMyMoneyCurrency curr = file.getCurrencyByQualifId(currID);
	
	return curr;
    }

    // ----------------------------
    
    /**
     * @return The currency-format to use for formating.
     * @throws InvalidSecCurrTypeException 
     * @throws InvalidSecCurrIDException 
     */
    private NumberFormat getCurrencyFormat() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
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
    public String getValueFormatted() throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
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
