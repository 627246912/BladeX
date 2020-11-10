package org.springblade.resource.service;

import java.util.concurrent.Future;

public interface IntranetSmsService {


	/**
	 * 发送短信
	 *
	 * @param phone
	 * @param content
	 * @return
	 */
	boolean sendSms(String phone,String content);

	/**
	 * 异步发送短信
	 *
	 * @param phone
	 * @param content
	 * @return
	 */
	Future<Boolean> asyncSendSms(String phone, String content);


}
