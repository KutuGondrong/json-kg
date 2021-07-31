package com.kutugondrong.jsonkg

/**
 * KG KutuGondrong
 * @return the desired name of the field when it is serialized or deserialized
 *
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
    AnnotationTarget.PROPERTY_SETTER,
)
annotation class SerializedName(
    val value: String,
    val serialize: Boolean = true
)
