package org.springblade.constants;

public abstract class CommonConstant {
    /** 站点类型：配电房*/
    public static final int STATION_STATUS_PDF = 1;

    public static final int DEFAULT_KEEP_SCALE = 2;

    /** 站点类型：箱变*/
    public static final int STATION_STATUS_XB = 2;
    /** 通信状态正常*/
    public static final int COMMUNICATION_STATUS_NORMAL = 0;


    public static final int ALARM_LEVEL_NORMAL = 1;		//告警等级：普通
    public static final int ALARM_LEVEL_SERIOUS = 2;	//告警等级：严重
    public static final int ALARM_LEVEL_FATAL = 3;		//告警等级：致命

    public static final int ALARM_STATUS_NOTALLOC = 0;		//0:未分配
    public static final int ALARM_STATUS_ALLOCED = 1;		//1：已分配
    public static final int ALARM_STATUS_DEALED = 2;		//2：已处理
    public static final int ALARM_STATUS_FAILED = 3;		//3：处理失败
    private static final long serialVersionUID = 1L;

    public static final int DEVICE_TYPE_SMART = 0;		//设备类型,0->智能机 1->GPRS DTU
    public static final int DEVICE_TYPE_GPRSDTU = 1;		// 1->GPRS DTU

    public static final int DEVICE_STATUS_NORMAL = 0;		//0:有效
    public static final int DEVICE_STATUS_STOP = 1;		//1：无效

    public static final int DEVICE_ITEM_TYPE_YAOCE = 1;		//1传输遥测2传输遥信3传输遥调4传输遥控5计算遥测6计算遥信
    public static final int DEVICE_ITEM_TYPE_YAOXIN = 2;
    public static final int DEVICE_ITEM_TYPE_YAOTIAO = 3;
    public static final int DEVICE_ITEM_TYPE_YAOKONG = 4;
    public static final int DEVICE_ITEM_TYPE_YAOCE_CALCU = 5;
    public static final int DEVICE_ITEM_TYPE_YAOXIN_CALCU = 6;

    public static final int BYTE_TYPE_4321 = 0;		//0-4321大端；1-1234小端；2-3412；3-2143
    public static final int BYTE_TYPE_1234 = 1;		//
    public static final int BYTE_TYPE_3412 = 2;		//
    public static final int BYTE_TYPE_2143 = 3;		//


    public static final int ITEM_RULE_REAL = 0;		//0实时1加2减
    public static final int ITEM_RULE_ADD = 1;		//
    public static final int ITEM_RULE_MINUS = 2;		//

    public static final int VALUE_TYPE_SIGNED = 0;		//0 有符号整形； 1 无符号整形； 2 浮点型
    public static final int VALUE_TYPE_NOTSIGNED = 1;		//
    public static final int VALUE_TYPE_FLOAT = 2;		//

    public static final int ALARM_NO = 0;		//是否告警,0:否，1：是
    public static final int ALARM_YES = 1;		//

    public static final String UNDERLINE = "_";		//


    /** 最大删除ID大小*/
    public static final int MAX_DELETE_SIZE = 10;

    /** 管理员键值*/
    public static final String MANAGER_ROLE_KEY = "admin";

    /** 超级管理员id */
    public static final Integer MANAGER_USER_ID = 1;

    /** 品牌唯一键*/
    public static final String BRAND_UNIQUE_KEY = "B&&%s";

    /** 品牌型号唯一键*/
    public static final String BRAND_TYPE_UNIQUE_KEY = "BT&&%s";

    /**上传图片格式定义*/
    public final static String IMAGE_TYPE = ".jpg,.gif,.png,.bmp,.jpeg";

    /**图片存放路径 */
    public final static String UPLOAD_IMG_DIR = "images";
    /**文档存放路径*/
    public final static String UPLOAD_DOC_DIR = "docs";
    public final static String UPLOAD_OPERATIONSTANDARD_DIR = "operationStandard";
    public final static String UPLOAD_FAULTSTANDARD_DIR = "faultStandard";
    public final static String UPLOAD_FAULTHADNLE_DIR = "faultHandle";
    public final static String UPLOAD_EQUIPMENT_DIR = "equipment";
    public final static String UPLOAD_DIAGRAM_DIR = "diagram";
    /**二维码存放路径 */
    public final static String UPLOAD_QRCODE_DIR = "qrcode";


    /**前缀*/
    public final static String EQUIPMENT_CODE_PREFIX = "SZML";
    /**设备*/
    public final static String EQUIPMENT_CODE_DEVICE = "DEV";
    /**设施*/
    public final static String EQUIPMENT_CODE_FACILITY = "FAC";


    /** 最大批处理数量*/
    public static final int BATCH_MAX_SIZE = 1000;


    /** 消息内容定义 */
    public static final String JOB_REPAIR_CONTENT = "【新报修任务】%s%s！";
    public static final String JOB_ALARM_CONTENT = "【新告警任务】%s%s！";
    public static final String REPAIR_NOTICE_CONTENT = "【新报修】%s%s！";

    /** 用户唯一键 */
    public static final String USRE_KEY = "%s&&%s";

    /** 允许操作的级别 */
    public static final Integer CAN_OPT_RANK = 3;

    /** 系统级别 */
    public static final Integer SYS_RANK = 2;

    /*变压器类型*/
    public static final String GROUP_LEFT_TYPE = "group-left";
    /*变压器组合类型*/
    public static final String GROUP_RIGHT_TYPE = "group-right";
    /*柜集合类型*/
    public static final String GUI_LIST_TYPE = "gui-list";
    /*柜子类型*/
    public static final String GUI_TYPE = "gui";
    /*柜子下面开关类型*/
    public static final String GUI_SWITCH_TYPE = "gui-switch";
    /*进线开关类型*/
    public static final String JINXIAN_SWITCH_TYPE="jinxian-switch";
    /*无功补偿柜类型*/
    public static final String WUGONG_BOX_TYPE = "wugong-box";

    /*追加键*/
    public static final String APPEND_KEY = "&";

    /*网关,rtu 告警等级*/
    public static final Integer Interruption_ALARM_LEVEL = 1;

    /** 未结束告警类型 */
    public static final int UN_END_ALARM_TYPE = 1;

    public final static String STATION_STATUS_ONLINE = "在线";
    public final static String STATION_STATUS_OFFLINE = "离线";
    public final static String STATION_STATUS_NOTEST = "未调试";

    /** 数据字典额外属性键值*/
    public static final String LABEL_KEY = "label";
    public static final String COLOR_KEY = "color";
    public static final String SCOPE_KEY = "scope";

    public static final String DEFAULT_COLOR = "#FF5453";

    /** 政府用户账号标识*/
    public static final String GOVERNMENT_USER_MARK = "_gov";
}
