package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.entities.SecKill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        jedisPool = new JedisPool(ip, port);
    }

    private RuntimeSchema<SecKill> secKillRuntimeSchema = RuntimeSchema.createFrom(SecKill.class);

    public SecKill getSeckill(long seckillId){

        //logic of redis operation.

        try {
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "seckill:" + seckillId;

                //no serialization in redis
                //get byte[] -> deserialization -> Object()
                //we will not use serialization provided by java,
                //we use self-defined serialization provided
                //by some open-source community instead.
                //protostuff: only pojo.
                byte[] bytes = jedis.get(key.getBytes());

                if (bytes != null){

                    //new an empty Seckill object
                    SecKill secKill = secKillRuntimeSchema.newMessage();

                    //seckill deserialization.
                    ProtostuffIOUtil.mergeFrom(bytes, secKill, secKillRuntimeSchema);

                    return secKill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String putSeckill(SecKill secKill){
        //set Object -> serialization -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();

            try {
                String key = "seckill:" + secKill.getSeckillId();
                byte[] bytes = ProtostuffIOUtil.toByteArray(secKill, secKillRuntimeSchema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

                //timeout cache, we will cache as long as timeout.
                int timeout = 60 * 60;//1 hour, in seconds
                String result = jedis.setex(key.getBytes(), timeout, bytes);

                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
