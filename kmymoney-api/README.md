# Major Changes 
## V. 0.3 &rarr; 0.4
**The** major change here: 

Write access to all supported entities (there are a few unsupported ones  left).

In addition to that:

* More interface methods (get/set).
* Fixed a couple of bugs.
* Renamed class `KMMCurrPair` to `KMMPricePairID`.

## V. 0.2 &rarr; 0.3
First version that you can seriously use.
We'll consider this a "good beta".

However, still only read-access.

## V. 0.1 &rarr; 0.2
(Pre-)Alpha.

# Planned
It should go without saying, but the following points are of course subject to change and by no means a promise that they will actually be implemented soon:

* Add support for other entities (institution, budget, etc.)

* Better test case coverage.

* Possibly some macro code (i.e., wrappers for generating specific variants), e.g. wrappers for:

  * booking dividend payments from a share.

* Possibly write a set of generally-usable command-line tools for basic handling of reading/writing activities, based on existing set of simple examples (but in a separate module).

* Last not least: Provide user documentation.

# Known Issues
* When you generate securities-related transactions (buy/sell shares, dividends etc.) with this library, then KMyMoney will show a warning triangle, even though everything is correctly set. It seems that it just wants these transactions to be explicitly checked by its internal engine, but I do not understand yet how and where exaclty it keeps track of that.

  Cf. example program `GenTrx` in sibling module `kmymoney-api-examples`.