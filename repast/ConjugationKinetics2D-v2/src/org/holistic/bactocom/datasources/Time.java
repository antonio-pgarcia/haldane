package org.holistic.bactocom.datasources;

import org.holistic.bactocom.ModelRatesHelper;
import repast.simphony.context.Context;
import repast.simphony.data2.AggregateDataSource;
import repast.simphony.engine.environment.RunEnvironment;


/**
 * @author Antonio Prestes Garcia
 *
 */
public class Time implements AggregateDataSource {
		
	@Override
	public String getId() {
		return "Time";
	}

	@Override
	public Class<?> getDataType() {
		return int.class;
	}

	@Override
	public Class<?> getSourceType() {
		return Context.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object get(Iterable<?> objs, int size) {
		double t= RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		return (double) t;
	}

	@Override
	public void reset() {
	}

}
