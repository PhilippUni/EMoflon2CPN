package translation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import translation.Mapper;
import translation.MyGetter;
import translation.TranslationException;
import translation.mapper.methodConstructor.EmoflonMethod;
import translation.mapper.methodConstructor.MethodConstructor;
import translation.mapper.methodConstructor.MethodConstructorException;
import translation.mapper.methodConstructor.MethodNameConstructorException;
import translation.parser.XmlNode;

public class MapperImpl implements Mapper {
	private boolean onlyCpnFile;
	private boolean noEachTime;
	private XmlNodeFactory factory = XmlNodeFactory.getFactory();;
	private XmlNode emoflonTree;
	private XmlNode cpnTree;
	private List<XmlNode> nodes = new ArrayList<XmlNode>();
	private List<XmlNode> edges = new ArrayList<XmlNode>();
	private List<XmlNode> cpnChildren = new ArrayList<XmlNode>();
	private Map<XmlNode, XmlNode> nodeMapping = new HashMap<XmlNode, XmlNode>();
	private Map<String, String> cpnProperties = new HashMap<String, String>();
	private List<XmlNode> places = new ArrayList<XmlNode>();
	private List<XmlNode> trans = new ArrayList<XmlNode>();
	private List<XmlNode> arcs = new ArrayList<XmlNode>();
	private List<StopNode> stopNodes = new ArrayList<StopNode>();
	private XmlNode startNode;
	private XmlNode forEachNode=null;
	private int port;
	private Map<String, EmoflonMethod> methods = new HashMap<String, EmoflonMethod>();
	private MethodConstructor methodConstructor;
	private int disconnectCounter = 1;
	
	public MapperImpl(XmlNode emoflonTree, Class<?> chosenClass, String chosenMethod, boolean onlyCpnFile) {
		this(emoflonTree, 9000, chosenClass, chosenMethod, onlyCpnFile);
	}
	
	public MapperImpl(XmlNode emoflonTree, int port, Class<?> chosenClass, String chosenMethod, boolean onlyCpnFile) {
		this.emoflonTree = emoflonTree;
		this.port = port;
		this.onlyCpnFile = onlyCpnFile;
		methodConstructor = new MethodConstructor(chosenClass, chosenMethod);
	}
	
	public Map<String, EmoflonMethod> getMethods() {
		return methods;
	}
	
	public XmlNode getMappedCpnTree() {
		if(cpnTree == null)
			cpnTree = startMapping();
		
		return cpnTree;
	}
	
