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
import org.kmymoney.api.read.KMyMoneyPrice;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.InvalidQualifSecCurrIDException;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.kmymoney.tools.CommandLineTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.Helper;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetSecInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetSecInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String              kmmFileName = null;
  private static Helper.CmdtySecMode mode        = null;
  private static KMMSecID            secID       = null;
  private static String              isin        = null;
  private static String              name        = null;
  
  private static boolean showQuotes = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetSecInfo tool = new GetSecInfo ();
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
        
    Option optSecID = OptionBuilder
      .hasArg()
      .withArgName("ID")
      .withDescription("Security ID")
      .withLongOpt("security-id")
      .create("sec");
          
    Option optISIN = OptionBuilder
      .hasArg()
      .withArgName("isin")
      .withDescription("ISIN")
      .withLongOpt("isin")
      .create("is");
        
    Option optName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Name (or part of)")
      .withLongOpt("name")
      .create("n");
          
    // The convenient ones
    Option optShowQuote = OptionBuilder
      .withDescription("Show quotes")
      .withLongOpt("show-quotes")
      .create("squt");
            
    options = new Options();
    options.addOption(optFile);
    options.addOption(optMode);
    options.addOption(optSecID);
    options.addOption(optISIN);
    options.addOption(optName);
    options.addOption(optShowQuote);
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

    KMyMoneySecurity sec = null;
    
    if ( mode == Helper.CmdtySecMode.ID )
    {
      sec = kmmFile.getSecurityByID(secID);
      if ( sec == null )
      {
        System.err.println("Could not find a security with this ID.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.CmdtySecMode.ISIN )
    {
      sec = kmmFile.getSecurityByCode(isin);
      if ( sec == null )
      {
        System.err.println("Could not find securities with this ISIN.");
        throw new NoEntryFoundException();
      }
    }
    else if ( mode == Helper.CmdtySecMode.NAME )
    {
      Collection<KMyMoneySecurity> secList = kmmFile.getSecuritiesByName(name); 
      if ( secList.size() == 0 )
      {
        System.err.println("Could not find securities matching this name.");
        throw new NoEntryFoundException();
      }
      if ( secList.size() > 1 )
      {
        System.err.println("Found " + secList.size() + " securities matching this name.");
        System.err.println("Please specify more precisely.");
        throw new TooManyEntriesFoundException();
      }
      sec = secList.iterator().next(); // first element
    }
    
    // ----------------------------

    try
    {
      System.out.println("Qualified ID:      '" + sec.getQualifID() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Qualified ID:      " + "ERROR");
    }

    try
    {
      System.out.println("Symbol:            '" + sec.getSymbol() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Symbol:            " + "ERROR");
    }

    try
    {
      System.out.println("toString:          " + sec.toString());
    }
    catch (Exception exc)
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("Type:              " + sec.getType());
    }
    catch (Exception exc)
    {
      System.out.println("Type:              " + "ERROR");
    }

    try
    {
      System.out.println("Name:              '" + sec.getName() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Name:              " + "ERROR");
    }

    try
    {
      System.out.println("PP:                " + sec.getPP());
    }
    catch (Exception exc)
    {
      System.out.println("PP:                " + "ERROR");
    }

    try
    {
      System.out.println("SAF:               " + sec.getSAF());
    }
    catch (Exception exc)
    {
      System.out.println("SAF:               " + "ERROR");
    }

    try
    {
      System.out.println("Rounding method:   " + sec.getRoundingMethod());
    }
    catch (Exception exc)
    {
      System.out.println("Rounding method:   " + "ERROR");
    }

    try
    {
      System.out.println("Trading currency:  " + sec.getTradingCurrency());
    }
    catch (Exception exc)
    {
      System.out.println("Trading currency:  " + "ERROR");
    }

    try
    {
      System.out.println("Trading market:    '" + sec.getTradingMarket() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Trading market:    " + "ERROR");
    }

    // ---

    if ( showQuotes )
      showQuotes(sec);
  }

  // -----------------------------------------------------------------

  private void showQuotes(KMyMoneySecurity sec) throws InvalidQualifSecCurrIDException
  {
    System.out.println("");
    System.out.println("Quotes:");

    System.out.println("");
    System.out.println("Number of quotes: " + sec.getQuotes().size());
    
    System.out.println("");
    for (KMyMoneyPrice prc : sec.getQuotes())
    {
      System.out.println(" - " + prc.toString());
    }

    System.out.println("");
    System.out.println("Youngest Quote:");
    System.out.println(sec.getYoungestQuote());
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
      mode = Helper.CmdtySecMode.valueOf(cmdLine.getOptionValue("mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Mode:         " + mode);

    // <security-id>
    if ( cmdLine.hasOption("security-id") )
    {
      if ( mode != Helper.CmdtySecMode.ID )
      {
        System.err.println("<security-id> must only be set with <mode> = '" + Helper.CmdtySecMode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        secID = new KMMSecID( cmdLine.getOptionValue("security-id") );
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <security-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.CmdtySecMode.ID )
      {
        System.err.println("<security-id> must be set with <mode> = '" + Helper.CmdtySecMode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Security ID:  '" + secID + "'");

    // <isin>
    if ( cmdLine.hasOption("isin") )
    {
      if ( mode != Helper.CmdtySecMode.ISIN )
      {
        System.err.println("<isin> must only be set with <mode> = '" + Helper.CmdtySecMode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        isin = cmdLine.getOptionValue("isin");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <isin>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( mode == Helper.CmdtySecMode.ISIN )
      {
        System.err.println("<isin> must be set with <mode> = '" + Helper.CmdtySecMode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("ISIN:         '" + isin + "'");

    // <name>
    if ( cmdLine.hasOption("name") )
    {
      if ( mode != Helper.CmdtySecMode.NAME )
      {
        System.err.println("<name> must only be set with <mode> = '" + Helper.CmdtySecMode.NAME.toString() + "'");
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
      if ( mode == Helper.CmdtySecMode.NAME )
      {
        System.err.println("<name> must be set with <mode> = '" + Helper.CmdtySecMode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Name:         '" + name + "'");

    // <show-quotes>
    if (cmdLine.hasOption("show-quotes"))
    {
      showQuotes = true;
    }
    else
    {
      showQuotes = false;
    }

    if (!scriptMode)
      System.err.println("Show quotes: " + showQuotes);
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetSecInfo", options);
  }
}
