package org.kmymoney.api.read.impl.hlp;

import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Counters implements FileStats {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Counters.class);

	// ---------------------------------------------------------------

	private KMyMoneyFileImpl kmmFile = null;

	// ---------------------------------------------------------------

	public FileStats_Counters(KMyMoneyFileImpl kmmFile) {
		this.kmmFile = kmmFile;
	}

	// ---------------------------------------------------------------

	@Override
	public int getNofEntriesInstitutions() {
		return kmmFile.getCountDataFor("institution");
	}

	@Override
	public int getNofEntriesAccounts() {
		return kmmFile.getCountDataFor("account");
	}

	@Override
	public int getNofEntriesTransactions() {
		return kmmFile.getCountDataFor("transaction");
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return ERROR; // n/a
	}

	@Override
	public int getNofEntriesPayees() {
		return kmmFile.getCountDataFor("payee");
	}

	// ----------------------------

	@Override
	public int getNofEntriesSecurities() {
		return kmmFile.getCountDataFor("security");
	}

	@Override
	public int getNofEntriesCurrencies() {
		return kmmFile.getCountDataFor("currency");
	}

	// ----------------------------

	@Override
	public int getNofEntriesPricePairs() {
		return kmmFile.getCountDataFor("pricepair");
	}

	@Override
	public int getNofEntriesPrices() {
		return ERROR; // n/a
	}

}
