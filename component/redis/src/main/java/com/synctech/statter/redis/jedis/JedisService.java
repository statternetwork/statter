package com.synctech.statter.redis.jedis;

import com.alibaba.fastjson.JSONObject;
import com.synctech.statter.redis.config.vo.Get;
import com.synctech.statter.redis.config.vo.Hget;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Service
public class JedisService {

    public static int DEFAULT_EXPIRE_SECOND = 30;

    @Autowired
    JedisPool jedisPool;

    public Jedis get() {
        Jedis j = jedisPool.getResource();
        if (null == j) {
            throw new RuntimeException("cannot get jedis resource");
        }
        return j;
    }

    public void process(Consumer<Jedis> c) {
        Jedis j = get();
        try {
            c.accept(j);
        } finally {
            flush(j);
        }
    }

    public long expire(String k, int seconds) {
        Jedis j = get();
        try {
            return j.expire(k, seconds);
        } finally {
            flush(j);
        }
    }

    public boolean exists(String k) {
        Jedis j = get();
        try {
            return j.exists(k);
        } finally {
            flush(j);
        }
    }

    public String get(String k) {
        Jedis j = get();
        try {
            if (j.exists(k)) {
                String v = j.get(k);
                return v;
            }
            return null;
        } finally {
            flush(j);
        }
    }

    public <T> T get(String k, Class<T> objectClass) {
        String v = this.get(k);
        if (!StringUtils.hasText(v)) return null;
        return JSONObject.parseObject(v, objectClass);
    }

    public <R> R get(Get<R> p, Function<Get, R> func) {
        R r = get(p.getKey(), p.getResultClazz());
        if (null != r) return r;
        boolean lockSuccess = checkLock(p.getKey(), p.getLockKey(), DEFAULT_EXPIRE_SECOND);
        if (!lockSuccess) return get(p.getKey(), p.getResultClazz());
        try {
            r = func.apply(p);
            if (null != r) set(p.getKey(), r);
        } finally {
            unlock(p.getLockKey());
        }
        return r;
    }

    public String set(String k, String v) {
        Jedis j = get();
        try {
            return j.set(k, v);
        } finally {
            flush(j);
        }
    }

    public String set(String k, Object v) {
        return this.set(k, JSONObject.toJSONString(v));
    }

    public long setnx(String k, Object v) {
        Jedis j = get();
        try {
            return j.setnx(k, JSONObject.toJSONString(v));
        } finally {
            flush(j);
        }
    }

    public long del(String k) {
        Jedis j = get();
        try {
            return j.del(k);
        } finally {
            flush(j);
        }
    }

    public boolean hexists(String k, String f) {
        Jedis j = get();
        try {
            return j.hexists(k, f);
        } finally {
            flush(j);
        }
    }

    public Map<String, String> hgetAll(String key) {
        Jedis j = get();
        try {
            return j.hgetAll(key);
        } finally {
            flush(j);
        }
    }

    public String hget(String k, String f) {
        Jedis j = get();
        try {
            if (j.hexists(k, f)) {
                String v = j.hget(k, f);
                return v;
            }
            return null;
        } finally {
            flush(j);
        }
    }

    public <T> T hget(String k, String f, Class<T> returnClass) {
        String v = this.hget(k, f);
        if (!StringUtils.hasText(v)) return null;
        return JSONObject.parseObject(v, returnClass);
    }

    public <R> R hget(Hget<R> p, Function<Hget, R> func) {
        R r = hget(p.getKey(), p.getField(), p.getResultClazz());
        if (null != r) return r;
        boolean lockSuccess = checkLock(p.getKey(), p.getField(), p.getLockKey(), DEFAULT_EXPIRE_SECOND);
        if (!lockSuccess) return hget(p.getKey(), p.getField(), p.getResultClazz());
        try {
            r = func.apply(p);
            if (null != r) hset(p.getKey(), p.getField(), r);
        } finally {
            unlock(p.getLockKey());
        }
        return r;
    }


    public long hset(String k, String f, String v) {
        Jedis j = get();
        try {
            return j.hset(k, f, v);
        } finally {
            flush(j);
        }
    }

    public long hset(String k, String f, Object v) {
        return this.hset(k, f, JSONObject.toJSONString(v));
    }

    public long hsetAll(String k, Map<String, String> v) {
        Jedis j = get();
        try {
            return j.hset(k, v);
        } finally {
            flush(j);
        }
    }

    public long hdel(String k, String f) {
        Jedis j = get();
        try {
            return j.hdel(k, f);
        } finally {
            flush(j);
        }
    }

    public void flush(Jedis j) {
        if (j != null) {
            j.close();
        }
    }

    /**
     * check the cache while the loop of catching lock,
     * if catch the lock, return true;
     * if the cache is produced by the other producer, return false;
     *
     * @param k
     * @param lk
     * @param seconds
     * @return
     */
    public boolean checkLock(String k, String lk, int seconds) {
        boolean lockSuccess = false;
        try {
            synchronized (lk) {
                int count = 0;
                while (0 == setnx(lk, "1") && count < (seconds * 10)) { // avoid to threads block here
                    // if the cache is produced in this thread catching lock, interrupt this loop
                    if (exists(k)) return false;
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
                if (count >= seconds) throw new JedisException("can not catch the jedis lock[" + lk + "]");
            }
            lockSuccess = true;
        } finally {
            if (lockSuccess) expire(lk, seconds);// if catch the lock, set the expire time of the lock
        }
        return true;
    }

    /**
     * check the cache while the loop of catching lock,
     * if catch the lock, return true;
     * if the cache is produced by the other producer, return false;
     *
     * @param k
     * @param f
     * @param lk
     * @param seconds
     * @return
     */
    public boolean checkLock(String k, String f, String lk, int seconds) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        boolean lockSuccess = false;
        try {
            synchronized (lk) {
                int count = 0;
                while (0 == setnx(lk, "1") && count < (seconds * 10)) { // avoid to threads block here
                    // if the cache is produced in this thread catching lock, interrupt this loop
                    if (hexists(k, f)) return false;
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                    }
                    continue;
                }
                if (count >= seconds) throw new JedisException("can not catch the jedis lock[" + lk + "]");
            }
            lockSuccess = true;
        } finally {
            lock.unlock();
            if (lockSuccess) expire(lk, seconds);// if catch the lock, set the expire time of the lock
        }
        return true;
    }

//    public void lock(String lockKey) {
//        Jedis j = get();
//        try {
//            lock(lockKey, DEFAULT_EXPIRE_SECOND, j);
//        } finally {
//            flush(j);
//        }
//    }

    public void lock(String lk, int seconds, Jedis j) {
        if (null == j) {
            throw new RuntimeException("jedis resource is null");
        }
        try {
            int count = 0;
            while (0 == j.setnx(lk, "1") && count < seconds) { // avoid to threads block here
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                }
                continue;
            }
            if (count >= seconds) throw new JedisException("can not catch the jedis lock[" + lk + "]");
        } finally {
            j.expire(lk, seconds);
        }
    }

    public void unlock(String lockKey) {
        Jedis j = get();
        try {
            unlock(lockKey, j);
        } finally {
            flush(j);
        }
    }

    public void unlock(String lockKey, Jedis j) {
        if (null == j) {
            throw new RuntimeException("jedis resource is null");
        }
        j.del(lockKey);
    }

}
