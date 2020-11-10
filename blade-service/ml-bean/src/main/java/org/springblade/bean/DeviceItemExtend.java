package org.springblade.bean;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/6/26
 * @Description:
 */
public class DeviceItemExtend extends DeviceItem {
    private static final long serialVersionUID = -6062094332775112576L;
    /**
     * 显示状态（0显示 1隐藏）
     */
    private Integer visible;
    /**
     * 历史曲线（0否   1是）
     */
    private Integer historyCurve;

    /**
     * 趋势分析（0否   1是）
     */
    private Integer trendAnalysis;
    /**
     * 能耗分析（0否   1是）
     */
    private Integer energyAnalysis;


    public Integer getVisible() {
        return visible;
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Integer getHistoryCurve() {
        return historyCurve;
    }

    public void setHistoryCurve(Integer historyCurve) {
        this.historyCurve = historyCurve;
    }

    public Integer getTrendAnalysis() {
        return trendAnalysis;
    }

    public void setTrendAnalysis(Integer trendAnalysis) {
        this.trendAnalysis = trendAnalysis;
    }

    public Integer getEnergyAnalysis() {
        return energyAnalysis;
    }

    public void setEnergyAnalysis(Integer energyAnalysis) {
        this.energyAnalysis = energyAnalysis;
    }
}
