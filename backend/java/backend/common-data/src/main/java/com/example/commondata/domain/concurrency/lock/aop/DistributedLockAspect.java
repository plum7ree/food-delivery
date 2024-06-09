//package com.example.commondata.domain.concurrency.lock.aop;
//
//import com.example.commondata.domain.concurrency.lock.annotation.DistributedLock;
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.redisson.api.RLock;
//import org.redisson.api.RedissonClient;
//import org.springframework.stereotype.Component;
//
//@Aspect
//@Component
//@RequiredArgsConstructor
//public class DistributedLockAspect {
//
//    private static final String LOCK_PREFIX = "lock:";
//
//    private final RedissonClient redissonClient;
//
//    @Around("@annotation(com.example.commondata.domain.concurrency.lock.annotation.DistributedLock)")
//    public Object tryLock(final ProceedingJoinPoint joinPoint) throws Throwable {
//        var signature = (MethodSignature) joinPoint.getSignature();
//        var method = signature.getMethod();
//        var lockAnnotation = method.getAnnotatedReturnType(DistributedLock.class);
//
//        var key = String.join(LOCK_PREFIX, )
//        var rlock = redissonClient.getLock(key);
//    }
//}
