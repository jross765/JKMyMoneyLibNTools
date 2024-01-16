package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMCurrPair;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMID;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PRICE;
import org.kmymoney.api.generated.PRICEPAIR;
import org.kmymoney.api.generated.PRICES;
import org.kmymoney.api.numbers.FixedPointNumber;
import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPricePair;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyPricePairImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePricePair;
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
    	super(createPricePair_int(file), file);
    }

    public KMyMoneyWritablePricePairImpl(KMyMoneyPricePairImpl prcPair) {
    	super(prcPair.getJwsdpPeer(), prcPair.getKMyMoneyFile());
    }

    // ---------------------------------------------------------------

    /**
     * The KMyMoney file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    /**
     * The KMyMoney file is the top-level class to contain everything.
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

    private static PRICEPAIR createPricePair_int(final KMyMoneyWritableFileImpl file) {
	
        ObjectFactory factory = file.getObjectFactory();
        
        PRICEPAIR jwsdpPrcPair = file.createPricePairType();
        
        // ::EMPTY
        // set nothing (sic)
    
        file.getRootElement().getPRICES().getPRICEPAIR().add(jwsdpPrcPair);
        file.setModified(true);
    
        LOGGER.debug("createPricePair_int: Created new price pair (core): " + 
                     jwsdpPrcPair.getFrom() + "/" + jwsdpPrcPair.getTo());
    
        return jwsdpPrcPair;
    }

    // ---------------------------------------------------------------

	@Override
    public void setFromSecCurrQualifID(final KMMQualifSecCurrID qualifID) {
		if ( qualifID == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! qualifID.isSet() )
//			throw new IllegalArgumentException("unset ID given");

    	jwsdpPeer.setFrom(qualifID.toString());
    	getWritableKMyMoneyFile().setModified(true);
    }

	@Override
	public void setFromSecurityQualifID(final KMMQualifSecID qualifID) {
		if ( qualifID == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! qualifID.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		jwsdpPeer.setFrom(qualifID.toString());
		getWritableKMyMoneyFile().setModified(true);
	}

	@Override
	public void setFromCurrencyQualifID(final KMMQualifCurrID qualifID) {
		if ( qualifID == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! qualifID.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		jwsdpPeer.setFrom(qualifID.toString());
		getWritableKMyMoneyFile().setModified(true);
	}

	@Override
	public void setFromSecurity(final KMyMoneySecurity sec) {
		if ( sec == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! sec.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		jwsdpPeer.setFrom(sec.toString());
		getWritableKMyMoneyFile().setModified(true);
	}

	@Override
	public void setFromCurrencyCode(final String code) {
		if ( code == null )
			throw new IllegalArgumentException("null code given");

		if ( code.trim().length() == 0 )
			throw new IllegalArgumentException("empty code given");

		setFromCurrencyQualifID(new KMMQualifCurrID(code));
	}

	@Override
	public void setFromCurrency(final KMyMoneyCurrency curr) {
		if ( curr == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! curr.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		jwsdpPeer.setFrom(curr.toString());
		getWritableKMyMoneyFile().setModified(true);
	}

    // ----------------------------
    
	@Override
	public void setToCurrencyQualifID(KMMQualifCurrID qualifID) {
		if ( qualifID == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! qualifID.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		if ( qualifID.getType() != KMMQualifSecCurrID.Type.CURRENCY )
			throw new InvalidQualifSecCurrTypeException("Is not a currency: " + qualifID.toString());

		jwsdpPeer.setTo(qualifID.getCode());
		getWritableKMyMoneyFile().setModified(true);
	}

	@Override
	public void setToCurrencyCode(String code) {
		if ( code == null )
			throw new IllegalArgumentException("null code given");

		if ( code.trim().length() == 0 )
			throw new IllegalArgumentException("empty code given");

		setToCurrencyQualifID(new KMMQualifCurrID(code));
	}

	@Override
	public void setToCurrency(KMyMoneyCurrency curr) {
		if ( curr == null )
			throw new IllegalArgumentException("null ID given");

		// ::TODO
//		if ( ! curr.isSet() )
//			throw new IllegalArgumentException("unset ID given");

		jwsdpPeer.setTo(curr.toString());
		setToCurrencyQualifID(curr.getQualifID());
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	return toStringShort();
    }

    public String toStringShort() {
	return getFromSecCurrStr() + ";" + getToCurrStr();
    }

    public String toStringLong() {
	String result = "KMMWritablePricePairImpl [";
	
	try {
	    result += "from-sec-curr=" + getFromSecCurrQualifID();
	} catch (Exception e) {
	    result += "from-sec-curr=" + "ERROR";
	}

	try {
	    result += ", to-curr=" + getToCurrencyQualifID();
	} catch (Exception e) {
	    result += ", to-curr=" + "ERROR";
	}
	
	result += "]";
	
	return result;
    }

}
