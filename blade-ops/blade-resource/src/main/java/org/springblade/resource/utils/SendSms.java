package org.springblade.resource.utils;

import org.smslib.Message;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

import java.time.LocalDateTime;

public class SendSms {

	public static boolean SMSReminds(String phone, String content) throws Exception{

		// 1、连接网关的id  
		// 2、com口名称，如COM1或/dev/ttyS1（根据实际情况修改
		// 3、串口波特率，如9600（根据实际情况修改）
		// 4、开发商
		// 5、型号 
		SerialModemGateway gateway  = new SerialModemGateway("model.com3", "COM3", 9600, "Cinterion","MC55i");

		// 设置true，表示该网关可以接收短信
		gateway.setInbound(true);
		// 设置true，表示该网关可以发送短信
		gateway.setOutbound(true);

		// -----------------创建发送短信的服务（它是单例的）----------------
		Service service = Service.getInstance();
		Service.getInstance().S.SERIAL_POLLING = true;

		boolean result = false;
		try {
			// ---------------------- 将设备加到服务中----------------------
			service.addGateway(gateway);
			// ------------------------- 启动服务 -------------------------
			service.startService();
			// ------------------------- 发送短信 -------------------------  
			OutboundMessage msg = new OutboundMessage(phone , content);
			msg.setEncoding(Message.MessageEncodings. ENCUCS2);
			System.out.println("startDate====="+ LocalDateTime.now().toString());
			result = service.sendMessage(msg);
			System.out.println("endDate====="+ LocalDateTime.now().toString());
			// ------------------------- 关闭服务 -------------------------  
			service.stopService();
			service .removeGateway(gateway);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			service.stopService();
			service .removeGateway(gateway);
		}
		return result;
	}

}
