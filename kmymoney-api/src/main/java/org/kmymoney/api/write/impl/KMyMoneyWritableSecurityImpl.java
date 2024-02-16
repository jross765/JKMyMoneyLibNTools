package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.math.BigInteger;
import java.util.List;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.generated.KEYVALUEPAIRS;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.hlp.KMyMoneyObject;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.kmymoney.api.read.impl.hlp.KVPListDoesNotContainKeyException;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.kmymoney.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneySecurityImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritableSecurityImpl extends KMyMoneySecurityImpl 
                                          implements KMyMoneyWritableSecurity
{
	/**
	 * Automatically created logger for debug and error-output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableSecurityImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the KMyMoneyWritableObject-interface.
     */
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

	// ---------------------------------------------------------------

	/**
	 * Please use ${@link KMyMoneyWritableFile#createWritableSecurity()}.
	 *
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 */
	@SuppressWarnings("exports")
	public KMyMoneyWritableSecurityImpl(final SECURITY jwsdpPeer, final KMyMoneyWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Please use ${@link KMyMoneyWritableFile#createWritableSecurity()}.
	 *
	 * @param file the file we belong to
	 * @param id   the ID we shall have
	 */
	protected KMyMoneyWritableSecurityImpl(final KMyMoneyWritableFileImpl file) {
		super(createSecurity_int(file, file.getNewSecurityID()), file);
	}

	public KMyMoneyWritableSecurityImpl(final KMyMoneySecurityImpl sec) {
		super(sec.getJwsdpPeer(), sec.getKMyMoneyFile());
	}

	// ---------------------------------------------------------------

	/**
	 * Delete this security and remove it from the file.
	 *
	 * @see KMyMoneyWritableSecurity#remove()
	 */
	public void remove() throws InvalidQualifSecCurrTypeException, InvalidQualifSecCurrIDException {
		SECURITY peer = jwsdpPeer;
		(getKMyMoneyFile()).getRootElement().getSECURITIES().getSECURITY().remove(peer);
		(getKMyMoneyFile()).removeSecurity(this);
	}

	// ---------------------------------------------------------------

	/**
	 * Creates a new Transaction and add's it to the given kmymoney-file Don't
	 * modify the ID of the new transaction!
	 *
	 * @param file the file we will belong to
	 * @param newID the ID we shall have
	 * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
	 */
	protected static SECURITY createSecurity_int(
			final KMyMoneyWritableFileImpl file, 
			final KMMSecID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}

		SECURITY jwsdpSec = file.createSecurityType();

		jwsdpSec.setId(newID.toString());
		jwsdpSec.setType(null);
		jwsdpSec.setSymbol(Const.SEC_SYMBOL_DEFAULT);
		jwsdpSec.setName("no name given");;
		jwsdpSec.setPp(BigInteger.valueOf(Const.SEC_PP_DEFAULT));
		jwsdpSec.setSaf(BigInteger.valueOf(Const.SEC_SAF_DEFAULT));
		jwsdpSec.setTradingMarket(null);
		jwsdpSec.setTradingCurrency(file.getDefaultCurrencyID());
		
		// ::TODO: Key-value pair for ISIN

        file.getRootElement().getSECURITIES().getSECURITY().add(jwsdpSec);
		file.setModified(true);

		LOGGER.debug("createSecurity_int: Created new security (core):" + jwsdpSec.getId());

		return jwsdpSec;
	}

	// ---------------------------------------------------------------

	@Override
	public void setSymbol(final String symb) {
		if ( symb == null ) {
			throw new IllegalArgumentException("null symbol given!");
		}

		if ( symb.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty symbol given!");
		}

		String oldSymb = getSymbol();
		jwsdpPeer.setSymbol(symb);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("symbol", oldSymb, symb);
		}
	}

	@Override
	public void setCode(final String code) {
		if ( code == null ) {
			throw new IllegalArgumentException("null code given!");
		}

		if ( code.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty code given!");
		}

		String oldCode = getCode();
		setUserDefinedAttribute("kmm-security-id", code); // sic, no try-catch-block here
		
		// Already done:
		// getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("code", oldCode, code);
		}
	}

	// ---------------------------------------------------------------

	@Override
	public void setType(final KMMSecCurr.Type type) {
		setTypeBigInt(type.getCode());
	}

	public void setTypeBigInt(final BigInteger type) {
		if ( type == null ) {
			throw new IllegalArgumentException("null type given!");
		}

		if ( type.intValue() < 0 ) {
			throw new IllegalArgumentException("type < 0 given!"); // sic, 0 is allowed!
		}

		if ( type.intValue() == KMMSecCurr.Type.UNSET ) {
			throw new IllegalArgumentException("unset type given!");
		}

		BigInteger oldType = getTypeBigInt();
		jwsdpPeer.setType(type);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("type", oldType, type);
		}
	}

	@Override
	public void setName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given!");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty name given!");
		}

		String oldName = getName();
		jwsdpPeer.setName(name);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("name", oldName, name);
		}
	}

	@Override
	public void setPP(final BigInteger pp) {
		if ( pp == null ) {
			throw new IllegalArgumentException("null PP given!");
		}

		if ( pp.intValue() <= 0 ) {
			throw new IllegalArgumentException("PP is <= 0");
		}
		
		BigInteger oldPP = getPP();
		jwsdpPeer.setPp(pp);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("pp", oldPP, pp);
		}
	}

	@Override
	public void setRoundingMethod(final KMMSecCurr.RoundingMethod meth) {
		// TODO
	}

	@Override
	public void setSAF(final BigInteger saf) {
		if ( saf == null ) {
			throw new IllegalArgumentException("null SAF given!");
		}

		if ( saf.intValue() <= 0 ) {
			throw new IllegalArgumentException("SAF is <= 0");
		}
		
		BigInteger oldSAF = getSAF();
		jwsdpPeer.setSaf(saf);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("saf", oldSAF, saf);
		}
	}

	@Override
	public void setTradingCurrency(final KMMQualifCurrID currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("null trading currency given!");
		}

		KMMQualifCurrID oldCurrID = getTradingCurrency();
		jwsdpPeer.setTradingCurrency(currID.getCurrency().getCurrencyCode());
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("tradingCurrency", oldCurrID, currID);
		}
	}

	@Override
	public void setTradingMarket(final String mkt) {
		if ( mkt == null ) {
			throw new IllegalArgumentException("null trading market given!");
		}

		if ( mkt.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty trading market given!");
		}

		String oldMkt = getTradingMarket();
		jwsdpPeer.setTradingMarket(mkt);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
		    propertyChangeSupport.firePropertyChange("tradingMarket", oldMkt, mkt);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * The kmymoney-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
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

	/**
	 * @param name  the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link KMyMoneyObject#getUserDefinedAttribute(String)}
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		KEYVALUEPAIRS kvps = jwsdpPeer.getKEYVALUEPAIRS();
		if ( kvps == null ) {
			// key-value-pair(s) does/do not exist yet
			ObjectFactory fact = getKMyMoneyFile().getObjectFactory();
			if ( jwsdpPeer.getKEYVALUEPAIRS() == null ) {
				// The structure KEYVALUEPAIRS (note the s) does not
				// exist yet -- e.t. has to be built from scratch
				kvps = fact.createKEYVALUEPAIRS();
				jwsdpPeer.setKEYVALUEPAIRS(kvps);
			}
		}
		
		String val = null;
		try {
			val = getUserDefinedAttribute(name);
		} catch ( KVPListDoesNotContainKeyException |
				  NullPointerException exc ) {
			LOGGER.warn("setUserDefinedAttribute: Cannot get actual user-defined value for key '" + name + "'; " + 
	                    "does not exist yet.");
			LOGGER.warn("setUserDefinedAttribute: Will generate it.");
		}
		
		if ( val == null ) {
			// The structure KEYVALUEPAIRS already exists, but
			// there is no PAIR entry yet that matches the key.
			ObjectFactory fact = getKMyMoneyFile().getObjectFactory();
			PAIR kvp = fact.createPAIR();
			kvp.setKey(name);
			kvp.setValue("");
			jwsdpPeer.getKEYVALUEPAIRS().getPAIR().add(kvp);
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(kvpList, getWritableKMyMoneyFile(), 
			                             name, value);

		// Already done in HasWritableUserDefinedAttributesImpl.setUserDefinedAttributeCore:
		// getKMyMoneyFile().setModified(true);
	}

	// -----------------------------------------------------------------

	@Override
    public String toString() {
	String result = "KMyMoneyWritableSecurityImpl ";
	
	result += "[id=" + getID();
	result += ", symbol='" + getSymbol() + "'";
	
	try {
	    result += ", type=" + getType();
	} catch (Exception e) {
	    result += ", type=" + "ERROR";
	}
	
	result += ", name='" + getName() + "'";
	result += ", pp=" + getPP();
	
	try {
	    result += ", rounding-method=" + getRoundingMethod();
	} catch (Exception e) {
	    result += ", rounding-method=" + "ERROR";
	}
	
	result += ", saf=" + getSAF();
	
	try {
	    result += ", trading-curr=" + getTradingCurrency();
	} catch (Exception e) {
	    result += ", trading-curr=" + "ERROR";
	}
	
	result += ", trading-mkt='" + getTradingMarket() + "'";
	
	result += "]";
	
	return result;
    }

}
