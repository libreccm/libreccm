LibreCCM TestUtils
==================

The TestUtils module `ccm-testutils` package provides several utility 
 classes for writing tests for specific classes. This includes tests for 
the implementations of `equals` and `hashCode` as well as 
`toString`. Also a utility for testing datasets for DBUnit tests 
is provided.

Other modules should import this module for the test 
scope only:

```
<dependencies>
    ...
    <dependency>
        <groupId>org.libreccm</groupId>
        <artifactId>ccm-testutils</artifactId>
        <version>6.7.0-SNAPSHOT</version>
        <scope>test</scope>
    </dependency>
    ...
</dependencies>
```

At the moment the following utilities are provided:

* [DataSetsVerifier](./apidocs/index.html?org/libreccm/testutils/DatasetsVerifier.html)

* [EqualsVerifier](./apidocs/index.html?org/libreccm/testutils/EqualsVerifier.html)

* [ToStringVerifier](./apidocs/index.html?org/libreccm/testutils/ToStringVerifier.html)

