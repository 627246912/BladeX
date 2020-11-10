package org.springblade.pms.enums;

import org.springblade.core.tool.utils.Func;

/**
 * @author bond
 * @date 2020/7/14 15:01
 * @desc
 */
public enum PmsItemShowSid {

	//要展示的实时数据
	SID300("300","0"),
	SID301("301","0"),
	SID302("302","0"),
	SID303("303","0"),
	SID304("304","0"),
	SID305("305","0"),
	SID306("306","0"),

	SID307("307","0"),
	SID308("308","0"),
	SID309("309","0"),
	SID310("310","0"),
	SID311("311","0"),
	SID312("312","0"),
	SID313("313","0"),
	SID314("314","0"),
	SID315("315","0"),
	SID316("316","0"),
	SID317("317","0"),
	SID318("318","0"),
	SID319("319","0"),
	SID320("320","0"),
	SID321("321","0"),
	SID1047("1047","0"),

	SID1062("1062","0"),
	SID1134("1134","0"),
	SID1109("1109","0"),
	SID1107("1107","0"),
	SID1135("1135","0"),
	SID1136("1136","0"),
	SID1137("1137","0"),
	SID1138("1138","0"),
	SID1526("1526","0"),
	SID1527("1527","0"),
	SID2003("2003","0"),
	SID2020("2020","0");










	public String id;
	public String value;

	PmsItemShowSid(String id, String value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static String getValue(String id){
		PmsItemShowSid[] alarmLevels = PmsItemShowSid.values();
		for (PmsItemShowSid alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
				return alarmLevel.value;
			}
		}
		return "";
	}

	/**
	 * 获取对应属性值
	 * @param id
	 * @return
	 */
	public static PmsItemShowSid getProductSid(String id){
		PmsItemShowSid[] alarmLevels = PmsItemShowSid.values();
		for (PmsItemShowSid alarmLevel : alarmLevels) {
			if(Func.equals(id,alarmLevel.id)){
				return alarmLevel;
			}
		}
		return null;
	}

	}
