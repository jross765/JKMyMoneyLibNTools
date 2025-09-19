# Project "Java KMyMoney Lib 'n' Tools"

`JKMyMoneyLibNTools` 
is a free and open-source set of Java-libraries for reading and writing the XML file 
format of the 
KMyMoney open source personal finance software 
([kmymoney.org](https://kmymoney.org)).

This project is not directly affiliated with / sponsored or coordinated by the developers of the 
KMyMoney project.

## Modules and Further Details

* [Base](https://github.com/jross765/JKMyMoneyLibNTools/tree/master/kmymoney-base/README.md)

* [API](https://github.com/jross765/JKMyMoneyLibNTools/tree/master/kmymoney-api/README.md)

* [API Extensions](https://github.com/jross765/JKMyMoneyLibNTools/tree/master/kmymoney-api-ext/README.md)

* [Example Programs](https://github.com/jross765/JKMyMoneyLibNTools/tree/master/kmymoney-api-examples/README.md)

* [Tools](https://github.com/jross765/JKMyMoneyLibNTools/tree/master/kmymoney-tools/README.md)

## Compatibility
### System and Format Compatibility
Version 0.8 of the library has been tested with 
KMyMoney 5.2.1 on Linux (locale de_DE) and 
OpenJDK 21.0.

**Caution: Version 0.8 only works with files generated with recently-released KMyMoney V. 5.2.x! Files generated with V. 5.1.x are not supported any more.**

### Locale/Language Compatibility
As far as the author knows, there should be no issues with other locales, 
but he has not tested it.

### Version Compatibility

| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 0.8     | no               | File format change (KMyMoney V. 5.2.x), "medium" changes in interfaces |
| 0.7     | almost           | Some non-trivial changes, although not dramatic |
| 0.6     | almost           | Minor changes in interfaces    |
| 0.5     | no               | Some substantial changes       |
| 0.4     | almost           | Minor changes in interfaces    |
| 0.3     | no               | Major changes in interfaces    |
| 0.2     | no               | Major changes in interfaces    |

## Major Changes
Here, only the top-level changes on module-level are mentioned. For more Details, 
cf. the README files of the resp. modules (links above).

### V. 0.7 &rarr; 0.8
* Module "API":
  * Changes to support new file format (KMyMoney V. 5.2).
  * No support for file format from V. 5.1.x any more.

### V. 0.6 &rarr; 0.7
* Module "API":
  * Some bug-fixing and cleanup-work, making code more robust.
  * New functionalities.

* Module "API Extensions": 
  * New sub-module.
  * Expanded functionality of already-existing module.

* Module "Tools": 
  * New Tools
  * Maintenance.

### V. 0.5 &rarr; 0.6
* Added module "Tools".

* New external dependency (outside of Maven central): 
[`SchnorxoLib`](https://github.com/jross765/Schnorxolib), 
a small library that contains some auxiliary stuff that is used both in this and the sister project. Some of the code in the module "Base" has moved there.

### V. 0.4 &rarr; 0.5
Changed project structure:

* Introduced new module "Base" (spun off from "API").

	This was necessary because the author is using the new module in other, external projects (not published).

* Introduced new module "API Extensions"

	Currently, this module it is very small. It will (hopefully) grow.

### V. 0.3 &rarr; 0.4 and Before
Cf. the README file of modules "API" and "Example programs" (links below).

## Level of Maturity
This software is beta.

It is worth noting, though, that the author has been using both the published tools 
as well as some unpublished ones on a nearly daily basis 
for over a year now 
to facilitate and part-automate his 
private finances' 
accounting. This proves that the 
software is well-tested and stable enough for a real-world setting (as opposed to 
theoretical test cases and arbitrary examples).

Therefore, the author now feels confident not just to use the software in his own particular productive environment, but also to encourage others to use it. However, he is experienced a developer enough to know that there are other production environments and other use cases out there, and that only by further usage and testing by at least a handful of other users in real-world scenarios for a year or so, the software can mature to finally attain genuine "production-ready" status.

In short: You are encouraged to use this software, but be advised to use it under the following principles:

*  Consider the API's read-branch and the read-only tools to be safe (i.e., they are not only *called* read-only, but they actually *are*).

* As for the write-branch, take the usual precautions: 

  * Do not just take the software and "wildly" change things in your valuable 
    KMyMoney 
    files that you may have been building for years or possibly even decades. 
    It still might contain some non-trivial bugs, and you should not assume that 
    it works correctly in all conceivable edge and corner cases.
  * If you write your own tools, do not *change* the 
    KMyMoney 
    file but *generate a new one* instead (as done in the published tools) and 
    keep the old version for a while.
  * If you have to change your file, **make backups before you use this lib/these tools!** Take your time and check the generated/changed files thoroughly before moving on.

## Compiling the Sources
To compile the sources, do the following:

1) Make sure that you have Maven installed on your system.

2) Build and install [`SchnorxoLib`](https://github.com/jross765/Schnorxolib) (cf. details there).

3) Clone this repository.

4) Check out the latest version tag. In this case: `V_0_8_0`.

     The author has, in the course of his professional career, met plenty of self-appointed super-pro developers 
     who do not seem to understand the concept of version tags and configuration management, 
     so please bear with him for telling you the seemlingly obvious...

5) Compile the sources:

      a) Adapt the path to your local repository in *all* pom.xml files (search for "`schnorxolib-base-systemPath`").
      b) Type:

        `$ ./build.sh`

## Planned

### Overall
* Split up the all-encompassing repository into several ones: One per module plus one for the parent.

  I know, I know, that will come with some disadvantages, and there are people who would advise 
  against it, for valid reasons. But life is not black and white, and while I acknowledge that 
  having everything in one single repository makes things easier in the early stages of development, 
  I am convinced that in the long run, the advantages of doing so will outweigh the disadvantages.

* Possibly contribute some more Java tools / wrapper scripts that already exist in separate repositories that currently are not published.

### Module-Specific
Cf. the according module's README file (links above).

## Sister Project
This project has a sister project: 
[`JGnuCashLibNTools`](https://github.com/jross765/JGnuCashLibNTools)

By now, both projects have roughly the same level of maturity. 
Obviously, the author strives to keep both projects symmetrical.

What does "symmetry" mean in this context? It means that this project has 
literally evolved from a source-code copy of its sister, `JGnuCashLibNTools`.
Meanwhile, changes and adaptations are going in both directions.
Let's call this "coupled development". 
Given that KMyMoney and GnuCash are two finance applications with quite a few 
similarities (both in business logic and file format), this approach makes sense
and has been working well so far.

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are a little more
complicated once you go into the details. Still, looking at the big picture and at least 
up to the current state of development, the author has managed to keep both projects very 
similar on a source code level -- so much so that you partially can use `diff`. 
You will, however, also see some exceptions here and there where that "low-level-symmetry" 
is not maintainable.

## Acknowledgements

Special thanks to **Marcus Wolschon (Sofware-Design u. Beratung)** and **Deniss Larka** -- 
they don't / did not contribute directly to this project, but they did the pioneering and 
stewardship work of the sister project `JGnuCashLibNTools` (and its predecessor, resp.) for quite
a few years, long before the author got into it. This project heavily makes use of the 
approaches and techniques in said project.

