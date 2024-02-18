A Java-library for reading and writing the XML file format of the KMyMoney open source personal finance software.

# Compatibility
## System Compatibility
Version 0.4.0 of the library has been tested with KMyMoney 5.1.3 on Linux (locale de_DE) and OpenJDK 17.0.

## Locale/Language Compatibility
As far as the author knows, there should be no issues with other locales, but he has not tested it.

## Version Compatibility
| Version | Backward Compat. | Note                           |
|---------|------------------|--------------------------------|
| 0.4     | no               | Minor changes in interfaces    |
| 0.3     | no               | Major changes in interfaces    |
| 0.2     | no               | Major changes in interfaces    |

# Modules and Further Details

* [API](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api/README.md)

* [Example Programs](https://github.com/jross765/jkmymoneylib/tree/master/kmymoney-api-examples/README.md)

# Sister Project
This project has a sister project: 

[`JGnuCashLib`](https://github.com/jross765/jgnucashlib)

Both projects do not have the same level of maturity, `JGnuCashLib` is currently much more advanced than `JKMyMoneyLib`. Obviously, the author strives to keep both projects symmetrical and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that this project has literally evolved / is literally evolving from a source-code copy of its sister, `JGnuCashLib` (i.e., copy the code, then adapt it). Given that KMyMoney and GnuCash are two finance applications with quite a few similarities in their resp. business logics file formats, this approach makes sense. 

Of course, this is a "10.000-metre bird's-eye view". As always in life, things are not that easy as soon as you go into the details. However, looking at the big picture and at least up to the current state of development, the author has managed to keep both projects very similar on a source code level -- so much so that you can partially can use `diff`. You will, however, also see some exceptions here and there where that "low-level-symmetry" is not maintainable. But still, in many of these cases, we can at least maintain a kind of "high-level-symmetry" that prevails on a higher abtraction level.

# Acknowledgements

Special thanks to **Marcus Wolschon (Sofware-Design u. Beratung)** and **Deniss Larka** -- They don't / did not contribute directly to this project, but they did the pioneering and stewardship work of the sister project `JGnuCashLib` (and its predecessor, resp.) for quite a few years, long before the author got into it. This project heavily makes use of the approaches and techniques in said project.

