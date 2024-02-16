// $Header: svn://gila_svn_priv/finanzen/KMyMoneyToolsXML/trunk/KMyMoneyToolsXML/src/main/java/de/riegelmuenchen/kmymoney/tools/xml/gen/GenPye.java 8485 2024-02-06 08:51:24Z thilo $

package org.example.kmymoneyapi.write;

import java.io.File;

import org.kmymoney.api.write.KMyMoneyWritablePayee;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class GenPye {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName = null;
    private static String kmmOutFileName = null;
    private static String name = null;
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenPye tool = new GenPye();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }


    protected void kernel() throws Exception {
	KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

	KMyMoneyWritablePayee pye = kmmFile.createWritablePayee();
	pye.setName(name);

	System.out.println("Payee to write: " + pye.toString());
	kmmFile.writeFile(new File(kmmOutFileName));
	System.out.println("OK");
    }
 
}
