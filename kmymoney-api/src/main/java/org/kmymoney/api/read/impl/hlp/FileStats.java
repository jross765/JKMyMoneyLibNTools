package org.kmymoney.api.read.impl.hlp;

// Statistics methods (for test purposes)
public interface FileStats {
    
    public int ERROR = -1; // ::MAGIC
    
    // ---------------------------------------------------------------

    int getNofEntriesAccounts();

    int getNofEntriesTransactions();

    int getNofEntriesTransactionSplits();

    // ----------------------------
    
    int getNofEntriesPayees();
    
    // ----------------------------
    
    int getNofEntriesSecurities();
    
    int getNofEntriesCurrencies();
    
    // ----------------------------
    
    int getNofEntriesPricePairs();

    int getNofEntriesPrices();

}
