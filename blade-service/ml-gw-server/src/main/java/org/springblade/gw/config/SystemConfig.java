package org.springblade.gw.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：bond
 * @date ：Created in 2020/3/11 12:14
 * @description：系统配置参数
 * @modified By：
 * @version: 1.0.0$
 */
@Data
@Configuration
public class SystemConfig {
	@Value("${mailian.service.idcard}")
	private String serverIdCard;
}
