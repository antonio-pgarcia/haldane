package org.holistic.bactocom.datasources;

import org.holistic.bactocom.ModelRatesHelper;
import repast.simphony.context.Context;
import repast.simphony.data2.AggregateDataSource;


/**
 * @author Antonio Prestes Garcia
 *
 */
public class GammaExperimentalT2RT implements AggregateDataSource {
		
	@Override
	public String getId() {
		return "WetLabRateCurve";
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
		Context context = (Context) objs.iterator().next();
		double rate= (double) ModelRatesHelper.getInstance().getExperimentalTransconjugantPerRecipientCell(context);
		return rate;
	}

		@Override
		public void reset() {
		}

}
