package org.example.kmymoneyapi.write.gen.simple;

import java.io.File;

import org.kmymoney.api.read.KMMSecCurr;
import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class GenSec {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.xml";
    private static String kmmOutFileName = "example_out.xml";

    private static String name   = "HyperCyberScam Corp.";
    private static String symbol = "SCAM";
    private static String isin   = "US0123456789";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenSec tool = new GenSec();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

	KMyMoneyWritableSecurity sec = kmmFile.createWritableSecurity(KMMSecCurr.Type.STOCK, isin, name);
	sec.setSymbol(symbol);

	if (symbol != null)
	    sec.setSymbol(symbol);

	System.out.println("Security to write: " + sec.toString());
	kmmFile.writeFile(new File(kmmOutFileName));
	System.out.println("OK");
    }

}
