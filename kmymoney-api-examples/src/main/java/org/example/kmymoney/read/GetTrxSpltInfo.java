package org.example.kmymoney.read;

import java.io.File;

import org.kmymoney.basetypes.KMMSplitID;
import org.kmymoney.read.KMyMoneyTransactionSplit;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

public class GetTrxSpltInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = null;
    private static String trxID = null;
    private static String spltID = null;
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetTrxSpltInfo tool = new GetTrxSpltInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

	KMyMoneyTransactionSplit splt = kmmFile.getTransactionSplitByID(new KMMSplitID(trxID, spltID));

	try {
	    System.out.println("Qualif. ID:     " + splt.getQualifId());
	} catch (Exception exc) {
	    System.out.println("Qualif. ID:     " + "ERROR");
	}

	try {
	    System.out.println("toString:       " + splt.toString());
	} catch (Exception exc) {
	    System.out.println("toString:       " + "ERROR");
	}

	try {
	    System.out.println("Transaction ID: " + splt.getTransaction().getId());
	} catch (Exception exc) {
	    System.out.println("Transaction ID: " + "ERROR");
	}

	try {
	    System.out.println("Account ID:     " + splt.getAccountId());
	} catch (Exception exc) {
	    System.out.println("Account ID:     " + "ERROR");
	}

	try {
	    System.out.println("Action:         " + splt.getAction());
	} catch (Exception exc) {
	    System.out.println("Action:         " + "ERROR");
	}

	try {
	    System.out.println("Value:          " + splt.getValueFormatted());
	} catch (Exception exc) {
	    System.out.println("Value:          " + "ERROR");
	}

	try {
	    System.out.println("Quantity:       " + splt.getSharesFormatted());
	} catch (Exception exc) {
	    System.out.println("Quantity:       " + "ERROR");
	}

	try {
	    System.out.println("Description:    '" + splt.getMemo() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:    " + "ERROR");
	}
    }
}
