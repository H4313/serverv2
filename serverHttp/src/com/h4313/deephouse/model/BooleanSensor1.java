package com.h4313.deephouse.model;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.frame.Frame;
import com.h4313.deephouse.sensor.SensorType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="booleansensor")
public class BooleanSensor1 extends Sensor {

	public BooleanSensor1(String id, SensorType type) {
		this.id = id;
		this.type = type;
	}
	
	protected boolean lastValue;

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
	@Id
	@Column(name="lastValue", nullable=false)
	public boolean isLastValue() {
		return lastValue;
	}
	
	public void setLastValue(boolean lastValue) {
		this.lastValue = lastValue;
	}
	
}
