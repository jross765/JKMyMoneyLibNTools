package org.kmymoney.api.read.impl.hlp;

import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Cache implements FileStats {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Cache.class);

	// ---------------------------------------------------------------

	private FileAccountManager     acctMgr = null;
	private FileTransactionManager trxMgr = null;
	private FilePayeeManager       pyeMgr = null;

	private FileSecurityManager    secMgr = null;
	private FileCurrencyManager    currMgr = null;
	private FilePriceManager       prcMgr = null;

	// ---------------------------------------------------------------

	public FileStats_Cache(final FileAccountManager acctMgr, final FileTransactionManager trxMgr,
			final FilePayeeManager pyeMgr, final FileSecurityManager secMgr, final FileCurrencyManager currMgr,
			final FilePriceManager prcMgr) {
		this.acctMgr = acctMgr;
		this.trxMgr = trxMgr;
		this.pyeMgr = pyeMgr;
		this.secMgr = secMgr;
		this.currMgr = currMgr;
		this.prcMgr = prcMgr;
	}

	public FileStats_Cache(final KMyMoneyFileImpl gcshFile) {
		this.acctMgr = gcshFile.getAcctMgr();
		this.trxMgr = gcshFile.getTrxMgr();
		this.pyeMgr = gcshFile.getPyeMgr();
		this.secMgr = gcshFile.getSecMgr();
		this.currMgr = gcshFile.getCurrMgr();
		this.prcMgr = gcshFile.getPrcMgr();
	}

	// ---------------------------------------------------------------

	@Override
	public int getNofEntriesAccounts() {
		return acctMgr.getNofEntriesAccountMap();
	}

	@Override
	public int getNofEntriesTransactions() {
		return trxMgr.getNofEntriesTransactionMap();
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return trxMgr.getNofEntriesTransactionSplitMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesPayees() {
		return pyeMgr.getNofEntriesPayeeMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesSecurities() {
		return secMgr.getNofEntriesSecurityMap();
	}

	@Override
	public int getNofEntriesCurrencies() {
		return currMgr.getNofEntriesCurrencyMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesPricePairs() {
		return ERROR; // n/a
	}

	@Override
	public int getNofEntriesPrices() {
		return prcMgr.getNofEntriesPriceMap();
	}

}
