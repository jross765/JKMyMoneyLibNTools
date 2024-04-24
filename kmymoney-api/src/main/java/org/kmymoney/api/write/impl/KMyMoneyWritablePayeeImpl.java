package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.math.BigInteger;

import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.api.generated.ADDRESS;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.impl.KMyMoneyPayeeImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePayee;
import org.kmymoney.api.write.aux.KMMWritableAddress;
import org.kmymoney.api.write.impl.aux.KMMWritableAddressImpl;
import org.kmymoney.api.write.impl.hlp.KMyMoneyWritableObjectImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of KMyMoneyPayeeImpl to allow read-write access instead of
 * read-only access.
 */
public class KMyMoneyWritablePayeeImpl extends KMyMoneyPayeeImpl 
                                       implements KMyMoneyWritablePayee 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(KMyMoneyWritablePayeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the KMyMoneyWritableObject-interface.
     */
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(getWritableKMyMoneyFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link KMyMoneyWritableFile#createWritablePayee()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
	public KMyMoneyWritablePayeeImpl(
			final PAYEE jwsdpPeer,
			final KMyMoneyWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link KMyMoneyWritableFile#createWritablePayee()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected KMyMoneyWritablePayeeImpl(final KMyMoneyWritableFileImpl file) {
	super(createPayee_int(file, file.getNewPayeeID()), file);
    }

    public KMyMoneyWritablePayeeImpl(final KMyMoneyPayeeImpl pye) {
	super(pye.getJwsdpPeer(), pye.getKMyMoneyFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given KMyMoney file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static PAYEE createPayee_int(
    		final KMyMoneyWritableFileImpl file, 
    		final KMMPyeID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}
    
        ObjectFactory factory = file.getObjectFactory();
    
        PAYEE jwsdpPye = file.createPayeeType();
    
        jwsdpPye.setId(newID.toString());
        jwsdpPye.setName("no name given");
    
        {
        	ADDRESS addr = factory.createADDRESS();
            addr.setCity(null);
            addr.setCounty("");
            addr.setPostcode("");
            addr.setState("");
            addr.setStreet("");
            addr.setTelephone("");
            addr.setZip("");
            addr.setZipcode("");
            jwsdpPye.setADDRESS(addr);
        }
    
        file.getRootElement().getPAYEES().getPAYEE().add(jwsdpPye);
        file.setModified(true);
    
        LOGGER.debug("createPayee_int: Created new payee (core): " + jwsdpPye.getId());
        
        return jwsdpPye;
    }

    // ---------------------------------------------------------------

	protected void setAddress(final KMMWritableAddressImpl addr) {
		super.setAddress(addr);
	}

    /**
     * Delete this Payee and remove it from the file.
     *
     * @see KMyMoneyWritablePayee#remove()
     */
    @Override
    public void remove() {
    	PAYEE peer = jwsdpPeer;
    	(getKMyMoneyFile()).getRootElement().getPAYEES().getPAYEE().remove(peer);
    	(getKMyMoneyFile()).removePayee(this);
    }

    // ---------------------------------------------------------------

    /**
     * The KMyMoney file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    /**
     * The KMyMoney file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyWritablePayee#getWritableAddress()
     */
    public KMMWritableAddress getWritableAddress() {
	return new KMMWritableAddressImpl(jwsdpPeer.getADDRESS());
    }

    /**
     * @see KMyMoneyPayee#getAddress()
     */
//    @Override
//    public KMMWritableAddress getAddress() {
//	return getWritableAddress();
//    }

    public KMMWritableAddress createWritableAddress() {
		KMMWritableAddressImpl addr = new KMMWritableAddressImpl(this);
		setAddress(addr);
		if ( helper.getPropertyChangeSupport() != null ) {
			helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableAddress());
		}
		return addr;
    }
    
	public void remove(KMMWritableAddress impl) {
		// ::TODO
	}

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyWritablePayee#setName(java.lang.String)
     */
    @Override
    public void setName(final String name) {
    	if ( name == null ) {
    		throw new IllegalArgumentException("null name given!");
    	}

    	if ( name.trim().length() == 0 ) {
    		throw new IllegalArgumentException("empty name given!");
    	}

    	String oldName = getName();
    	jwsdpPeer.setName(name);
    	getKMyMoneyFile().setModified(true);

    	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
    	if ( propertyChangeSupport != null) {
    		propertyChangeSupport.firePropertyChange("name", oldName, name);
    	}
    }

    @Override
    public void setAddress(final KMMAddress addr) {
	if ( addr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

	/*
	 * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
	 * jwsdpPeer.setPyeAddr(adrImpl.jwsdpPeer); } else
	 */
	
	{

	    if (jwsdpPeer.getADDRESS() == null) {
		jwsdpPeer.setADDRESS(getKMyMoneyFile().getObjectFactory().createADDRESS());
	    }

	    jwsdpPeer.getADDRESS().setCity(addr.getCity());
	    jwsdpPeer.getADDRESS().setCounty(addr.getCounty());
	    jwsdpPeer.getADDRESS().setPostcode(addr.getPostCode());
	    jwsdpPeer.getADDRESS().setState(addr.getState());
	    jwsdpPeer.getADDRESS().setStreet(addr.getStreet());
	    jwsdpPeer.getADDRESS().setTelephone(addr.getTelephone());
	    jwsdpPeer.getADDRESS().setZip(addr.getZip());
	    jwsdpPeer.getADDRESS().setZipcode(addr.getZipCode());
	}

	getKMyMoneyFile().setModified(true);
    }

    /**
     * @param notes user-defined notes about the customer (may be null)
     * @see KMyMoneyWritableCustomer#setNotes(String)
     */
    @Override
    public void setNotes(final String notes) {
		if ( notes == null ) {
			throw new IllegalArgumentException("null notes given");
		}
		
		if ( notes.isEmpty() ) {
			throw new IllegalArgumentException("empty notes given");
		}

		// Caution: empty string allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty notesgiven!");
//	}

		String oldNotes = getNotes();
		jwsdpPeer.setNotes(notes);
		getKMyMoneyFile().setModified(true);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null ) {
			propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
		}
    }

	@Override
	public void setDefaultAccountID(KMMComplAcctID acctID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setEmail(String eml) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setReference(String ref) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMatchingEnabled(BigInteger enbl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMatchKey(String key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUsingMatchKey(BigInteger key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMatchIgnoreCase(BigInteger val) {
		// TODO Auto-generated method stub
		
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyWritablePayeeImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", name='");
	buffer.append(getName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
