package org.springblade.resource.service.impl;

import org.springblade.resource.service.IntranetSmsService;
import org.springblade.resource.utils.SendSms;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * 内网短信发送
 *
 * @author bini
 * @since 2020-07-21
 */
@Service
public class IntranetSmsServiceImpl implements IntranetSmsService {

	@Override
	public boolean sendSms(String phone, String content) {
		boolean flag = false;
		try {
			flag = SendSms.SMSReminds(phone,content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Async
	@Override
	public Future<Boolean> asyncSendSms(String phone, String content) {
		boolean flag = false;
		try {
			flag = SendSms.SMSReminds(phone,content);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AsyncResult<Boolean>(flag);
	}

}
