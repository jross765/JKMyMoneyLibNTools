# Selecting Prices (Programmers)

Due to the specialness of 
prices, 
both on the technical level and on the business-logic level, there are several 
things you must understand/keep in mind before working with them.

In a nutshell, it boils down to the following:
There normally are technical IDs, and there are business logic IDs.
And sometimes, as here, there are pseudo-technical IDs, which in fact
are business-logic IDs.

Please read the document "ID Layers in 
KMyMoney" 
(folder `xsec`) first before 
you move on. It is important to understand the (non-)difference between 
KMyMoney's
technical and business-logic 
price IDs 
described there before reading the next section; 
otherwise it will probably confuse you.


## Specifying IDs in the API

I will be succint, because you are a developer and therefore should be
able to read code.

### ID Types

The module "Base" contains several ID types, most of them being
genuine technical ones. 

You will, however, also find: 

* `KMMPrcPrID` 
* `KMMPrcID` 

`KMMPrcPrID` stands for "price *pair* ID", which means something like:

* `USD:EUR` (the structure which contains all prices for USD in EUR).
* `E000001:EUR` (the structure which contains all prices for the 
  security with the ID `E000001` in EUR).

`KMMPrcID` stands for a "*complete* price ID", i.e. something like:

* `USD:EUR:2023-11-20` &rarr; the "parent" price pair being `USD:EUR`
* `E000001:EUR:2026-03-01` &rarr; the "parent" price pair being `E000001:EUR`

The point I want to make is: 
Do as if you did not know what exactly a 
`KMMPrcID` 
looks like; as if it were a 
padded number with a prefix
just as with all other 
KMyMoney 
objects. You don't "read" 
`Q000023`
(like: "understand" or "interpret" it),
do you? You just know it's there, get it from one method's output and put it into 
another one's args.
In short: *ignore its semantics*!

### Getting Price IDs

In short: The lib does not provide methods which you can get
*IDs* (yet). Instead, it provides methods which you can get 
*objects* with, which in turn, obviously, have an ID.

The class 
`KMyMoneyFileImpl` 
contains some methods for this:

* Get *one* price object by something:

  "Something" being an identifier, i.e. an ID object from above, 
  or a list of its consituents, or another (usually unique) criterion.

  * `getPricePairByID(...)`
  * `getPriceByID(...)`
  * `getPriceBySecIDDate(...)`
  * `getPriceByQualifSecIDDate(...)`
  * `getPriceByCurrIDDate(...)`
  * `getPriceByQualifCurrIDDate(...)`
  * `getPriceByCurrDate(...)`
  * `getPriceByQualifSecCurrIDDate(...)`

  They all return one 
  `KMyMoneyPrice` 
  object the ID of which you can get with the method 
  `getID()`.

* Get *several* price objects by something:

  * `getPricePairs()`
  * `getPrices()`
  * `getPricesBySecID(...)`
  * `getPricesByQualifSecID(...)`
  * `getPricesByCurrID(...)`
  * `getPricesByQualifCurrID(...)`
  * `getPricesByCurr(...)`
  * `getPricesByQualifSecCurrID(...)`

  They all return a *list* of 
  `KMyMoneyPrice` 
  objects.

Notice that there are some methods with `Qualif` in their name.
You, the programmer who uses the library, normally don't use these.
They are primarily written for me, the current maintainer, in 
order to be able to maintain maximum symmetry with the sister
project.

For the same reason, you normally will not use the "qualif" variants of
security/currency identifiers: `KMM(Qualif)SecID`, `KMM(Qualif)CurrID`, `KMMQualifSecCurrID`,
but just the simpler ones: `KMMSecID`, `KMMCurrID`.

Have a look at module "API Examples", program 
`GetPrcInfo` ::TODO
for a simple example on how to use them.

You will also find a more elaborate variant of it (with better code encapsulation)
in module "Tools", program 
`GetPrcList`.

Last not least: Have a look at the test cases for 
`KMyMoneyPriceImpl`.

### Selecting a Price Object with an ID

This section overlaps with the previos one, and there is a reason for it:

Once you have the 
`KMyMoneyPrice` 
object (or its ID, resp.), things are just as easy and straight forward as with
any other entity:
Either you already have the object (congrats), or you just have its ID (from a
mythical external source), and then you use:

`KMyMoneyFileImpl.getPriceByID(prcID)`.

Look at the example program in module "API Examples",
it is called
`GetPrcInfo`. ::TODO

(You will also find a more elaborate version of it
in module "Tools").
