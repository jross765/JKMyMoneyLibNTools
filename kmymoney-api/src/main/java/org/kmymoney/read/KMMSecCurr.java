package org.kmymoney.read;

public class KMMSecCurr {

    // Cf. https://github.com/KDE/kmymoney/blob/master/kmymoney/mymoney/mymoneyenums.h
    public static enum Type {
        STOCK,
        MUTUAL_FUND,
        BOND,
        CURRENCY,
        NONE,
    }

    // Cf. https://lxr.kde.org/source/office/kmymoney/kmymoney/mymoney/mymoneysecurity.cpp#0180
    public static enum RoundingMethod {
        NEVER,
        FLOOR,
        CEIL,
        TRUNCATE,
        PROMOTE,
        HALF_DOWN,
        HALF_UP,
        ROUND,
        UNKNOWN
    }

}
