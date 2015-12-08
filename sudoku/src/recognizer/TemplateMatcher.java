package recognizer;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import com.sun.org.apache.xpath.internal.operations.Bool;

import javafx.util.Pair;
import model.Sketch;
import model.Stroke;

public class TemplateMatcher {
	private List<Sketch> myTemplates;
    private List<RankedItem> myRankings;

    public static int N = 32;
    public static int SIZE = 400;
    public static Point K = new Point(200, 200);

    public static String NO_RESULT = "none";
    public static String SYMBOL_I_LABEL = "I";

    public static double LINE_RATIO_THRESHOLD_FLOOR = 1.0;
    public static double LINE_RATIO_THRESHOLD_CEILING = 1.2;
    public static double LINE_ANGLE_0 = 0.0;
    public static double LINE_ANGLE_180 = 180.0;
    public static double LINE_ANGLE_DEVIATION_THRESHOLD = 15.0;
    public static int TOP_RANKINGS_COUNT = 5;
    
    public TemplateMatcher(String dirPath)
    {
        // get the templates from the data directory
        try {
			myTemplates = CreateTemplates(dirPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public List<RankedItem> Rank(Sketch symbol)
    {
        // initialize rankings list
        List<RankedItem> rankings = new ArrayList<RankedItem>();

        //
        if (IsISymbol(symbol.getStrokes()))
        {
            for (int i = 0; i < myTemplates.size(); ++i)
            {
                rankings.add(new RankedItem(1, 1.0, 1.0));
            }

            myRankings = rankings;
            return rankings;
        }

        // normalize the symbol
        Sketch input = TemplateMatcher.Normalize(symbol);

        // classify the input
        Sketch minTemplate = myTemplates.get(0);
        for (int i = 0; i < myTemplates.size(); ++i)
        {
            //
            Sketch template = myTemplates.get(i);
            int label = template.getLabel();
            Pair<Double, Double> metrics = Metrics(input.getStrokes(), template.getStrokes());
            double distance = metrics.getKey();
            double coverage = metrics.getValue();
            //double distance = Distance(input, template);
            //double coverage = Coverage(input, template);
            double score = 1.0 - Math.abs((1.0 - distance) / (Math.sqrt(SIZE * SIZE + SIZE * SIZE))) / 10.0;

            // 
            rankings.add(new RankedItem(label, score, coverage));
        }

        // sort list
        rankings.sort(new ScoreComparator());

        //
        myRankings = rankings;

        return myRankings;
    }

    private boolean IsISymbol(List<Stroke> input)
    {
        // case: the symbol input is not unistroke
        if (input.size() != 1)
        {
            return false;
        }


        // get the unistroke
        Stroke stroke = input.get(0);

        // calculate the 5% and 95% index
        List<Point> stylusPoints = stroke.getPoints();
        int numPoints = stylusPoints.size();
        int index05 = (int)(numPoints * 0.05);
        int index95 = (int)(numPoints * 0.95);

        // create substroke
        List<Point> stylusSubpoints = new ArrayList<Point>();
        for (int i = index05; i <= index95; ++i)
        {
            stylusSubpoints.add(stylusPoints.get(i));
        }
        Stroke substroke = new Stroke(stylusSubpoints);

        // get endpoints from unistroke
        Point point1 = stylusPoints.get(index05);
        Point point2 = stylusPoints.get(index95);

        // calculate the ratio of the path length and length
        double pathLength = PathLength(substroke);
        double distance = Distance(point1, point2);
        double ratio = pathLength / distance;

        // case: the symbol is not a line
        if (ratio < LINE_RATIO_THRESHOLD_FLOOR || ratio > LINE_RATIO_THRESHOLD_CEILING)
        {
            return false;
        }

        // calculate the angle of the line
        double angle = getAngle(point1, point2);

        // case: the angle exceeds the flat incline angle
        if (angle >= LINE_ANGLE_0 && angle < LINE_ANGLE_0 + LINE_ANGLE_DEVIATION_THRESHOLD)
        {
            return true;
        }
        if (angle > LINE_ANGLE_180 - LINE_ANGLE_DEVIATION_THRESHOLD && angle <= LINE_ANGLE_180)
        {
            return true;
        }

        return false;
    }

    public int Classify(Sketch symbol)
    {    	
        symbol.setLabel(-1);
        
        if(isScribble(symbol))
        	return 0;
        // get the rankings
        myRankings = Rank(symbol);

        // get the top rankings
        List<RankedItem> topRankings = new ArrayList<RankedItem>();
        for (int i = 0; i < TOP_RANKINGS_COUNT; ++i)
        {
            topRankings.add(myRankings.get(i));
        }

        // sort top rankings by coverage
        topRankings.sort(new CoverageComparator());

        return topRankings.get(0).label;
    }
    
    private boolean isScribble(Sketch symbol){
    	BoundingBox bb = new BoundingBox(symbol);
    	return PathLength(symbol) > 10*Math.max(bb.Width(), bb.Height());
    }

    private Pair<Double, Double> Metrics(List<Stroke> A, List<Stroke> B)
    {
        //
        List<Point> input = new ArrayList<Point>();
        List<Point> template = new ArrayList<Point>();

        //
        for (Stroke stroke : A)
        {
            for(Point point : stroke.getPoints())
            {
                input.add(point);
            }
        }
        for (Stroke stroke : B)
        {
            for (Point point : stroke.getPoints())
            {
                template.add(point);
            }
        }

        //
        double d = 0.0;
        boolean[] c = new boolean[template.size()];
        for (Point point : input)
        {

            double minD = Double.MAX_VALUE;
            int minI = 0;
            for (int i = 0; i < template.size(); ++i)
            {
                Point other = template.get(i);
                double currD = Distance(point, other);
                if (currD < minD)
                {
                    minD = currD;
                    minI = i;
                }
            }
            
            if(minD > d)
            	d = minD;
            
            if(minI < c.length)
            	c[minI] = true;
        }
        
        double dBack = 0.0;
        boolean[] cBack = new boolean[input.size()];
        for (Point point : template)
        {

            double minD = Double.MAX_VALUE;
            int minI = 0;
            for (int i = 0; i < input.size(); ++i)
            {
                Point other = input.get(i);
                double currD = Distance(point, other);
                if (currD < minD)
                {
                    minD = currD;
                    minI = i;
                }
            }
            
            if(minD > dBack)
            	dBack = minD;
            
            if(minI < c.length)
            	c[minI] = true;
        }

        double distance = Math.max(d, dBack);
        int boolCount = 0;
        for (int i = 0; i < c.length; i++) {
			if(c[i])
				boolCount++;
		}
        
        int boolCountBack = 0;
        for (int i = 0; i < cBack.length; i++) {
			if(cBack[i])
				boolCountBack++;
		}
        
        double coverage = boolCount > boolCountBack ? (double)boolCount / (double)c.length : (double)boolCountBack / (double)cBack.length;
        
        return new Pair<Double, Double>(distance, coverage);
    }

    public static List<Sketch> CreateTemplates(String dataDirPath) throws IOException
    {
        // initialize the templates, subdirectory paths, and XML parser
        List<Sketch> templates = new ArrayList<Sketch>();
        List<String> dataFilePaths = new ArrayList<String>();
        // iterate through data files
        Files.walk(Paths.get(dataDirPath)).forEach(filePath -> {
            if (Files.isRegularFile(filePath)) {
                dataFilePaths.add(filePath.toString());
            }
        });
        
        for (String dataFilePath : dataFilePaths)
        {
            // ignore non-XML files
            if (!dataFilePath.endsWith(".xml"))
            {
                continue;
            }

            // get the template
            List<Sketch> temps = readXml(dataFilePath);
            
            for (int i = 0; i < temps.size(); i++) {
            	// transform the template
                temps.set(i, TemplateMatcher.Normalize(temps.get(i)));
			}

            templates.addAll(temps);
        }

        return templates;
    }
    
    public static List<Sketch> readXml(String path){
    	List<Sketch> sketches = new ArrayList<Sketch>();
    	File fXmlFile = new File(path);
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder;
    	Document doc;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			//optional, but recommended
	    	//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	    	doc.getDocumentElement().normalize();
	    			
	    	NodeList nList = doc.getElementsByTagName("number");

	    	for (int temp = 0; temp < nList.getLength(); temp++) {
	    		
	    		Node nNode = nList.item(temp);
	    				
	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			int label = Integer.valueOf(eElement.getAttribute("id"));
	    			NodeList iterations = eElement.getElementsByTagName("iter");
	    			for (int i = 0; i < iterations.getLength(); i++) {
						Node iNode = iterations.item(i);
						if (iNode.getNodeType() == Node.ELEMENT_NODE){
							Element itr = (Element) iNode;
							Sketch sketch = new Sketch(-1, -1);
							sketch.setLabel(label);
							NodeList strokes = itr.getElementsByTagName("stroke");
							for (int j = 0; j < strokes.getLength(); j++) {
								Node sNode = strokes.item(j);
								if(sNode.getNodeType() == Node.ELEMENT_NODE){
									Element str = (Element) sNode;
									Stroke stroke = new Stroke();
									NodeList points = str.getElementsByTagName("point");
									for (int k = 0; k < points.getLength(); k++) {
										Node pNode = points.item(k);
										if(pNode.getNodeType() == Node.ELEMENT_NODE){
											Element pt = (Element) pNode;
											String sPoint = pt.getTextContent();
											sPoint = sPoint.substring(1, sPoint.length() - 1);
											int comma = sPoint.indexOf(',');
											int x = Integer.valueOf(sPoint.substring(0, comma));
											int y = Integer.valueOf(sPoint.substring(comma + 1, sPoint.length()));
											stroke.addPoint(x, y);
										}
									}
									sketch.addStroke(stroke);
								}
							}
							sketches.add(sketch);						
						}
					}
	    			
	    		}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	    	
    	return sketches;
    }
    
    public static double getAngle(Point p1, Point p2) {
        double angle = (double) Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public static Sketch Normalize(Sketch originalStrokes)
    {
        // clone original strokes
        Sketch clonedStrokes = CloneStrokes(originalStrokes);

        // initialize the list of transformed strokes
        Sketch transformedStrokes = new Sketch(-1, -1);

        // resample the strokes
        transformedStrokes = Resample(clonedStrokes, N);

        // rescale the strokes
        transformedStrokes = Scale(transformedStrokes, SIZE);

        // translate the strokes
        transformedStrokes = Translate(transformedStrokes, K);

        // timestamp the strokes
        //transformedStrokes = Timestamp(transformedStrokes, originalStrokes);

        // return the transformed strokes
        return transformedStrokes;
    }

    public static Sketch CloneStrokes(Sketch original)
    {
        Sketch clone = new Sketch(-1, -1);
        for (Stroke stroke : original.getStrokes())
        {
            List<Point> points = new ArrayList<Point>();
            for (Point point : stroke.getPoints())
            {
                points.add(new Point(point.x, point.y));
            }
            Stroke newStroke = new Stroke(points);

            clone.addStroke(newStroke);
        }
        clone.setLabel(original.getLabel());

        return clone;
    }

    public List<Sketch> GetTemplates()
    {
        if (myTemplates == null)
        {
            return new ArrayList<Sketch>();
        }

        return myTemplates;
    }

    public static Sketch Resample(Sketch strokes, int n)
    {
        // set the variable for point spacing
        // initialize the variable for total distance
        // initialize list for new strokes
        double I = PathLength(strokes) / (n - 1);
        double D = 0.0;
        Sketch newStrokes = new Sketch(-1, -1);

        // iterate through each stroke points in a list of strokes
        for (Stroke stroke : strokes.getStrokes())
        {

            // initialize list of resampled stroke points
            // add the first stroke point
            List<Point> points = stroke.getPoints();
            if(points.size() > 0)
            {
	            List<Point> newPoints = new ArrayList<Point>();
	            newPoints.add(points.get(0));
	
	            //
	            for (int i = 1; i < points.size(); ++i)
	            {
	
	                double d = Distance(points.get(i - 1), points.get(i));
	                if (D + d >= I)
	                {
	
	                    double qx = points.get(i - 1).x + ((I - D) / d) * (points.get(i).x - points.get(i - 1).x);
	                    double qy = points.get(i - 1).y + ((I - D) / d) * (points.get(i).y - points.get(i - 1).y);
	                    Point q = new Point((int)qx, (int)qy);
	                    newPoints.add(q);
	                    points.set(i, q);
	                    D = 0.0;
	                }
	                else
	                {
	                    D += d;
	                }
	            }
	            D = 0.0;
	
	            //
	            Stroke newStroke = new Stroke(newPoints);
	            newStrokes.addStroke(newStroke);
            }
        }
        
        newStrokes.setLabel(strokes.getLabel());
        return newStrokes;
    }

    public static Sketch Scale(Sketch strokes, int size)
    {
        // 1. Gather the strokes' points to calculate the their bounding box.
        List<Point> points = new ArrayList<Point>();
        for (Stroke stroke : strokes.getStrokes())
        {
            for (Point point : stroke.getPoints())
            {
                points.add(point);
            }
        }
        BoundingBox B = BoundingBox(points);

        // 
        Sketch newStrokes = new Sketch(-1, -1);
        for (Stroke stroke : strokes.getStrokes())
        {

            List<Point> newPoints = new ArrayList<Point>();

            for (Point point : stroke.getPoints())
            {
                double qx = point.x * size / B.Width();
                double qy = point.y * size / B.Height();
                Point q = new Point((int)qx, (int)qy);
                newPoints.add(q);
            }

            //
            Stroke newStroke = new Stroke(newPoints);
            newStrokes.addStroke(newStroke);
        }

        //
        newStrokes.setLabel(strokes.getLabel());
        return newStrokes;
    }

    public static Sketch Translate(Sketch strokes, Point k)
    {
        Sketch newStrokes = new Sketch(-1, -1);

        //
        List<Point> allPoints = new ArrayList<Point>();
        for (Stroke stroke : strokes.getStrokes())
        {

            for (Point point : stroke.getPoints())
            {
                allPoints.add(point);
            }
        }
        Point c = Centroid(allPoints);

        for (Stroke stroke : strokes.getStrokes())
        {

            List<Point> newPoints = new ArrayList<Point>();

            for (Point point : stroke.getPoints())
            {

                double qx = point.x + k.x - c.x;
                double qy = point.y + k.y - c.y;
                Point q = new Point((int)qx, (int)qy);
                newPoints.add(q);
            }

            //
            Stroke newStroke = new Stroke(newPoints);
            newStrokes.addStroke(newStroke);
        }

        //
        newStrokes.setLabel(strokes.getLabel());
        return newStrokes;
    }

    private static Point Centroid(List<Point> points)
    {

        double meanX = 0.0;
        double meanY = 0.0;
        for (Point point : points)
        {
            meanX += point.x;
            meanY += point.y;
        }
        meanX /= points.size();
        meanY /= points.size();

        return new Point((int)meanX, (int)meanY);
    }

    private static BoundingBox BoundingBox(List<Point> points)
    {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Point point : points)
        {

            if (point.x < minX)
                minX = point.x;

            if (point.x > maxX)
                maxX = point.x;

            if (point.y < minY)
                minY = point.y;

            if (point.y > maxY)
                maxY = point.y;
        }

        return new BoundingBox(minX, minY, maxX, maxY);
    }

    public static double Distance(Point a, Point b)
    {
        double distance = Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));

        return distance;
    }

    public static double PathLength(Sketch strokes)
    {

        double d = 0.0;
        for (Stroke stroke : strokes.getStrokes())
        {

            d += PathLength(stroke);
        }

        return d;
    }

    public static double PathLength(Stroke stroke)
    {
        double d = 0.0;
        List<Point> points = stroke.getPoints();
        for (int i = 1; i < points.size(); ++i)
        {
            d += Distance(points.get(i - 1), points.get(i));
        }

        return d;
    }
}

class BoundingBox
{
    public BoundingBox(Stroke stroke)
    {
        CreateBoundingBox(stroke);
    }

