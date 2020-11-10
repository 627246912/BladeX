package org.springblade.pms.factory;


import org.springblade.enums.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: bond
 * @Date: 2019/6/26
 * @Description:
 */
public class ShowGroupServiceFactory {
    private static Map<Integer,ShowGroupService> showGroupServiceMap = new HashMap<>();

    static {
        showGroupServiceMap.put(ShowType.SHOW.getId(), deviceItemExtend -> Status.NORMAL.id.equals(deviceItemExtend.getVisible()));
        showGroupServiceMap.put(ShowType.HIDE.getId(),deviceItemExtend -> Status.DISABLE.id.equals(deviceItemExtend.getVisible()));
        showGroupServiceMap.put(ShowType.ALARM.getId(),deviceItemExtend -> BooleanEnum.YES.id.equals(deviceItemExtend.getPalarm()));
        showGroupServiceMap.put(ShowType.ENERGY.getId(),deviceItemExtend -> ItemBtype.ELECTRICITY.id.equals(deviceItemExtend.getBtype()));
        showGroupServiceMap.put(ShowType.SWITCH.getId(),deviceItemExtend -> ItemBtype.SWITCH.id.equals(deviceItemExtend.getBtype()));
        showGroupServiceMap.put(ShowType.YK.getId(),deviceItemExtend -> ItemStype.TRANSPORTYK.id.equals(deviceItemExtend.getStype()));
        showGroupServiceMap.put(ShowType.EARTHING.getId(),deviceItemExtend -> ItemBtype.EARTHING.id.equals(deviceItemExtend.getBtype()));
        showGroupServiceMap.put(ShowType.HISTORY_CURVE.getId(),deviceItemExtend -> BooleanEnum.YES.id.equals(deviceItemExtend.getHistoryCurve()));
    }

    /**
     * 获取分组策略
     * @param showType
     * @return
     */
    public static ShowGroupService getShowGroupStrategy(Integer showType){
        return showGroupServiceMap.get(showType);
    }
}
