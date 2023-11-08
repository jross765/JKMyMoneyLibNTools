package org.example.kmymoney.read;

import java.io.File;
import java.util.Collection;

import org.kmymoney.read.KMyMoneyPayee;
import org.kmymoney.read.NoEntryFoundException;
import org.kmymoney.read.TooManyEntriesFoundException;
import org.kmymoney.read.impl.KMyMoneyFileImpl;

public class GetPyeInfo {
    // BEGIN Example data -- adapt to your needs
    private static String kmmFileName = null;
    private static String pyeID = null;
    private static String name = null;
    // END Example data

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
	    pye = gcshFile.getPayeeById(pyeID);
	    if (pye == null) {
		System.err.println("Could not find a security with this ID.");
		throw new NoEntryFoundException();
	    }
	} else if (mode == Helper.Mode.NAME) {
	    Collection<KMyMoneyPayee> cmdtyList = gcshFile.getPayeesByName(name);
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
	    System.out.println("ID:                '" + pye.getId() + "'");
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
