package org.kmymoney.tools.xml.get.info;

import java.io.File;
import java.time.LocalDate;

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
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;
import org.kmymoney.base.basetypes.complex.KMMPriceID;
import org.kmymoney.base.basetypes.complex.KMMQualifCurrID;
import org.kmymoney.base.basetypes.complex.KMMQualifSecCurrID;
import org.kmymoney.tools.CommandLineTool;
import org.kmymoney.tools.xml.helper.CmdLineHelper;
import org.kmymoney.tools.xml.helper.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

public class GetPrcInfo extends CommandLineTool
{
  // Logger
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPrcInfo.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String               kmmFileName   = null;
  private static KMMPriceID           prcID         = null;
  private static KMMQualifSecCurrID   fromSecCurrID = null;
  private static KMMQualifCurrID      toCurrID      = null;
  private static Helper.DateFormat    dateFormat    = null;
  private static LocalDate            date          = null;
  
  private static boolean showQuotes = false;
  
  private static boolean scriptMode = false; // ::TODO

  public static void main( String[] args )
  {
    try
    {
      GetPrcInfo tool = new GetPrcInfo ();
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
      .create("if");
      
    Option optFromSecCurr= OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("sec/curr")
      .withDescription("From-commodity/currency")
      .withLongOpt("from-sec-curr")
      .create("fr");
    	          
    Option optToCurr = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("curr")
      .withDescription("To-currency")
      .withLongOpt("to-curr")
      .create("to");
    	    
    Option optDateFormat = OptionBuilder
      .hasArg()
      .withArgName("date-format")
      .withDescription("Date format")
      .withLongOpt("date-format")
      .create("df");
    	            
    Option optDate = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("date")
      .withDescription("Date")
      .withLongOpt("date")
      .create("dat");    	          
          
    // The convenient ones
    // ::EMPTY
            
    options = new Options();
    options.addOption(optFile);
    options.addOption(optFromSecCurr);
    options.addOption(optToCurr);
    options.addOption(optDateFormat);
    options.addOption(optDate);
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
    
    prcID = new KMMPriceID( fromSecCurrID, toCurrID, date );
    System.out.println("Price ID: " + prcID);

    KMyMoneyPrice prc = null;
    
    prc = kmmFile.getPriceByID(prcID);
    if ( prc == null )
    {
      System.err.println("Could not find a security with this ID.");
      throw new NoEntryFoundException();
    }
    
    // ----------------------------

    try
    {
      System.out.println("Parent price pair: '" + prc.getParentPricePair() + "'");
    }
    catch (Exception exc)
    {
      System.out.println("Parent price pair:  " + "ERROR");
    }

    try
    {
      System.out.println("toString:          " + prc.toString());
    }
    catch (Exception exc)
    {
      System.out.println("toString:          " + "ERROR");
    }
    
    try
    {
      System.out.println("From sec/curr:     " + prc.getFromSecCurrQualifID());
    }
    catch (Exception exc)
    {
      System.out.println("From sec/curr:     " + "ERROR");
    }

    try
    {
      System.out.println("To curr:           " + prc.getToCurrencyQualifID());
    }
    catch (Exception exc)
    {
      System.out.println("To curr:              " + "ERROR");
    }

    try
    {
      System.out.println("Date:              " + prc.getDate());
    }
    catch (Exception exc)
    {
      System.out.println("Date:              " + "ERROR");
    }

    try
    {
      System.out.println("Value:             " + prc.getValueFormatted());
    }
    catch (Exception exc)
    {
      System.out.println("Value:             " + "ERROR");
    }

    try
    {
      System.out.println("Source:            " + prc.getSource());
    }
    catch (Exception exc)
    {
      System.out.println("Source:            " + "ERROR");
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

    // <from-sec-curr>
    try
    {
      fromSecCurrID = KMMQualifSecCurrID.parse(cmdLine.getOptionValue("from-sec-curr")); 
      System.err.println("from-sec-curr: " + fromSecCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <from-sec-curr>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <to-curr>
    try
    {
      toCurrID = KMMQualifCurrID.parse(cmdLine.getOptionValue("to-curr")); 
      System.err.println("to-curr: " + toCurrID);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <to-curr>");
      throw new InvalidCommandLineArgsException();
    }
    
    // <date-format>
    dateFormat = CmdLineHelper.getDateFormat(cmdLine, "date-format");
    System.err.println("date-format: " + dateFormat);

    // <date>
    try
    {
      date = CmdLineHelper.getDate(cmdLine, "date", dateFormat); 
      System.err.println("date: " + date);
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <date>");
      throw new InvalidCommandLineArgsException();
    }
  }

  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("GetPrcInfo", options);
  }
}
