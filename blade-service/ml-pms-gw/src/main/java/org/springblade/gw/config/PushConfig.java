package org.springblade.gw.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/10/29
 * @Description:
 */
@Configuration
@PropertySource(value = {"classpath:pushconf.properties"})
public class PushConfig {
	@Value("${push.base.url:null}")
	public String PUSH_BASE_URL;

	@Value("${system.domain:null}")
	public String SYSTEM_DOMAIN;

	@Value("${system.id:null}")
	public String SYSTEM_ID;
	/**
	 * 网关信息读取
	 */
	@Value("${getdevice.uri}")
	public String GET_DEVICE_URI;

	@Value("${setjfpgdate.uri}")
	public String SET_JFPGDATE_URI;

	@Value("${setalarm.uri}")
	public String SET_ALARM_URI;

	@Value("${getalram.uri}")
	public String GET_ALARM_URI;

	@Value("${setyxalarm.uri}")
	public String SET_YXALARM_URI;
	/**
	 * 数据项
	 */
	@Value("${getitem.uri}")
	public String GET_ITEM_URI;

	@Value("${getsignal.uri}")
	public String GET_SIGNAL_URI;

	@Value("${setyctran.uri}")
	public String SET_YCTRAN_URI;

	@Value("${setyxtran.uri}")
	public String SET_YXTRAN_URI;

	@Value("${setycstore.uri}")
	public String SET_YCSTORE_URI;

	@Value("${addyccalc.uri}")
	public String ADD_CALCITEM_URI;

	@Value("${updateyccalc.uri}")
	public String UPDATE_YCCALC_URI;

	@Value("${updateyxcalc.uri}")
	public String UPDATE_YXCALC_URI;

	@Value("${getapptotalenergy.uri}")
	public String GET_APPTOTALENERGY_URI;

	@Value("${updateyktran.uri}")
	public String UPDATE_YKTRAN_URI;

	@Value("${setyk.uri}")
	public String SET_YK_URI;

	@Value("${getappJFPG.uri}")
	public String GET_APPTOTALJFPG_URI;

	@Value("${doc.dir:null}")
	public String DEFAULT_PATH;

	@Value("${addmodel.uri}")
	public String ADD_MODEL;
	/**
	 * RTU信息读取
	 */
	@Value("${getsub.uri}")
	public String GET_SUB_URI;

	@Value("${setuploadinterval.uri}")
	public String SET_UPLOAD_INTERVAL_URI;

	@Value("${clearalarm.uri}")
	public String CLEAR_ALARM_URI;

	@Value("${appdownloadpath.uri:null}")
	public String DOWNLOADPATH_URI;

	@Value("${getreconrd.uri}")
	public String GET_RECORD_URI;

	@Value("${getsignalex.uri}")
	public String GET_SIGNALEX_URI;

	@Value("${gwsubscribe.cache.prefix}")
	public String GW_CACHE_PREFIX;

	@Value("${itembtype.enabled}")
	public boolean ITEM_BTYPE_ENABLED = true;

	@Value("${alarmsignal.uri}")
	public String GET_ALARM_SIGNAL;

	@Value("${get15msignal.url}")
	public String GET_15MSIGNAL_URI;

	@Value("${jfpgPriceNew.uri}")
	public String JFPGPRICENEW_URL;

	@Value("${waterOrGasPrice.uri}")
	public String WATERORGASPRICE_URL;

	@Value("${ctrl5g.uri}")
	public String CTRL5G_URL;


	@Value("${ytset.uri}")
	public String YTSET;

	/**
	 * 使用配置地址
	 * @param subUrl
	 * @return
	 */
	public String getUrl(String subUrl){
		return PUSH_BASE_URL+subUrl;
	}

	/**
	 * 使用动态地址
	 * @param
	 * @param subUrl
	 * @return
	 */
	public String getUrl(String PUSH_BASE_URL,String subUrl){
		return PUSH_BASE_URL+subUrl;
	}
}
