A Java-library for reading and writing the XML file format of the KMyMoney open source personal finance software.

**Caution**: This software is pre-alpha.

# Sister Project
This project has a sister project: 

[`JGnuCashLib`](https://github.com/jross765/jgnucashlib)

Both projects do not on the same level of maturity, `JGnuCashLib` is currently much more advanced than JKMyMoneyLib. Obviously, the author strives to keep both projects symmetrical and to eventually have them both on a comparable level of maturity.

What is meant by "symmetry" is this context? It means that `JKMyMoneyLib` has literally evolved / is literally evolving from a source-code copy of its sister (i.e., copy the code, then adapt it). Given that KMyMoney and GnuCash are two finance applications with quite a few similarities in their resp. business logics file formats, this approach makes sense. 

Of course, this is a "10.000-metre bird's view". As always in life, things are not that easy as soon as you go into the details. However, looking at the big picture and generally speaking and at least up to the current state of development, the author has managed to keep both projects very similar on a source code level -- so much so that you will recognize the code and even can use `diff` here and there. You will see some exceptions here and there where that "low-level-symmetry" is not maintainable, of course. But still, in many of these cases, we can at least maintain a kind of "high-level-symmetry" that prevails on a higher abtraction level.

# Compatibility
Version 0.0.2 of the library has been tested with KMyMoney 5.1.3 on Linux (locale de_DE) and OpenJDK 18.0.

As far as the author knows, there should be no issues with other locales, but he has not tested it.

# Major Changes 
::TODO

# Planned
::TODO

# Acknowledgements
Special thanks to: 

* **Marcus Wolschon (Sofware-Design u. Beratung)** and **Deniss Larka** -- they don't / did not contribute directly to this project, but they did the pioneering on and stewardship of the sister project `JGnuCashLib` for quite a few years, long before the author got into it. This project heavily makes use of the approaches and techniques in said project.