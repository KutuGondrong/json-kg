package com.kutugondrong.jsonkg

/**
 * KG KutuGondrong
 * @return the desired name of the field when it is serialized for special case
 * Make sure when use this annotation values is not duplicated
 *
 *
 * @see JsonKG
 */

@MustBeDocumented
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class SpecialSerializedName(
    vararg val values: String = [],
)
