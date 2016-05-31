package com.dev.redisUtils;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 * 
 * @author 黄瑞庆 
 * Jedis操作
 * @version 1.0.0
 */
public class JedisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);
	
	
	/*   ########################################################
	 *   ##              Jedis String  start                   ##
	 *   ########################################################
	*/
	
	/**
	 * @操作数据类型String
	 * 获取String 类型的key的value
	 */
	public static String getJMapValue(String key) {
		String value = null;
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			value = jedis.get(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return value;
	}
	
	/**
	 * @操作数据类型String
	 * 设置String 类型的key value
	 */
	public static void setJMap(String key , String value) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			jedis.set(key, value);
		}catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		
		returnSource(jedis);
	}
	
	/**
	 * @操作数据类型String
	 * 同时设置多个key value
	 * @param 键值对  ("k1","v1","k2","v2")
	 * 参数可以为空但是有值是不需成对匹配
	 */
	public static void setJMap(String...keysValues) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			Transaction tx = jedis.multi();
			tx.mset(keysValues);
			tx.exec();
		}catch(Exception e) {
			jedis.close();
			logger.debug("一次添加多个操作-->"+e);
			throw new CustomException("一次添加多个操作-->"+e);
		}
		
	}
	
	/**
	 * @操作数据类型String
	 * 设置String 类型的key value 
	 * timeout 超时时间  单位（s）
	 */
	public static void setJTimeMap(String key, String value, int timeout) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			Transaction tx = jedis.multi();
			tx.set(key, value);
			tx.expire(key, timeout);
			tx.exec();
		}catch(Exception e){
			jedis.close();
			logger.debug("设置key,value,timeout出错-->"+e);
			throw new CustomException("设置key,value,timeout出错-->"+e);
		}
		
	}
	
	/**
	 * @操作数据类型String
	 * 同时设置多个key value
	 * @param map
	 */
	public static void setManyJMap(Map<String,String> map) {
		Jedis jedis = JedisPoolUtil.getJedis();
		try{
			if(map != null) {
				for(Map.Entry<String,String> m : map.entrySet()) {
					jedis.set(m.getKey(),m.getValue());
				}
			}
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		
	}
	
	/**
	 * @操作数据类型String
	 * @param key要删处的键 
	 * @return 0=失败， 1=成功
	 */
	public static Long delJMapKey(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long res = null;
		try{
			res = jedis.del(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return res;
	}
	
	/**
	 * @操作数据类型String
	 * 末尾追加内容
	 * @return 0=失败， 1=成功
	 */
	public static Long appendJValue(String key,String value) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long res = null;
		try{
			res = jedis.append(key, value);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return res;
	}
	/**
	 * @操作数据类型String
	 * 获取value值的长度
	 */
	public static Long  getJValueLength(String key ) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long value = null;
		try{
			value = jedis.strlen(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return value;
	}
	
	/**
	 * @操作数据类型String
	 * 递增数字 每次 加1
	 */
	public static Long incrNum(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Response<Long> value = null;
		try{
			Transaction tx = jedis.multi();
			value = tx.incr(key);
			tx.exec();
		}catch(Exception e) {
			jedis.close();
			logger.debug("递增事务出错-->"+e);
			throw new CustomException("递增事务出错-->"+e);
		}
		return value.get();
	}
	
	/**
	 * @操作数据类型String
	 * 递减数值 每次递减一
	 */
	public static Long decrNum(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Response<Long> index = null;
		try{
			Transaction tx = jedis.multi();
			index = tx.decr(key);
			tx.exec();
		}catch(Exception e) {
			jedis.close();
			logger.debug("事务递减出错-->"+e);
			throw new CustomException("事务递减出错-->"+e);
		}
		return index.get();
	}
	
	/*   ########################################################
	 *   ##              Jedis String  end                     ##
	 *   ########################################################
	*/
	
	
	
	
	
	/*   ########################################################
	 *   ##              Jedis List start                      ##
	 *   ########################################################
	*/
	
	/**
	 * @操作数据类型List
	 * 左侧添加list 元素
	 * @return 0=失败， 1=成功
	 */
	public static Long listAddLeft(String key ,String... values) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long res = null;
		try{
			res = jedis.lpush(key,values);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return res;
	}
	
	/**
	 * @操作数据类型List
	 * 右侧添加list 元素
	 * @return 0=失败， 1=成功
	 */
	public static Long listAddRight(String key, String... values) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long res =  null;
		try{
			res = jedis.rpush(key,values);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return res;
	}
	
	/**
	 * @操作数据类型List
	 * 查询list内元素
	 */
	public static List<String> getList(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		List<String> values = null;
		try{
			values = jedis.lrange(key,0,-1);
		}catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return values;
	}
	
	/**
	 * @操作数据类型List
	 * 获取list 的长度
	 */
	public static Long getListSize(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Long value = null;
		try{
			value = jedis.llen(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return value;
	}
	
	/**
	 * 操作数据类型List
	 * 默认0 为第一位
	 * @param key
	 * @param index1 获取list中指定区间的元素
	 * @param index2  最大值超出取值范围 将以list最大值取值 
	 * @return 最小值操作list最大值返回空
	 * 
	 */
	public static List<String> getPartList(String key ,int index1,int index2) {
		Jedis jedis = JedisPoolUtil.getJedis();
		List<String> values = null;
		try{
			values = jedis.lrange(key,index1,index2);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return values;
	}
	
	/**
	 * 操作数据类型List
	 * @param key
	 * @return 取出 list中最右的元素
	 */
	public static String getListRightValue(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		String value = null;
		try{
			value = jedis.rpop(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return value;
	}	

	/**
	 * 操作数据类型List
	 * @param key
	 * @return 取出list中最左的元素
	 * @如果使用队列不建议使用 推荐使用 getListRightValue(String key)
	 */
	public static String getListLeftValue(String key) {
		Jedis jedis = JedisPoolUtil.getJedis();
		String value = null;
		try{
			jedis.lpop(key);
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return value;
	}
	
	/**
	 * 操作数据类型List
	 * @param key 值所在的List
	 * @param value 删除的值
	 * @return 0=失败， 1=成功
	 */
	public static Long delListVaule(String key,String value) {
		Jedis jedis = JedisPoolUtil.getJedis();
		Response<Long>  res;
		try{
			Transaction tx = jedis.multi();
			res = tx.lrem(key, 0, value);
			tx.exec();
		} catch(Exception e) {
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return res.get();
	}
	
	/**
	 * 操作数据类型List
	 * @param key 所在list 
	 * @param index1 最小值 ，建议不要大于最大值，若大于则删除所有值
	 * @param index2 最大值，大于最大值时将保留最小值到最大值之间的值
	 * @return 返回ok 成功 
	 */
	public static String retainListValue(String key ,int index1 ,int index2) {
		Jedis jedis = JedisPoolUtil.getJedis();
		String res = null;
		try{
			res = jedis.ltrim(key,index1,index2);
			} catch(Exception e) {
				jedis.close();
				logger.debug("jedis操作异常-->"+e);
				throw new CustomException("jedis操作异常-->"+e);
			}
		return res;
		
	}


	/*   ########################################################
	 *   ##              Jedis List end                        ##
	 *   ########################################################
	*/
	
	
	
	
	
	
	// ********************** 公共方法 ******************************
	/**
	 * 获取操作键的数据类型
	 * key不存在返回 none
	 */
	public static String getRKeyType(String key ) {
		Jedis jedis = JedisPoolUtil.getJedis();
		String type = null;
		try{
			type = jedis.type(key);
		} catch(Exception e) {
			jedis.close();
			jedis.close();
			logger.debug("jedis操作异常-->"+e);
			throw new CustomException("jedis操作异常-->"+e);
		}
		return type;
	}
	
	/**
	 * 用完jedis 返还到连接池 
	 * @param jedis
	 */
	private static void returnSource(Jedis jedis) {
	    jedis.close();
	}
}