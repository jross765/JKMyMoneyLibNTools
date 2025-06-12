package org.example.kmymoneyapi.write.gen.simple;

import java.io.File;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.write.KMyMoneyWritableAccount;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.simple.KMMAcctID;
import org.kmymoney.base.basetypes.simple.KMMSecID;

public class GenAcct
{
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.xml";
    private static String gcshOutFileName = "example_out.xml";
    
    private static String                name1         = "FlixIt";
    private static KMyMoneyAccount.Type  type1         = KMyMoneyAccount.Type.EXPENSE;
    private static KMMQualifCurrID       currID1       = new KMMQualifCurrID("EUR");
    private static KMMAcctID             parentIDCore1 = new KMMAcctID("A000031"); // Fernsehen
    private static KMMComplAcctID        parentID1     = new KMMComplAcctID(parentIDCore1);
    
    private static String                name2         = "Depot Sparkasse";
    private static KMyMoneyAccount.Type  type2         = KMyMoneyAccount.Type.STOCK;
    private static KMMSecID              secID2        = new KMMSecID("DE9876543210");
    private static KMMAcctID             parentIDCore2 = new KMMAcctID("A000061"); // Finanzanlagen
    private static KMMComplAcctID        parentID2     = new KMMComplAcctID(parentIDCore2);
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
    	try {
    		GenAcct tool = new GenAcct();
    		tool.kernel();
    	} catch (Exception exc) {
    		System.err.println("Execution exception. Aborting.");
    		exc.printStackTrace();
    		System.exit(1);
    	}
    }

    protected void kernel() throws Exception
    {
    	KMyMoneyWritableFileImpl gcshFile = new KMyMoneyWritableFileImpl(new File(gcshInFileName));

    	KMyMoneyWritableAccount acct1 = gcshFile.createWritableAccount(type1, currID1, parentID1, name1);
    	System.out.println("Account to write: " + acct1.toString());
    	gcshFile.writeFile(new File(gcshOutFileName));
    	System.out.println("OK");

    	System.out.println("");
    	KMyMoneyWritableAccount acct2 = gcshFile.createWritableAccount(type2, secID2, parentID2, name2);
    	System.out.println("Account to write: " + acct2.toString());
    	gcshFile.writeFile(new File(gcshOutFileName));
    	System.out.println("OK");
    }
  
}
