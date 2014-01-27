package com.h4313.deephouse.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.h4313.deephouse.exceptions.DeepHouseException;
import com.h4313.deephouse.frame.Frame;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.Constant;

@Entity
public abstract class Sensor implements Serializable{

		protected String id;
		protected SensorType type;
		
		@Transient
		public String getFrame() {
			String frame = "";
			// TODO ecriture des trames
			frame += type.getTypeFrame();
			frame += getDatas();
			frame += id;
			frame += Constant.FRAME_STATUS_AND_CHECKSUM;
			return frame;
		}
		
		
		@Id
		@Column(name="id", nullable=false)
		public String getId() {
			return id;
		}



		public void setId(String id) {
			this.id = id;
		}


		@Column(name="sensortype", nullable=false)
		public SensorType getType() {
			return type;
		}



		public void setType(SensorType type) {
			this.type = type;
		}



		public abstract void update(Frame frame) throws DeepHouseException;
		
		@Transient
		public abstract String getDatas();

	}