	private XmlNode startMapping() {
		if(!onlyCpnFile)
			methodConstructor.initialize();
		
		cpnChildren.add(factory.pageattr(emoflonTree.getProperty("name")));
		cpnProperties.put("id", factory.getNextId());
		System.out.println("get this");
		XmlNode cpn = new XmlNode("page", null, cpnChildren, cpnProperties);
		List<XmlNode> emoflonChildren = emoflonTree.getChildren().stream().filter(n -> n.getIdentifier().equals("activity")).findFirst().get().getChildren();
		
		
		//List<XmlNode> forEachNodesID = emoflonChildren.stream().filter(n-> MyGetter.getProperty(n, forEach));
		//List <XmlNode> nodesTest = new ArrayList<XmlNode>();
		//List <XmlNode> edgesTest = new ArrayList<XmlNode>();
		//boolean test= emoflonChildren.stream().noneMatch(e -> e.getProperties().containsKey("forEach"));
		//System.out.println(test);
		/**for(XmlNode xmlNode:emoflonChildren) {						// einteilung vorgezogen für ForEach Tests
			if (xmlNode.getIdentifier().equals("ownedActivityNode")) {
				nodes.add(xmlNode);
			}
			else if(xmlNode.getIdentifier().equals("ownedActivityEdge"))
				edges.add(xmlNode);
			//else
				//throw new MapperException("Mapper can't handle the node " + xmlNode.getIdentifier() + ": " + xmlNode.getProperties().toString());
				
				
		}*/
			/**String isForEach = MyGetter.getProperty(xmlNode, "forEach");				// vorerst rausgenommen
			int forEachId = EmoflonAddressInterpreter.addressToNumber(MyGetter.getProperty(xml, "source"));
			int i = EmoflonAddressInterpreter.address
			String NodeId = xmlNode.get
			if (isForEach.equals("true")) {
				emoflonChildren.stream()
			}
		}*/
		for(XmlNode xmlNode : emoflonChildren) {// xmlNode.getProperties("forEach");
			if(xmlNode.getIdentifier().equals("ownedActivityNode")) {
				nodes.add(xmlNode);
				String xsi_type = MyGetter.getProperty(xmlNode, "xsi:type");
				String isForEach =MyGetter.getProperty(xmlNode, "forEach");						// PNOTE: change here
				if (isForEach.equals("true")) {
					System.out.println("the Node "+xmlNode.toString()+"is for Each"); 		
					/**XmlNode child = mapEmoflonNodeToCpnTransition(MyGetter.getProperty(xmlNode, "name"));
					trans.add(child);
					nodeMapping.put(xmlNode, child);
					if(!onlyCpnFile) {
						EmoflonMethod method = methodConstructor.constructMethod(xmlNode);				// just creates Method.
						methods.put(method.getName(), method);											// 
					}
					continue;*/
				}	
				
				if(xsi_type.equals("activities:StartNode")) {
					
				}
				
				else if(xsi_type.equals("activities:StoryNode")) {
					XmlNode child = mapEmoflonNodeToCpnTransition(MyGetter.getProperty(xmlNode, "name"));
					trans.add(child);
					nodeMapping.put(xmlNode, child);
					if(!onlyCpnFile) {
						EmoflonMethod method = methodConstructor.constructMethod(xmlNode);				// just creates Method.
						methods.put(method.getName(), method);											// 
					}
				}	
				else if(xsi_type.equals("activities:StatementNode")) {
					XmlNode child = mapEmoflonNodeToCpnTransition(MyGetter.getProperty(xmlNode, "name"));
					trans.add(child);
					nodeMapping.put(xmlNode, child);
				}
				else if(xsi_type.equals("activities:StopNode")) {
					
				}
			}
			else if(xmlNode.getIdentifier().equals("ownedActivityEdge")) {
				edges.add(xmlNode);
			}
				
			else
				throw new MapperException("Mapper can't handle the node " + xmlNode.getIdentifier() + ": " + xmlNode.getProperties().toString());
		}
		
		for(XmlNode xmlNode : edges) {
			/**if(xmlNode.getProperties().containsKey("guard") &&xmlNode.getProperty("guard").equals("END")) {
				
			}*/
			XmlNode place = mapEmoflonEdgeToCpnPlace(xmlNode);
			if(place != null)
				places.add(place);
		}
		
		String startId = MyGetter.getProperty(startNode, "id");
		XmlNode startPlace = factory.place("Start", "1`true");
		XmlNode connectTrans = factory.trans(false,"connect", "action\nacceptConnection(\"Emoflon2Cpn\"," + port +  ")");
		cpnChildren.add(startPlace);
		cpnChildren.add(connectTrans);
		arcs.add(factory.arc("PtoT", 1, MyGetter.getProperty(connectTrans, "id"), MyGetter.getProperty(startPlace, "id"), connectTrans, startPlace, "true"));
		arcs.add(factory.arc("TtoP", 1, MyGetter.getProperty(connectTrans, "id"), startId, connectTrans, startNode, "true"));
		for(StopNode stopNode: stopNodes) {
			XmlNode place = stopNode.getNode();
			String arcGuard = stopNode.getArcGuard();
			String id = MyGetter.getProperty(place, "id");
			XmlNode endPlace = factory.place("End" + disconnectCounter, null);
			XmlNode disconnectTrans = factory.trans(false, "disconnect" + disconnectCounter, "action\ncloseConnection(\"Emoflon2Cpn\")");
			disconnectCounter++;
			cpnChildren.add(endPlace);
			cpnChildren.add(disconnectTrans);
			arcs.add(factory.arc("TtoP", 1, MyGetter.getProperty(disconnectTrans, "id"), MyGetter.getProperty(endPlace, "id"), disconnectTrans, endPlace, "true"));
			arcs.add(factory.arc("PtoT", 1, MyGetter.getProperty(disconnectTrans, "id"), id, disconnectTrans, place, arcGuard));
		}
		
		cpnChildren.addAll(places);
		cpnChildren.addAll(trans);
		cpnChildren.addAll(arcs);
		cpnChildren.add(factory.constraints());
		return cpn;
	}
	
