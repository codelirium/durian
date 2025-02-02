# Durian releases

### Version 3.2.0 - TBD ([javadoc](http://diffplug.github.io/durian/javadoc/snapshot/))

### Version 3.1.1 - July 27th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/3.1.1/), [jcenter](https://bintray.com/diffplug/opensource/durian/3.1.1/view))

* Gah! MANIFEST.MF still had -SNAPSHOT version.  Fixed now.  Would be really nice if we could get MANIFEST.MF generation working.

### Version 3.1 - July 24th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/3.1/), [jcenter](https://bintray.com/diffplug/opensource/durian/3.1/view))

* When `Errors.asRuntime()` wraps an exception as a `RuntimeException`, it now uses `Errors.WrappedAsRuntimeException` rather than a generic `RuntimeException`.

### Version 3.0 - July 19th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/3.0/), [jcenter](https://bintray.com/diffplug/opensource/durian/3.0/view))

* Now available on Maven Central!
* Merged `GetterSetter` and `Box` into just `Box`.
* Rather than `Box` and `Box.NonNull extends Box`, we now have `Box` and `Box.Nullable`.
	+ Non-null by default is much better.
	+ There should not be an inheritance hierarchy between the nullable and non-null versions, and now there isn't.
* Added a mechanism for testing trees for equality.
* Other miscellaneous changes.

### Version 2.0 - May 13th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/2.0/), [jcenter](https://bintray.com/diffplug/opensource/durian/2.0/view))

* Renamed ErrorHandler to Errors.  This was done mainly to avoid name conflicts with the many other ErrorHandler classes that are out in the wild, but it also has the advantage of being shorter.

### Version 1.0.1 - May 13th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/1.0.1/), [jcenter](https://bintray.com/diffplug/opensource/durian/1.0.1/view))

* The Maven POM was missing the FindBugs annotations, which was causing compile warnings for users of the library.  It now includes them under the proper 'provided' scope.

### Version 1.0 - May 13th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/1.0/), [jcenter](https://bintray.com/diffplug/opensource/durian/1.0/view))

* First stable release.

### Version 0.1 - April 20th 2015 ([javadoc](http://diffplug.github.io/durian/javadoc/0.1/), [jcenter](https://bintray.com/diffplug/opensource/durian/0.1/view))

* First release, to test out that we can release to jcenter and whatnot.
