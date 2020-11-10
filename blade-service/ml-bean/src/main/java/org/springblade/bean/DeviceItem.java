package org.springblade.bean;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceItem implements Serializable {
	private static final long serialVersionUID = 3917299294520828407L;
	private String id;
	private String gwid;             //网关ID，即gwid
	private Integer comid;           //通讯端口：例如com1即为1,com2即为2
	private String rtuid;            //RTU设备ID
	private String modelid;          //模型文件ID
	private Integer stype;           //1传输遥测2传输遥信3传输遥调4传输遥控5计算遥测6计算遥信
	private Integer sid;             //数据项模型内编号,RTU内唯一
	private String name;             //名称
	private String unit;             //单位
	private Integer type;            //数据类型，0 有符号整形;1 无符号整形;2 浮点型
	private Float ratio;             //比例系数
	private Float ulimit;            //上限，用于告警判断
	private Float llimit;            //下限，用于告警判断
	private Integer store;           //存储，默认-1不存; 0存储，大于1表示存储周期，单位为秒，并且按存储周期存
	private Float maxval;            //遥调最大值
	private Float minval;            //遥调最小值
	private Integer group;           //分组值
	private Integer duration;        //数据异常持续时间，单位秒，超过这个时间触发告警
	private Integer virtual;         //是否计算虚量，最大、最小、平均
	private Integer calcrule;        //计算规则，0实时1加2减，尖峰平谷和存储周期，两种时间点的数据相加相减
	private String desc0;            //遥信,desc_0
	private String desc1;            //遥信,desc_1
	private String val0;             //遥信,desc_0
	private String val1;             //遥控,val_1
	private Float basic;             //基值
	private String getval;           //计算值规则，即JS脚本，数据项对应类别stype等于5或6
	private String shortname;        //简称
	private Float uulimit;           //上上限
	private Float lllimit;           //下下限
	private String relid;            //计算规则，主计算元素，单个数据项id
	private String relids;           //计算规则，参与计算元素，单个数据项id
	private Integer btype;           //0,"无",1,"电表",2,"水表",3,"气量",4,"电压",5,"电流",6,"门禁",7,"燃气",8,"烟感",9,"开关",10,"通信",11,"跳闸"
	private String desc;             //描述
	private Integer palarm;          //平台是否告警
	private Integer alarmsms;        //告警是否发送短信，0不发，1发，默认0
	private Integer alarmemail;      //告警是否发送邮件，0不发，1发，默认0
	private String alarmurl;         //告警信息推送地址，默认空
	private Integer yxalarmval;      //遥信告警值，取0或1，默认-1标识未设置
	private Integer yxalarmlevel;    //遥信告警时，告警等级，0预警，1普通，2严重，同告警信息表告警等级
	private String rtuidcb;          //组合的RTUID
	private Float ctratio;           //CT变比
	private Double val;				 //实时数据
	private String createtime;       //创建时间
	private String displayname;      //简称
	private String displaydesc;
	private Integer record;          //是否为滤波 0：非滤波，1：滤波
	private String alarmperiod;      //告警时间段
	private Integer alarmtype;       //告警类型
	/**
	 * 数据项性质 01公共用电；02免费加水；03公厕用水
	 */
	private Integer itemNature;

	/**
	 * 数据项分类，1电，2水
	 */
	private Integer itemType;

	public String getGwid() {
		return gwid;
	}
	public void setGwid(String gwid) {
		this.gwid = gwid;
	}
	public Integer getComid() {
		return comid;
	}
	public void setComid(Integer comid) {
		this.comid = comid;
	}
	public String getRtuid() {
		return rtuid;
	}
	public void setRtuid(String rtuid) {
		this.rtuid = rtuid;
	}
	public String getModelid() {
		return modelid;
	}
	public void setModelid(String modelid) {
		this.modelid = modelid;
	}
	public Integer getStype() {
		return stype;
	}
	public void setStype(Integer stype) {
		this.stype = stype;
	}
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Float getRatio() {
		return ratio;
	}
	public void setRatio(Float ratio) {
		this.ratio = ratio;
	}
	public Float getUlimit() {
		return ulimit;
	}
	public void setUlimit(Float ulimit) {
		this.ulimit = ulimit;
	}
	public Float getLlimit() {
		return llimit;
	}
	public void setLlimit(Float llimit) {
		this.llimit = llimit;
	}
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public Float getMaxval() {
		return maxval;
	}
	public void setMaxval(Float maxval) {
		this.maxval = maxval;
	}
	public Float getMinval() {
		return minval;
	}
	public Float getCtratio() {
		return ctratio;
	}
	public void setCtratio(Float ctratio) {
		this.ctratio = ctratio;
	}
	public void setMinval(Float minval) {
		this.minval = minval;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer group) {
		this.group = group;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	public Integer getVirtual() {
		return virtual;
	}
	public void setVirtual(Integer virtual) {
		this.virtual = virtual;
	}
	public Integer getCalcrule() {
		return calcrule;
	}
	public void setCalcrule(Integer calcrule) {
		this.calcrule = calcrule;
	}
	public String getDesc0() {
		return desc0;
	}
	public void setDesc0(String desc0) {
		this.desc0 = desc0;
	}
	public String getDesc1() {
		return desc1;
	}
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}
	public String getVal0() {
		return val0;
	}
	public void setVal0(String val0) {
		this.val0 = val0;
	}
	public String getVal1() {
		return val1;
	}
	public void setVal1(String val1) {
		this.val1 = val1;
	}
	public Float getBasic() {
		return basic;
	}
	public void setBasic(Float basic) {
		this.basic = basic;
	}
	public String getGetval() {
		return getval;
	}
	public void setGetval(String getval) {
		this.getval = getval;
	}
	public Float getUulimit() {
		return uulimit;
	}
	public void setUulimit(Float uulimit) {
		this.uulimit = uulimit;
	}
	public Float getLllimit() {
		return lllimit;
	}
	public void setLllimit(Float lllimit) {
		this.lllimit = lllimit;
	}
	public String getRelid() {
		return relid;
	}
	public void setRelid(String relid) {
		this.relid = relid;
	}
	public String getRelids() {
		return relids;
	}
	public void setRelids(String relids) {
		this.relids = relids;
	}
	public Integer getBtype() {
		return btype;
	}
	public void setBtype(Integer btype) {
		this.btype = btype;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Integer getPalarm() {
		return palarm;
	}
	public void setPalarm(Integer palarm) {
		this.palarm = palarm;
	}
	public Integer getAlarmsms() {
		return alarmsms;
	}
	public void setAlarmsms(Integer alarmsms) {
		this.alarmsms = alarmsms;
	}
	public String getAlarmurl() {
		return alarmurl;
	}
	public void setAlarmurl(String alarmurl) {
		this.alarmurl = alarmurl;
	}
	public Integer getYxalarmval() {
		return yxalarmval;
	}
	public void setYxalarmval(Integer yxalarmval) {
		this.yxalarmval = yxalarmval;
	}
	public Integer getYxalarmlevel() {
		return yxalarmlevel;
	}
	public void setYxalarmlevel(Integer yxalarmlevel) {
		this.yxalarmlevel = yxalarmlevel;
	}
	public String getRtuidcb() {
		return rtuidcb;
	}
	public void setRtuidcb(String rtuidcb) {
		this.rtuidcb = rtuidcb;
	}

	public Double getVal() {
		return val;
	}

	public void setVal(Double val) {
		this.val = val;
	}

	public String getShortname() {
		return shortname;
	}
	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getDisplayname() {
		return displayname;
	}
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	public String getDisplaydesc() {
		return displaydesc;
	}
	public void setDisplaydesc(String displaydesc) {
		this.displaydesc = displaydesc;
	}
	public Integer getAlarmemail() {
		return alarmemail;
	}
	public void setAlarmemail(Integer alarmemail) {
		this.alarmemail = alarmemail;
	}
	public Integer getRecord() {
		return record;
	}

	public void setRecord(Integer record) {
		this.record = record;
	}

	public String getAlarmperiod() {
		return alarmperiod;
	}

	public void setAlarmperiod(String alarmperiod) {
		this.alarmperiod = alarmperiod;
	}

	public Integer getAlarmtype() {
		return alarmtype;
	}

	public void setAlarmtype(Integer alarmtype) {
		this.alarmtype = alarmtype;
	}
	public Integer getItemNature() {
		return itemNature;
	}
	public void setItemNature(Integer itemNature) {
		this.itemNature = itemNature;
	}
	public Integer getItemType() {
		return itemType;
	}
	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}
}
