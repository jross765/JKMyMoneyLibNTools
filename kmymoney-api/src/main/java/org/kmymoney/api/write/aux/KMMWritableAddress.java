package org.kmymoney.api.write.aux;

import org.kmymoney.api.read.aux.KMMAddress;

public interface KMMWritableAddress extends KMMAddress {

	/**
	 * 
	 * @param street
	 * 
	 * @see #getStreet()
	 */
	void  setStreet(String street);
    
	/**
	 * 
	 * @param city
	 * 
	 * @see #getCity()c
	 */
    void  setCity(String city);

    /**
     * 
     * @param county
     * 
     * @see #getCounty()
     */
    void  setCounty(String county);

    /**
     * 
     * @param state
     * 
     * @see #getState()
     */
    void  setState(String state);
    
    // ----------------------------
    
    /**
     * 
     * @param postCode
     * 
     * @see #getPostCode()
     */
    void  setPostCode(String postCode);
    
    /**
     * 
     * @param zip
     * 
     * @see #getZip()
     * @see #setZipCode(String)
     */
    void  setZip(String zip);
    
    /**
     * 
     * @param zipCode
     * 
     * @see #getZipCode()
     * @see #setZip(String)
     */
    void  setZipCode(String zipCode);
    
    // ----------------------------
    
    /**
     * 
     * @param telephone
     * 
     * @see #getTelephone()
     */
    void setTelephone(String telephone);
    
}
