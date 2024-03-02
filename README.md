Project JKMyMoneyLib
=====================

`JKMyMoneyLib` is a free and open-source Java-library for reading and writing the XML file format of the KMyMoney open source personal finance software (`kmymoney.org`).

It is not directly affiliated with / sponsored or coordinated by the developers of the KMyMoney project.

# Modules and Further Details

* [Base](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-base/README.md)

* [API](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api/README.md)

* [API Extensions](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api-ext/README.md)

* [Example Programs](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api-examples/README.md)

# Compatibility
## System Compatibility
Version 0.4 of the library has been tested with 
KMyMoney 5.1.3 on Linux (locale de_DE) and 
OpenJDK 17.0.

Java 11 or earlier won't work.

## Locale/Language Compatibility
As far as the author knows, there should be no issues with other locales, 
but he has not tested it.

## Version Compatibility
| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 0.5     | (w.i.p.)         |                                |
| 0.4     | no               | Minor changes in interfaces    |
| 0.3     | no               | Major changes in interfaces    |
| 0.2     | no               | Major changes in interfaces    |

# Major Changes
Here, only the top-level changes are mentioned. For more Details, cf. the README files of the resp. modules (links below).

## V. 0.4 &rarr; 0.5
Changed project structure:

* Introduced new module "Base" (spun off from "API").

	This was necessary because the author is using the new module in other, external projects (not published).

* Introduced new module "API Extensions"

	Currently, it contains just one class. That will (hopefully) grow.

* Improved symmetry with sister project.

## V. 0.3 &rarr; 0.4 and Before
Cf. the README file of modules "API" and "Example programs" (links below).

# Sister Project
This project has a sister project: 

[`JGnuCashLib`](https://github.com/jross765/jgnucashlib)

Both projects do not have the same level of maturity, `JGnuCashLib` is currently a little more 
advanced than `JKMyMoneyLib`. Obviously, the author strives to keep both projects symmetrical 
and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that this project has literally evolved / 
is literally evolving from a source-code copy of its sister, `JGnuCashLib` (i.e., copy the code, 
then adapt it). Given that KMyMoney and GnuCash are two finance applications with quite a few 
similarities (both in business logic and file format), this approach makes sense. 

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are a little more
complicated once you go into the details. Still, looking at the big picture and at least 
up to the current state of development, the author has managed to keep both projects very 
similar on a source code level -- so much so that you partially can use `diff`. You will, 
however, also see some exceptions here and there where that "low-level-symmetry" is not 
maintainable.

# Acknowledgements

Special thanks to **Marcus Wolschon (Sofware-Design u. Beratung)** and **Deniss Larka** -- 
they don't / did not contribute directly to this project, but they did the pioneering and 
stewardship work of the sister project `JGnuCashLib` (and its predecessor, resp.) for quite
 a few years, long before the author got into it. This project heavily makes use of the 
approaches and techniques in said project.

