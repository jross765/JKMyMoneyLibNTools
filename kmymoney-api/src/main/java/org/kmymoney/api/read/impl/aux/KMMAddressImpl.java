package org.kmymoney.api.read.impl.aux;

import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.generated.ADDRESS;

public class KMMAddressImpl implements KMMAddress {

    /**
     * The JWSDP-object we are wrapping.
     */
    private final ADDRESS jwsdpPeer;

    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public KMMAddressImpl(final ADDRESS newPeer) {
	jwsdpPeer = newPeer;
    }

    // -----------------------------------------------------------

    /**
     * @return The JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public ADDRESS getJwsdpPeer() {
	return jwsdpPeer;
    }

    // -----------------------------------------------------------

    @Override
    public String getStreet() {
        return jwsdpPeer.getStreet();
    }

    @Override
    public String getCity() {
	return jwsdpPeer.getCity();
    }

    @Override
    public String getCounty() {
	return jwsdpPeer.getCounty();
    }

    @Override
    public String getState() {
        return jwsdpPeer.getState();
    }
    
    // ----------------------------    

    @Override
    public String getPostCode() {
	return jwsdpPeer.getPostcode();
    }

    @Override
    public String getZip() {
	return jwsdpPeer.getZip();
    }

    @Override
    public String getZipCode() {
	return jwsdpPeer.getZipcode();
    }

    // ----------------------------
    
    @Override
    public String getTelephone() {
	return jwsdpPeer.getTelephone();
    }
    
    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "KMMAddressImpl [";
	
	result += "street='" + getStreet() + "'";
	result += ", city='" + getCity() + "'";
	result += ", county='" + getCounty() + "'";
	result += ", state='" + getState() + "'";
	result += ", postcode='" + getPostCode() + "'";
	result += ", zip='" + getZip() + "'";
	result += ", zip-code='" + getZipCode() + "'";
	result += ", telephone='" + getTelephone() + "'";
	
	result += "]";
	
	return result;
    }

}
