package org.kmymoney.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kmymoney.api.basetypes.complex.KMMComplAcctID;
import org.kmymoney.api.basetypes.complex.KMMComplAcctID.Top;
import org.kmymoney.api.generated.ACCOUNT;
import org.kmymoney.api.generated.KMYMONEYFILE;
import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneyAccount.Type;
import org.kmymoney.api.read.NoEntryFoundException;
import org.kmymoney.api.read.TooManyEntriesFoundException;
import org.kmymoney.api.read.UnknownAccountTypeException;
import org.kmymoney.api.read.impl.KMyMoneyAccountImpl;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	private KMyMoneyFileImpl kmmFile;

	private Map<KMMComplAcctID, KMyMoneyAccount> acctMap;

	// ---------------------------------------------------------------

	public FileAccountManager(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
		init(kmmFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final KMYMONEYFILE pRootElement) {
		acctMap = new HashMap<KMMComplAcctID, KMyMoneyAccount>();

		for ( ACCOUNT jwsdpAcct : pRootElement.getACCOUNTS().getACCOUNT() ) {
			try {
				KMyMoneyAccount acct = createAccount(jwsdpAcct);
				acctMap.put(acct.getID(), acct);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Account-Entry with id=" + jwsdpAcct.getId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in account map: " + acctMap.size());
	}

	/**
	 * @param jwsdpAcct the JWSDP-peer (parsed xml-element) to fill our object with
	 * @return the new KMyMoneyAccount to wrap the given jaxb-object.
	 */
	protected KMyMoneyAccountImpl createAccount(final ACCOUNT jwsdpAcct) {
		KMyMoneyAccountImpl acct = new KMyMoneyAccountImpl(jwsdpAcct, kmmFile);
		LOGGER.debug("Generated new account: " + acct.getID());
		return acct;
	}

	// ---------------------------------------------------------------

	public void addAccount(KMyMoneyAccount acct) {
		acctMap.put(acct.getID(), acct);
		LOGGER.debug("Added account to cache: " + acct.getID());
	}

	public void removeAccount(KMyMoneyAccount acct) {
		acctMap.remove(acct.getID());
		LOGGER.debug("Removed account from cache: " + acct.getID());
	}

	// ---------------------------------------------------------------

	public KMyMoneyAccount getAccountByID(final KMMComplAcctID id) {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyAccount retval = acctMap.get(id);
		if ( retval == null ) {
			System.err.println(
					"getAccountById: No Account with ID '" + id + "'. We know " + acctMap.size() + " accounts.");
		}

		return retval;
	}

	public Collection<KMyMoneyAccount> getAccountsByParentID(final KMMComplAcctID acctID) {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		SortedSet<KMyMoneyAccount> retval = new TreeSet<KMyMoneyAccount>();

		for ( Object element : acctMap.values() ) {
			KMyMoneyAccount account = (KMyMoneyAccount) element;

			KMMComplAcctID parentID = account.getParentAccountID();
			if ( parentID == null ) {
				if ( acctID == null ) {
					retval.add((KMyMoneyAccount) account);
				}
			} else {
				if ( parentID.equals(acctID) ) {
					retval.add((KMyMoneyAccount) account);
				}
			}
		}

		return retval;
	}

	public Collection<KMyMoneyAccount> getAccountsByName(final String name) {
		return getAccountsByName(name, true, true);
	}

	public Collection<KMyMoneyAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		Collection<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();

		for ( KMyMoneyAccount acct : acctMap.values() ) {
			if ( relaxed ) {
				if ( qualif ) {
					if ( acct.getQualifiedName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				}
			} else {
				if ( qualif ) {
					if ( acct.getQualifiedName().equals(expr) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().equals(expr) ) {
						result.add(acct);
					}
				}
			}
		}

		return result;
	}

	public KMyMoneyAccount getAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		Collection<KMyMoneyAccount> acctList = getAccountsByName(name, qualif, false);
		if ( acctList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( acctList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return acctList.iterator().next();
	}

	/*
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountByID first and only call this method if the returned account does
	 * not have the right name.
	 */
	public KMyMoneyAccount getAccountByNameEx(final String nameRegEx)
			throws NoEntryFoundException, TooManyEntriesFoundException {

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		KMyMoneyAccount foundAccount = getAccountByNameUniq(nameRegEx, true);
		if ( foundAccount != null ) {
			return foundAccount;
		}
		Pattern pattern = Pattern.compile(nameRegEx);

		for ( KMyMoneyAccount account : acctMap.values() ) {
			Matcher matcher = pattern.matcher(account.getName());
			if ( matcher.matches() ) {
				return account;
			}
		}

		return null;
	}

	/*
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public KMyMoneyAccount getAccountByIDorName(final KMMComplAcctID id, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		KMyMoneyAccount retval = getAccountByID(id);
		if ( retval == null ) {
			retval = getAccountByNameUniq(name, true);
		}

		return retval;
	}

	/*
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public KMyMoneyAccount getAccountByIDorNameEx(final KMMComplAcctID id, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		KMyMoneyAccount retval = getAccountByID(id);
		if ( retval == null ) {
			retval = getAccountByNameEx(name);
		}

		return retval;
	}

	public Collection<KMyMoneyAccount> getAccountsByTypeAndName(Type type, String expr, boolean qualif, boolean relaxed)
			throws UnknownAccountTypeException {
		Collection<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();

		for ( KMyMoneyAccount acct : getAccountsByName(expr, qualif, relaxed) ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		return result;
	}

	// ---------------------------------------------------------------

	public Collection<KMyMoneyAccount> getAccounts() {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(new TreeSet<>(acctMap.values()));
	}

	public Collection<? extends KMyMoneyAccount> getParentlessAccounts() {
		try {
			Collection<KMyMoneyAccount> retval = new TreeSet<KMyMoneyAccount>();

			for ( KMyMoneyAccount account : getAccounts() ) {
				if ( account.getParentAccountID() == null ) {
					retval.add(account);
				}

			}

			return retval;
		} catch (RuntimeException e) {
			LOGGER.error("getRootAccounts: Problem getting all root-account", e);
			throw e;
		} catch (Throwable e) {
			LOGGER.error("getRootAccounts: SERIOUS Problem getting all root-account", e);
			return new ArrayList<KMyMoneyAccount>();
		}
	}

	public Collection<KMMComplAcctID> getTopAccountIDs() {
		Collection<KMMComplAcctID> result = new ArrayList<KMMComplAcctID>();

		result.add(KMMComplAcctID.get(Top.ASSET));
		result.add(KMMComplAcctID.get(Top.LIABILITY));
		result.add(KMMComplAcctID.get(Top.INCOME));
		result.add(KMMComplAcctID.get(Top.EXPENSE));
		result.add(KMMComplAcctID.get(Top.EQUITY));

		return result;
	}

	public Collection<KMyMoneyAccount> getTopAccounts() {
		Collection<KMyMoneyAccount> result = new ArrayList<KMyMoneyAccount>();

		for ( KMMComplAcctID acctID : getTopAccountIDs() ) {
			KMyMoneyAccount acct = getAccountByID(acctID);
			result.add(acct);
		}

		return result;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesAccountMap() {
		return acctMap.size();
	}

}
