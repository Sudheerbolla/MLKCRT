package com.godavarisandroid.mystore.utils.calenderutils.materialcalendarview;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that a public API (public class, method or field) is will almost certainly
 * be changed or removed in a future release. An API bearing this annotation should not
 * be used or relied upon in production code. APIs exposed with this annotation exist
 * to allow broad testing and feedback on experimental features.
 **/
@Retention(RetentionPolicy.CLASS)
@Target({
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE})
@Documented
@Experimental
public @interface Experimental {
}