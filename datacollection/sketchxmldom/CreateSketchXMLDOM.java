package sketchxmldom;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import painting.Figure;
import painting.Stroke;


public class CreateSketchXMLDOM {
	public static void writeToXML(String filename, List<Figure> figures) {
		
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			
			Element rootElement = doc.createElement("sketch");
			doc.appendChild(rootElement);
		    
			Element size = doc.createElement("size");
			size.appendChild(doc.createTextNode(figures.get(0).getWidth()
							 + "x" + figures.get(0).getHeight()));
			rootElement.appendChild(size);
			
			// XML Doc
		    for(int i = 0; i < figures.size(); ++i) {
		    	Figure f = figures.get(i);
		    	Element number = doc.createElement("number");
		    	rootElement.appendChild(number);
		    	number.setAttribute("id", Integer.toString(i+1));
		    	if(f == null) break;
		    	for(int j = 0; j < f.numIters; ++j) {
		    		Element iter = doc.createElement("iter");
		    		number.appendChild(iter);
		    		iter.setAttribute("num", Integer.toString(j+1));
		    		int nStrokes = f.getStrokes(j).size();
		    		if(nStrokes > 0) {
		    			Element numStrokes = doc.createElement("numStrokes");
		    			numStrokes.appendChild(doc.createTextNode(
		    					               Integer.toString(nStrokes)));
		    			iter.appendChild(numStrokes);
		    			ArrayList<Stroke> strokes = f.getStrokes(j);
		    			for(int k = 0; k < nStrokes; ++k) {
		    				Element stroke = doc.createElement("stroke");
		    				iter.appendChild(stroke);
		    				stroke.setAttribute("id", Integer.toString(k+1));
		    				int nPoints = strokes.get(k).getPoints().size();
		    				if(nPoints > 0) {
		    					Element numPoints = doc.createElement("numPoints");
		    					numPoints.appendChild(doc.createTextNode(
		    										  Integer.toString(nPoints)));
		    					stroke.appendChild(numPoints);
		    					for(int l = 0; l < nPoints; ++l) {
		    						Point p = strokes.get(k).getPoints().get(l);
		    						Element point = doc.createElement("point");
		    						point.appendChild(doc.createTextNode("("+(int)p.getX()
		    													 +","+(int)p.getY()+")"));
		    						stroke.appendChild(point);
		    					}
		    				}
		    			}
		    		}
		    	}
		    }
		    
		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		    
		    DOMSource source = new  DOMSource(doc);
		    StreamResult result = new StreamResult(new File(filename));
		    //StreamResult result = new StreamResult(System.out);
		    
		    transformer.transform(source, result);
			
		} catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch(TransformerException te) {
			te.printStackTrace();
		}

	}
	
}
