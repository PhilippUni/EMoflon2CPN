package translation.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import translation.parser.XmlNode;

public class XmlNodeFactory {
	private static XmlNodeFactory factory ;
	private XmlNodeFactory() {};
	
	private int placeCounter = 0;
	private int transCounter = 0;
	private int placeRow = 0;
	private int transRow = 0;
	
	private class Coordinates {
		private double x;
		private double y;
		public Coordinates(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return this.x;
		}
		
		public double getY() {
			return this.y;
		}
	}
	
	private Coordinates getNextCoordinates(boolean place) {
		double x;
		double y;
		
		if(place) {
			x = placeCounter++ * 150.0;
			placeCounter = x <= 600 ? placeCounter : 0;
			placeRow += x <= 600 ? 0 : 1;
			y = placeRow * 300.0;
		}
		else {
			x = transCounter++ * 150.0;
			transCounter = x <= 600 ? transCounter : 0;
			transRow += x <= 600 ? 0 : 1;
			y = transRow * 300.0 + 150.0;
		}
		return new Coordinates(x, y);
	}
	
	
	public static XmlNodeFactory getFactory() {
		if(factory == null) factory = new XmlNodeFactory();
		return factory;
	}
	
	public void resetFactory() {
		placeCounter = placeRow = transCounter = transRow = 0;
	}
	
	private long startNumber = System.currentTimeMillis() / 1000;
	public String getNextId() {
		return "ID" + Long.toUnsignedString(startNumber++).substring(0, 10);
	}
	
	public XmlNode posattr(double x, double y) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("x", Double.toString(x));
		properties.put("y", Double.toString(y));
		return new XmlNode("posattr", null, null, properties);
	}
	
	public XmlNode fillattr(String colour, String pattern, boolean filled) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("colour", colour);
		properties.put("pattern", pattern);
		properties.put("filled", Boolean.toString(filled));
		return new XmlNode("fillattr", null, null, properties);
	}
	
	public XmlNode lineattr(String colour, int thick, String type) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("colour", colour);
		properties.put("thick", Long.toString(thick));
		properties.put("type", type);
		return new XmlNode("lineattr", null, null, properties);
	}
	
	public XmlNode textattr(String colour, boolean bold) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("colour", colour);
		properties.put("bold", Boolean.toString(bold));
	return new XmlNode("textattr", null, null, properties);
	}
	
	public XmlNode text(String tool, String version, String content) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("tool", tool);
		properties.put("version", version);
		return new XmlNode("text", content, null, null);
	}
	
	public XmlNode box(double w, double h) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("w", Double.toString(w));
		properties.put("h", Double.toString(h));
		return new XmlNode("box", null, null, properties);
	}
	
	public XmlNode binding(double x, double y) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("x", Double.toString(x));
		properties.put("y", Double.toString(y));
		return new XmlNode("binding", null, null, properties);
	}
	
	public XmlNode cond(double x, double y) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", null));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("cond", null, children, properties);
	}
	
	public XmlNode time(double x, double y) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", null));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("time", null, children, properties);
	}
	
	public XmlNode code(double x, double y, String content) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", content));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("code", null, children, properties);
	}
	
	public XmlNode priority(double x, double y) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", null));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("priority", null, children, properties);
	}
	
	public XmlNode trans(Boolean explicit, String text, String content) {
		Coordinates c = getNextCoordinates(false);
		double x = c.getX();
		double y = c.getY();
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", text));
		children.add(box(60.0, 40.0));
		children.add(binding(7.2, -3.0));
		children.add(cond(x - 39.0, y + 31.0));
		children.add(time(x + 44.5, y + 31.0));
		children.add(code(x + 64.5, y - 52.0, content));
		children.add(priority(x - 68.0, y - 31.0));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		properties.put("explicit", Boolean.toString(explicit));
		return new XmlNode("trans", null, children, properties);
	}
	
	public XmlNode ellipse(double w, double h) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("w", Double.toString(w));
		properties.put("h", Double.toString(h));
		return new XmlNode("ellipse", null, null, properties);
	}
	
	public XmlNode token(double x, double y) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("x", Double.toString(x));
		properties.put("y", Double.toString(y));
		return new XmlNode("token", null, null, properties);
	}
	
	public XmlNode marking(double x, double y, boolean hidden) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("x", Double.toString(x));
		properties.put("y", Double.toString(y));
		properties.put("hidden", Boolean.toString(hidden));
		return new XmlNode("marking", null, null, properties);
	}
	
	public XmlNode type(double x, double y) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", "BOOL"));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("type", null, children, properties);
	}
	
	public XmlNode initmark(double x, double y, String text) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", text));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("initmark", null, children, properties);
	}
	
	public XmlNode place(String text, String initMarking) {
		Coordinates c = getNextCoordinates(true);
		double x = c.getX();
		double y = c.getY();
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y));
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", text));
		children.add(ellipse(60.0, 40.0));
		children.add(token(10.0, 0.0));
		children.add(marking(0.0, 0.0, false));
		children.add(type(x + 39.0, y - 23.0));
		children.add(initmark(x + 56.0, y + 24.0, initMarking));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("place", null, children, properties);
	}
	
	public XmlNode arrowattr(double headsize, int currentcyckle) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("headsize", Double.toString(headsize));
		properties.put("currentcyckle", Integer.toString(currentcyckle));
		return new XmlNode("arrowattr", null, null, properties);
	}
	
	public XmlNode transend(String idref) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("idref", idref);
		return new XmlNode("transend", null, null, properties);
	}
	
	public XmlNode placeend(String idref) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("idref", idref);
		return new XmlNode("placeend", null, null, properties);
	}
	
	public XmlNode annot(double x, double y, String content) {
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(x, y)); //TODO Hope for cpn autofill
		children.add(fillattr("White", "Solid", false));
		children.add(lineattr("Black", 0 , "Solid"));
		children.add(textattr("Black", false));
		children.add(text("CPN Tools", "4.0.1", content));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		return new XmlNode("annot", null, children, properties);
	}
	
	public XmlNode arc(String orientation, int order, String transend, String placeend, XmlNode start, XmlNode end, String annotation) { 
		start = start.getChildren().stream().filter(n -> n.getIdentifier().equals("posattr")).findFirst().get();
		end = end.getChildren().stream().filter(n -> n.getIdentifier().equals("posattr")).findFirst().get();
		double startX = Double.parseDouble(start.getProperty("x"));
		double startY = Double.parseDouble(start.getProperty("y"));
		double endX = Double.parseDouble(start.getProperty("x"));
		double endY = Double.parseDouble(start.getProperty("y"));
		double x = Math.min(startX, endX) + Math.abs(endX - startX) / 2;
		double y = Math.min(startY, endY) + Math.abs(endY - startY) / 2;
		List<XmlNode> children = new ArrayList<XmlNode>();
		children.add(posattr(0.0, 0.0));
		children.add(fillattr("White", "", false));
		children.add(lineattr("Black", 1 , "Solid"));
		children.add(textattr("Black", false));
		children.add(transend(transend));
		children.add(placeend(placeend));
		children.add(annot(x, y, annotation));
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("id", getNextId());
		properties.put("orientation", orientation);
		properties.put("order", Integer.toString(order));
		return new XmlNode("arc", null, children, properties);
	}

	public XmlNode pageattr(String name) {
		Map<String, String> properties = new LinkedHashMap<String, String>();
		properties.put("name", name);
		return new XmlNode("pageattr", null, null, properties);
	}

	public XmlNode constraints() {
		return new XmlNode("constraints", null, null, null);
	}
}
