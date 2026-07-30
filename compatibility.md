# Compatibility

## System and Format Compatibility
Version 2026-07
of the libs and tools has been tested with 
KMyMoney 5.2.2 
on Linux (locale de_DE) and 
OpenJDK 21.0.

**Caution: The lib and tools only work with files generated with KMyMoney V. 5.2.x. Files generated with V. 5.1.x are not supported.**

## Locale/Language Compatibility

* *API*:
  As far as the author knows, there is no language/locale-dependent stuff
  in the API (Core) as well as in the additions and tools. So, there should 
  be no issues with other locales.

  However, he has not tested that thoroughly.

* *Viewer*:
  The viewer supports the following locale languages:

  * English
  * French 
  * German

## Version Compatibility

| **Overall Version** | **Backward Compat.** | **Note**       |
|---------|---------|-----------------------------------------|
| 2026-07 | yes     | Only additions to and deprecations in interfaces |
| 2026-04 | no      | "Medium" changes in interfaces          |
| 0.9     | almost  | Minor changes in interfaces             |
| (older) |         | (Cf. Git history)                       |
