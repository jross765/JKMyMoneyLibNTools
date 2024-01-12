package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.kmymoney.api.Const;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyPriceImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePrice;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshPriceImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritablePriceImpl extends KMyMoneyPriceImpl 
                                       implements KMyMoneyWritablePrice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritablePriceImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the KMyMoneyWritableObject-interface.
     */
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(this);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneyWritablePriceImpl(
	    final Price jwsdpPeer,
	    final KMyMoneyWritableFile file) {
	super(jwsdpPeer, file);
    }

    public KMyMoneyWritablePriceImpl(final KMyMoneyWritableFileImpl file) {
	super(createPrice_int(file, file.getNewPriceID()), file);
    }

    public KMyMoneyWritablePriceImpl(KMyMoneyPriceImpl prc) {
	super(prc.getJwsdpPeer(), prc.getKMyMoneyFile());
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
//  public GCshWritablePrice createWritablePrice(final GCshPrice prc) {
//	GCshWritablePriceImpl splt = new GCshWritablePriceImpl(this, prc);
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
        
        Price prc = file.createPriceType();
    
        {
            Price.PriceId gncPrcID = factory.createPricePriceId();
            gncPrcID.setType(Const.XML_DATA_TYPE_GUID);
            gncPrcID.setValue(newID.toString());
            prc.setPriceId(gncPrcID);
        }
        
        {
            Price.PriceCommodity cmdty = factory.createPricePriceCommodity();
            cmdty.setSecSpace("xxx");
            cmdty.setSecId("yyy");
            prc.setPriceCommodity(cmdty);
        }
    
        {
            Price.PriceCurrency curr = factory.createPricePriceCurrency();
            curr.setSecSpace(GCshSecCurrNameSpace.CURRENCY);
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
    public void setFromSecCurrQualifID(GCshSecCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromCommodityQualifID(GCshSecID qualifID) {
	jwsdpPeer.getPriceCommodity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setFromCommodity(KMyMoneyCommodity cmdty) {
	setFromSecCurrQualifID(cmdty.getQualifID());
    }

    @Override
    public void setFromCurrencyCode(String code) {
	setFromCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setFromCurrency(KMyMoneyCommodity curr) {
	setFromCommodity(curr);	
    }
    
    // ----------------------------

    @Override
    public void setToCurrencyQualifID(GCshSecCurrID qualifID) {
	if ( ! qualifID.getNameSpace().equals(GCshSecCurrNameSpace.CURRENCY) )
	    throw new InvalidSecCurrTypeException("Is not a currency: " + qualifID.toString());
	
	jwsdpPeer.getPriceCurrency().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setToCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCurrency().setSecSpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setSecId(qualifID.getCode());
	getWritableKMyMoneyFile().setModified(true);
    }

    @Override
    public void setToCurrencyCode(String code) {
	setToCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setToCurrency(KMyMoneyCommodity curr) {
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
