package com.dev.redisUtils;
/**
 * 自定义异常
 * @author Administrator
 *
 */
public class CustomException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	public CustomException(){
		super();
	};
	
	
	String message;

	public CustomException(String message){
		super(message);
	}
	public CustomException(String message , Throwable cause){
		super(message, cause);
	}
	public CustomException(Throwable cause){
		super(cause);
	}
	
}