	private XmlNode mapEmoflonEdgeToCpnPlace(XmlNode edge) {
		int source = EmoflonAddressInterpreter.addressToNumber(MyGetter.getProperty(edge, "source"));
		int target = EmoflonAddressInterpreter.addressToNumber(MyGetter.getProperty(edge, "target"));
		XmlNode sourceNode = nodes.get(source);
		XmlNode targetNode = nodes.get(target);
		String sourceName = sourceNode.getProperty("comment");
		String targetName = targetNode.getProperty("comment");
		XmlNode sourceTrans = nodeMapping.get(sourceNode);
		XmlNode targetTrans = nodeMapping.get(targetNode);
		String sourceId = MyGetter.getProperty(sourceTrans, "id");
		String targetId = MyGetter.getProperty(targetTrans, "id");
		XmlNode existingSourceArc = getArcWithSameTrans(sourceId, false);
		XmlNode existingTargetArc = getArcWithSameTrans(targetId, true);
		
		String edgeGuard = edge.getProperty("guard");
		String arcGuard = null;
		String nextIstForEach = MyGetter.getProperty(targetNode, "forEach");
		if(edgeGuard != null) {
			if(edgeGuard.equals("SUCCESS")) arcGuard= Boolean.toString(true);
			else if(edgeGuard.equals("FAILURE")) arcGuard  = Boolean.toString(false);
			else if (edgeGuard.equals("END")) {
				arcGuard= Boolean.toString(false); 	// PNOTE: stimmt so noch nicht aber funktioniert
				
			}
			else if (edgeGuard.equals("EACH_TIME")) {
				System.out.println("vorsicht Each Time");								// PNOTE: muss noch aktion bekommen
				arcGuard =Boolean.toString(true);
			}
			else throw new MapperException("Unknown edge guard between " + source + " and " + target);
		}
		else arcGuard= Boolean.toString(true);
		
		if(existingSourceArc != null) {		// && EACH_TIME tausgenommen
			String existingPlaceId = MyGetter.getProperty(existingSourceArc.getChild("placeend"), "idref");
			XmlNode existingPlace = places.stream().filter(p ->p.getProperty("id").equals(existingPlaceId)).findFirst().get();
			if(targetId != null) {
				arcs.add(factory.arc("PtoT", 1, targetId, existingPlaceId, targetTrans, existingPlace, arcGuard));
			}
			else {
				stopNodes.add(new StopNode(existingPlace, arcGuard));	
			}
			XmlNode child = existingPlace.getChild("text");
			String currentNamePart = null;
			child.setContent(child.getContent() + "&amp;" +
					((currentNamePart = targetNode.getProperty("name")) == null ? "StopNode" : currentNamePart));
			return null;
		}
		if(existingTargetArc != null&&MyGetter.getProperty(targetNode, "forEach").equals("nope")) {		
			String existingPlaceId = MyGetter.getProperty(existingTargetArc.getChild("placeend"), "idref");
			XmlNode existingPlace = places.stream().filter(p ->p.getProperty("id").equals(existingPlaceId)).findFirst().get();
			arcs.add(factory.arc("TtoP", 1, targetId, existingPlaceId, targetTrans, existingPlace, "receive()"));
			XmlNode child = existingPlace.getChild("text");
			String currentNamePart = null;
			child.setContent(((currentNamePart = sourceNode.getProperty("name")) == null ? "StopNode" : currentNamePart)
					+ "&amp;" +  child.getContent());
			return null;
		}
		if (nextIstForEach.equals("true")&&existingTargetArc!= null) {
			String usedPlaceId = forEachNode.getProperty("id");
			//XmlNode existingPlace = places.stream().filter(p-> p.getProperty("id").equals(existingPlaceId)).findFirst().get();
			arcs.add(factory.arc("TtoP", 1, sourceId, usedPlaceId, sourceTrans, forEachNode, "receive()"));
			XmlNode child = forEachNode.getChild("text");
			String currentNamePart = null;
			child.setContent(((currentNamePart = sourceNode.getProperty("name")) == null ? "StopNode" : currentNamePart)
					+ "&amp;" +  child.getContent());
			return null;
			
		}
		
		StringBuilder nameBuilder = new StringBuilder();
		String currentNamePart = null;
		nameBuilder.append((currentNamePart = sourceNode.getProperty("name")) == null ? "StartNode" : currentNamePart);
		nameBuilder.append('-');
		nameBuilder.append((currentNamePart = targetNode.getProperty("name")) == null ? "StopNode" : currentNamePart);
		XmlNode place = factory.place(nameBuilder.toString(), null);
		String placeId = MyGetter.getProperty(place, "id");
		if(MyGetter.getProperty(targetNode, "forEach").equals("true")&&existingSourceArc==null&&existingTargetArc==null) {
			forEachNode=place;
		}
		if(sourceId != null) {
		/**	if(edgeGuard.equals("END"))
				arcs.add(factory.arc("TtoP", 1, sourceId, placeId, sourceTrans, place, "receive()"));
		else*/
				arcs.add(factory.arc("TtoP", 1, sourceId, placeId, sourceTrans, place, "receive()"));
		} else {
			startNode = place;
		}
		if(targetId != null) {
			if(edgeGuard!=null&&edgeGuard.equals("END")) {
				arcs.add(factory.arc("PtoT", 1, targetId, placeId, targetTrans, place, "receive()"));
			}
			else
				arcs.add(factory.arc("PtoT", 1, targetId, placeId, targetTrans, place, arcGuard));
		} else {
			stopNodes.add(new StopNode(place, arcGuard));
		}
		return place;
	}
	
