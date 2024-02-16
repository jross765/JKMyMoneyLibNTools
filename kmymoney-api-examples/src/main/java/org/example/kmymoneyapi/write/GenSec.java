// $Header: svn://gila_svn_priv/finanzen/KMyMoneyToolsXML/trunk/KMyMoneyToolsXML/src/main/java/de/riegelmuenchen/kmymoney/tools/xml/gen/GenSec.java 8591 2024-02-16 08:49:43Z thilo $

package org.example.kmymoneyapi.write;

import java.io.File;

import org.kmymoney.api.write.KMyMoneyWritableSecurity;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class GenSec {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName = null;
    private static String kmmOutFileName = null;

    private static String name = null;
    private static String symbol = null;
    private static String isin = null;
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

	KMyMoneyWritableSecurity sec = kmmFile.createWritableSecurity();
	sec.setName(name);
	sec.setUserDefinedAttribute("kmm-security-id", isin);

	if (symbol != null)
	    sec.setSymbol(symbol);

	System.out.println("Security to write: " + sec.toString());
	kmmFile.writeFile(new File(kmmOutFileName));
	System.out.println("OK");
    }

}
