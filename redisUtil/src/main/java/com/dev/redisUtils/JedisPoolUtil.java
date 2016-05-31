package com.dev.redisUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {

	private static Logger logger = LoggerFactory.getLogger(JedisPoolUtil.class);
	
	/** 
     * 在不同的线程中使用相同的Jedis实例会发生奇怪的错误。但是创建太多的实现也不好因为这意味着会建立很多sokcet连接， 
     * 也会导致奇怪的错误发生。单一Jedis实例不是线程安全的。为了避免这些问题，可以使用JedisPool, 
     * JedisPool是一个线程安全的网络连接池。可以用JedisPool创建一些可靠Jedis实例，可以从池中拿到Jedis的实例。 
     * 这种方式可以解决那些问题并且会实现高效的性能 
     */  
	
	static{
		 getPool();	
	}
	// 单利模式  提供 jedispool
	private static JedisPool jedisPool = null;
	
	public static JedisPool getPool() {
		if(jedisPool == null) {
		JedisPoolConfig config = new JedisPoolConfig();
		/*
		 * 控制一个pool可分配多少个jedis 实例。通过pool.getResource()来获取。
		 * 如果赋值-1 则表示不限制；若果pool 已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
		 */
		config.setMaxTotal(5);
		//控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        config.setMaxIdle(2);
       //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
        config.setMaxWaitMillis(60000);
        //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
        config.setTestOnBorrow(true);
        
        jedisPool = new JedisPool(config, "192.168.3.10", 6379,6000);// ip 端口 超时时间6000ms(单位毫秒)
        
		}
		return jedisPool;
	}
	
	/** 
	 * a、获取jedis实例时，实际上可能有两类错误。一类是pool.getReource()，得不到可用的jedis实例；
	 * 另一类是jedis.set/get时出错也会抛出异常；
	 * 为了实现区分，所以根据instance是否为null来实现，如果为空就证明instance根本就没初始化，也就不用return给pool；
	 * 如果instance不为null，则证明是需要返还给pool的；
	 * b、在instance出错时，必须调用returnBrokenResource返还给pool，
	 * 否则下次通过getResource得到的instance的缓冲区可能还存在数据，出现问题！
	 */
	 /*public static void returnResource(JedisPool pool, Jedis jedis) {
	        if (jedis != null) {
	        	// 返回连接池
	            pool.returnResource(jedis);
	        }
	 }*/
	
	public  static Jedis getJedis() {
		Jedis jedis = null;
		try{
			jedis = getPool().getResource();
		}catch(Exception e) {
			logger.debug("JedisPool链接失败-->",e);
			throw new CustomException("JedisPool链接失败",e);
		}
		return jedis;
	}
	
}
