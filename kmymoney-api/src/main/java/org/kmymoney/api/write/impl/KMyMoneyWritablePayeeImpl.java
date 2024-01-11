package org.kmymoney.api.write.impl;

import java.beans.PropertyChangeSupport;

import org.kmymoney.api.Const;
import org.kmymoney.api.basetypes.simple.KMMID;
import org.kmymoney.api.generated.ObjectFactory;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.generated.PAYEES;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.aux.KMMAddress;
import org.kmymoney.api.read.impl.KMyMoneyPayeeImpl;
import org.kmymoney.api.write.KMyMoneyWritableFile;
import org.kmymoney.api.write.KMyMoneyWritablePayee;
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
    private final KMyMoneyWritableObjectImpl helper = new KMyMoneyWritableObjectImpl(this);

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
	super(createPayee_int(file, KMMID.getNew()), file);
    }

    public KMyMoneyWritablePayeeImpl(final KMyMoneyPayeeImpl pye) {
	super(pye.getJwsdpPeer(), pye.getKMyMoneyFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given kmymoney-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static PAYEE createPayee_int(
	    final KMyMoneyWritableFileImpl file, 
	    final KMMID pyeID) {
	if ( ! pyeID.isSet() ) {
	    throw new IllegalArgumentException("GUID not set!");
	}
    
        ObjectFactory factory = file.getObjectFactory();
    
        PAYEE jwsdpPye = file.createGncGncPayeeType();
    
        jwsdpPye.setPayeeTaxincluded("USEGLOBAL");
        jwsdpPye.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpPye.setPayeeUseTt(0);
        jwsdpPye.setPayeeName("no name given");
    
        {
            GncGncPayee.PayeeGuid id = factory.createGncGncPayeePayeeGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(pyeID.toString());
            jwsdpPye.setPayeeGuid(id);
            jwsdpPye.setPayeeId(id.getValue());
        }
    
        {
            org.kmymoney.api.generated.Address addr = factory.createAddress();
            addr.setAddrAddr1("");
            addr.setAddrAddr2("");
            addr.setAddrName("");
            addr.setAddrAddr3("");
            addr.setAddrAddr4("");
            addr.setAddrName("");
            addr.setAddrEmail("");
            addr.setAddrFax("");
            addr.setAddrPhone("");
            addr.setVersion(Const.XML_FORMAT_VERSION);
            jwsdpPye.setPayeeAddr(addr);
        }
    
        {
            GncGncPayee.PayeeCurrency currency = factory.createGncGncPayeePayeeCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(KMMCmdtyCurrNameSpace.CURRENCY);
            jwsdpPye.setPayeeCurrency(currency);
        }
    
        jwsdpPye.setPayeeActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(jwsdpPye);
        file.setModified(true);
    
        LOGGER.debug("createPayee_int: Created new pyeor (core): " + jwsdpPye.getPayeeGuid().getValue());
        
        return jwsdpPye;
    }

    /**
     * Delete this Payee and remove it from the file.
     *
     * @see KMyMoneyWritablePayee#remove()
     */
    @Override
    public void remove() {
    	PAYEE peer = getJwsdpPeer();
	(getKMyMoneyFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getKMyMoneyFile()).removePayee(this);
    }

    // ---------------------------------------------------------------

    /**
     * The kmymoney-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getWritableKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    /**
     * The kmymoney-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public KMyMoneyWritableFileImpl getKMyMoneyFile() {
	return (KMyMoneyWritableFileImpl) super.getKMyMoneyFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyWritablePayee#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String number) {
	if ( number == null ) {
	    throw new IllegalArgumentException("null number given!");
	}

	if ( number.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty number given!");
	}

	String oldNumber = getNumber();
	getJwsdpPeer().setPayeeId(number);
	getKMyMoneyFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("PayeeNumber", oldNumber, number);
	}
    }

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
	getJwsdpPeer().setPayeeName(name);
	getKMyMoneyFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    @Override
    public void setAddress(final KMMAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

	/*
	 * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
	 * getJwsdpPeer().setPyeAddr(adrImpl.getJwsdpPeer()); } else
	 */
	
	{

	    if (getJwsdpPeer().getPayeeAddr() == null) {
		getJwsdpPeer().setPayeeAddr(getKMyMoneyFile().getObjectFactory().createAddress());
	    }

	    getJwsdpPeer().getPayeeAddr().setAddrAddr1(adr.getAddressLine1());
	    getJwsdpPeer().getPayeeAddr().setAddrAddr2(adr.getAddressLine2());
	    getJwsdpPeer().getPayeeAddr().setAddrAddr3(adr.getAddressLine3());
	    getJwsdpPeer().getPayeeAddr().setAddrAddr4(adr.getAddressLine4());
	    getJwsdpPeer().getPayeeAddr().setAddrName(adr.getAddressName());
	    getJwsdpPeer().getPayeeAddr().setAddrEmail(adr.getEmail());
	    getJwsdpPeer().getPayeeAddr().setAddrFax(adr.getFax());
	    getJwsdpPeer().getPayeeAddr().setAddrPhone(adr.getTel());
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
	    throw new IllegalArgumentException("null notesgiven!");
	}

	// Caution: empty string allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty notesgiven!");
//	}

	String oldNotes = getNotes();
	getJwsdpPeer().setPayeeNotes(notes);
	getKMyMoneyFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
	}
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyWritablePayee#getWritableAddress()
     */
    @Override
    public KMMWritableAddress getWritableAddress() {
	return new KMMWritableAddressImpl(getJwsdpPeer().getPayeeAddr(), getKMyMoneyFile());
    }

    /**
     * @see KMyMoneyPayee#getAddress()
     */
    @Override
    public KMMWritableAddress getAddress() {
	return getWritableAddress();
    }

    // ---------------------------------------------------------------

	public void clean() {
		helper.cleanSlots();
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("KMyMoneyWritablePayeeImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", number='");
	buffer.append(getNumber() + "'");
	
	buffer.append(", name='");
	buffer.append(getName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
