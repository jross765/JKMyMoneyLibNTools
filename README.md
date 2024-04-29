# Project "JKMyMoney Lib 'n' Tools"

`JKMyMoneyLib` is a free and open-source Java-library for reading and writing the XML 
file format of the KMyMoney open source personal finance software 
([kmymoney.org](https://kmymoney.org)).

It is not directly affiliated with / sponsored or coordinated by the developers of the 
KMyMoney project.

## Modules and Further Details

* [Base](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-base/README.md)

* [API](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api/README.md)

* [API Extensions](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api-ext/README.md)

* [Example Programs](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api-examples/README.md)

* [Tools](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-tools/README.md)

## Compatibility
### System Compatibility
Version 0.6 of the library has been tested with 
KMyMoney 5.1.3 on Linux (locale de_DE) and 
OpenJDK 17.0.

Java 11 or earlier won't work.

### Locale/Language Compatibility
As far as the author knows, there should be no issues with other locales, 
but he has not tested it.

### Version Compatibility

| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 0.6     | (WIP) almost?    | Only minor changes             |
| 0.5     | no               | Some substantial changes       |
| 0.4     | no               | Minor changes in interfaces    |
| 0.3     | no               | Major changes in interfaces    |
| 0.2     | no               | Major changes in interfaces    |

## Major Changes
Here, only the top-level changes on module-level are mentioned. For more Details, 
cf. the README files of the resp. modules (links above).

### V. 0.5 &rarr; 0.6 (WIP)
* Added module "Tools".

* New external dependency (outside of Maven central): [`SchnorxoLib`](https://github.com/jross765/schnorxolib), a small library that contains some auxiliary stuff that is used both in this and the sister project.

### V. 0.4 &rarr; 0.5
Changed project structure:

* Introduced new module "Base" (spun off from "API").

	This was necessary because the author is using the new module in other, external projects (not published).

* Introduced new module "API Extensions"

	Currently, this module it is very small. It will (hopefully) grow.

### V. 0.3 &rarr; 0.4 and Before
Cf. the README file of modules "API" and "Example programs" (links below).

## Level of Maturity
This software is still in its beta stage.

Although the author, at this stage, feels more or less comfortable with using this
library for write-access (given all the test cases he has contributed), he still 
recommends not just taking it and "wildly" changing things in your valuable KMyMoney
files that you may have been building for years or possibly even decades. Although 
he is using it for his own needs, he reckons that it still contains non-trivial bugs,
and it definitely has not been sufficiently exposed to real-world data yet to blindly 
rely on it working correctly in all conceivable edge and corner cases.

In other words: **Make backups before you use this lib!** Take your time and check
the generated/changed files thoroughly before moving on.

## Compiling the Sources
To compile the sources, do the following:

1) Make sure that you have Maven installed on your system.

2) Build and install [`SchnorxoLib`](https://github.com/jross765/schnorxolib) (cf. details there).

3) Clone this repository.

4) Check out the latest version tag. In this case: `V_1_5_0`.

   The author has, in the course of his professional career, met plenty of self-appointed super-pro developers 
   who do not seem to understand the concept of version tags and configuration management, 
   so please bear with him...

5) Compile the sources:
    a) Adapt the path to your local repository in *all* pom.xml files (search for "`schnorxolib-base-systemPath`").
    b) Type:

        `$ ./build.sh`

## Sister Project
This project has a sister project: 
[`JGnuCashLib`](https://github.com/jross765/jgnucashlib)

Both projects have roughly the same level of maturity, `JGnuCashLib` currently being a little 
more advanced than `JKMyMoneyLib`. Obviously, the author strives to keep both projects 
symmetrical and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that this project has, in the early
stages, literally evolved from a source-code copy of its sister, `JGnuCashLib`.
Meanwhile, changes and adaptations are going in both directions.
Given that KMyMoney and GnuCash are two finance applications with quite a few 
similarities (both in business logic and file format), this approach makes sense
and has been working well so far.

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are a little more
complicated once you go into the details. Still, looking at the big picture and at least 
up to the current state of development, the author has managed to keep both projects very 
similar on a source code level -- so much so that you partially can use `diff`. You will, 
however, also see some exceptions here and there where that "low-level-symmetry" is not 
maintainable.

## Acknowledgements

Special thanks to **Marcus Wolschon (Sofware-Design u. Beratung)** and **Deniss Larka** -- 
they don't / did not contribute directly to this project, but they did the pioneering and 
stewardship work of the sister project `JGnuCashLib` (and its predecessor, resp.) for quite
a few years, long before the author got into it. This project heavily makes use of the 
approaches and techniques in said project.

