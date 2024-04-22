package org.example.kmymoneyapi.read;

import java.io.File;

import org.kmymoney.api.read.KMyMoneyCurrency;
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;

public class GetCurrInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = "example_in.xml";
    private static String symbol      = "abc";
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

	KMMQualifCurrID currID = new KMMQualifCurrID(symbol);
	KMyMoneyCurrency curr = gcshFile.getCurrencyByQualifID(currID);
	if (curr == null) {
	    System.err.println("Could not find currency with qualif. ID " + currID.toString());
	    throw new NoEntryFoundException();
	}

	// ------------------------

	try {
	    System.out.println("Qualified ID:      '" + curr.getQualifID() + "'");
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

    private void showQuotes(KMyMoneyCurrency curr) {
	System.out.println("");
	System.out.println("Quotes:");

	System.out.println("");
	System.out.println("Number of quotes: " + curr.getQuotes().size());

	System.out.println("");
	for (KMyMoneyPrice prc : curr.getQuotes()) {
	    System.out.println(" - " + prc.toString());
	}

	System.out.println("");
	System.out.println("Youngest Quote:");
	System.out.println(curr.getYoungestQuote());
    }
}
