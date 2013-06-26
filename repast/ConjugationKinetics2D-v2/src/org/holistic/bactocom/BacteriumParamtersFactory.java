package org.holistic.bactocom;

import java.util.HashMap;
import java.util.Map;

public class BacteriumParamtersFactory {
	public static BacteriumParameters getParameters() {
		return BacteriumParametersImpl.getInstance();
	}
}


class BacteriumParametersImpl implements BacteriumParameters {
	private Map<String, Object> parameters= new HashMap<String, Object>();
	private static BacteriumParametersImpl instance= new BacteriumParametersImpl(); 

	private BacteriumParametersImpl() {
	}
	
	public static BacteriumParametersImpl getInstance() {
		return instance;
	}
	
	@Override
	public Object setValue(String k, Object v) {
		Object p= null;
		p= parameters.put(k, v);
		return p;
	}

	@Override
	public Object getValue(String k) {
		Object v= null;
		v= parameters.get(k);
		return v;
	}

}