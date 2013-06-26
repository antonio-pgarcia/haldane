package org.holistic.bactocom;

public interface BacteriumParameters {
	public static String GAMMA= "gamma";
	public static String Gr= "Gr";
	public static String Gd= "Gd";
	public static String Gt= "Gt";
	public static String isOnlyOriT= "mob";
	public static String EQUATION= "equation";
	
	public static Double MIN_LENGTH= 0.8D;
	public static Double MAX_LENGTH= 2.0D;
	
	public Object setValue(String k, Object v);
	public Object getValue(String k);
}
