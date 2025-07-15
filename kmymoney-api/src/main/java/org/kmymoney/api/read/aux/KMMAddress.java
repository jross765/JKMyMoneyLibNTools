package org.kmymoney.api.read.aux;

public interface KMMAddress {

    /**
     * @return the street of the address
     */
    String getStreet();
    
    /**
     * @return the city of the address
     */
    String getCity();

    /**
     * @return the county of the address
     */
    String getCounty();

    /**
     * @return the state (US) or region/province/whatever (non-US) of the address
     */
    String getState();
    
    // ----------------------------
    
    /**
     * @return the post code of the address
     * 
     * @see #getZip()
     * @see #getZipCode()
     */
    String getPostCode();
    
    /**
     * @return the ZIP code of the address
     * 
     * @see #getPostCode()
     * @see #getZipCode()
     */
    String getZip();
    
    /**
     * @return the ZIP code of the address
     * 
     * @see #getPostCode()
     * @see #getZip()
     */
    String getZipCode();
    
    // ----------------------------
    
    /**
     * @return the telephone number of the address
     */
    String getTelephone();
    
}
