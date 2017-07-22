# Custom Postfix Templates for Intellij IDEA

**Custom Postfix Templates** is an Intellij IDEA plugin that allows you to define your own custom [postfix templates](https://blog.jetbrains.com/idea/2014/03/postfix-completion/).

## Download

You can download the plugin **Custom Postfix Templates** via *Settings → Plugins → Browse Repositories*.

## Usage

The plugin comes with a predefined set of templates (details below) which you can immediatly try out in a Java file.
For instance, type 

    "1".to
    
in a new line of a Java file and press tab.
A popup should appear showing you different conversion options (`toInt`, `toDouble`, ...).

## Preconfigured Java templates for Scala users and those who want to simplify their life

These templates provide some common Scala functions, such as:
* `toByte`, `toChar`, `toInt`, `toLong`, `toFloat`, `toDouble`
* `toList`, `toSet`, `toMap`
* `sort`, `sortBy`, `minBy`, `maxBy`, `groupBy`
* `exists`, `forall`
* `reverse`, `concat`
* Optional.`forEach`
* String.`r` to convert the string into a regular expression

I assume that most of the templates are also useful for Non-Scala users, since they provide a cleaner interface to convert values
or help further when certain expected methods are not available.

## Edit the templates

Go to *Tools → Custom Postfix Templates → Edit Java Templates* to open an editor tab with the java templates.
Here you can easily change, remove, or add new templates matching your needs.

The format of the file is very simple:
* Each postfix template definition starts with a `.` followed by the template name, the separator `:` and a template description.
* In the subsequent *mapping* lines you define in which cases the template is applicable and how it shall be replaced.
* Each *mapping* line consists of a type, followed by `→` and the template code used as replacement.
  * The type can be either a Java class name or one of the following special types:
    * `ARRAY` - denotes any Java array
    * `BOOLEAN` - denotes boxed or unboxed boolean expressions
    * `ITERABLE_OR_ARRAY` - denotes any iterable or array
    * `NON_VOID` - denotes any non-void expression
    * `NOT_PRIMITIVE` - denotes any non-primitive value
    * `NUMBER` - denotes any boxed or unboxed number
  * The template code can be any text that might contain the following template variables:
    * `$expr$` - denotes the expression the template shall be applied to
    * `$END$` - denotes the final cursor position after the template application

Ideally the editor should give you via syntax and error highlighting some hints if you make some syntax mistakes.
Moreover it should provide you with a code completion for Java class names and the given special types.

## Roadmap

* At the moment only Java templates are supported, but the goal is to extend the plugin to support more languages.
* Reformatting should result in a nice and clean document with aligned `→` characters.

## Contribute

Any contributions are welcome.  Just fork the project, make your changes and create a pull request.

# See also
* [feature request for custom postfix completion at jetbrains.com](https://youtrack.jetbrains.com/issue/IDEA-122443).
