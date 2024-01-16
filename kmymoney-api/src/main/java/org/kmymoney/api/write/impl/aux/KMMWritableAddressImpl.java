package org.kmymoney.api.write.impl.aux;

import org.kmymoney.api.generated.ADDRESS;
import org.kmymoney.api.read.impl.aux.KMMAddressImpl;
import org.kmymoney.api.write.aux.KMMWritableAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMMAddressImpl to allow read-write access instead of
 * read-only access.
 */
public class KMMWritableAddressImpl extends KMMAddressImpl 
                                    implements KMMWritableAddress 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KMMWritableAddressImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public KMMWritableAddressImpl(final ADDRESS jwsdpPeer) {
    	super(jwsdpPeer);
    }

    public KMMWritableAddressImpl(final KMMAddressImpl addr) {
    	super(addr.getJwsdpPeer());
    }

    // ---------------------------------------------------------------

	@Override
	public void setStreet(String street) {
		getJwsdpPeer().setStreet(street);		
	}

	@Override
	public void setCity(String city) {
		getJwsdpPeer().setCity(city);		
	}

	@Override
	public void setCounty(String county) {
		getJwsdpPeer().setCounty(county);		
	}

	@Override
	public void setState(String state) {
		getJwsdpPeer().setState(state);		
	}

	@Override
	public void setPostCode(String postCode) {
		getJwsdpPeer().setPostcode(postCode);		
	}

	@Override
	public void setZip(String zip) {
		getJwsdpPeer().setZip(zip);		
	}

	@Override
	public void setZipCode(String zipCode) {
		getJwsdpPeer().setZipcode(zipCode);		
	}

	@Override
	public void setTelephone(String telephone) {
		getJwsdpPeer().setTelephone(telephone);		
	}
    
    // ---------------------------------------------------------------

    @Override
	public String toString() {
		String result = "KMMWritableAddressImpl [";

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
