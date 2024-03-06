package org.example.kmymoneyapi.write.gen.simple;

import java.io.File;

import org.kmymoney.api.write.KMyMoneyWritablePayee;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class GenPye {
    // BEGIN Example data -- adapt to your needs
    private static String kmmInFileName  = "example_in.xml";
    private static String kmmOutFileName = "example_out.xml";
    
    private static String name = "Mama & Papa";
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

	KMyMoneyWritablePayee pye = kmmFile.createWritablePayee(name);

	System.out.println("Payee to write: " + pye.toString());
	kmmFile.writeFile(new File(kmmOutFileName));
	System.out.println("OK");
    }
 
}
