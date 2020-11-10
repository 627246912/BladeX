package org.springblade.energy.factory;


import org.springblade.bean.DeviceItemExtend;

/**
 * @Auther: bond
 * @Date: 2019/6/26
 * @Description:
 */
@FunctionalInterface
public interface ShowGroupService {
    /**
     * 判断显示分组类型
     * @param deviceItemExtend
     * @return
     */
    boolean checkShowType(DeviceItemExtend deviceItemExtend);
}
