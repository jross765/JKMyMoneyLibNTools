package org.kmymoney.tools.xml.get.sonstige;

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
import org.apache.log4j.Logger;
import org.kmymoney.base.basetypes.complex.KMMComplAcctID;
import org.kmymoney.base.basetypes.simple.KMMSecID;
import org.kmymoney.tools.CommandLineTool;
import org.kmymoney.tools.xml.helper.Helper;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.cmdlinetools.CouldNotExecuteException;
import xyz.schnorxoborx.base.cmdlinetools.InvalidCommandLineArgsException;

import org.kmymoney.api.read.KMyMoneyAccount;
import org.kmymoney.api.read.KMyMoneySecurity;
import org.kmymoney.api.read.impl.KMyMoneyFileImpl;

public class GetStockAcct extends CommandLineTool
{
  // Logger
  private static Logger logger = Logger.getLogger(GetStockAcct.class);
  
  // private static PropertiesConfiguration cfg = null;
  private static Options options;
  
  private static String         kmmFileName = null;
  
  private static Helper.Mode    acctMode    = null;
  private static Helper.SecMode secMode     = null;
  private static KMMComplAcctID acctID      = null;
  private static String         acctName    = null;
  
  private static KMMSecID       secID       = null;
  private static String         isin        = null;
  private static String         secName     = null;
  
  private static boolean scriptMode = false;

