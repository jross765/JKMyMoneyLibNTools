# Major Changes
Here, only the top-level changes on module-level are mentioned. 
For more details, cf. the README files of the resp. modules (links above).

## V. 2026-04 &rarr; 2026-06
* Overall / cross-module:
  * Introduced budgets.
  * Deprecated all `FixedPointNumber`-related stuff.
  * Partially re-implemented things so that `BigFraction` is used
    instead of `FixedPointNumber`.

* Parent repo (this one): Nothing special.

* Module "Base": Nothing special apart from above-mentioned.

* Module "API (Core)":
  * Maintenance.

* Module "API Specialized Entities: Maintenance.

* Module "API Extensions": Maintenance.

* Module "API Examples": 
  * New example programs for budgets.

* Module "Tools": 
  * New tools for budgets.
  * Maintenance.

* Module "Viewer":
  * Added french language files.
  * Generalized rendering of unbalanced and/or tagged transactions.
  * Maintenance.

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 0.10    |
| API (Core)               | 0.10    |
| API Specialized Entities | 0.4     |
| API Extensions           | 0.10    |
| API Examples             | 0.10    |
| Tools                    | 0.10    |
| Viewer                   | 1.3     |

## V. 0.9 &rarr; 2026-04
**Caution: With this release, the top-level version naming scheme has changed
in order to avoid confusion with the single modules' version numbers.**

* Parent repo (this one): Nothing special.

* Module "Base": Changes that improve the symmetry with the sister project.

* Module "API (Core)":
  * Loading files now shows progress bars in console (optional).
  * Bug fixes.
  * Maintenance.

* Module "API Specialized Entities: New.

* Module "API Extensions": Maintenance.

* Module "API Examples": 
  * New example program for API Specialized Entities".
  * New package structure to better reflect different modules.

* Module "Tools":
  * All tools now load files showing progress bars (cf. Module "API (Core)").
  * Maintenance.

* Module "Viewer":
  * Program now accepts various command line args, supporting start variants,
    supporting various additonal use cases.
  * Maintenance.

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 0.9     |
| API (Core)               | 0.9     |
| API Specialized Entities | 0.3     |
| API Extensions           | 0.9     |
| API Examples             | 0.9     |
| Tools                    | 0.9     |
| Viewer                   | 1.2     |

## V. 0.8 (RESTRUCT) &rarr; 0.9
**Caution: Please note that, due to the changes in the last major release 
(splitting up the one big repository in several smaller ones), 
from now on, each module is versioned on its own, and the overall project's version 
(0.9, in this case) 
need not be/is not identical to the single modules' versions any more.**

* Parent repo (this one): Finished restruct work, i.e. made the
  (Maven) modules' repos Git sub-modules as well.

* Module "Viewer": New.

* Module "API": Bug-fixes and mini-improvements.

* The other modules have changed only technically; essentially (i.e., code) unchanged:
  * "Base"
  * "API Examples"
  * "API Extensions"
  * "Tools"

Module versions:

| **Name**                 | **Version** |
|--------------------------|---------|
| Base                     | 0.8.1   |
| API (Core)               | 0.8.1   |
| API Extensions           | 0.8.1   |
| API Examples             | 0.8.1   |
| Tools                    | 0.8.1   |
| Viewer                   | 1.1.0   |

## V. 0.8 &rarr; 0.8 (RESTRUCT)
Split up the all-encompassing repository into several ones: One per module plus one for the parent (this one).

Apart from that, I have made *no relevant changes* (i.e. only small changes in the README-files etc., but not in the actual source code).

*Rationale*:

I know, that comes with some disadvantages, and there are quite a few people who would advise against it for valid reasons. 

That being said, life's not black and white, and while I acknowledge that having everything in one single repository makes things easier in the early stages of development, I am convinced that in the long run, the advantages of doing so will outweigh the disadvantages for the following reasons:

* The modules' rates of change will vary considerably (they already do, and they will problably do even more in the years to come).

* It feels odd *not* to have "API Examples" and "Tools" in separate repositories (and that's just the most obvious example).

* The measure will greatly facilitate accepting and managing future contributions from others (or possibly handing single modules completely over to others), which I currently would feel much more inclined to do for the modules "API Extensions" and "Tools" than for the other ones.
  
* Last not least, I manage some additional (unpublished) projects that way, and I would like to keep things consistent (you see, my day has only 24 hours just as yours, and I have other things to do...).

*History*:

I have made a clean cut:

* The top-level repository (this one) contains the whole history up to V. 0.8. 
* The newly-generated sub-repos contain no history until V. 0.8. But they will contain their respective module's history from that point onwards.

## V. 0.7 &rarr; 0.8
* Module "API":
  * Changes to support new file format (KMyMoney V. 5.2).
  * No support for file format from V. 5.1.x any more.

## V. 0.6 &rarr; 0.7
* Module "API":
  * Some bug-fixing and cleanup-work, making code more robust.
  * New functionalities.

* Module "API Extensions": 
  * New sub-module.
  * Expanded functionality of already-existing module.

* Module "Tools": 
  * New Tools
  * Maintenance.

## V. 0.5 &rarr; 0.6
* Added module "Tools".

* New external dependency (outside of Maven central): 
[`SchnorxoLib`](https://github.com/jross765/Schnorxolib), 
a small library that contains some auxiliary stuff that is used both in this and the sister project. Some of the code in the module "Base" has moved there.

## V. 0.4 &rarr; 0.5
Changed project structure:

* Introduced new module "Base" (spun off from "API").

	This was necessary because the author is using the new module in other, external projects (not published).

* Introduced new module "API Extensions"

	Currently, this module it is very small. It will (hopefully) grow.

## V. 0.3 &rarr; 0.4 and Before
Cf. the README file of modules "API" and "Example programs" (links below).
