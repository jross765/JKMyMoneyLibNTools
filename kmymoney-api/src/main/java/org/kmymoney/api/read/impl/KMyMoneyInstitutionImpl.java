package org.kmymoney.api.read.impl;

import java.util.List;

import org.kmymoney.api.generated.INSTITUTION;
import org.kmymoney.api.generated.PAIR;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyInstitution;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.impl.aux.KMMAddressImpl;
import org.kmymoney.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.kmymoney.api.read.impl.hlp.KMyMoneyObjectImpl;
import org.kmymoney.api.read.impl.hlp.KVPListDoesNotContainKeyException;
import org.kmymoney.base.basetypes.simple.KMMInstID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KMyMoneyInstitutionImpl extends KMyMoneyObjectImpl 
								     implements KMyMoneyInstitution 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyInstitutionImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * the JWSDP-object we are facading.
     */
    protected final INSTITUTION jwsdpPeer;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMyMoneyInstitutionImpl(
	    final INSTITUTION peer, 
	    final KMyMoneyFile kmmFile) {
    	super(kmmFile);

    	jwsdpPeer = peer;
    }

	// ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public INSTITUTION getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    @Override
    public KMMInstID getID() {
    	return new KMMInstID(jwsdpPeer.getId());
    }

    // ---------------------------------------------------------------

    @Override
    public String getName() {
    	return jwsdpPeer.getName();
    }

	@Override
	public String getSortCode() {
		return jwsdpPeer.getSortcode();
	}

	@Override
	public KMMAddress getAddress() {
		if ( jwsdpPeer.getADDRESS() == null )
			return null;

		return new KMMAddressImpl(jwsdpPeer.getADDRESS());
	}

    // ---------------------------------------------------------------

	@Override
	public String getBIC() {
    	try {
    		return getUserDefinedAttribute("bic"); // ::MAGIC
    	} catch (KVPListDoesNotContainKeyException exc) {
    		return null;
    	}
	}

	@Override
	public String getURL() {
    	try {
    		return getUserDefinedAttribute("url"); // ::MAGIC
    	} catch (KVPListDoesNotContainKeyException exc) {
    		return null;
    	}
	}

    // ---------------------------------------------------------------

	/**
	 * @param name the name of the user-defined attribute
	 * @return the value or null if not set
	 */
	public String getUserDefinedAttribute(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}

		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}

		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(kvpList, name);
	}

    /**
     * @return all keys that can be used with
     *         ${@link #getUserDefinedAttribute(String)}}.
     */
	public List<String> getUserDefinedAttributeKeys() {
		if ( jwsdpPeer.getKEYVALUEPAIRS() == null) {
			return null;
		}
		
		List<PAIR> kvpList = jwsdpPeer.getKEYVALUEPAIRS().getPAIR();
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeKeysCore(kvpList);
	}

    // -----------------------------------------------------------------

    @Override
	public String toString() {
		String result = "KMyMoneyInstitutionImpl ";

		result += "[id=" + getID();

		result += ", name='" + getName() + "'";
		result += ", sort-code='" + getSortCode() + "'";
		// result += ", address=" + getAddress();
		result += ", bic='" + getBIC() + "'";
		result += ", url='" + getURL() + "'";

		result += "]";

		return result;
	}

}
