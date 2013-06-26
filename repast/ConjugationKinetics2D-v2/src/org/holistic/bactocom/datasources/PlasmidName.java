package org.holistic.bactocom.datasources;

import org.holistic.bactocom.ModelRatesHelper;
import org.holistic.bactocom.MyParameters;

import repast.simphony.context.Context;
import repast.simphony.data2.AggregateDataSource;


/**
 * @author Antonio Prestes Garcia
 *
 */
public class PlasmidName implements AggregateDataSource {
		
	@Override
	public String getId() {
		return "Plasmid";
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
		return (String) MyParameters.getPlasmidName();
	}

	@Override
	public void reset() {
	}

}
