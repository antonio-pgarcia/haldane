package org.holistic.bactocom;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;

public class BacteriumStyle2D extends DefaultStyleOGL2D {
	private ShapeFactory2D shapeFactory;

	  @Override
	  public void init(ShapeFactory2D factory) {
	    this.shapeFactory = factory;
	  }
	
	  @Override
	  public Color getColor(Object agent) {
		  Bacterium b= (Bacterium) agent;
		  switch(b.getState()) {
		  	case D: {
		  		return new Color(168,0,0); // dark red
		  	}

		  	case R: {
		  		return new Color(0,168,0); // dark green
		  	}
	
		  	case T: {
		  		return new Color(0,0,168); // dark blue
		  	}
		  		
		  }
		  return new Color(255,255,255);
	  }
  
		
	  @Override
	  public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		  if (spatial == null) {
			  spatial = shapeFactory.createCircle(15,20);
		  }
		  return spatial;
	  }
}