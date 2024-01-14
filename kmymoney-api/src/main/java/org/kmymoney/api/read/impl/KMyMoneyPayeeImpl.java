package org.kmymoney.api.read.impl;

import java.math.BigInteger;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.impl.aux.KMMAddressImpl;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyPayeeImpl extends KMyMoneyObjectImpl
                               implements KMyMoneyPayee 
{

	private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyPayeeImpl.class);

	// ---------------------------------------------------------------

	/**
	 * the JWSDP-object we are facading.
	 */
	protected PAYEE jwsdpPeer;

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public KMyMoneyPayeeImpl(final PAYEE peer, final KMyMoneyFile kmmFile) {
		super(kmmFile);
		jwsdpPeer = peer;
	}

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public PAYEE getJwsdpPeer() {
	return jwsdpPeer;
    }

	// ---------------------------------------------------------------

	@Override
	public KMMPyeID getID() {
		return new KMMPyeID(jwsdpPeer.getId());
	}

	@Override
	public String getName() {
		return jwsdpPeer.getName();
	}

	@Override
	public KMMComplAcctID getDefaultAccountID() {
		if ( jwsdpPeer.getDefaultaccountid() == null )
			return null;

		return new KMMComplAcctID(jwsdpPeer.getDefaultaccountid());
	}

	@Override
	public KMMAddress getAddress() {
		if ( jwsdpPeer.getADDRESS() == null )
			return null;

		return new KMMAddressImpl(jwsdpPeer.getADDRESS());
	}

	@Override
	public String getEmail() {
		return jwsdpPeer.getEmail();
	}

	@Override
	public String getReference() {
		return jwsdpPeer.getReference();
	}

	@Override
	public String getNotes() {
		return jwsdpPeer.getNotes();
	}

	// ---------------------------------------------------------------

	@Override
	public BigInteger getMatchingEnabled() {
		return jwsdpPeer.getMatchingenabled();
	}

	@Override
	public String getMatchKey() {
		return jwsdpPeer.getMatchkey();
	}

	@Override
	public BigInteger getUsingMatchKey() {
		return jwsdpPeer.getUsingmatchkey();
	}

	@Override
	public BigInteger getMatchIgnoreCase() {
		return jwsdpPeer.getMatchignorecase();
	}

	// ---------------------------------------------------------------

	@Override
	public String toString() {
		return "KMyMoneyPayeeImpl [id=" + getID() + ", name='" + getName() + "']";
	}

}
