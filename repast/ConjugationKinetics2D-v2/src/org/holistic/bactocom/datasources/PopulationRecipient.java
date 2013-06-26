package org.holistic.bactocom.datasources;

import org.holistic.bactocom.MyPopulationBookkeeper;
import repast.simphony.context.Context;
import repast.simphony.data2.AggregateDataSource;


/**
 * @author Antonio Prestes Garcia
 *
 */
public class PopulationRecipient implements AggregateDataSource {
		
	@Override
	public String getId() {
		return "Nr";
	}

	@Override
	public Class<?> getDataType() {
		return int.class;
	}

	@Override
	public Class<?> getSourceType() {
		return Context.class;
	}

	@Override
	public Object get(Iterable<?> objs, int size) {
		double p= (double) MyPopulationBookkeeper.getInstance().getR();
		return p;
	}

		@Override
		public void reset() {
		}

}
