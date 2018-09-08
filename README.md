# Custom Postfix Templates for Intellij IDEA

**Custom Postfix Templates** is an Intellij IDEA plugin that allows you to define your own custom [postfix templates](https://blog.jetbrains.com/idea/2014/03/postfix-completion/).
At the moment it supports the following programming languages with : Java, Scala, Kotlin (untyped templates), Dart (untyped templates), and JavaScript (untyped templates).

## So what is the difference to IDEA's postfix templates?

Since IDEA 2018 you are now able to define your own postfix templates in the settings UI (*Editor → General → Postfix Templates*). However, this is a pretty new feature and it's less functional than this plugin. Here are some of the **advantages of this plugin**:

* You can **define different template rules for the same template name**, e.g. .toList should behave differently for arrays and for sets.
* You can **use template variables** (e.g. `$varName$`) which are filled by the user while applying the template.
* You can **use live template macros** to automatically fill some of the template variables (e.g. `$var:suggestVariableName()$`) as well as you can define default values.
* You can **restrict the availability of templates or template rules to the availability of certain classes or libraries** (e.g. expand `"test".val` to `val s = "test"` if Lombok is available).
* It allows you to **use static imports instead of class imports** (e.g. `array.toList` can be expanded to `asList(array)` instead of `Arrays.asList(array)` if you add `[USE_STATIC_IMPORTS]` to the rule).
* It **comes with 82 useful and editable postfix templates** for Java with in total 203 template rules, e.g.
  * `string.toInt` → `Integer.parse(string)`
  * `array.toList` → `Arrays.asList(array)`
  * `file.lines` → `Files.readAllLines(file.toPath(), Charset.forName("UTF-8"))`
  * `file.getName().val` → `final String name = file.getName();`
* The text based format for defining your templates allows you to **easily share** them via copy and paste.

## Screencast

![Screen Cast](https://github.com/xylo/intellij-postfix-templates/blob/master/videos/vid1/vid1.png)

## Download

You can download the plugin **Custom Postfix Templates** via *Settings → Plugins → Browse Repositories*.

## Usage

The plugin comes with a predefined set of templates for Java and Scala (see below) which can be immediatly applied in Java/Scala files.
For instance, write 

    "1".toInt
    
in a Java file.  If the completion popup does not automatically show up, press *Ctrl+SPACE*.
Select the `.toInt` template and see how it is expanded.

And if you want to see the template definition, just press *Alt+ENTER* in the completiion popup and select *Edit '.toInt' template*.

## Preconfigured Java templates which bring a tiny bit of the Scala/Kotlin feeling to Java

The following templates are shipped with the plugin and shall provide you with a template basis and with some useful examples:
* `.toByte`, `.toShort`, `.toChar`, `.toInt`, `.toLong`, `.toFloat`, `.toDouble`, `.format` - convert strings and numbers
* `.toList`, `.toSet`, `.toMap` - convert arrays, collections, iterables, and streams to lists, sets, or maps
* `.sort`, `.sortBy` - sort arrays, lists, and streams (by attribute)
* `.minBy`, `.maxBy` - find the minimum/maximum in arrays, collections, iterables, and streams 
* `.groupBy` - group arrays, collections, iterables, and streams by attribute
* `.exists`, `.forall` - test if one/all element(s) of an array, a collection, an iterable, or a stream hold(s) a given condition
* `.reverse` - reverse arrays, lists, and strings
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
* `.isEmpty` - null-safe isEmpty check for collections, maps and strings
* `.isNotEmpty` - null-safe isNotEmpty check for collections, maps and strings
* `.isBlank` - null-safe isBlank check strings
* `.isNotBlank` - null-safe isNotBlank check for strings
* `.apply` - apply a runnable, supplier, consumer, or predicate
* `.lines` - get the lines of text files, paths, input streams, and strings
* `.content` - get the text content of files, paths, input streams, and URLs
* `.inputStream` - get input stream of files, URLs, strings
* `.outputStream` - get output stream for files
* `.bufferedReader` - get buffered reader for files, input streams, and URLs
* `.bufferedWriter` - get buffered writer for files and output streams
* `.printStream` - get PrintStream for files and output streams
* `.r` - convert a string into a regular expression
* `.val` - extract the expression as value (similar to the `.var` template)
* `.new` - create a new instance of a class
* `.soutv` - print variable to System.out

The idea behind these templates is to bring a tiny bit of Scala feeling back to Java.

For Scala users there is the live template `_` which expands to `v -> v` to accelerate the creating of lambda expressions.

### Special templates for IDEA (plugin) developers

* `.toVirtualFile` - convert to virtual file
* `.toFile` - convert to file
* `.getAttributes` - get file attributes
* `.openInEditor` - open file in editor
* `.getVirtualFile` - get virtual file
* `.getDocument` - get IDEA document
* `.getPsiFile` - get PSI file
* `.getPsiJavaFile` - get PSI Java file
* `.getPsiPackage` - get PSI package
* `.getChildrenOfType` - get children of an PsiElement and of a certain type
* `.getModule` - get IDEA module
* `.getProject` - get IDEA project
* `.getFileEditorManager` - get FileEditorManager
* `.getPsiManager` - get PsiManager
* `.getPsiFileFactory` - get PsiFileFactory
* `.getProjectRootManager` - get ProjectRootManager
* `.getTemplateManager` - get TemplateManager
* `.getJavaPsiFacade` - get JavaPsiFacade
* `.runReadAction` - wrap in an runWriteAction(...) block
* `.runWriteAction` - wrap in an runWriteAction(...) block
* `.invokeLater` - wrap in an invokeLater(...) block
* `.showDiff` - open a diff view

## Edit the templates

Press *Shift+Alt+P* (or go to menu *Tools → Custom Postfix Templates → Edit Templates of Current Language*)
to open the custom postfix templates for the programming language in your current editor.
Here you can easily change, remove, or add new templates matching your needs.
Note that you have to save the template file explicitly (via *Ctrl+S*) in order to update the postfix templates in the IDE.

### Template definitions

The file may contain multiple template definitions of the form:
```
.TEMPLATE_NAME : TEMPLATE_DESCRIPTION
    TEMPLATE_RULE1
    TEMPLATE_RULE2
    ...
```
Each template definition consists of a template name, a template description and an arbitrary number of template rules.  The template name is used as key in the code completion and the template description is shown as hint in the code completion popup.  The template rules define on which types the template can be applied and how the application is performed.

### Simple template rules

A simple template rule has the form
```
    MATCHING_TYPE  →  TEMPLATE_CODE
```
whereas
* *MATCHING_TYPE* defines the type the template can be applied to, and
* *TEMPLATE_CODE* defines how the template is applied (how the expression is replaced).

The options for *MATCHING_TYPE* may differ from programming language to programming language:
* In **Java** the *MATCHING_TYPE* can be either a Java class name or one of the following special types:
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
  * `NUMBER_LITERAL` - any number literal
  * `BYTE_LITERAL` - a byte literal
  * `SHORT_LITERAL` - a short literal
  * `CHAR_LITERAL` - a char literal
  * `INT_LITERAL` - an int literal
  * `LONG_LITERAL` - a long literal
  * `FLOAT_LITERAL` - a float literal
  * `DOUBLE_LITERAL` - a double literal
  * `STRING_LITERAL` - a String literal
  * `CLASS` - any class reference
* In **Scala** the *MATCHING_TYPE* can be either a Java class name or one of the following special types:
  * `ANY` - any expression
  * `VOID` - any void (Unit) expression
  * `NON_VOID` - any non-void (non-Unit) expression
  * `BOOLEAN` - scala.Boolean or java.lang.Boolean
  * `NUMBER` - any Scala or Java number value
  * `BYTE` - scala.Byte or java.lang.Byte
  * `SHORT` - scala.Short or java.lang.Short
  * `CHAR` - scala.Char or java.lang.Char
  * `INT` - scala.Int or java.lang.Integer
  * `LONG` - scala.Long or java.lang.Long
  * `FLOAT` - scala.Float or java.lang.Float
  * `DOUBLE` - scala.Double or java.lang.Double
* In **JavaScript** the *MATCHING_TYPE* has to be `ANY`.
* In **Kotlin** the *MATCHING_TYPE* has to be `ANY`.
* In **Dart** the *MATCHING_TYPE* has to be `ANY`.

The *TEMPLATE_CODE* can be any text which may also contain template variables used as placeholder.
* Simple template variables have the format `$NAME$`.
* The following template variables have a special meaning:
  * `$expr$` - the expression the template shall be applied to
  * `$END$` - the final cursor position after the template application
* All other variables will be replaced interactively during the template expansion.
* If you want to change the order of variables, set default values or use live template macros for filling the variables automatically, you can use the following variable format:
  ```
  $NAME#NO:EXPRESSION:DEFAULT_VALUE$
  ```
  * *NAME* - name of the variable; use a `*` at the end of the name to skip user interaction
  * *NO* (optional) - number of the variable (defining in which order the variables are expanded)
  * *EXPRESSION* (optional) - a live template macro used to generate a replacement (e.g. `suggestVariableName()`)
  * *DEFAULT_VALUE* (optional) - a default value that may be used by the macro

Template examples:
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

While writing the templates you can use the code completion for completing class names, variable names, template macros and arrows (→).

### Advanced template rules

In the chapter above some options have been omitted for simplicity.  If you need more functionality here is the full format of template rules including two optional parameters:
```
    MATCHING_TYPE [REQUIRED_CLASS]  →  TEMPLATE_CODE [FLAG]
```
* *REQUIRED_CLASS* (optional) is a name of a class that needs to be available in the module to activate the template rule (see next section for a detailed explaination)
* *FLAG* (optional) can be one of the following flags:
  * [`USE_STATIC_IMPORTS`](#USE_STATIC_IMPORTS) - adds static method imports automatically if possible
  * `SKIP` - skips the rule

#### Writing library specific template rules via REQUIRED_CLASS

Sometimes you may want to write library specific template rules, i.e. rules that shall be only applied when a certain library is included in the project.  For instance, take a look at the `.val` template provided with this plugin:
```
.val : extract as value
	NON_VOID [lombok.val]    →  val $var:suggestVariableName()$ = $expr$;
	NON_VOID                 →  final $type*:expressionType(expr))$ $var:suggestVariableName()$ = $expr$;
```
It can be applied to any non-void expression and expands either to
```
val myVar = myExpression;
```
if lombok is available, or to
```
final MyType myVar = myExpression;
```
if you're using Java without lombok.

In this exmaple template the `[lombok.val]` part after the matching type is used to restrict the rule appliction to those cases where the class `lombok.val` is available in the class path.

In general you can use any class name between the square brackets you want to define a restriction on.

#### FLAGs

##### USE_STATIC_IMPORTS

If you tag a template rule with `[USE_STATIC_IMPORTS]` all static methods that are used will be automatically imported and your code gets more compact.  For instance, lets take the following template rule:
```
.toList : convert to List
	ARRAY  →  java.util.Arrays.asList($expr$) [USE_STATIC_IMPORTS]
```
Since the rule is tagged with `[USE_STATIC_IMPORTS]` expanding of `array.toList` does not lead to `Arrays.asList(array)` but to `asList(array)` and the following line is added to your import statements:
```
import static java.util.Arrays.asList;
```

## Upgrade / reset templates and configure the plugin

Go to *Settings → Editor → Custom Postfix Templates*.  Here you can chose between two different lambda styles and reset your templates to the predefined ones of the plugin.  Alternatively you can also upgrade your templates file by building a diff between the predefined ones and yours.

## Roadmap

### Version 2

Version 2 is already in work and will be released in the next weeks/months.

It will bring you the following features:
* It allows you to split your templates file into multiple ones.
* It allows you to import templates from web sources and local files.
* It solves the complicated update procedure of merging your templates with the ones from the plugin 
  by separating your templates from auto-updatable web templates
* You will be able to share your templates with others.

## Contribute

Any contributions are welcome.  Just fork the project, make your changes and create a pull request.

# See also
* [Feature request for custom postfix completion at jetbrains.com](https://youtrack.jetbrains.com/issue/IDEA-122443)
