package com.h4313.deephouse.model;

import java.io.Serializable;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.frame.Frame;
import com.h4313.deephouse.sensor.SensorType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
public class BooleanSensor1 extends Sensor implements Serializable {

	public BooleanSensor1(){
		
	}
	
	public BooleanSensor1(String id, SensorType type) {
		this.id = id;
		this.type = type;
	}
	
	protected boolean lastValue;
	
	@Transient
	public String getDatas() {
		String datas = "";
		if(lastValue) {
			datas += "";
		}
		return datas;
	}
	
	
	public void setDatas(String datas){
		
	}
	
	@Override
	public void update(Frame frame) throws DeepHouseException {
		// TODO Auto-generated method stub
		
	}
	
	@Column(name="lastValue", nullable=false)
	public boolean isLastValue() {
		return lastValue;
	}
	
	public void setLastValue(boolean lastValue) {
		this.lastValue = lastValue;
	}
	
}
