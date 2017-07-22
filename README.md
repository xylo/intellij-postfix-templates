# Custom Postfix Templates for Intellij IDEA

''Custom Postfix Templates'' is an Intellij IDEA plugin that allows the user to define custom [postfix templates](https://blog.jetbrains.com/idea/2014/03/postfix-completion/) for Java (and in future also for other languages).

TODO: update this README

At the moment IDEA does not support custom postfix templates.
But there is a [popular feature request](https://youtrack.jetbrains.com/issue/IDEA-122443) and
a ([pull request](https://github.com/JetBrains/intellij-community/pull/505) implementing the feature.

## Usage

* download one of the template files of this repository and save it as `.ideaPostfixTemplates` in your home directory
* start the IDEA verion with the custom-postfix-templates feature
* use your templates :)

In order to get new templates applied you have to restart IDEA.

## Java templates for Scala users

These templates provide some common Scala functions, such as:
* toByte, toChar, toInt, toLong, toFloat, toDouble
* toList, toSet, toMap
* sort, sortBy, minBy, maxBy, groupBy
* exists, forall
* reverse, concat
* Optional.forEach
* String.r to convert the string into a regular expression

I assume that most of the templates are also useful for Non-Scala users, since they provide a cleaner interface to convert values
or help further when certain expected methods are not available.

## Contribute

Any contributions are welcome.
Note that at the moment only Java templates are supported.
