# Selecting Prices (Users)

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


## Using the Tools

We use the test data file provided with module "API Extensions".[^2]
For test and illustration purposes, we have put securities into this file using various different systems (i.e., name spaces) -- something you normally would not do in real life.

There are several tools where you will have to specify a price,
but only one of them supports all variants that we will cover in this
section: `GetPrc`.
We have provided a wrapper shell script for it: `kmm_get_prc_info.sh`.

Then, you have several options:

* *Mode "ID"*:
  Specifying the price by its (pseudo-)technical ID.
  
  This variant has two sub-variants:
  
  * *Sub-mode "DIRECT"*:
    Specifying the (pseudo-)technical ID directly, using the syntax 
    `<from-sec-curr>:<to-curr>:<date>`.

    This approach always works. 
    However, in 
    case of an error, you won't get precise logs and could possibly spend a lot of time 
    finding it.

    Example:

    ```bash
    $ kmm_get_prc_info.sh \
        -f test.kmy \
        -psm ID \
        -pssm DIRECT \
        -prc USD:EUR:2023-11-01
    ```

    or 

    ```bash
    $ kmm_get_prc_info.sh \
        -f test.kmy \
        -psm ID \
        -pssm DIRECT \
        -prc E000003:EUR:2023-03-06
    ```

    In essence, with the direct method, we kind of "stubbornly" pretend not to know that the "technical" ID is, 
    in fact, a business-logic ID. Instead, we see the ID as just an unnecessarily long string
    that has no further meaning.

    The advantage of this "stubborn" approach is that you can use all provided tools
    somewhat symmetrically (remember that all other entities in 
    KMyMoney 
    do have genuine
    technical IDs).

  * *Sub-mode "INDIRECT"*:
    Specifying the (pseudo-)technical ID indirectly, using the various command line options.

    This approach leverages the 
    `JKMyMoneyLib`'s 
    built-in type-safety early in the execution
    and will thus lead to far more precise error logs in case of an error.

    Example:

    ```bash
    $ kmm_get_prc_info.sh \
        -f test.kmy \
        -psm ID \
        -pssm INDIRECT \
        -fsc CURRENCY:USD \
        -tc CURRENCY:EUR \
        -df ISO \
        -dat 2023-11-01
    ```
    
    or

    ```bash
    $ kmm_get_prc_info.sh \
        -f test.kmy \
        -psm ID \
        -pssm INDIRECT \
        -fsc SECURITY:E000003 \
        -tc CURRENCY:EUR \
        -df ISO \
        -dat 2023-03-06
    ```

    Notice the prefixes `CURRENCY` and `SECURITY`. They are not optional.

    In essence, with the indirect method, we "acknowledge" the semantics of the 
    pseudo-technical ID, as opposed the the direct method.
  
    (It is worth mentioning that the type-safety-advantage is not really that
    important in cases like these, at least if you look narrowly at it. But once
    you look at the bigger picture, you will see that there is another reason why
    the current maintainer chose to support both variants: In order to maintain symmetry/
    keep things consistent between this mechanism and the way you specify security IDs 
    in the sister project; Notice that in `JGnuCashLibNTools`, there actually is a more
    pronounced difference between the pros and cons of both methods. Cf. the sister project's 
    user documentation for details).
  
* *Mode "SEC_DATE"*:
  Specifying the price by the pair `(sec-curr-id/date)`, which uniquely identifies
  a price object. 
  
  This option is sort-of between the technical and the busines-logic layer.
  
  Example:

  ```bash
  $ kmm_get_prc_info.sh \
      -f test.kmy \
      -psm SEC_DATE \
      -fsc SECURITY:E000003 \
      -df ISO \
      -dat 2023-03-06
  ```

* *Mode "ISIN_DATE"*:
  The same as with mode "SEC_DATE" above, but instead of specifying the security by
  its technical ID, we take its business logic ID (the field "Code" in KMyMoney lingo), 
  i.e. its ISIN, CUSIP, SEDOL, WKN or similar official security identifiers (without 
  a market 
  prefix).

  This approach obviously only works when you actually have filled the field 
  "Code".
  However, once you have your data in order, it is the most convenient method.

  Example:

  ```bash
  $ kmm_get_prc_info.sh \
      -f test.kmy \
      -psm ISIN_DATE \
      -is DE000BASF111 \
      -df ISO \
      -dat 2023-03-06
  ```

  Notice that -- for the time being -- the exact same command is used when you have CUSIPs, SEDOLs, WKNs or something else in your "Code" field instead of ISINs. Still the above notation with "ISIN" etc. is used.[^4]


[^2]: The test data files of the other modules will almost certainly work as well -- they all are very similar.

[^4]: The current maintainer uses only ISINs in his own 
      personal finances'
      file, because the ISIN is the only truly global and stable identifier.
