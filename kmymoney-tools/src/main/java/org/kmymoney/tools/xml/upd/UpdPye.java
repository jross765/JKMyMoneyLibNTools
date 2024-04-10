package org.kmymoney.tools.xml.upd;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.kmymoney.base.basetypes.simple.KMMPyeID;
import org.kmymoney.tools.CommandLineTool;

import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.kmymoney.api.read.NoEntryFoundException;
// ::TODO
// import org.kmymoney.api.read.PayeeNotFoundException;
import org.kmymoney.api.write.KMyMoneyWritablePayee;
import org.kmymoney.api.write.impl.KMyMoneyWritableFileImpl;

public class UpdPye extends CommandLineTool
{
  // Logger
  private static Logger logger = Logger.getLogger(UpdPye.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String   kmmInFileName = null;
  private static String   kmmOutFileName = null;
  private static KMMPyeID pyeID = null;

  private static String name = null;
  private static String descr = null;

  private static KMyMoneyWritablePayee pye = null;

  public static void main( String[] args )
  {
    try
    {
      UpdPye tool = new UpdPye ();
      tool.execute(args);
    }
    catch (CouldNotExecuteException exc) 
    {
      System.err.println("Execution exception. Aborting.");
      exc.printStackTrace();
      System.exit(1);
    }
  }

  @Override
  protected void init() throws Exception
  {
    // pyeID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFileIn = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file (in)")
      .withLongOpt("kmymoney-in-file")
      .create("if");
          
    Option optFileOut = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file (out)")
      .withLongOpt("kmymoney-out-file")
      .create("of");
      
    Option optID = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("UUID")
      .withDescription("Payee ID")
      .withLongOpt("payee-id")
      .create("id");
            
    Option optNumber = OptionBuilder
      .hasArg()
      .withArgName("number")
      .withDescription("Payee number")
      .withLongOpt("number")
      .create("num");
    	    
    Option optName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Payee name")
      .withLongOpt("name")
      .create("nam");
    
    Option optDescr = OptionBuilder
      .hasArg()
      .withArgName("descr")
      .withDescription("Payee description")
      .withLongOpt("description")
      .create("desc");
      
    // The convenient ones
    // ::EMPTY
          
    options = new Options();
    options.addOption(optFileIn);
    options.addOption(optFileOut);
    options.addOption(optID);
    options.addOption(optNumber);
    options.addOption(optName);
    options.addOption(optDescr);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    KMyMoneyWritableFileImpl kmmFile = new KMyMoneyWritableFileImpl(new File(kmmInFileName));

    try 
    {
      pye = kmmFile.getWritablePayeeByID(pyeID);
      System.err.println("Payee before update: " + pye.toString());
    }
    catch ( Exception exc )
    {
      System.err.println("Error: Could not find/instantiate payee with ID '" + pyeID + "'");
      // ::TODO
//      throw new PayeeNotFoundException();
      throw new NoEntryFoundException();
    }
    
    doChanges(kmmFile);
    System.err.println("Payee after update: " + pye.toString());
    
    kmmFile.writeFile(new File(kmmOutFileName));
    
    System.out.println("OK");
  }

  private void doChanges(KMyMoneyWritableFileImpl kmmFile) throws Exception
  {
    if ( name != null )
    {
      System.err.println("Setting name");
      pye.setName(name);
    }

    if ( descr != null )
    {
      System.err.println("Setting description");
      pye.setNotes(descr);
    }
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args) throws InvalidCommandLineArgsException
  {
    CommandLineParser parser = new GnuParser();
    CommandLine cmdLine = null;
    try
    {
      cmdLine = parser.parse(options, args);
    }
    catch (ParseException exc)
    {
      System.err.println("Parsing options failed. Reason: " + exc.getMessage());
    }

    // ---

    // <kmymoney-in-file>
    try
    {
      kmmInFileName = cmdLine.getOptionValue("kmymoney-in-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-in-file>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("KMyMoney file (in): '" + kmmInFileName + "'");
    
    // <kmymoney-out-file>
    try
    {
      kmmOutFileName = cmdLine.getOptionValue("kmymoney-out-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-out-file>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("KMyMoney file (out): '" + kmmOutFileName + "'");
    
    // <payee-id>
    try
    {
      pyeID = new KMMPyeID( cmdLine.getOptionValue("payee-id") );
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <payee-id>");
      throw new InvalidCommandLineArgsException();
    }
    System.err.println("Payee ID: " + pyeID);

    // <name>
    if ( cmdLine.hasOption("name") ) 
    {
      try
      {
        name = cmdLine.getOptionValue("name");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Name: '" + name + "'");

    // <description>
    if ( cmdLine.hasOption("description") ) 
    {
      try
      {
        descr = cmdLine.getOptionValue("description");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <description>");
        throw new InvalidCommandLineArgsException();
      }
    }
    System.err.println("Description: '" + descr + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "UpdPye", options );
  }
}
