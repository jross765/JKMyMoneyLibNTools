package org.kmymoney.api.write.impl;

import org.kmymoney.api.Const;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
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
	public KMyMoneyWritableSecurityImpl(final GncSecurity jwsdpPeer, final KMyMoneyWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Please use ${@link KMyMoneyWritableFile#createWritableSecurity()}.
	 *
	 * @param file the file we belong to
	 * @param id   the ID we shall have
	 */
	protected KMyMoneyWritableSecurityImpl(final KMyMoneyWritableFileImpl file) {
		super(createSecurity_int(file, GCshID.getNew()), file);
	}

	public KMyMoneyWritableSecurityImpl(KMyMoneySecurityImpl sec) {
		super(sec.getJwsdpPeer(), sec.getKMyMoneyFile());
	}

	// ---------------------------------------------------------------

	/**
	 * Delete this commodity and remove it from the file.
	 * 
	 * @throws InvalidCmdtyCurrIDException
	 * @throws ObjectCascadeException
	 * @throws InvalidCmdtyCurrTypeException
	 *
	 * @see KMyMoneyWritableSecurity#remove()
	 */
	public void remove() throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException {
		GncSecurity peer = getJwsdpPeer();
		(getKMyMoneyFile()).getRootElement().getGncBook().getBookElements().remove(peer);
		(getKMyMoneyFile()).removeSecurity(this);
	}

	// ---------------------------------------------------------------

	/**
	 * Creates a new Transaction and add's it to the given kmymoney-file Don't
	 * modify the ID of the new transaction!
	 *
	 * @param file the file we will belong to
	 * @param guid the ID we shall have
	 * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
	 */
	protected static GncSecurity createSecurity_int(final KMyMoneyWritableFileImpl file, final GCshID cmdtID) {
		if ( !cmdtID.isSet() ) {
			throw new IllegalArgumentException("GUID not set!");
		}

		GncSecurity jwsdpCmdty = file.createGncGncSecurityType();

		jwsdpCmdty.setCmdtyFraction(Const.CMDTY_FRACTION_DEFAULT);
		jwsdpCmdty.setVersion(Const.XML_FORMAT_VERSION);
		jwsdpCmdty.setCmdtyName("no name given");
		jwsdpCmdty.setCmdtySpace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString()); // ::TODO : soft
		jwsdpCmdty.setCmdtyId("XYZ"); // ::TODO
		jwsdpCmdty.setCmdtyXcode(Const.CMDTY_XCODE_DEFAULT);

		file.getRootElement().getGncBook().getBookElements().add(jwsdpCmdty);
		file.setModified(true);

		LOGGER.debug("createSecurity_int: Created new commodity (core): " + jwsdpCmdty.getCmdtySpace() + ":"
				+ jwsdpCmdty.getCmdtyId());

		return jwsdpCmdty;
	}

	// ---------------------------------------------------------------

	@Override
	public void setQualifID(GCshCmdtyCurrID qualifId) throws InvalidCmdtyCurrTypeException {
		if ( qualifId == null ) {
			throw new IllegalArgumentException("null qualif-ID given!");
		}

		getJwsdpPeer().setCmdtySpace(qualifId.getNameSpace());
		getJwsdpPeer().setCmdtyId(qualifId.getCode());

		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setXCode(String xCode) {
		if ( xCode == null ) {
			throw new IllegalArgumentException("null x-code given!");
		}

		if ( xCode.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty x-code given!");
		}

		getJwsdpPeer().setCmdtyXcode(xCode);
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setName(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given!");
		}

		if ( name.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty name given!");
		}

		getJwsdpPeer().setCmdtyName(name);
		getKMyMoneyFile().setModified(true);
	}

	@Override
	public void setFraction(Integer fract) {
		if ( fract <= 0 ) {
			throw new IllegalArgumentException("Fraction is <= 0");
		}

		getJwsdpPeer().setCmdtyFraction(fract);
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

		String result = "KMyMoneyWritableSecurityImpl [";

		try {
			result += "qualif-id='" + getQualifID().toString() + "'";
		} catch (InvalidCmdtyCurrTypeException e) {
			result += "qualif-id=" + "ERROR";
		}

		result += ", namespace='" + getNameSpace() + "'";
		result += ", name='" + getName() + "'";
		result += ", x-code='" + getXCode() + "'";
		result += ", fraction=" + getFraction() + "]";

		return result;
	}

}
