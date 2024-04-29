package org.kmymoney.tools.xml.get.info;

import java.io.File;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.kmymoney.base.basetypes.simple.KMMInstID;
import org.kmymoney.tools.CommandLineTool;
import org.kmymoney.tools.xml.gen.complex.GenDepotTrx;
import org.kmymoney.tools.xml.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.kmymoney.api.read.KMyMoneyInstitution;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;

public class GetInstInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetInstInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String      kmmFileName = null;
  private static Helper.Mode mode        = null;
  private static KMMInstID   instID       = null;
  private static String      name        = null;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetInstInfo tool = new GetInstInfo ();
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
    // acctID = UUID.randomUUID();

//    cfg = new PropertiesConfiguration(System.getProperty("config"));
//    getConfigSettings(cfg);

    // Options
    // The essential ones
    Option optFile = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("file")
      .withDescription("KMyMoney file")
      .withLongOpt("kmymoney-file")
      .create("f");
      
    Option optMode = OptionBuilder
       .isRequired()
       .hasArg()
       .withArgName("mode")
       .withDescription("Selection mode")
       .withLongOpt("mode")
       .create("m");
        
    Option optInstID = OptionBuilder
      .hasArg()
      .withArgName("ID")
      .withDescription("Institution ID")
      .withLongOpt("institution-id")
      .create("inst");
          
    Option optName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Name (or part of)")
      .withLongOpt("name")
      .create("n");
          
    // The convenient ones
    // ::EMPTY
            
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optInstID);
    options.addOption(optName);
  }

  @Override
  protected void getConfigSettings(PropertiesConfiguration cs) throws Exception
  {
    // ::EMPTY
  }
  
  @Override
  protected void kernel() throws Exception
  {
    KMyMoneyFileImpl kmmFile = new KMyMoneyFileImpl(new File(kmmFileName));

    KMyMoneyInstitution inst = null;
    
    if ( mode == Helper.Mode.ID )
    {
      inst = kmmFile.getInstitutionByID(instID);
      if ( inst == null )
      {
        System.err.println("Could not find an institution with this ID.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.Mode.NAME )
    {
      Collection<KMyMoneyInstitution> cmdtyList = kmmFile.getInstitutionsByName(name); 
      if ( cmdtyList.size() == 0 )
      {
        System.err.println("Could not find institutions matching this name.");
        throw new NoEntryFoundException();
      }
      if ( cmdtyList.size() > 1 )
      {
        System.err.println("Found " + cmdtyList.size() + "institutions matching this name.");
        System.err.println("Please specify more precisely.");
        throw new TooManyEntriesFoundException();
      }
      inst = cmdtyList.iterator().next(); // first element
    }
    
    // ----------------------------

    try
    {
      System.out.println("ID:                '" + inst.getID() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("ID:                " + "ERROR");
    }

    try
    {
      System.out.println("toString:          " + inst.toString());
    }
    catch (Exception exc)
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("Name:              '" + inst.getName() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Name:              " + "ERROR");
    }

    try
    {
      System.out.println("Sort code:         " + inst.getSortCode());
    }
    catch (Exception exc)
    {
      System.out.println("Sort code:             " + "ERROR");
    }

    try
    {
      System.out.println("Address:           " + inst.getAddress());
    }
    catch (Exception exc)
    {
      System.out.println("Address:           " + "ERROR");
    }
    
    try
    {
      System.out.println("BIC:               '" + inst.getBIC() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("BIC:               " + "ERROR");
    }
    
    try
    {
      System.out.println("URL:               '" + inst.getURL() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("URL:               " + "ERROR");
    }
  }

  // -----------------------------------------------------------------

  @Override
  protected void parseCommandLineArgs(String[] args)
      throws InvalidCommandLineArgsException
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

    // <kmymoney-file>
    try
    {
      kmmFileName = cmdLine.getOptionValue("kmymoney-file");
    }
    catch (Exception exc)
    {
      System.err.println("Could not parse <kmymoney-file>");
      throw new InvalidCommandLineArgsException();
    }

    if (!scriptMode)
      System.err.println("KMyMoney file: '" + kmmFileName + "'");

    // <mode>
    try
    {
      mode = Helper.Mode.valueOf(cmdLine.getOptionValue("mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Mode:     " + mode);

    // <institution-id>
    if ( cmdLine.hasOption("institution-id") )
    {
      if ( mode != Helper.Mode.ID )
      {
        System.err.println("<institution-id> must only be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        instID = new KMMInstID( cmdLine.getOptionValue("institution-id") );
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <institution-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.Mode.ID )
      {
        System.err.println("<institution-id> must be set with <mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Institution ID: '" + instID + "'");

    // <name>
    if ( cmdLine.hasOption("name") )
    {
      if ( mode != Helper.Mode.NAME )
      {
        System.err.println("<name> must only be set with <mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        name = cmdLine.getOptionValue("name");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.Mode.NAME )
      {
        System.err.println("<name> must be set with <mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Name:     '" + name + "'");
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetInstInfo", options);
  }
}
