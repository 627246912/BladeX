package org.springblade.enums;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/5/27
 * @Description:
 */
public enum CurveType {

	VOLTAGE(1,"电压"),
	ELECTRICCURRENT(2,"电流"),
	POWERFACTOR(3,"功率因数"),
    EQ_TEMPERATURE(4,"温度"),
	FREQUENCY(5,"频率"),
	HARMONIC(6,"谐波"),
	ELECTRICITY(7,"电量"),
    LOAD_RATE(8,"开关负载率"),

	DIANLIUBUPINGHENG(9,"三相电流不平衡度"),

	DIANYABODONG(10,"电压波动"),
	XIANSUNFENXI(11,"线损分析"),
	XIAOLVFENXI(12,"效率分析"),
	SUNSHILVFENXI(13,"损失率分析"),
	FUZAILVFENXI(14,"负载率分析"),

	SHUILIANG(15,"水量"),
	QILIANG(16,"气量"),

	XIANDIANYA(17,"线电压"),
	YOUGONGGONGLV(18,"用功功率"),
	WUGONGGONGLV(19,"无功功率"),
	SHIZAIGGONGLV(20,"视在功率"),
	ABCPOWERFACTOR(21,"ABC功率因数"),
	BIANYAQI_RATE(22,"变压器负载率")
	;








    private Integer id;
    private String value;

    CurveType(Integer id, String value) {
        this.id = id;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public String getValue() {
        return value;
    }



    /**
     * 根据id获取对应类型
     * @param id
     * @return
     */
    public static CurveType getById(Integer id){
        CurveType[] types = CurveType.values();
        for (CurveType type : types) {
            if(type.getId().equals(id)){
                return type;
            }
        }
        return null;
    }
}