	private XmlNode mapEmoflonNodeToCpnTransition(String name) throws MapperException {
		return factory.trans(false, name, "action\nsend(\"" + name + "\")");
	}
	
	/**private String getProperty(XmlNode node, String property) throws MapperException { // ersetzt durch eigene Klasse mit Static Metode
		if(node == null) return null;
		if(!node.getProperties().containsKey(property)) return "nope";
		String value = node.getProperty(property);
		if(value == null) throw new MapperException("Mapper found no property " + property + " in node " + node.getIdentifier());
		return value;
	}*/
	
	private XmlNode getArcWithSameTrans(String transId, boolean TtoP) {
		try {
			return arcs.stream().filter(a -> checkIdRef(a, transId, TtoP)).findFirst().get();
		}
		catch(NoSuchElementException e) {
			return null;
		}
	}
	
	private boolean checkIdRef(XmlNode arc, String transId, boolean TtoP) {
		XmlNode child = arc.getChild("transend");
		if(TtoP) 
			return arc.getProperty("orientation").equals("TtoP")? child.getProperty("idref").equals(transId) : false;
		else
			return arc.getProperty("orientation").equals("TtoP")? child.getProperty("idref").equals(transId) : false;
	}
	
	private class StopNode {
		XmlNode node;
		String arcGuard;
		
		public StopNode(XmlNode node, String arcGuard) {
			this.node = node;
			this.arcGuard = arcGuard;
		}
		public XmlNode getNode() {
			return node;
		}
		public String getArcGuard() {
			return arcGuard;
		}
	}
}
