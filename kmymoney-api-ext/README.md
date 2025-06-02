# Notes on the Module "API Extensions"

This module provides simplified, high-level access functions to a 
KMyMoney 
file via the "API" module (sort of "macros") for specialized, complex tasks.

## Sub-Modules
Currently, the module consists of two sub-modules:

* "SecAcct"
* "TrxMgr"

### SecAcct
This sub-module contains classes that provide a simplified, high-level interface for...

* ...generating and maintaining stock accounts,
* ...generating buy- and dividend transactions in a securities account (brokerage account).

### TrxMgr
This sub-module contains classes that help to...

* ...find transaction and splits by setting filter criteria,
* ...merge stock account transcations,
* ...generally manipulate transactions in a more convenient way than by using the pure API.

## Major Changes
### V. 0.6 &rarr; 0.7
* Added sub-module TrxMgr.
  * New: `Transaction(Split)Filter`
  * New: `TransactionFinder`
  * New: `TransactionManager`, `TransactionMergerXYZ` (the latter in two variants)

### V. 0.5 &rarr; 0.6
* Sub-module SecAcct:
  * Added support for stock splits / reverse splits.
  * Added helper class that filters out inactive stock accounts.
  * Added `WritableSecuritiesAccountManager` (analogous to separation in module "API").

### V. 0.4 &rarr; 0.5
Created module.

## Planned
* Sub-module SecAcct: 
	* More variants of buy/sell/dividend/etc. transactions, including wrappers which you provide account names to instead of account IDs.
	* Possibly new class for high-level consistency checks of existing transactions, e.g.: All dividends of domestic shares are actually posted to the domestic dividend account.

* New sub-module for accounting-macros, such as closing the books.

* New sub-module for management of securities and currencies (esp. bulk quote import).

## Known Issues
(None)

