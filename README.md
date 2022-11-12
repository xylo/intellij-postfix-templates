# Custom Postfix Templates for Intellij IDEA

**Custom Postfix Templates** is an Intellij IDEA plugin that allows you to define your own custom [postfix completion templates](https://blog.jetbrains.com/idea/2014/03/postfix-completion/).
At the moment it supports the following programming languages with : Java, Scala, SQL, PHP, Go, Groovy, Python, LaTeX, Kotlin (untyped templates), Dart (untyped templates), JavaScript (untyped templates), and Rust (untyped templates).

## So what is the difference to IDEA's postfix templates?

Since IDEA 2018 you are now able to define your own postfix templates in the settings UI (*Editor → General → Postfix Templates*). However, this is a pretty new feature and it's less functional than this plugin. Here are some of the **advantages of this plugin**:

* You can **define different template rules for the same template name**, e.g. .toList should behave differently for arrays and for sets.
* You can **use template variables** (e.g. `$varName$`) which are filled by the user while applying the template.
* You can **use live template macros** to automatically fill some of the template variables (e.g. `$var:suggestVariableName()$`) as well as you can define default values.
* You can **restrict the availability of templates or template rules to the availability of certain classes or libraries** (e.g. expand `"test".val` to `val s = "test"` if Lombok is available).
* It allows you to **use static imports instead of class imports** (e.g. `array.toList` can be expanded to `asList(array)` instead of `Arrays.asList(array)` if you add `[USE_STATIC_IMPORTS]` to the rule).
* It **comes with more than 500 editable postfix templates** with more than 700 template rules, e.g.
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

## Kinds of template files

There are three different types of template files:
* User template files: use them to define your own templates and/or override local or web template rules
* Local template files: loaded from the local file system, read-only, and updated automatically when an IDEA project is opened
* Web template files: loaded from the web, read-only, and updated automatically once a day

## Order of template files/rules

Template rules are applied in a first-come-first-serve manner, i.e., more specific rules/files should be placed above more general rules/files.
Reorder files in the tree by selecting them and by using the up/down buttons.

## Predefined web templates files

The plugin comes with a set of so-called "web template files" which provide in total [more than 200 useful templates](https://github.com/xylo/intellij-postfix-templates/wiki). 
While web template files are read-only and shall not be edited by the user because of automatic updates, you can still edit or deactivate templates of these files.

To change or deactivate a predefined template you just have to start the template name completion with *Ctrl+Space* and then press *ALT+Enter* and select the third item (*Edit .TEMPLATE_NAME template*).  The corresponding web template file is opened and you see the definition of the template rule.  Since you cannot this template file directly you have to override the template rule by pressing *Alt+Enter* and selecting *Override template rule*.  This overriding works in a way that your template rule needs to be loaded before the predefined template gets loaded.  This is done by adding your rule to a user template file which is placed above the predefined web template file in the plugin settings.  In case that you don't have a user template file which is loaded before, you are offered to create one.  After you selected an existing user template or created a new one the template rule to override is automatically added to this file and you can start adapting it.  To deactivate a template rule, replace the rigth side of the rule with *[SKIP]*. 

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
* *[`MATCHING_TYPE`](#matching_type)* defines the type the template can be applied to, and
* *[`TEMPLATE_CODE`](#template_code)* defines how the template is applied (how the expression is replaced).

#### MATCHING_TYPE

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
* In **SQL** the *MATCHING_TYPE* can be either a Java class name or one of the following special types:
  * `ANY` - any expression
  * `UNKNOWN` - unknown expression
  * `DEFAULT` - ?
  * `INTEGER` - integer expression
  * `REAL` - real expression
  * `STRING` - string expression
  * `BOOLEAN` - boolean expression
  * `DATE_TIME` - date-time expression
  * `DATE` - date expression
  * `TIME` - time expression
  * `TIMESTAMP` - timestamp expression
  * `INTERVAL` - interval expression
  * `BYTES` - bytes expression
  * `REFERENCE` - ?
  * `ARRAY` - array expression
  * `COLLECTION` - collection expression
  * `TABLE` - table reference
  * `RECORD` - ?
  * `SETO` - ?
* In **PHP** the *MATCHING_TYPE* can be either a PHP class name or one of the following special types:
  * `ANY` - any expression
  * `empty`
  * `null`
  * `string`
  * `boolean`
  * `int`
  * `float`
  * `object`
  * `callable`
  * `resource`
  * `array`
  * `iterable`
  * `number`
  * `void`
  * `unset`
  * `static`
  * `\Closure`
  * `\Exception`
  * `\Throwable`
* In **Go** the *MATCHING_TYPE* can be one of the following special types:
  * `ANY` - any expression
  * `ARRAY` - any array
  * `BOOLEAN` - any boolean expression
  * `STRING` - any string expression
  * `INT` - any integer expression
  * `INT64` - any 64 bit integer expression
  * `UINT` - any unsigned integer expression
  * `FLOAT` - any floating point expression
  * `FLOAT32` - any 32 bit floating point expression
  * `FLOAT64` - any 64 bit floating point expression
  * `BYTESLICE` - any byte slice expression
  * `ERROR` - any error expression
  * `COMPLEX` - ???
  * `NIL` - any expression of type Nil
* In **Groovy** the *MATCHING_TYPE* can be either a Java/Groovy class name or one of the following special types:
  * `ANY` - any expression
  * `ARRAY` - any Java array
  * `BOOLEAN` - boxed or unboxed boolean expressions
  * `ITERABLE_OR_ARRAY` - any iterable or array
  * `NUMBER` - any boxed or unboxed number
  * `BYTE` - a boxed or unboxed byte value
  * `SHORT` - a boxed or unboxed short value
  * `CHAR` - a boxed or unboxed char value
  * `INT` - a boxed or unboxed int value
  * `LONG` - a boxed or unboxed long value
  * `FLOAT` - a boxed or unboxed float value
  * `DOUBLE` - a boxed or unboxed double value
  * `CLASS` - any class reference
* In **Python** the *MATCHING_TYPE* can be one of the following special types:
  * `ANY` - any expression
	* `object`
	* `list`
	* `dict`
	* `set`
	* `tuple`
	* `int`
	* `float`
	* `complex`
	* `str`
	* `unicode`
	* `bytes`
	* `bool`
	* `classmethod`
	* `staticmethod`
	* `type`
* In **Kotlin** the *MATCHING_TYPE* has to be `ANY`.
* In **Dart** the *MATCHING_TYPE* has to be `ANY`.
* In **JavaScript** the *MATCHING_TYPE* has to be `ANY`.
* In **Rust** the *MATCHING_TYPE* has to be `ANY`
* In **Latex** the *MATCHING_TYPE* can be one of the following types:
  * `ANY` - any expression in any context
  * `TEXT` - any expression that is *not* within a math environment
  * `MATH` - any expression that *is* within a math environment

#### TEMPLATE_CODE

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
* If you want to create multi-line templates you can use a backslash (`\`) at the end of a line to indicate that the template code continues at the next line.

#### Template Examples

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
* Multi-line template:
  ```
  .for : iterate over ...
      ITERABLE_OR_ARRAY → for ($ELEMENT_TYPE:iterableComponentType(expr):"java.lang.Object"$ $VAR:suggestVariableName()$ : $expr$) {\
        $END$\
      }
  ```

While writing the templates you can use the code completion for completing class names, variable names, template macros and arrows (→).

### Advanced template rules

In the chapter above some options have been omitted for simplicity.  If you need more functionality here is the full format of template rules including two optional parameters:
```
    MATCHING_TYPE [REQUIRED_CLASS]  →  TEMPLATE_CODE [FLAG]
```
* *REQUIRED_CLASS* (optional) is a name of a class that needs to be available in the module to activate the template rule (see next section for a detailed explaination)
* *FLAG* (optional) can be one of the following flags:
  * [`SKIP`](#skip) - skips the rule
  * [`USE_STATIC_IMPORTS`](#use_static_imports) - adds static method imports automatically if possible (Java only)
  * [`IMPORT` ...](#import) - adds an import to the file header (Scala only)

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

##### SKIP

You can use the `[SKIP]` flag for deactivating the template rule for a given matching type.

Example:
```
.sort : sort naturally
	de.endrullis.lazyseq.LazySeq  →  [SKIP]
	java.util.List                →  java.util.Collections.sort($expr$)
```

In this example a postfix template `.sort` is defined.
The first rule tells the plugin that there shall be no completition for expressions of type `LazySeq`.
The second rule defines how `List` expressions shall be completed.

##### USE_STATIC_IMPORTS

If you tag a template rule for Java with `[USE_STATIC_IMPORTS]` all static methods that are used will be automatically imported and your code gets more compact.  For instance, lets take the following template rule:
```
.toList : convert to List
	ARRAY  →  java.util.Arrays.asList($expr$) [USE_STATIC_IMPORTS]
```
Since the rule is tagged with `[USE_STATIC_IMPORTS]` expanding of `array.toList` does not lead to `Arrays.asList(array)` but to `asList(array)` and the following line is added to your import statements:
```
import static java.util.Arrays.asList;
```

##### IMPORT

If you tag a template rule for Scala with `[IMPORT FULLY_QUALIFIED_CLASSNAME]` the given class (or method) import is automatically added to the file header when the template gets applied:
```
.printStream : get PrintStream
	java.io.File  →  new PrintStream($expr$)   [IMPORT java.io.PrintStream]
```
Note that you can use the `IMPORT` flag multiple times.

## Update templates and open plugin settings

Go to *Settings → Editor → Custom Postfix Templates* or *Tools → Custom Postfix Templates → Open Settings / Upgrade Templates*.  There you can chose between two different lambda styles and check/uncheck the template files you want to enable/disable.

## Contribute

Any contributions are welcome.  Just fork the project, make your changes and create a pull request.

Here are some guides:
* [Create a template file for a utility class/library](https://github.com/xylo/intellij-postfix-templates/wiki/Create-a-template-file-for-a-utility-class-library)
* [Add new language support](https://github.com/xylo/intellij-postfix-templates/wiki/Add-new-language-support)

# See also
* [Feature request for custom postfix completion at jetbrains.com](https://youtrack.jetbrains.com/issue/IDEA-122443)
