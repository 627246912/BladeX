package org.springblade.energy.statistics.enums;

import org.springblade.core.tool.utils.Func;

import java.util.Arrays;
import java.util.List;

/**
 * @Auther: bond
 * @Date: 2020/5/21
 * @Description:
 */
public enum DataMold {
	FLGNOW("0","当前数据"),FLGTB("1","同期数据"),FLGHB("2","环比数据");

    public String id;
    public String value;

	public static boolean getflgnow(String dataMolds) {
		if(Func.isEmpty(dataMolds)){
			return false;
		}
		List<String> DataMolds = Arrays.asList(dataMolds.split(","));
		for(String DataMold:DataMolds){
			switch (DataMold){
				case "0":
					return true;
				case "1":
					return true;
				case "2":
					return true;
				default:
					return true;
			}
		}
		return false;
	}
	public static boolean getflgHb(String dataMolds) {
		if(Func.isEmpty(dataMolds)){
			return false;
		}
		List<String> DataMolds = Arrays.asList(dataMolds.split(","));
		for(String DataMold:DataMolds){
			switch (DataMold){
				case "2":
					return true;
			}
		}
		return false;
	}
	public static boolean getflgTb(String dataMolds) {
		if(Func.isEmpty(dataMolds)){
			return false;
		}
		List<String> DataMolds = Arrays.asList(dataMolds.split(","));
		for(String DataMold:DataMolds){
			switch (DataMold){
				case "1":
					return true;
			}
		}
		return false;
	}

	DataMold(String id, String value) {
        this.id = id;
        this.value = value;
    }




}
