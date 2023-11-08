package org.example.kmymoney.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.read.KMyMoneySecurity;
import org.kmymoney.read.NoEntryFoundException;
import org.kmymoney.read.TooManyEntriesFoundException;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

public class GetSecInfo {
    public enum Mode
    {
      ID,
      ISIN,
      NAME
    }
    
    // -----------------------------------------------------------------

    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = null;
    private static Mode mode = null;
    private static String secID = null;
    private static String isin = null;
    private static String name = null;
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetSecInfo tool = new GetSecInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(kmmFileName));

	KMyMoneySecurity sec = null;

	if (mode == Mode.ID) {
	    sec = gcshFile.getSecurityById(secID);
	    if (sec == null) {
		System.err.println("Could not find a security with this ID.");
		throw new NoEntryFoundException();
	    }
	} else if (mode == Mode.ISIN) {
	    sec = gcshFile.getSecurityByCode(isin);
	    if (sec == null) {
		System.err.println("Could not find securities with this ISIN.");
		throw new NoEntryFoundException();
	    }
	} else if (mode == Mode.NAME) {
	    Collection<KMyMoneySecurity> cmdtyList = gcshFile.getSecuritiesByName(name);
	    if (cmdtyList.size() == 0) {
		System.err.println("Could not find securities matching this name.");
		throw new NoEntryFoundException();
	    }
	    if (cmdtyList.size() > 1) {
		System.err.println("Found " + cmdtyList.size() + "securities matching this name.");
		System.err.println("Please specify more precisely.");
		throw new TooManyEntriesFoundException();
	    }
	    sec = cmdtyList.iterator().next(); // first element
	}

	// ----------------------------

	try {
	    System.out.println("Qualified ID:      '" + sec.getQualifId() + "'");
	} catch (Exception exc) {
	    System.out.println("Qualified ID:      " + "ERROR");
	}

	try {
	    System.out.println("Symbol:            '" + sec.getSymbol() + "'");
	} catch (Exception exc) {
	    System.out.println("Symbol:            " + "ERROR");
	}

	try {
	    System.out.println("toString:          " + sec.toString());
	} catch (Exception exc) {
	    System.out.println("toString:          " + "ERROR");
	}

	try {
	    System.out.println("Type:              " + sec.getType());
	} catch (Exception exc) {
	    System.out.println("Type:              " + "ERROR");
	}

	try {
	    System.out.println("Name:              '" + sec.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:              " + "ERROR");
	}

	try {
	    System.out.println("PP:                " + sec.getPP());
	} catch (Exception exc) {
	    System.out.println("PP:                " + "ERROR");
	}

	try {
	    System.out.println("SAF:               " + sec.getSAF());
	} catch (Exception exc) {
	    System.out.println("SAF:               " + "ERROR");
	}

	try {
	    System.out.println("Rounding method:   " + sec.getRoundingMethod());
	} catch (Exception exc) {
	    System.out.println("Rounding method:   " + "ERROR");
	}

	try {
	    System.out.println("Trading currency:  " + sec.getTradingCurrency());
	} catch (Exception exc) {
	    System.out.println("Trading currency:  " + "ERROR");
	}

	try {
	    System.out.println("Trading market:    '" + sec.getTradingMarket() + "'");
	} catch (Exception exc) {
	    System.out.println("Trading market:    " + "ERROR");
	}

	// ---

	showQuotes(sec);
    }

    // -----------------------------------------------------------------

    private void showQuotes(KMyMoneySecurity sec) throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	System.out.println("");
	System.out.println("Quotes:");

	System.out.println("");
	System.out.println("Number of quotes: " + sec.getQuotes().size());

	System.out.println("");
	for (KMMPrice prc : sec.getQuotes()) {
	    System.out.println(" - " + prc.toString());
	}

	System.out.println("");
	System.out.println("Youngest Quote:");
	System.out.println(sec.getYoungestQuote());
    }
}
