package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMMPriceImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritablePricePairImpl extends KMyMoneyPricePairImpl 
                                           implements KMyMoneyWritablePricePair 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritablePricePairImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the KMyMoneyWritableObject-interface.
     */
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(this);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneyWritablePricePairImpl(
    		final PRICEPAIR jwsdpPeer,
    		final KMyMoneyWritableFile file) {
    	super(jwsdpPeer, file);
    }

    public KMyMoneyWritablePricePairImpl(final KMyMoneyWritableFileImpl file) {
	super(createPrice_int(file, file.getNewPriceID()), file);
    }

    public KMyMoneyWritablePricePairImpl(KMyMoneyPricePairImpl prcPair) {
	super(prcPair.getJwsdpPeer(), prcPair.getKMyMoneyFile());
    }

    // ---------------------------------------------------------------

    /**
     * The kmymoney-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    /**
     * The kmymoney-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    // ---------------------------------------------------------------
    
    // ::TODO
//  public KMMWritablePrice createWritablePrice(final KMMPrice prc) {
//	KMMWritablePriceImpl splt = new KMMWritablePriceImpl(this, prc);
//	addPrice(splt);
//	return splt;
//  }

    private static PRICE createPrice_int(
	    final KMyMoneyWritableFileImpl file, 
	    final KMMID newID) {
	
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("empty ID given");
		}
        ObjectFactory factory = file.getObjectFactory();
        
        PRICE prc = file.createPriceType();
    
        {
            Price.PriceId gncPrcID = factory.createPricePriceId();
            gncPrcID.setType(Const.XML_DATA_TYPE_GUID);
            gncPrcID.setValue(newID.toString());
            prc.setPriceId(gncPrcID);
        }
        
        {
            Price.PriceSecurity cmdty = factory.createPricePriceSecurity();
            cmdty.setSecSpace("xxx");
            cmdty.setSecId("yyy");
            prc.setPriceSecurity(cmdty);
        }
    
        {
            Price.PriceCurrency curr = factory.createPricePriceCurrency();
            curr.setSecSpace(KMMSecCurrNameSpace.CURRENCY);
            curr.setSecId(file.getDefaultCurrencyID());
            prc.setPriceCurrency(curr);
        }
        
        {
            Price.PriceTime prcTim = factory.createPricePriceTime();
            LocalDate tsDate = LocalDate.now(); // ::TODO
            prcTim.setTsDate(tsDate.toString());
            prc.setPriceTime(prcTim);
        }
        
        prc.setPriceType(Type.LAST.getCode());
        prc.setPriceSource(Source.USER_PRICE.getCode());
        prc.setPriceValue("1");
        
        // file.getRootElement().getGncBook().getBookElements().add(prc);
        GncPricedb priceDB = file.getPrcMgr().getPriceDB();
	priceDB.getPrice().add(prc);
        file.setModified(true);
    
        return prc;
    }

    // ---------------------------------------------------------------

    @Override
    public void setFromSecCurrQualifID(KMMSecCurrID qualifID) {
	jwsdpPeer.getPriceSecurity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceSecurity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromSecurityQualifID(GKSecID qualifID) {
	jwsdpPeer.getPriceSecurity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceSecurity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromCurrencyQualifID(KMMCurrID qualifID) {
	jwsdpPeer.getPriceSecurity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceSecurity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromSecurity(KMyMoneySecurity cmdty) {
	setFromSecCurrQualifID(cmdty.getQualifID());
    }

    @Override
    public void setFromCurrencyCode(String code) {
	setFromCurrencyQualifID(new KMMCurrID(code));
    }

    @Override
    public void setFromCurrency(KMyMoneySecurity curr) {
	setFromSecurity(curr);	
    }
    
    // ----------------------------

    @Override
    public void setToCurrencyQualifID(KMMQualifSecCurrID qualifID) {
    	if ( qualifID == 0 )
    	    throw new IllegalArgumentException("null ID given");
    	
	if ( qualifID.getType() != KMMQualifSecCurrID.Type.CURRENCY )
	    throw new InvalidQualifSecCurrTypeException("Is not a currency: " + qualifID.toString());
	
	jwsdpPeer.getPriceCurrency().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setToCurrencyQualifID(KMMQualifCurrID qualifID) {
	jwsdpPeer.getPriceCurrency().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setToCurrencyCode(String code) {
	setToCurrencyQualifID(new KMMQualifCurrID(code));
    }

    @Override
    public void setToCurrency(KMyMoneyCurrency curr) {
	setToCurrencyQualifID(curr.getQualifID());
    }
    
    // ----------------------------

    @Override
    public void setDate(LocalDate date) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableKMyMoneyFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, date);
	}
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableKMyMoneyFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, dateTime);
	}
    }

    @Override
    public void setSource(Source src) {
	setSourceStr(src.getCode());
    }

    public void setSourceStr(String str) {
	jwsdpPeer.setPriceSource(str);
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setType(Type type) {
	setTypeStr(type.getCode());
    }

    public void setTypeStr(String typeStr) {
	jwsdpPeer.setPriceType(typeStr);
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setValue(FixedPointNumber val) {
	jwsdpPeer.setPriceValue(val.toKMyMoneyString());
	getWritableKMyMoneyFile().setModified(true);
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	String result = "KMyMoneyWritablePriceImpl [";
	
	try {
	    result += "id='" + getID() + "'";
	} catch (Exception e) {
	    result += "id=" + "ERROR";
	}
	
	try {
	    result += ", from-sec-curr-qualif-id='" + getFromSecCurrQualifID() + "'";
	} catch (Exception e) {
	    result += ", from-sec-curr-qualif-id=" + "ERROR";
	}
	
	try {
	    result += ", to-curr-qualif-id='" + getToCurrencyQualifID() + "'";
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
