Java SE Extensions
===

Copyright (C) 2013 Zhao Yi

Licensed under GNU GPL Version 3.

The aggregator of all Java SE Extension Modules.

zse-common
---
An extension to the most commonly used Java SE classes, such as utility classes
for manipulating objects, strings, etc., and a string-object conversion framework.

It is designed to be as simple as possible, without any dependencies on 3rd party
libraries.

zse-opt
---
Provides a simple and type-safe framework to manage application options.

An option is represented by an `Option` instance, which defines the option's
name, value type and default value. The `OptionManager` interface defines a set
of commonly used operations on `Option` instances, such as accessing values,
registering option change listeners, and storing options. There are two built-in
implementations of `OptionManager`: `PreferencesOptionManager` and
`PropertiesOptionManager`.

zse-swing
---
Contains useful classes for Java Swing GUI development, including new or improved
components, a simple parser for declarative GUI XML, and utility methods to switch
Look and Feel at runtime, etc.
