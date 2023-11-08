package org.example.kmymoney.read;

import java.io.File;

import org.kmymoney.basetypes.InvalidSecCurrIDException;
import org.kmymoney.basetypes.InvalidSecCurrTypeException;
import org.kmymoney.basetypes.KMMCurrID;
import org.kmymoney.read.KMyMoneyCurrency;
import org.kmymoney.read.NoEntryFoundException;
import org.kmymoney.read.aux.KMMPrice;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

public class GetCurrInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = null;
    private static String symbol = null;
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetCurrInfo tool = new GetCurrInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(kmmFileName));

	KMMCurrID currID = new KMMCurrID(symbol);
	KMyMoneyCurrency curr = gcshFile.getCurrencyByQualifId(currID);
	if (curr == null) {
	    System.err.println("Could not find currency with qualif. ID " + currID.toString());
	    throw new NoEntryFoundException();
	}

	try {
	    System.out.println("Qualified ID:      '" + curr.getQualifId() + "'");
	} catch (Exception exc) {
	    System.out.println("Qualified ID:      " + "ERROR");
	}

	try {
	    System.out.println("Symbol:            '" + curr.getSymbol() + "'");
	} catch (Exception exc) {
	    System.out.println("Symbol:            " + "ERROR");
	}

	try {
	    System.out.println("toString:          " + curr.toString());
	} catch (Exception exc) {
	    System.out.println("toString:          " + "ERROR");
	}

	try {
	    System.out.println("Type:              " + curr.getType());
	} catch (Exception exc) {
	    System.out.println("Type:              " + "ERROR");
	}

	try {
	    System.out.println("Name:              '" + curr.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:              " + "ERROR");
	}

	try {
	    System.out.println("PP:                " + curr.getPP());
	} catch (Exception exc) {
	    System.out.println("PP:                " + "ERROR");
	}

	try {
	    System.out.println("SAF:               " + curr.getSAF());
	} catch (Exception exc) {
	    System.out.println("SAF:               " + "ERROR");
	}

	try {
	    System.out.println("SCF:               " + curr.getSCF());
	} catch (Exception exc) {
	    System.out.println("SCF:               " + "ERROR");
	}

	try {
	    System.out.println("Rounding method:   " + curr.getRoundingMethod());
	} catch (Exception exc) {
	    System.out.println("Rounding method:   " + "ERROR");
	}

	// ---

	showQuotes(curr);
    }

    // -----------------------------------------------------------------

    private void showQuotes(KMyMoneyCurrency curr) throws InvalidSecCurrTypeException, InvalidSecCurrIDException {
	System.out.println("");
	System.out.println("Quotes:");

	System.out.println("");
	System.out.println("Number of quotes: " + curr.getQuotes().size());

	System.out.println("");
	for (KMMPrice prc : curr.getQuotes()) {
	    System.out.println(" - " + prc.toString());
	}

	System.out.println("");
	System.out.println("Youngest Quote:");
	System.out.println(curr.getYoungestQuote());
    }
}
