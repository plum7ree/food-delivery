//package com.example.commondata.domain.concurrency.lock.annotation;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target(ElementType.METHOD)
//@Retention(RetentionPolicy.RUNTIME)
//
//public @interface DistributedLock {
//    String key();
//    long expireTimeInMs() default 500L;
//    long waitTimeInMs() default 1000L;
//
//}
