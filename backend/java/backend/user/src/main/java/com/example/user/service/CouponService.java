package com.example.user.service;

public class CouponService {
}

//@RequiredArgsConstructor
//@Component
//public class RedisTransaction {
//  public Object execute(
//      RedisOperations<String, Object> redisTemplate, RedisOperation operation, Object vo) {
//    return redisTemplate.execute(
//        new SessionCallback<Object>() {
//          @Override
//          public Object execute(RedisOperations callbackOperations) throws DataAccessException {
//            // [1] REDIS 트랜잭션 Start
//            callbackOperations.multi();
//            // [2] Operation 실행
//            operation.execute(callbackOperations, vo);
//            // [3] REDIS 트랜잭션 End
//            return callbackOperations.exec();
//          }
//        });
//  }
//}
//

//package com.example.user.component;
//
//import lombok.RequiredArgsConstructor;
//import org.redisson.api.RSet;
//import org.redisson.api.RedissonClient;
//import org.redisson.api.TransactionOptions;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class CouponTransaction {
//  private final RedissonClient redissonClient;
//
//  public Object execute(RedisOperation operation, Object vo) {
//    return redissonClient.createTransaction(TransactionOptions.defaults()).execute(() -> {
//      operation.execute(redissonClient, vo);
//      return null;
//    });
//  }
//}
//
//public interface RedisOperation<T> {
//  Long count(RedissonClient redissonClient, T t);
//  Long add(RedissonClient redissonClient, T t);
//  Long remove(RedissonClient redissonClient, T t);
//  Boolean delete(RedissonClient redissonClient, T t);
//  Boolean expire(RedissonClient redissonClient, T t, Duration duration);
//  String generateValue(T t);
//  void execute(RedissonClient redissonClient, T t);
//}
//
//@Slf4j
//@Component
//public class TimeAttackOperation implements RedisOperation<TimeAttackVO> {
//  @Override
//  public Long count(RedissonClient redissonClient, TimeAttackVO vo) {
//    String key = vo.getKey();
//    RSet<Object> set = redissonClient.getSet(key);
//    Long size = set.size();
//    log.debug("[TimeAttackOperation] [count] key ::: {}, size ::: {}", key, size);
//    return size;
//  }
//
//  @Override
//  public Long add(RedissonClient redissonClient, TimeAttackVO vo) {
//    String key = vo.getKey();
//    String value = this.generateValue(vo);
//    RSet<Object> set = redissonClient.getSet(key);
//    Long result = set.add(value) ? 1L : 0L;
//    log.debug("[TimeAttackOperation] [add] key ::: {}, value ::: {}, result ::: {}", key, value, result);
//    return result;
//  }
//
//  @Override
//  public Long remove(RedissonClient redissonClient, TimeAttackVO vo) {
//    String key = vo.getKey();
//    String value = this.generateValue(vo);
//    RSet<Object> set = redissonClient.getSet(key);
//    Long result = set.remove(value) ? 1L : 0L;
//    log.debug("[TimeAttackOperation] [remove] key ::: {}, value ::: {}, result ::: {}", key, value, result);
//    return result;
//  }
//
//  @Override
//  public Boolean delete(RedissonClient redissonClient, TimeAttackVO vo) {
//    String key = vo.getKey();
//    Boolean result = redissonClient.getSet(key).delete();
//    log.debug("[TimeAttackOperation] [delete] key ::: {}, result ::: {}", key, result);
//    return result;
//  }
//
//  @Override
//  public Boolean expire(RedissonClient redissonClient, TimeAttackVO vo, Duration duration) {
//    String key = vo.getKey();
//    Boolean result = redissonClient.getSet(key).expire(duration.toMillis(), TimeUnit.MILLISECONDS);
//    log.debug("[TimeAttackOperation] [expire] key ::: {}, expire ::: {}, result ::: {}", key, duration, result);
//    return result;
//  }
//
//  @Override
//  public String generateValue(TimeAttackVO vo) {
//    return String.valueOf(vo.getUserId());
//  }
//
//  @Override
//  public void execute(RedissonClient redissonClient, TimeAttackVO vo) {
//    this.count(redissonClient, vo);
//    this.add(redissonClient, vo);
//  }
//}