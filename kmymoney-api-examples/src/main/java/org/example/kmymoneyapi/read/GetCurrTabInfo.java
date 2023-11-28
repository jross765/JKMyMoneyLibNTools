package org.example.kmymoneyapi.read;

import java.io.File;

import org.kmymoney.api.currency.ComplexPriceTable;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;

public class GetCurrTabInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.xml";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetCurrTabInfo tool = new GetCurrTabInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(gcshFileName));

	ComplexPriceTable tab = gcshFile.getCurrencyTable();
	System.out.println(tab.toString());
    }
}
