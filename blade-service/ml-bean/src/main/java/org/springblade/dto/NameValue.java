package org.springblade.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Auther: wangqiaoqing
 * @Date: 2019/2/21
 * @Description:
 */
@ApiModel(description = "名称值")
public class NameValue<T> implements Serializable {
    private static final long serialVersionUID = 4355136444686135749L;

	@ApiModelProperty(value = "code")
	private String code;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "值")
    private T value;

    public NameValue() {
    }

    public NameValue(String code,String name, T value) {
    	this.code=code;
        this.name = name;
        this.value = value;
    }

    public String getCode(){
    	return code;
	}
	public void setCode(String code){
		this.code=code;
	}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
