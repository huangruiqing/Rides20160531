package com.dev.redisUtils;

public class Test {
 public static void main(String[] args) {
	
	 JedisPoolUtil.getJedis(); 
	  
			
 	}
/* public static void su(String...keysvalues ) {
		try{
			JedisPoolUtil.getJedis().mset(keysvalues);
		}catch(Exception e) {
			//logger.debug("参数个数不对-->"+e);
			throw new CustomException("参数个数不对-->"+e);
		}
	}*/
}
