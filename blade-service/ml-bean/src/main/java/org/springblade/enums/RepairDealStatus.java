package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2018/7/27
 * @Description:报修状态
 */
public enum RepairDealStatus {
    NODEAL(0,"未处理"), SENDED(1,"已派单"),REPAIRED(2,"已修复"),NONEEDTODEAL(3,"无需处理"),DELETE(4,"已删除");

    public Integer id;
    public String value;

    RepairDealStatus(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    /**
     * 获取对应属性值
     * @param id
     * @return
     */
    public static String getValue(Integer id){
        RepairDealStatus[] repairDealStatuss = RepairDealStatus.values();
        for (RepairDealStatus repairDealStatus : repairDealStatuss) {
            if(id.intValue() == repairDealStatus.id.intValue()){
                return repairDealStatus.value;
            }
        }
        return "";
    }
}
