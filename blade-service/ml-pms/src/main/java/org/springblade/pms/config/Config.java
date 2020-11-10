package org.springblade.pms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: bond
 * @Date: 2020/03/19
 * @Description:
 */
@Data
@Configuration
//@PropertySource(value = {"classpath:pushconf.properties", "classpath:conf.properties"})
public class Config {
    @Value("${push.base.url:null}")
    public String baseUrl;

}
