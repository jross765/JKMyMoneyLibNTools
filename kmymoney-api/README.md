# Notes on the Module "API"

This is the core module of the project, providing all low-level read-/write access functions to a KMyMoney file.

## Major Changes 
### V. 0.5 &rarr; 0.6
* Finally added institutions and dependent code in other classes (e.g. `KMyMoney(Writable)Account(Impl)`).

* `KMyMoney(Writable)Security(Impl)`: New methods `get(Writable)StockAccounts()`, which is a handy short-cut for specific use cases.

* `KMyMoney(Writable)File(Impl)`: Implemented empty skeleton methods that had been forgotten and previously just returned null, such as `getAccountsByName()`.

* Added a few method implementations that had been forgotten here and there.

* Re-iterated over some code, e.g.:
	* `KMyMoney(Writable)Currency(Impl)`
	* `KMyMoneySecurityImpl`
	* `KMyMoney(Writable)FileImpl`
	* Improved overall robustness by more consistently checking method parameters.

* Finally completed list of internal manager classes `File[XYZ]Manager`.

* Minor changes in interfaces.

* Fixed a couple of bugs.

* Some code-cleanup here and there.

* Improved test coverage.

### V. 0.4 &rarr; 0.5
* Extracted some basic packages to new module "Base".

* Clean-up work, most of it under the hood.

* Improved test coverage.

### V. 0.3 &rarr; 0.4
**The** major change here: 

Write access to all supported entities (there are a few unsupported ones  left).

In addition to that:

* More interface methods (get/set).
* Fixed a couple of bugs.
* Renamed class `KMMCurrPair` to `KMMPricePairID`.

### V. 0.2 &rarr; 0.3
First version that you can seriously use.
We'll consider this version the first beta.

However, still only read-access.

### V. 0.1 &rarr; 0.2
(Pre-)Alpha.

## Planned
It should go without saying, but the following points are of course subject to change and by no means a promise that they will actually be implemented soon:

* Add support for other entities (institution, budget, etc.)

* Better test case coverage.

* Possibly write a set of generally-usable command-line tools for basic handling of reading/writing activities, based on existing set of simple examples (but in a separate module).

* Last not least: Provide user documentation.

## Known Issues
* Performance: When using the `Writable`-classes (i.e., generating new objects or changing existing ones), the performance is less-than-overwhelming, especially when working with larger files.

* Generating new objects currently only works (reliably) when at least one object of the same type (an institution, say) is already in the file.

* When you generate a price pair that does not exist yet (or a price for a price pair that does not exist yet), then it will be written into the file but not be visible in KMyMoney. In order to make it visible, you first have to generate the according currency.

  *Example*: As in the test data file, your standard currency is EUR, you have one foreign currency defined (USD) and a couple of securities. Now, you generate a price (pair) BRL/EUR (cf. example program `GenPrc`). Then, in KMyMoney, when you go to Tools  &rarr; Prices, this price will not be shown, although it's actually in the file. In order to make it visible, you go to Tools &rarr; Currencies, click the "add" button and add the Brazilian Real as a currency. Then, generated price will be visible.

* `KMyMoneyWritableAccounts`: Getting a list of writable account objects (we have several methods for this) takes very long. Please be patient.
