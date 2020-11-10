package org.springblade.resource.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.resource.service.IntranetSmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 内网短信 控制器
 *
 * @author bini
 * @since 2020-07-21
 */
@ApiIgnore
@RestController
@AllArgsConstructor
@RequestMapping("/intraNetSms")
//@PreAuth(RoleConstant.HAS_ROLE_ADMIN)
@Api(value = "内网短信", tags = "内网短信")
public class IntranetSmsController extends BladeController {

	private IntranetSmsService intranetSmsService;
	/**
	 * 发送短信,不存库直接发，需优化
	 */
	@PostMapping("/sendSms")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "发送短信", notes = "发送短信")
//	@CacheEvict(cacheNames = {RESOURCE_CACHE}, allEntries = true)
	public R enable(@ApiParam(value = "手机号", required = true) @RequestParam String phone,
					@ApiParam(value = "内容", required = true) @RequestParam String content) {
//		intranetSmsService.sendSms(phone, content);
		Future<Boolean> future = intranetSmsService.asyncSendSms(phone, content);
		while (true) {  ///这里使用了循环判断，等待获取结果信息
			if (future.isDone()) {  //判断是否执行完毕
				try {
					System.out.println("Result from asynchronous process - " + future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				break;
			}
			System.out.println("wait sms result. ");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return R.status(true);
	}

}
