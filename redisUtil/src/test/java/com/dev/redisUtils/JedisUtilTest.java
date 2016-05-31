package com.dev.redisUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;

public class JedisUtilTest  extends TestCase{
	
	private static Logger logger = LoggerFactory.getLogger(JedisUtilTest.class);
	/*
	 * 链接测试
	 */
	public void testConncect() {
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			System.out.println(jedis.ping());//链接成功返回pong
		} catch(Exception e){
			logger.debug("wewew:"+e);
			try{
				jedis.close();
			}catch(Exception ex){
				logger.debug("1111111:"+ex);
			}
		}
		
	}
	
	// ***************List test***********************
	
	/**
	 * @操作数据类型 List
	 * 左侧添加list 元素
	 */
	public void testLpush() {
		Jedis jedis = null;
		String key = "users";
		Long start = System.currentTimeMillis();
		try{
			jedis = JedisPoolUtil.getJedis();
			//Transaction tx = jedis.multi();
			for(int i=0;i<200000;i++) {
				jedis.lpush(key,"user:"+i);
				//tx.lpush(key,"user:"+i);
			}
		//	tx.exec();
			Long end = System.currentTimeMillis();
			System.out.println("耗时："+(end-start)/1000F +"s");
		}catch(Exception e){
			logger.debug("异常："+e);
			jedis.close();
		}finally{
			try{
				returnSource(jedis);
			}catch(Exception e) {
				logger.debug("dd-->"+e);
			}
		}
		
		//returnSource(jedis);
	}
	
	/**
	 * @操作数据类型 List
	 * 右侧添加list 元素
	 */
	public void testRpush() {
		Jedis jedis = JedisPoolUtil.getJedis();
		jedis.rpush("list","2","3");
		returnSource(jedis);
	}
	
	/**
	 * @操作数据类型 List
	 * 查询list内元素
	 */
	public static void testGetList() {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long start = System.currentTimeMillis();
		List<String> values = jedis.lrange("users",0,-1);
		System.out.println("values:"+values);
		Long end = System.currentTimeMillis();
		System.out.println("耗时："+(end-start)/1000f +"s");
		returnSource(jedis);
	}
	
	/**
	 * @操作数据类型 List
	 * 删除list 中指定的值
	 */
	public static void testDelListVaule() {
		String key = "users";
		String value = "user:99";
		Jedis jedis = JedisPoolUtil.getJedis();
		Transaction tx = jedis.multi();
		Response<Long>  res = tx.lrem(key, 0, value);
		tx.exec();
		System.out.println("res:"+res.get());
		returnSource(jedis);
		
	}
	
	/**
	 * 保留指定范围
	 * 默认0 是第一位
	 */
	public static void testRetainListValue() {
		Jedis jedis = JedisPoolUtil.getJedis();
		String key ="users";
		String res = jedis.ltrim(key, 0,10);
		System.out.println("res:"+res);
	}
	
	/**
	 * 获取指定索引的值
	 */
	
	
	
	
	
	public void testTime() {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long start = System.currentTimeMillis();
		List<String> values = jedis.lrange("users",0,-1);
		String key = "user:0";
		//System.out.println(values.get(199999));
		int i = 0;
		int size = values.size()-1;
		while(i<size && !key.equals(values.get(i)) ){
			i++;
		}
		if(key.equals(values.get(i)) ) {
			Long end = System.currentTimeMillis();
			System.out.println("i"+i);
			System.out.println("耗时："+(end-start)/1000f +"s");
			return;
		}
		Long end = System.currentTimeMillis();
		System.out.println("耗时1："+(end-start)/1000f +"s");
	}
	
	
	
	
	/**
	 * 获取list中指定区间的元素
	 * 默认0 为第一位
	 * 最大值超出取值范围 将以list最大值取值 
	 * 最小值操作list最大值返回空
	 */
	public static void testGetPartList() {
		Jedis jedis = JedisPoolUtil.getJedis();
		String key = "list";
		List<String> values = jedis.lrange(key,5,6);
		System.out.println("values:"+values);
		returnSource(jedis);
	}
	
	
	/**
	 * @操作数据类型 List
	 * 获取list 的长度
	 */
	public static void testListSize() {
		String key = "list";
		Jedis jedis = JedisPoolUtil.getJedis();
		Long value = jedis.llen(key);
		System.out.println(value);
		returnSource(jedis);
		//return value;
			
	}
	
	/**
	 *  @操作数据类型List
	 *  取出list中最右的元素
	 */
	public static void testGetListRightValue() {
		String key = "list";
		Jedis jedis = JedisPoolUtil.getJedis();
		String value = jedis.rpop(key);
		System.out.println("value:"+value);
		returnSource(jedis);
	}	
	
	/**
	 *  @操作数据类型List
	 *  取出list中最左的元素
	 */
	public static void testGetListLeftValue() {
		String key = "list";
		Jedis jedis = JedisPoolUtil.getJedis();
		String value = jedis.lpop(key);
		System.out.println("value:"+value);
		returnSource(jedis);
	}
	
	
	
	// **************String test**********************
	
	/**
	 * 递增数字 每次 加1
	 * @throws InterruptedException 
	 */
	public static void testIncrNum() throws InterruptedException {
		incrNum();
		/*Thread m = new MyThred();
		Thread m2 = new MyThred();
		Thread m3 = new MyThred();
		Thread m4 = new MyThred();
		
		m.start();
		m.sleep(1000);*/
//		m2.start();
//		m3.start();
//		m4.start();
		
		/*String key = "index";
		Jedis jedis = JedisPoolUtil.getJedis();
		Transaction tx = JedisPoolUtil.getJedis().multi();
		Long index = jedis.incr(key);
		tx.exec();
		System.out.println("r:"+index);
*/		
	}
	
	/**
	 * 递增数字 每次 加1
	 */
	public static void incrNum() {
		System.out.println("into");
		String key = "index";
		Jedis jedis = null;
		try{
			jedis = JedisPoolUtil.getJedis();
			Transaction tx = jedis.multi();
			Response<Long> index = tx.incr(key);
			tx.exec();
			System.out.println("index:"+index.get());
			//int i = 10 / 0;
		}catch(Exception e) {
			logger.debug("递增事务出错-->"+e);
			JedisPoolUtil.getPool().returnBrokenResource(jedis);
			throw new CustomException("递增事务出错-->"+e);
		}finally{
			System.out.println(jedis.toString());
			returnSource(jedis);
		}
		
	}
	
	/**
	 * @操作数据类型 String
	 * 递减数值 每次递减一
	 */
	public static void testDecrNum() {
		String key = "index";
		Jedis jedis = null;
		Response<Long> index = null;
		try{
			jedis = JedisPoolUtil.getJedis();
			Transaction tx = jedis.multi();
			index = tx.decr(key);
			tx.exec();
		}catch(Exception e) {
			logger.debug("事务递减出错-->"+e);
			JedisPoolUtil.getPool().returnBrokenResource(jedis);
			throw new CustomException("事务递减出错-->"+e);
		}
		System.out.println("index:"+index.get());
		returnSource(jedis);
	}
	
	
	
	 // 内部类
	
		public static class MyThred extends Thread{
			@Override
			public void run() {
				incrNum();
			}
			
			
			/**
			 * 递增数字 每次 加1
			 */
			public static void incrNum() {
				String key = "index";
				Jedis jedis = null;
				try{
					jedis = JedisPoolUtil.getJedis();
					Transaction tx = jedis.multi();
					Long index = jedis.incr(key);
					tx.exec();
				}catch(Exception e) {
					throw new CustomException("递增事务出错-->"+e);
				}
				
				
			}
		}
	
	/**
	 * @操作数据类型 String
	 * @param key 要删处的键 
	 */
	public static void testDel() {
		String key = "k2";
		System.out.println(JedisPoolUtil.getJedis().del(key));
	}
	
	/**
	 * @操作数据类型 String
	 * 末尾追加内容
	 */
	public static void testAppend() {
		String key = "k1";
		String value = " cccc";
		JedisPoolUtil.getJedis().append(key, value);
	}
	
	/**
	 * @操作数据类型 String
	 * 获取key 的长度
	 */
	public static void testKeyLength() {
		String key = "k1";
		System.out.println(JedisPoolUtil.getJedis().strlen(key));
	}
	
	/**
	 * 获取操作键的数据类型
	 * key不存在返回 none
	 */
	public void testType() {
		String key = "k1";
		System.out.println(
				JedisPoolUtil.getJedis().type(key));
		
	}
	
	
	/**
	 * 设置String 类型的key value
	 */
	public void testSetRMap( ) {
		String key = "name:1";
		String value = "huang"; 
		JedisPoolUtil.getJedis().set(key, value);
	}
	
	/**
	 * 设置String 类型的key value 
	 * timeout 超时时间  单位（s）
	 */
	public void testSetRMap2() {
		String key = "name:3";
		String value = "marx";
		int timeout = 10;
		Jedis jedis = JedisPoolUtil.getJedis();
		jedis.set(key, value);
		jedis.expire(key, timeout);
	}
	
	/**
	 * 获取String 类型的key的value
	 */
	public String testGetRMapValue() {
		String key = "name:2";
		System.out.println(JedisPoolUtil.getJedis().get(key));
		return JedisPoolUtil.getJedis().get(key);
	}
	
	/**
	 * 同时设置多个key value
	 */
	public void testAddManyMap(Map<String,String> map) {
		if(map != null) {
			for(Map.Entry<String,String> m : map.entrySet()) {
				JedisPoolUtil.getJedis().set(m.getKey(),m.getValue());
			}
		}
	}
	
	/**
	 * 同时设置多个key value
	 */
	public static void testSu() {
		su("k1","v1","k2","v2");
	}
	
	public static void su(String...keysvalues ) {
		try{
			JedisPoolUtil.getJedis().mset(keysvalues);
		}catch(Exception e) {
			//logger.debug("参数个数不对-->"+e);
			throw new CustomException("参数个数不对-->"+e);
		}
	}
	
	
	
	/**
	 * 事务的测试
	 * @throws InterruptedException 
	 */
	public void testTx() throws InterruptedException {
		MyThread t1 = new MyThread();
		
		//MyThread t3 = new MyThread();
		/*t1.start();
		t2.start();
		t2.sleep(100);
		t3.start();
		t3.sleep(300);*/
	}

	
	public class MyThread extends Thread {
		Long start = System.currentTimeMillis();
		@Override
		public void run() {
			Jedis jedis = JedisPoolUtil.getJedis();
			/*Transaction tx = JedisPoolUtil.getJedis().multi();
			Long res = JedisPoolUtil.getJedis().incr("num");
			tx.exec();
			Long end = System.currentTimeMillis();
			System.out.println("res:"+res+"-->"+(end-start));
			JedisPoolUtil.getJedis().disconnect();*/
		}
	}
	/*public class MyThread2 extends Thread {
		@Override
		public void run() {
			Long start = System.currentTimeMillis();
			Transaction tx = JedisPoolUtil.getJedis().multi();
			Long res = JedisPoolUtil.getJedis().incr("num");
			tx.exec();
			Long end = System.currentTimeMillis();
			System.out.println("res:"+res+"-->"+(end-start));
			JedisPoolUtil.getJedis().disconnect();
		}
	}
	public class MyThread3 extends Thread {
		@Override
		public void run() {
			Long start = System.currentTimeMillis();
			Long res = JedisPoolUtil.getJedis().incr("num");
			Long end = System.currentTimeMillis();
			System.out.println("res3:"+res+"-->"+(end-start));
			JedisPoolUtil.getJedis().disconnect();
		}
	}
	public class MyThread4 extends Thread {
		@Override
		public void run() {
			Long start = System.currentTimeMillis();
			String res = JedisPoolUtil.getJedis().get("num");
			Long end = System.currentTimeMillis();
			System.out.println("res4:"+res+"-->"+(end-start));
			JedisPoolUtil.getJedis().disconnect();
		}
	}*/
	
	/**
	 * 用完jedis 返还到连接池 
	 * @param jedis
	 */
	private static void returnSource(Jedis jedis) {
		//用完jedis 返还到连接池 
		jedis.close();
		
	}
	
	public static Integer searchList(String key ,String value) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try {
			Transaction tx = jedis.multi();
			Response<List<String>> list = tx.lrange(key,0,-1);
			tx.exec();
			if(list != null ) {
				List<String> values = list.get();
				tx.exec();
				int i = 0;
				int size = values.size()-1;
				while(i<size && !key.equals(values.get(i))){
					i++;
				}
				if(key.equals(values.get(i)) ) {
					return i;
				}
			}
		} catch (Exception e) {
			logger.debug("遍历"+key+":"+value+"异常-->"+e);
			throw new CustomException("遍历"+key+":"+value+"异常-->"+e);
		} finally{
			returnSource(jedis);
		}
		return null;
	}
	
	
}
