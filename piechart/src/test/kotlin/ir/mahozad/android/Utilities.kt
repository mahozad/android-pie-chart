package ir.mahozad.android

import org.assertj.core.api.ObjectAssert
import org.assertj.core.util.FloatComparator

fun ObjectAssert<Bounds>.isEqualToBounds(other: Bounds) {
    usingRecursiveComparison()
        .withComparatorForFields(FloatComparator(0.01f), Bounds::left.name, Bounds::top.name, Bounds::right.name, Bounds::bottom.name)
        .isEqualTo(other)
}
