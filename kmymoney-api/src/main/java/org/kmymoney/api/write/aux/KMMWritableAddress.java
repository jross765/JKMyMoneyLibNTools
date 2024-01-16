package org.kmymoney.api.write.aux;

public interface KMMWritableAddress {

	void  setStreet(String street);
    
    void  setCity(String city);

    void  setCounty(String county);

    void  setState(String state);
    
    // ----------------------------
    
    void  setPostCode(String postCode);
    
    void  setZip(String zip);
    
    void  setZipCode(String zipCode);
    
    // ----------------------------
    
    void setTelephone(String telephone);
    
}
