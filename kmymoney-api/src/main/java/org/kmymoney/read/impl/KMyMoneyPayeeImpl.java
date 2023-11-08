package org.kmymoney.read.impl;

import java.math.BigInteger;

import org.kmymoney.generated.ADDRESS;
import org.kmymoney.generated.PAYEE;
import org.kmymoney.read.KMyMoneyFile;
import org.kmymoney.read.KMyMoneyPayee;
import org.kmymoney.read.aux.KMMAddress;
import org.kmymoney.read.impl.aux.KMMAddressImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyPayeeImpl implements KMyMoneyPayee {

    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyPayeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private PAYEE jwsdpPeer;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneyPayeeImpl(
	    final PAYEE peer) {

	jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    @Override
    public String getId() {
	return jwsdpPeer.getId();
    }

    @Override
    public String getName() {
	return jwsdpPeer.getName();
    }

    @Override
    public String getDefaultAccountId() {
	return jwsdpPeer.getDefaultaccountid();
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
	return "KMyMoneyPayeeImpl [id=" + getId() + ", name='" + getName() + "']";
    }

}
