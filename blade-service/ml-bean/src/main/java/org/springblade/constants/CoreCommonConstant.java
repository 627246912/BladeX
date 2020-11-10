package org.springblade.constants;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/10/22
 * @Description:
 */
public abstract class CoreCommonConstant {
    /** 最大批处理数量*/
    public static final int BATCH_MAX_SIZE = 1000;
    /**上传图片格式定义*/
    public final static String IMAGE_TYPE = ".jpg,.gif,.png,.bmp,.jpeg";

    /** 超级管理员角色键值 */
    public static final String MANAGER_ROLE_KEY = "admin";

    /** 系统用户*/
    public static final String SYSTEM_USER_NAME = "system";

    public static final String SPLIT_COMMA_KEY = ",";
    public static final String SPLIT_SEMICOLON_KEY = ";";
    public static final String SPLIT_SPOT_KEY = ".";
    /**追加键*/
    public static final String APPEND_KEY = "&";

    public static final String WHERE_STR = "where ";

    public static final String REQ_JSON_BODY = "json_body_request";

    public static final Double ITEM_INITIAL_VALUE = -99.9d;

    public static final String DEFAULT_NAN = "--";
}
