package org.example.kmymoneyapi.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.api.read.KMyMoneyPayee;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.simple.KMMPyeID;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class GetPyeInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = "example_in.xml";
    private static Helper.Mode mode   = Helper.Mode.ID;
    private static KMMPyeID pyeID     = new KMMPyeID("xyz");
    private static String pyeName     = "abc";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetPyeInfo tool = new GetPyeInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	KMyMoneyFileImpl gcshFile = new KMyMoneyFileImpl(new File(kmmFileName));

	KMyMoneyPayee pye = null;
	if (mode == Helper.Mode.ID) {
	    pye = gcshFile.getPayeeByID(pyeID);
	    if (pye == null) {
		System.err.println("Could not find a security with this ID.");
		throw new NoEntryFoundException();
	    }
	} else if (mode == Helper.Mode.NAME) {
	    Collection<KMyMoneyPayee> cmdtyList = gcshFile.getPayeesByName(pyeName);
	    if (cmdtyList.size() == 0) {
		System.err.println("Could not find securities matching this name.");
		throw new NoEntryFoundException();
	    }
	    if (cmdtyList.size() > 1) {
		System.err.println("Found " + cmdtyList.size() + "securities matching this name.");
		System.err.println("Please specify more precisely.");
		throw new TooManyEntriesFoundException();
	    }
	    pye = cmdtyList.iterator().next(); // first element
	}

	// ----------------------------

	try {
	    System.out.println("ID:                '" + pye.getID() + "'");
	} catch (Exception exc) {
	    System.out.println("ID:                " + "ERROR");
	}

	try {
	    System.out.println("toString:          " + pye.toString());
	} catch (Exception exc) {
	    System.out.println("toString:          " + "ERROR");
	}

	try {
	    System.out.println("Name:              '" + pye.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:              " + "ERROR");
	}

	try {
	    System.out.println("Address:           '" + pye.getAddress() + "'");
	} catch (Exception exc) {
	    System.out.println("Address:           " + "ERROR");
	}

	try {
	    System.out.println("eMail:             " + pye.getEmail());
	} catch (Exception exc) {
	    System.out.println("eMail:             " + "ERROR");
	}

	try {
	    System.out.println("Notes:             " + pye.getNotes());
	} catch (Exception exc) {
	    System.out.println("Notes:             " + "ERROR");
	}
    }
}
