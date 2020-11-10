package org.springblade.constants;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/3/11
 * @Description:
 */
public class GwsubscribeConstant {
    /**
     * 处理结果:成功
     */
    public static final int HANDLER_RESULT_SUCCESS = 1;
    /**
     * 处理结果:失败
     */
    public static final int HANDLER_RESULT_FAIL = 0;

    /** 响应数据名称 */
    public static final String PUSH_RESPONSE_DATA = "data";
    /** 响应编码 */
    public static final String PUSH_RESPONSE_CODE = "code";
    /** 响应成功编码 */
    public static final String PUSH_RESPONSE_SUCCESS = "0";
    /** */
    public static final String PUSH_RESPONSE_TOTAL = "total";

    public static final Double ITEM_INITIAL_VALUE = -99.9d;

	public static final String ITEM_NULL_VALUE = "--";

    /** redis缓存键相关  */
    public static final String DEVICE_CODE_TO_DEVICE="codegw";//key:网关 value:网关信息
    public static final String DEVICE_CODE_TO_STATUS="gwst";//key:网关 value:网关状态
    public static final String DEVICE_SUB_ITEM="rtuitem"; //key:rtuid value:rtu下的数据项
    public static final String ID_ITEM="iditem";//key:数据项ID value:数据项信息
    public static final String BTYPE_ITEM_IDS = "btit:";//网关业务分类
    /** 运算库 */
    public static final String DEVICE_CODE_CALC_ITEM="codeci";//key:网关 value:网关所以计算数据项信息
    public static final String DEVICE_ITEM_REAL_TIME_DATA="itemrd";//实时数据
    public static final String DEVICE_CODE_TO_DEVICE_SUB="gwrtu";//key:网关 value:网关所以rtu信息
    public static final String SUB_ID_TO_DEVICE_SUB="codertu";//key:rtuid value:rtu信息

    /** 推送redis默认失效时间(永不失效) */
    public static final long PUSH_REDIS_DEFAULT_EXPIRE = -1;

    /** 判断告警类型 */
    public static final String DAT_KEY = "dat";
    public static final String YSYC_KEY = "ysyc";
    public static final String UNDERLINE = "_";
    public static final String SPLIT_COLON_KEY = ":";
    public static final String UN_STATUS_NAME = "无状态";
    public static final String OPEN_STATUS_NAME = "分";
    public static final String CLOSE_STATUS_NAME = "合";

    public static final String UPLIMIT = " 超过上限";
    public static final String LOWLIMIT = " 低于下限";
    public static final String UPUPLIMIT = " 超过上上限";
    public static final String LOWLOWLIMIT = " 低于下下限";
    public static final String ALARM_CURRENT_VALUE = " 当前值： ";
    public static final String COMMUNICATION_INTERRUPTION = "通讯中断";
    public static final String ALARM_CREATE = "产生";
    public static final String ALARM_CLEAN = "消除";
    public static final String DEVICE_NAME = "网关";
    public static final String EQ_NAME = "设备";

    /** 保留小数位 */
    public static final int DEFAULT_KEEP_SCALE = 3;

    /** 最大处理数据项id数 */
    public static final int BATCH_MAX_ITEM_SIZE = 100;
    public static final int BATCH_MAX_SUB_SIZE = 10;

    /** 实时数据预留时间 */
    public static final int MAX_RT_TIME = 5;
    /**
     * 实时数据缓存失效时间
     */
    public static final long RTDATA_EXPIRE_TIME = 3600;
    /**
     * 默认无物模型id
     */
    public static final int DEFAULT_THING_ID = 0;
    /**
     * 电气火灾类型id
     */
    public static final int ELECTRICAL_FIRE_DATA_ID = 1;
    /**
     * 默认存储周期 (单位秒 1个小时)
     */
    public static final int DEFAULT_SOTRE = 3600;

    /**
     * 默认无物模型名称
     */
    public static final String DEFAULT_THING_NAME = "其他";

	/**
	 * 网关属性最新更新数据
	 */
	public static final String dataRefreshTime="gwdatarefreshtime:";
}