    public BoundingBox(Sketch sketch)
    {
        List<Point> points = new ArrayList<Point>();
        for (Stroke stroke : sketch.getStrokes())
        {
            points.addAll(stroke.getPoints());
        }

        CreateBoundingBox(new Stroke(points));
    }

    public BoundingBox(double minX, double minY, double maxX, double maxY)
    {
        MinX = minX;
        MinY = minY;
        MaxX = maxX;
        MaxY = maxY;
    }

    private void CreateBoundingBox(Stroke stroke)
    {
        MinX = Double.MAX_VALUE;
        MinY = Double.MAX_VALUE;
        MaxX = Double.MIN_VALUE;
        MaxY = Double.MIN_VALUE;

        List<Point> points = stroke.getPoints();
        if (points.size() == 1)
        {
            Point point = points.get(0);
            MinX = MaxX = point.x;
            MinY = MaxY = point.y;
        }
        else
        {
            for(Point point : points)
            {
                if (point.x < MinX)
                {
                    MinX = point.x;
                }
                if (point.x > MaxX)
                {
                    MaxX = point.x;
                }

                if (point.y < MinY)
                {
                    MinY = point.y;
                }
                if (point.y > MaxY)
                {
                    MaxY = point.y;
                }
            }
        }
    }

    public String ToString()
    {
        return "MinX=" + MinX + " | MinY=" + MinY + " | MaxX=" + MaxX + " | MaxY=" + MaxY;
    }

    public double Width()
    {

        return MaxX - MinX;
    }

    public double Height()
    {

        return MaxY - MinY;
    }

    public double MinX;

    public double MinY;

    public double MaxX;

    public double MaxY;
}

class RankedItem
{
	public int label;
	public Double score;
	public Double coverage;
	
	public RankedItem(int label, double score, double coverage){
		this.label = label;
		this.score = score;
		this.coverage = coverage;
	}
}

class ScoreComparator implements Comparator<RankedItem> {
    @Override
    public int compare(RankedItem o1, RankedItem o2) {
        return o2.score.compareTo(o1.score);
    }
}

class CoverageComparator implements Comparator<RankedItem> {
    @Override
    public int compare(RankedItem o1, RankedItem o2) {
        return o2.coverage.compareTo(o1.coverage);
    }
}
