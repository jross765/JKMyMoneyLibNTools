package org.kmymoney.read;

public interface KMyMoneySecurity {

    // For the following types cf.:
    // https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h

    // ::MAGIC
    // ::TODO Convert to enum
    public static final int TYPE_STOCK       = 0;
    public static final int TYPE_MUTUAL_FUND = 1;
    public static final int TYPE_BOND        = 2;
    public static final int TYPE_CURRENCY    = 3;
    public static final int TYPE_NONE        = 4;

    // ---------------------------------------------------------------
    
}
