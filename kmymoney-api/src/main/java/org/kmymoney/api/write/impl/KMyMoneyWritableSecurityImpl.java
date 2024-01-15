package org.kmymoney.api.write.impl;

import java.math.BigInteger;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.KMMSecCurr.RoundingMethod;
import org.kmymoney.api.read.KMMSecCurr.Type;
import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.read.UnknownRoundingMethodException;
import org.kmymoney.api.read.UnknownSecurityTypeException;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.kmymoney.api.write.hlp.KMyMoneyWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneySecurityImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritableSecurityImpl extends KMyMoneySecurityImpl 
                                          implements KMyMoneyWritableSecurity,
                                                     KMyMoneyWritableObject
{
	/**
	 * Automatically created logger for debug and error-output.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritableSecurityImpl.class);

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

	public KMyMoneyWritableSecurityImpl(KMyMoneySecurityImpl sec) {
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
			throw new IllegalArgumentException("empty ID given");
		}

		SECURITY jwsdpSec = file.createSecurityType();

		jwsdpSec.setType(null);
		jwsdpSec.setSymbol(Const.SEC_SYMBOL_DEFAULT);
		jwsdpSec.setName("no name given");;
		jwsdpSec.setPp(BigInteger.valueOf(Const.SEC_PP_DEFAULT));
		jwsdpSec.setSaf(BigInteger.valueOf(Const.SEC_SAF_DEFAULT));
		jwsdpSec.setTradingMarket(null);
		jwsdpSec.setTradingCurrency(file.getDefaultCurrencyID());
		
		// ::TODO: Key-value pair for ISIN

		file.getSecurities().add(jwsdpSec);
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

		jwsdpPeer.setSymbol(symb);
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setCode(final String code) {
		
	}

	// ---------------------------------------------------------------

	@Override
	public void setType(final KMMSecCurr.Type type) {
		
	}

	@Override
	public void setName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given!");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty name given!");
		}

		jwsdpPeer.setName(name);
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setPP(final BigInteger pp) {
		if ( pp == null ) {
			throw new IllegalArgumentException("null PP given!");
		}

		if ( pp.intValue() <= 0 ) {
			throw new IllegalArgumentException("PP is <= 0");
		}
		
		jwsdpPeer.setPp(pp.intValue());
		getKMyMoneyFile().setModified(true);
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
		
		jwsdpPeer.setPp(saf.intValue());
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setTradingCurrency(final KMMQualifCurrID currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("null trading currency given!");
		}

		jwsdpPeer.setTradingCurrency(currID.getCurrency().getCurrencyCode());
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setTradingMarket(final String mkt) {
		if ( mkt == null ) {
			throw new IllegalArgumentException("null trading market given!");
		}

		if ( mkt.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty trading market given!");
		}

		jwsdpPeer.setTradingMarket(mkt);
		getKMyMoneyFile().setModified(true);
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
	 * @see KMyMoneyWritableObject#setUserDefinedAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	// ::TODO ?
	public void setUserDefinedAttribute(final String name, final String value) {
		// ::EMPTY
	}

	// ::TODO
	public void clean() {
		// helper.cleanSlots();
	}

	// -----------------------------------------------------------------

	@Override
    public String toString() {
	String result = "KMyMoneyWritableSecurityImpl ";
	
	result += "[id=" + getID();
	result += ", symbol='" + getSymbol() + "'";
	
	try {
	    result += ", type=" + getType();
	} catch (UnknownSecurityTypeException e) {
	    result += ", type=" + "ERROR";
	}
	
	result += ", name='" + getName() + "'";
	result += ", pp=" + getPP();
	
	try {
	    result += ", rounding-method=" + getRoundingMethod();
	} catch (UnknownRoundingMethodException e) {
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
