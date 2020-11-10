package org.springblade.energy.statistics.repository;

import org.springblade.constants.GwsubscribeConstant;
import org.springblade.core.tool.utils.Func;
import org.springblade.energy.statistics.dto.CurveDataResq;
import org.springblade.util.BigDecimalUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bond
 * @date 2020/9/3 17:49
 * @desc
 */
public class CurveDataUtil {

	/**
	 * Y的数据除以val在乘以
	 * @return
	 */
	public static List<CurveDataResq> yvalsdivmul(List<CurveDataResq> list,Float div,Float mul){
		if(Func.isEmpty(list)){
			return list;
		}
		for(CurveDataResq curveDataResq:list){
			List<Object> resYvals= new ArrayList<>();
			List<Object> Yvals= curveDataResq.getYvals();
			for(Object Yval:Yvals){
				if(!Func.equals(Yval,GwsubscribeConstant.ITEM_NULL_VALUE)){
					Yval=	BigDecimalUtil.mulF( BigDecimalUtil.divF(Float.valueOf(Yval.toString()),div),mul);
				}
				resYvals.add(Yval);
			}
			curveDataResq.setYvals(resYvals);
			curveDataResq.setUnit("%");
			curveDataResq.setItemName("负载率");
		}

		return list;
	}

//	public static void main(String[] args) {
//		List<Object> Yvals= new ArrayList<>();
//		List<Object> resYvals= new ArrayList<>();
//		Yvals.add(100);
//		Yvals.add(GwsubscribeConstant.ITEM_NULL_VALUE);
//
//		for(Object Yval:Yvals){
//			if(!Func.equals(Yval,GwsubscribeConstant.ITEM_NULL_VALUE)){
//				Yval=	BigDecimalUtil.mulF( BigDecimalUtil.divF(Float.valueOf(Yval.toString()),2f),10f);
//			}
//			resYvals.add(Yval);
//		}
//		for(Object Yval:resYvals){
//			System.out.println(Yval);
//		}
//	}
}