  public static void main( String[] args )
  {
    try
    {
      GetStockAcct tool = new GetStockAcct ();
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
      
    Option optAcctMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode for account")
      .withLongOpt("account-mode")
      .create("am");
      
    Option optSecMode = OptionBuilder
      .isRequired()
      .hasArg()
      .withArgName("mode")
      .withDescription("Selection mode for security")
      .withLongOpt("security-mode")
      .create("sm");
        
    Option optAcctID = OptionBuilder
      .hasArg()
      .withArgName("acctid")
      .withDescription("Account-ID")
      .withLongOpt("account-id")
      .create("acct");
    
    Option optAcctName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Account name (or part of)")
      .withLongOpt("account-name")
      .create("an");
      
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
          
    Option optSecName = OptionBuilder
      .hasArg()
      .withArgName("name")
      .withDescription("Security name (or part of)")
      .withLongOpt("security-name")
      .create("sn");
            
    // The convenient ones
    Option optScript = OptionBuilder
      .withDescription("Script Mode")
      .withLongOpt("script")
      .create("sl");            
          
    options = new Options();
    options.addOption(optFile);
    options.addOption(optAcctMode);
    options.addOption(optSecMode);
    options.addOption(optAcctID);
    options.addOption(optAcctName);
    options.addOption(optSecID);
    options.addOption(optISIN);
    options.addOption(optSecName);
    options.addOption(optScript);
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

    KMyMoneyAccount acct = null;
    
    if (acctMode == Helper.Mode.ID)
    {
      acct = kmmFile.getAccountByID(acctID);
      if (acct == null)
      {
        if ( ! scriptMode )
          System.err.println("Found no account with that name");
        throw new NoEntryFoundException();
      }
    }
    else if (acctMode == Helper.Mode.NAME)
    {
      Collection<KMyMoneyAccount> acctList = null;
      acctList = kmmFile.getAccountsByTypeAndName(KMyMoneyAccount.Type.INVESTMENT, acctName, 
                                                  true, true);
      if (acctList.size() == 0)
      {
        if ( ! scriptMode )
          System.err.println("Found no account with that name.");
        throw new NoEntryFoundException();
      }
      else if (acctList.size() > 1)
      {
        if ( ! scriptMode )
        {
          System.err.println("Found " + acctList.size() + " accounts with that name.");
          System.err.println("Taking first one.");
        }
      }
      acct = acctList.iterator().next();
    }

    if ( ! scriptMode )
      System.out.println("Account:  " + acct.toString());
    
    // ----------------------------

    KMyMoneySecurity sec = null;
    
    if ( secMode == Helper.SecMode.ID )
    {
      sec = kmmFile.getSecurityByID(secID);
      if ( sec == null )
      {
        if ( ! scriptMode )
          System.err.println("Could not find a security with this ID.");
        throw new NoEntryFoundException();
      }
    }
    else if ( secMode == Helper.SecMode.ISIN )
    {
      sec = kmmFile.getSecurityByCode(isin);
      if ( sec == null )
      {
        if ( ! scriptMode )
          System.err.println("Could not find securities with this ISIN.");
        throw new NoEntryFoundException();
      }
    }
    else if ( secMode == Helper.SecMode.NAME )
    {
      Collection<KMyMoneySecurity> cmdtyList = kmmFile.getSecuritiesByName(secName); 
      if ( cmdtyList.size() == 0 )
      {
        if ( ! scriptMode )
          System.err.println("Could not find securities matching this name.");
        throw new NoEntryFoundException();
      }
      if ( cmdtyList.size() > 1 )
      {
        if ( ! scriptMode )
        {
          System.err.println("Found " + cmdtyList.size() + "securities matching this name.");
          System.err.println("Please specify more precisely.");
        }
        throw new TooManyEntriesFoundException();
      }
      sec = cmdtyList.iterator().next(); // first element
    }
    
    if ( ! scriptMode )
      System.out.println("Security: " + sec.toString());
    
    // ----------------------------
    
    for ( KMyMoneyAccount subAcct : acct.getChildren() ) {
      if ( subAcct.getType() == KMyMoneyAccount.Type.STOCK &&
           subAcct.getQualifSecCurrID().equals(sec.getQualifID()) ) {
          System.out.println(subAcct.getID());
      }
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

    // <script>
    if ( cmdLine.hasOption("script") )
    {
      scriptMode = true; 
    }
    // System.err.println("Script mode: " + scriptMode);
    
    // ---

    // <kmymoney-file>
    try
    {
      kmmFileName = cmdLine.getOptionValue("kmymoney-file");
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <kmymoney-file>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("KMyMoney file:     '" + kmmFileName + "'");
    
    // <account-mode>
    try
    {
      acctMode = Helper.Mode.valueOf(cmdLine.getOptionValue("account-mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <account-mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Account mode:  " + acctMode);

    // <security-mode>
    try
    {
      secMode = Helper.SecMode.valueOf(cmdLine.getOptionValue("security-mode"));
    }
    catch ( Exception exc )
    {
      System.err.println("Could not parse <security-mode>");
      throw new InvalidCommandLineArgsException();
    }
    
    if ( ! scriptMode )
      System.err.println("Security mode: " + secMode);

    // <account-id>
    if ( cmdLine.hasOption("account-id") )
    {
      if ( acctMode != Helper.Mode.ID )
      {
        System.err.println("<account-id> must only be set with <account-mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        acctID = new KMMComplAcctID( cmdLine.getOptionValue("account-id") );
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <account-id>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( acctMode == Helper.Mode.ID )
      {
        System.err.println("<account-id> must be set with <account-mode> = '" + Helper.Mode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Account ID:    '" + acctID + "'");

    // <account-name>
    if ( cmdLine.hasOption("account-name") )
    {
      if ( acctMode != Helper.Mode.NAME )
      {
        System.err.println("<account-name> must only be set with <account-mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        acctName = cmdLine.getOptionValue("account-name");
      }
      catch ( Exception exc )
      {
        System.err.println("Could not parse <name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( acctMode == Helper.Mode.NAME )
      {
        System.err.println("<account-name> must be set with <account-mode> = '" + Helper.Mode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }      
    }
    
    if ( ! scriptMode )
      System.err.println("Account name:  '" + acctName + "'");

    // <security-id>
    if ( cmdLine.hasOption("security-id") )
    {
      if ( secMode != Helper.SecMode.ID )
      {
        System.err.println("<security-id> must only be set with <security-mode> = '" + Helper.SecMode.ID.toString() + "'");
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
      if ( secMode == Helper.SecMode.ID )
      {
        System.err.println("<security-id> must be set with <security-mode> = '" + Helper.SecMode.ID.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Security ID:   '" + secID + "'");

    // <isin>
    if ( cmdLine.hasOption("isin") )
    {
      if ( secMode != Helper.SecMode.ISIN )
      {
        System.err.println("<isin> must only be set with <security-mode> = '" + Helper.SecMode.ISIN.toString() + "'");
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
      if ( secMode == Helper.SecMode.ISIN )
      {
        System.err.println("<isin> must be set with <security-mode> = '" + Helper.SecMode.ISIN.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("ISIN:          '" + isin + "'");

    // <security-name>
    if ( cmdLine.hasOption("security-name") )
    {
      if ( secMode != Helper.SecMode.NAME )
      {
        System.err.println("<security-name> must only be set with <security-mode> = '" + Helper.SecMode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
      
      try
      {
        secName = cmdLine.getOptionValue("security-name");
      }
      catch (Exception exc)
      {
        System.err.println("Could not parse <security-name>");
        throw new InvalidCommandLineArgsException();
      }
    }
    else
    {
      if ( secMode == Helper.SecMode.NAME )
      {
        System.err.println("<security-name> must be set with <security-mode> = '" + Helper.SecMode.NAME.toString() + "'");
        throw new InvalidCommandLineArgsException();
      }
    }

    if (!scriptMode)
      System.err.println("Security name: '" + secName + "'");
  }
  
  @Override
  protected void printUsage()
  {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "GetSubAcct", options );
    
    System.out.println("");
    System.out.println("Valid values for <account-mode>:");
    for ( Helper.Mode elt : Helper.Mode.values() )
      System.out.println(" - " + elt);

    System.out.println("");
    System.out.println("Valid values for <security-mode>:");
    for ( Helper.SecMode elt : Helper.SecMode.values() )
      System.out.println(" - " + elt);
  }
}
