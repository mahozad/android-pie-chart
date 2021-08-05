A good book for 2D and 3D mathematics used in computer graphics (thanks to a comment of [this post](https://stackoverflow.com/q/57773649)):

[Graphics Gems 1st Edition](https://www.amazon.com/dp/0122861663) by Andrew S. Glassner (1993)  
See *Nice Numbers for Graph Labels (657)* for how to generate axis labels for charts.

Graphics Gems V. A Collection of Practical Techniques for the Computer Graphics Programmer

https://www.raywenderlich.com/142-android-custom-view-tutorial
https://developer.android.com/codelabs/advanced-android-kotlin-training-custom-views#0

[This video](https://youtu.be/jlKrTTdTCUE) explains the Android canvas in detail
and [this](https://youtu.be/H05mF0qrBVA)
and [this](https://youtu.be/4NNmMO8Aykw)

The difference between extending from *View* and extending from *Drawable* is that
unlike *View*, a *Drawable* does not have any facility to receive events
or otherwise interact with the user.

For unit testing a custom view see
https://stackoverflow.com/q/48965231/8583692 and
https://stackoverflow.com/q/38816098/8583692
also see https://android.googlesource.com/platform/cts/+/0ceca29/tests/tests/content/src/android/content/cts/ContextTest.java

We could have extended our custom view from `ViewGroup` or one of its subclasses
like `LinearLayout` instead of the `View` class.
In fact, there is a CSS flexbox implementation for android by Google called
[flexbox-layout](https://github.com/google/flexbox-layout).
In addition, the `LinearLayout` is pretty much like the CSS flexbox except that it
does not have the wrapping feature.
Plus, ConstraintLayout and its [Flow feature](https://bignerdranch.com/blog/constraintlayout-flow-simple-grid-building-without-nested-layouts/)
could also imitate the flexbox and its wrapping feature.

Also see [this](https://sriramramani.wordpress.com/2015/05/06/custom-viewgroups/) and
[this](https://dzone.com/articles/how-to-create-a-custom-layout-in-android-by-extend).
