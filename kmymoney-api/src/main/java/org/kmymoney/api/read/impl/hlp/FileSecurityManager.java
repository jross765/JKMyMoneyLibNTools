package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.api.basetypes.complex.InvalidQualifSecCurrTypeException;
import org.kmymoney.api.basetypes.complex.KMMQualifSecID;
import org.kmymoney.api.basetypes.simple.KMMSecID;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.generated.SECURITY;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.api.read.impl.KMyMoneySecurityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSecurityManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileSecurityManager.class);

	// ---------------------------------------------------------------

	private KMyMoneyFileImpl kmmFile;

	private Map<KMMSecID, KMyMoneySecurity> secMap;
	private Map<String, KMMSecID>           symbMap;
	private Map<String, KMMSecID>           codeMap;

	// ---------------------------------------------------------------

	public FileSecurityManager(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
		init(kmmFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final KMYMONEYFILE pRootElement) {
		secMap = new HashMap<KMMSecID, KMyMoneySecurity>();
		symbMap = new HashMap<String, KMMSecID>();
		codeMap = new HashMap<String, KMMSecID>();

		for ( SECURITY jwsdpSec : pRootElement.getSECURITIES().getSECURITY() ) {
			try {
				KMyMoneySecurityImpl sec = createSecurity(jwsdpSec);
				secMap.put(sec.getID(), sec);
				symbMap.put(sec.getSymbol(), new KMMSecID(jwsdpSec.getId()));
				codeMap.put(sec.getCode(), new KMMSecID(jwsdpSec.getId()));
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Security-Entry with id=" + jwsdpSec.getId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in security map: " + secMap.size());
	}

	protected KMyMoneySecurityImpl createSecurity(final SECURITY jwsdpSec) {
		KMyMoneySecurityImpl sec = new KMyMoneySecurityImpl(jwsdpSec, kmmFile);
		LOGGER.debug("Generated new security: " + sec.getID());
		return sec;
	}

	// ---------------------------------------------------------------

	public void addSecurity(KMyMoneySecurity sec) {
		secMap.put(sec.getID(), sec);

		if ( sec.getSymbol() != null )
			symbMap.put(sec.getSymbol(), sec.getQualifID().getSecID());

		if ( sec.getCode() != null )
			codeMap.put(sec.getCode(), sec.getQualifID().getSecID());

		LOGGER.debug("Added security to cache: " + sec.getID());
	}

	public void removeSecurity(KMyMoneySecurity sec) {
		secMap.remove(sec.getID());

		for ( String symb : symbMap.keySet() ) {
			if ( symbMap.get(symb).equals(sec.getQualifID().getSecID()) )
				symbMap.remove(symb);
		}

		for ( String code : codeMap.keySet() ) {
			if ( codeMap.get(code).equals(sec.getQualifID().getSecID()) )
				codeMap.remove(code);
		}

		LOGGER.debug("Removed security from cache: " + sec.getID());
	}

	// ---------------------------------------------------------------

	public KMyMoneySecurity getSecurityByID(final KMMSecID id) {
		if ( secMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneySecurity retval = secMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getSecurityById: No Security with ID '" + id + "'. We know " + secMap.size() + " securities.");
		}

		return retval;
	}

	public KMyMoneySecurity getSecurityByID(final String idStr) {
		if ( idStr == null ) {
			throw new IllegalStateException("null string given");
		}

		if ( idStr.trim().equals("") ) {
			throw new IllegalStateException("Search string is empty");
		}

		KMMSecID secID = new KMMSecID(idStr);
		return getSecurityByID(secID);
	}

	public KMyMoneySecurity getSecurityByQualifID(final KMMQualifSecID secID) {
		return getSecurityByID(secID.getCode());
	}

	public KMyMoneySecurity getSecurityByQualifID(final String qualifIDStr)
			throws InvalidQualifSecCurrIDException, InvalidQualifSecCurrTypeException {
		if ( qualifIDStr == null ) {
			throw new IllegalStateException("null string given");
		}

		if ( qualifIDStr.trim().equals("") ) {
			throw new IllegalStateException("Search string is empty");
		}

		KMMQualifSecID secID = KMMQualifSecID.parse(qualifIDStr);
		return getSecurityByQualifID(secID);
	}

	public KMyMoneySecurity getSecurityBySymbol(final String symb) {
		if ( secMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		if ( symbMap.size() != secMap.size() ) {
			// ::CHECK
			// CAUTION: Don't throw an exception, at least not in all cases,
			// because this is not necessarily an error: Only if the KMyMoney
			// file does not contain quotes for foreign currencies (i.e. currency-
			// commodities but only security-commodities is this an error.
			// throw new IllegalStateException("Sizes of root elements are not equal");
			LOGGER.debug("getSecurityBySymbol: Sizes of root elements are not equal.");
		}

		KMMSecID qualifID = symbMap.get(symb);
		if ( qualifID == null ) {
			LOGGER.warn("getSecurityBySymbol: No Security with symbol '" + symb + "'. We know " + symbMap.size()
					+ " securities in map 2.");
		}

		KMyMoneySecurity retval = secMap.get(qualifID);
		if ( retval == null ) {
			LOGGER.warn("getSecurityBySymbol: Security with qualified ID '" + qualifID + "'. We know " + secMap.size()
					+ " securities in map 1.");
		}

		return retval;
	}

	public KMyMoneySecurity getSecurityByCode(final String code) {
		if ( secMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		if ( codeMap.size() != secMap.size() ) {
			// ::CHECK
			// CAUTION: Don't throw an exception, at least not in all cases,
			// because this is not necessarily an error: Only if the KMyMoney
			// file does not contain quotes for foreign currencies (i.e. currency-
			// commodities but only security-commodities is this an error.
			// throw new IllegalStateException("Sizes of root elements are not equal");
			LOGGER.debug("getSecurityByCode: Sizes of root elements are not equal.");
		}

		KMMSecID qualifID = codeMap.get(code);
		if ( qualifID == null ) {
			LOGGER.warn("getSecurityByCode: No Security with symbol '" + code + "'. We know " + codeMap.size()
					+ " securities in map 2.");
		}

		KMyMoneySecurity retval = secMap.get(qualifID);
		if ( retval == null ) {
			LOGGER.warn("getSecurityByCode: No Security with qualified ID '" + qualifID + "'. We know " + secMap.size()
					+ " securities in map 1.");
		}

		return retval;
	}

	public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr) {
		return getSecuritiesByName(expr, true);
	}

	public Collection<KMyMoneySecurity> getSecuritiesByName(final String expr, final boolean relaxed) {
		if ( secMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		Collection<KMyMoneySecurity> result = new ArrayList<KMyMoneySecurity>();

		for ( KMyMoneySecurity sec : getSecurities() ) {
			if ( sec.getName() != null ) // yes, that can actually happen!
			{
				if ( relaxed ) {
					if ( sec.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(sec);
					}
				} else {
					if ( sec.getName().equals(expr) ) {
						result.add(sec);
					}
				}
			}
		}

		return result;
	}

	public KMyMoneySecurity getSecurityByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		Collection<KMyMoneySecurity> cmdtyList = getSecuritiesByName(expr, false);
		if ( cmdtyList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( cmdtyList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return cmdtyList.iterator().next();
	}

	public Collection<KMyMoneySecurity> getSecurities() {
		return secMap.values();
	}

	// ---------------------------------------------------------------

	public int getNofEntriesSecurityMap() {
		return secMap.size();
	}

}
