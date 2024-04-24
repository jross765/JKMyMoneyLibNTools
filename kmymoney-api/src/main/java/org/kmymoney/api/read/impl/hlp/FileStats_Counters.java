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
		return kmmFile.getRootElement().getINSTITUTIONS().getCount().intValue();
	}

	@Override
	public int getNofEntriesAccounts() {
		return kmmFile.getRootElement().getACCOUNTS().getCount().intValue();
	}

	@Override
	public int getNofEntriesTransactions() {
		return kmmFile.getRootElement().getTRANSACTIONS().getCount().intValue();
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return ERROR; // n/a
	}

	@Override
	public int getNofEntriesPayees() {
		return kmmFile.getRootElement().getPAYEES().getCount().intValue();
	}

	// ----------------------------

	@Override
	public int getNofEntriesSecurities() {
		return kmmFile.getRootElement().getSECURITIES().getCount().intValue();
	}

	@Override
	public int getNofEntriesCurrencies() {
		return kmmFile.getRootElement().getCURRENCIES().getCount().intValue();
	}

	// ----------------------------

	@Override
	public int getNofEntriesPricePairs() {
		return kmmFile.getRootElement().getPRICES().getCount().intValue();
	}

	@Override
	public int getNofEntriesPrices() {
		return ERROR; // n/a
	}

}
