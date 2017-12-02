# Custom Postfix Templates for Intellij IDEA

**Custom Postfix Templates** is an Intellij IDEA plugin that allows you to define your own custom [postfix templates](https://blog.jetbrains.com/idea/2014/03/postfix-completion/).
At the moment it supports the following programming languages: Java, JavaScript, Kotlin (untyped templates only).

![Screen Cast](https://github.com/xylo/intellij-postfix-templates/blob/master/videos/vid1/vid1.png)

## Download

You can download the plugin **Custom Postfix Templates** via *Settings → Plugins → Browse Repositories*.

## Usage

The plugin comes with a predefined set of templates for Java (see below) which can be immediatly applied in a Java files.
For instance, write 

    "1".toInt
    
in a Java file.  If the completion popup does not automatically show up, press *Ctrl+SPACE*.
Select the `.toInt` template and see how it is expanded.

## Preconfigured Java templates for Scala users and those who want to simplify their life

The following templates are shipped with the plugin and shall provide you with a template basis and with some useful examples:
* `.toByte`, `.toShort`, `.toChar`, `.toInt`, `.toLong`, `.toFloat`, `.toDouble`, `.format` - convert strings and numbers
* `.toList`, `.toSet`, `.toMap` - convert arrays, collections, iterables, and streams to lists, sets, or maps
* `.sort`, `.sortBy` - sort arrays, lists, and streams (by attribute)
* `.minBy`, `.maxBy` - find the minimum/maximum in arrays, collections, iterables, and streams 
* `.groupBy` - group arrays, collections, iterables, and streams by attribute
* `.exists`, `.forall` - test if one/all element(s) of an array, a collection, an iterable, or a stream hold(s) a given condition
* `.reverse` - reverse arrays and lists
* `.concat` - concatenate arrays, collections, and streams
* `.mkString` - join the elements (strings) of an array, a collection, an iterable, or a stream into one string by using a given separator
* `.stream` - convert iterable to stream
* `.map` - map the entries of lists, sets, and maps
* `.mapKeys` - map the keys of a map
* `.mapValues` - map the values of a map
* `.getOrElseUpdate` - return the map value of a given key or compute it and return it
* `.filter` - filter the elements of lists, sets, maps, and iterables
* `.reduce` - reduce the elements of arrays, collections, and iterables
* `.fold` - reduce the elements of arrays, collections, and iterables by using a neutral element (similar to Scala fold)
* `.find` - find an element in arrays, collections, iterables, and streams
* `.take` - take a certain number of elements from a stream
* `.drop` - skip a certain number of elements from a stream
* `.size` - get the length or an array
* `.get` - get an element of an array by index
* `.forEach` - iterate over arrays, and optionals
* `.apply` - apply a runnable, supplier, consumer, or predicate
* `.lines` - get the lines of text files, paths, input streams, and strings
* `.content` - get the text content of files, paths, input streams, and URLs
* `.bufferedReader` - create a buffered reader for an input steam
* `.r` - convert a string into a regular expression
* `.val` - extract the expression as Lombok value (similar to the `.var` template)
* `.new` - create a new instance of a class

The idea behind these templates is to bring a tiny bit of Scala feeling back to Java.

For Scala users there is the live template `_` which expands to `v -> v` to accelerate the creating of lambda expressions.

## Edit the templates

Go to the menu *Tools → Custom Postfix Templates → Edit Java Templates* to open an editor tab with the java templates.
Here you can easily change, remove, or add new templates matching your needs.
Note that you have to save the template file explicitly (via *Ctrl+S*) in order to update the postfix templates in the IDE.

The file may contain multiple template definitions of the form:
```
.TEMPLATE_NAME : TEMPLATE_DESCRIPTION
    MATCHING_TYPE1  →  TEMPLATE_CODE1
    MATCHING_TYPE2  →  TEMPLATE_CODE2
    ...
```
* The options for *MATCHING_TYPE* differ from language to language:
  * Java: The *MATCHING_TYPE* can be either a Java class name or one of the following special types:
    * `ANY` - any expression
    * `VOID` - any void expression
    * `NON_VOID` - any non-void expression
    * `ARRAY` - any Java array
    * `BOOLEAN` - boxed or unboxed boolean expressions
    * `ITERABLE_OR_ARRAY` - any iterable or array
    * `NOT_PRIMITIVE` - any non-primitive value
    * `NUMBER` - any boxed or unboxed number
    * `BYTE` - a boxed or unboxed byte value
    * `SHORT` - a boxed or unboxed short value
    * `CHAR` - a boxed or unboxed char value
    * `INT` - a boxed or unboxed int value
    * `LONG` - a boxed or unboxed long value
    * `FLOAT` - a boxed or unboxed float value
    * `DOUBLE` - a boxed or unboxed double value
    * `CLASS` - any class reference
  * JavaScript: The *MATCHING_TYPE* has to be `ANY`.
  * Kotlin: The *MATCHING_TYPE* has to be `ANY`.
* The *TEMPLATE_CODE* can be any text which may also contain template variables used as placeholder.
  * The following template variables have a special meaning:
    * `$expr$` - the expression the template shall be applied to
    * `$END$` - the final cursor position after the template application
  * All other variables will be replaced interactively during the template expansion.
    The variables have the following format:
    ```
    $NAME#NO:EXPRESSION:DEFAULT_VALUE$
    ```
    * *NAME* - name of the variable; use a `*` at the end of the name to skip user interaction
    * *NO* (optional) - number of the variable (defining in which order the variables are expanded)
    * *EXPRESSION* (optional) - a live template macro used to generate a replacement (e.g. `suggestVariableName()`)
    * *DEFAULT_VALUE* (optional) - a default value that may be used by the macro

* Template examples:
  * Artificial example showing variable reordering, variable reusage, interaction skipping, macros, and default values:
    ```
    .test : test
	    NON_VOID → "$user*#1:user()$: $second#3:className()$ + $first#2::"1st"$ + $first$" + $expr$
    ```
  * Real world example: Write a variable to the debug log, including the developer name, the class name, and method name:
    ```
    .logd : log a variable
        NON_VOID → Log.d("$user*:user():"MyTag"$", "$className*:className()$ :: $methodName*:methodName()$): $expr$="+$expr$);
    ```

While writing the templates you can use the code completion for class names, variable names, template macros and arrows (→).

## Upgrade / reset templates and configure the plugin

Go to *Settings → Editor → Custom Postfix Templates*.  Here you can chose between two different lambda styles and reset your templates to the predefined ones of the plugin.  Alternatively you can also upgrade your templates file by building a diff between the predefined ones and yours.

## Roadmap

### Version X

* Support for other languages

## Contribute

Any contributions are welcome.  Just fork the project, make your changes and create a pull request.

# See also
* [Feature request for custom postfix completion at jetbrains.com](https://youtrack.jetbrains.com/issue/IDEA-122443)
