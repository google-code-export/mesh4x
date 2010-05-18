package org.mesh4j.meshes.model;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlMixed;

@XmlAccessorType(XmlAccessType.NONE)
public class DataSet extends AbstractModel {
	
	public static final String TYPE_PROPERTY = "dataset_type";
	public static final String SCHEDULE_PROPERTY = "dataset_schedule";
	public static final String SCHEDULINGOPTION_PROPERTY = "dataset_schedulingoption";
	
	@XmlElement
	private DataSetType type;
	@XmlElement
	private Schedule schedule;
	@XmlElement
	private SchedulingOption schedulingOption;
	@XmlElementWrapper(name = "dataSources")
	@XmlElementRefs({
        	@XmlElementRef(type = EpiInfoDataSource.class) 
        })
	@XmlMixed()
	private List<DataSource> dataSources;
	
	public void setSchedule(Schedule schedule) {
		Schedule oldSchedule = this.schedule;
		this.schedule = schedule;
		firePropertyChange(SCHEDULE_PROPERTY, oldSchedule, schedule);
	}
	
	public Schedule getSchedule() {
		return this.schedule;
	}
	
	public void setSchedulingOption(SchedulingOption schedulingOption) {
		SchedulingOption oldSchedulingOption = this.schedulingOption;
		this.schedulingOption = schedulingOption;
		firePropertyChange(SCHEDULINGOPTION_PROPERTY, oldSchedulingOption, schedulingOption);
	}
	
	public SchedulingOption getSchedulingOption() {
		return this.schedulingOption;
	}
	
	public void setType(DataSetType type) {
		DataSetType oldType = this.type; 
		this.type = type;
		firePropertyChange(TYPE_PROPERTY, oldType, type);
	}
	
	public DataSetType getType() {
		return this.type;
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

}
