# History of notable changes introduced in each version

## v0.6.2 (2021-08-25)
#### Bug fixes
  - Fix the bug with single-slice pie not drawn ([`d79fdf15`](https://github.com/mahozad/android-pie-chart/commit/d79fdf15))
  - Fix the problem with legend box border ([`3b9d942a`](https://github.com/mahozad/android-pie-chart/commit/3b9d942a))
#### Other
  - Update showcase app to show random charts on click ([`cbd2c212`](https://github.com/mahozad/android-pie-chart/commit/cbd2c212))

[All commits since version 0.6.1](https://github.com/mahozad/android-pie-chart/compare/v0.6.1...v0.6.2)

## v0.6.1 (2021-08-22)
#### Bug fixes
  - Fix the bug with some properties change having no effect in the chart ([`eb17cf50`](https://github.com/mahozad/android-pie-chart/commit/eb17cf50))
#### Other
  - Migrate the showcase app to Jetpack Compose ([`66abcd04`](https://github.com/mahozad/android-pie-chart/commit/66abcd04))

[All commits since version 0.6.0](https://github.com/mahozad/android-pie-chart/compare/v0.6.0...v0.6.1)

## v0.6.0 (2021-08-21)
#### New features
  - Add resource version of properties ([`0885cf07`](https://github.com/mahozad/android-pie-chart/commit/0885cf07) and other commits)
  - Add label offset property to *Slice* class ([`a8755a65`](https://github.com/mahozad/android-pie-chart/commit/a8755a65))
#### Updates
  - Changes to chart properties now work (multiple commits)
  - Update dimension properties to be of type *Dimension* ([`c7b1b5e5`](https://github.com/mahozad/android-pie-chart/commit/c7b1b5e5))
#### Bug fixes
  - Fix the bug with first slice of the pie ([`6e50d892`](https://github.com/mahozad/android-pie-chart/commit/6e50d892))

[All commits since version 0.5.0](https://github.com/mahozad/android-pie-chart/compare/v0.5.0...v0.6.0)

## v0.5.0 (2021-08-01)
#### New features
  - Legends ([`17d74f86`](https://github.com/mahozad/android-pie-chart/commit/17d74f86) and other commits)  
    Including properties and attributes for:
    - Legends automatic wrapping
    - Legends box (enable/disable, title, background, border, color, position, alignment)
    - Legends arrangement (horizontal/vertical)
    - Legends percentage (enable/disable, size, color)
    - Legends alignment
    - Legends icons (default icons, tint, height, margin from legend text)
  - Center label ([`5907b86d`](https://github.com/mahozad/android-pie-chart/commit/5907b86d))
  - Center background ([`f88d28c5`](https://github.com/mahozad/android-pie-chart/commit/f88d28c5))
#### Updates
  - Update some library dependencies ([`e9420e85`](https://github.com/mahozad/android-pie-chart/commit/e9420e85))
  - Update Kotlin version ([`ce8cda52`](https://github.com/mahozad/android-pie-chart/commit/ce8cda52))
#### Other
  - Fix empty javadoc jar by including dokka documentation in it ([`87195e51`](https://github.com/mahozad/android-pie-chart/commit/87195e51))

[All commits since version 0.4.0](https://github.com/mahozad/android-pie-chart/compare/v0.4.0...v0.5.0)

## v0.4.0 (2021-07-04)
#### New features
  - Outside inward circular label ([`3bad21df`](https://github.com/mahozad/android-pie-chart/commit/3bad21df))
  - Outside outward circular label ([`3bad21df`](https://github.com/mahozad/android-pie-chart/commit/3bad21df))
  - Attribute to change label icon tint for all slices ([`891f77be`](https://github.com/mahozad/android-pie-chart/commit/891f77be))
#### Updates
  - Update *androidx.core* dependency ([`173eb33b`](https://github.com/mahozad/android-pie-chart/commit/173eb33b))
  - Decrease default icon margin ([`e0724cb7`](https://github.com/mahozad/android-pie-chart/commit/e0724cb7))
#### Removals
  - Remove unneeded library dependencies ([`820810fd`](https://github.com/mahozad/android-pie-chart/commit/820810fd))

[All commits since version 0.3.0](https://github.com/mahozad/android-pie-chart/compare/v0.3.0...v0.4.0)

## v0.3.0 (2021-06-05)
#### New features
  - Top and bottom placements for label icons ([`065b44aa`](https://github.com/mahozad/android-pie-chart/commit/065b44aa))
  - Icon for outside label ([`065b44aa`](https://github.com/mahozad/android-pie-chart/commit/065b44aa))
  - New label type `NONE` ([`cdbb69e6`](https://github.com/mahozad/android-pie-chart/commit/cdbb69e6))
  - Attribute to change label icon placement for all slices ([`f2cd0152`](https://github.com/mahozad/android-pie-chart/commit/f2cd0152))
#### Updates
  - Decrease default label font size ([`7c42e475`](https://github.com/mahozad/android-pie-chart/commit/7c42e475))
  - *PieChart::slices* property is now immutable `var` instead of mutable `val` ([`53a3381c`](https://github.com/mahozad/android-pie-chart/commit/53a3381c))

[All commits since version 0.2.0](https://github.com/mahozad/android-pie-chart/compare/v0.2.0...v0.3.0)

## v0.2.0 (2021-05-29)
#### New features
  - Outside label (currently not supporting icon) ([`d5269dd9`](https://github.com/mahozad/android-pie-chart/commit/d5269dd9))
  - Outward Pointer for slices ([`d5269dd9`](https://github.com/mahozad/android-pie-chart/commit/d5269dd9))
  - Label icon for inside labels ([`7dffb164`](https://github.com/mahozad/android-pie-chart/commit/7dffb164))
  - Properties for changing label colors ([`7c374cec`](https://github.com/mahozad/android-pie-chart/commit/7c374cec))
#### Updates
  - Attributes are now public to use in layouts ([`64a1f18d`](https://github.com/mahozad/android-pie-chart/commit/64a1f18d))

[All commits since version 0.1.0](https://github.com/mahozad/android-pie-chart/compare/v0.1.0...v0.2.0)

## v0.1.0 (2021-05-19)
This is the first release of the library.


[comment]: <> (NOTE: Be aware that modifying the format of this file might impact the script that makes the body of GitHub releases)


# Template:
## vx.y.z (yyyy-mm-dd)
#### New features
  - new feature 1
#### Updates
  - change 1
#### Bug fixes
  - bug fix 1
#### Deprecations
  - deprecation 1
#### Removals
  - removal 1
#### Other
  - other 1
