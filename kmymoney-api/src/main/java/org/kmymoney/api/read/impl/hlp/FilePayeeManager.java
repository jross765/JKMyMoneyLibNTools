package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.PAYEE;
import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneyPayeeImpl;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FilePayeeManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FilePayeeManager.class);

	// ---------------------------------------------------------------

	protected KMyMoneyFileImpl kmmFile;

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

	protected KMyMoneyPayeeImpl createPayee(final PAYEE jwsdpPye) {
		KMyMoneyPayeeImpl pye = new KMyMoneyPayeeImpl(jwsdpPye, kmmFile);
		LOGGER.debug("createPayee: Generated new payee: " + pye.getID());
		return pye;
	}

	// ---------------------------------------------------------------

	public void addPayee(KMyMoneyPayee pye) {
		if ( pye == null ) {
			throw new IllegalStateException("null payee given");
		}

		pyeMap.put(pye.getID(), pye);
		LOGGER.debug("addPayee: Added payee to cache: " + pye.getID());
	}

	public void removePayee(KMyMoneyPayee pye) {
		if ( pye == null ) {
			throw new IllegalStateException("null payee given");
		}

		pyeMap.remove(pye.getID());
		LOGGER.debug("removePayee: Added payee to cache: " + pye.getID());
	}

	// ---------------------------------------------------------------

	public KMyMoneyPayee getPayeeByID(final KMMPyeID pyeID) {
		if ( pyeID == null ) {
			throw new IllegalStateException("null payee ID given");
		}

		if ( ! pyeID.isSet() ) {
			throw new IllegalStateException("unset payee ID given");
		}

		if ( pyeMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyPayee retval = pyeMap.get(pyeID);
		if ( retval == null ) {
			LOGGER.warn("getPayeeByID: No Payee with ID '" + pyeID + "'. We know " + pyeMap.size() + " payees.");
		}

		return retval;
	}

	public List<KMyMoneyPayee> getPayeesByName(String expr) {
		if ( expr == null ) {
			throw new IllegalStateException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalStateException("empty expression given");
		}

		return getPayeesByName(expr, true);
	}

	public List<KMyMoneyPayee> getPayeesByName(String expr, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalStateException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalStateException("empty expression given");
		}

		if ( pyeMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<KMyMoneyPayee> result = new ArrayList<KMyMoneyPayee>();

		for ( KMyMoneyPayee pye : getPayees() ) {
			if ( pye.getName() != null ) {
				if ( relaxed ) {
					if ( pye.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
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

	public KMyMoneyPayee getPayeesByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( expr == null ) {
			throw new IllegalStateException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalStateException("empty expression given");
		}

		List<KMyMoneyPayee> cmdtyList = getPayeesByName(expr, false);
		if ( cmdtyList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( cmdtyList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return cmdtyList.get(0);
	}

	public Collection<KMyMoneyPayee> getPayees() {
		if ( pyeMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(pyeMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesPayeeMap() {
		return pyeMap.size();
	}

}
