package org.kmymoney.api.read.aux;

public interface KMMAddress {

    /**
     * @return
     */
    String getStreet();
    
    /**
     * @return
     */
    String getCity();

    /**
     * @return
     */
    String getCounty();

    /**
     * @return
     */
    String getState();
    
    // ----------------------------
    
    /**
     * @return
     */
    String getPostCode();
    
    /**
     * @return
     */
    String getZip();
    
    /**
     * @return
     */
    String getZipCode();
    
    // ----------------------------
    
    /**
     * @return
     */
    String getTelephone();
    
}
