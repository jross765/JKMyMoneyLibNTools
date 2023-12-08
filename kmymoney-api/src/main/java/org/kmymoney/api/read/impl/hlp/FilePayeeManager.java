package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kmymoney.api.basetypes.simple.KMMPyeID;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.read.KMyMoneyFile;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyPayeeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePayeeManager {
    
    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePayeeManager.class);

    // ---------------------------------------------------------------

    private KMyMoneyFileImpl kmmFile;

    private Map<KMMPyeID, KMyMoneyPayee> pyeMap;

    // ---------------------------------------------------------------

    public FilePayeeManager(KMyMoneyFileImpl kmmFile) {
	this.kmmFile = kmmFile;
	init(kmmFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final KMYMONEYFILE pRootElement) {
	pyeMap = new HashMap<KMMPyeID, KMyMoneyPayee>();

	for ( PAYEE jwsdpPye : pRootElement.getPAYEES().getPAYEE() ) {
	    try {
		KMyMoneyPayeeImpl pye = createPayee(jwsdpPye);
		pyeMap.put(pye.getID(), pye);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Payee-Entry with id=" + jwsdpPye.getId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in payee map: " + pyeMap.size());
    }

    /**
     * @param jwsdpPye the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new KMyMoneyPayee to wrap the given JAXB object.
     */
    protected KMyMoneyPayeeImpl createPayee(final PAYEE jwsdpPye) {
	KMyMoneyPayeeImpl pye = new KMyMoneyPayeeImpl(jwsdpPye);
	return pye;
    }

    // ---------------------------------------------------------------

    public void addPayee(KMyMoneyPayee pye) {
	pyeMap.put(pye.getID(), pye);
	LOGGER.debug("Added payee to cache: " + pye.getID());
    }

    public void removePayee(KMyMoneyPayee pye) {
	pyeMap.remove(pye.getID());
	LOGGER.debug("Added payee to cache: " + pye.getID());
    }

    // ---------------------------------------------------------------

    /**
     * @see KMyMoneyFile#getPayeeByID(java.lang.String)
     */
    public KMyMoneyPayee getPayeeByID(final KMMPyeID id) {
	if (pyeMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	KMyMoneyPayee retval = pyeMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getPayeeById: No Payee with ID '" + id + "'. We know " + pyeMap.size() + " payees.");
	}
	
	return retval;
    }

    public Collection<KMyMoneyPayee> getPayeesByName(String expr) {
	return getPayeesByName(expr, true);
    }

    public Collection<KMyMoneyPayee> getPayeesByName(String expr, boolean relaxed) {
	if (pyeMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	Collection<KMyMoneyPayee> result = new ArrayList<KMyMoneyPayee>();

	for ( KMyMoneyPayee pye : getPayees() ) {
	    if ( pye.getName() != null ) 
	    {
		if ( relaxed ) {
		    if ( pye.getName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(pye);
		    }
		} else {
		    if ( pye.getName().equals(expr) ) {
			result.add(pye);
		    }
		}
	    }
	}
	
	return result;
    }

    public KMyMoneyPayee getPayeesByNameUniq(String expr)
	    throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<KMyMoneyPayee> cmdtyList = getPayeesByName(expr, false);
	if ( cmdtyList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( cmdtyList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return cmdtyList.iterator().next();
    }

    /**
     * @see KMyMoneyFile#getPayees()
     */
    public Collection<KMyMoneyPayee> getPayees() {
	return pyeMap.values();
    }

    // ---------------------------------------------------------------
    
    public int getNofEntriesPayeeMap() {
	return pyeMap.size();
    }
    
}
