import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * 解析dot文件
 *
 * @author lily
 * */
public class DotParser
{
    public DotParser(String entityFilename, ArrayList<Location> location)
    {
        try {
            //parse() to parse the document
            Parser parser = new Parser();
            FileReader reader = new FileReader(entityFilename);
            parser.parse(reader);
            //getGraphs Returns the main Graphs found in the Reader stream.
            ArrayList<Graph> graphs = parser.getGraphs();
            //getSubgraphs returns the sub graphs found in the main graph.
            ArrayList<Graph> subGraphs = graphs.get(0).getSubgraphs();
            for(Graph g : subGraphs){
                if(g.getId().getId().equals("locations")) {
                    ArrayList<Graph> subGraphs1 = g.getSubgraphs();
                    for (Graph g1 : subGraphs1){
                        Location loc = parseSingleLoc(g1);
                        ArrayList<Graph> subGraphs2 = g1.getSubgraphs();
                        //get the item information and store it in current location
                        for (Graph g2 : subGraphs2) { parseEntity(g2, loc); }
                        location.add(loc);
                    }
                }
                else if (g.getId().getId().equals("paths")) { parsePath(g, location); }
            }
        } catch (FileNotFoundException fnfe) { System.out.println(fnfe); }
          catch (ParseException pe) { System.out.println(pe); }
    }

    /** parse the entity information in current location */
    private static void parseEntity(Graph graph, Location loc)
    {
        ArrayList<Node> nodesEnt = graph.getNodes(false);
        String id = graph.getId().getId();
        for (Node nEnt : nodesEnt) {
            String name = nEnt.getId().getId();
            String desc = nEnt.getAttribute("description");
            if(id.equals("artefacts")) {
                loc.addEntity("Artefact", name, desc);
            }
            else if(id.equals("furniture")){
                loc.addEntity("Furniture", name, desc);
            }
            else if(id.equals("characters")){
                loc.addEntity("Character", name, desc);
            }
        }
    }

    /** parse the name and description of the location */
    private static Location parseSingleLoc(Graph graph)
    {
        ArrayList<Node> nodesLoc = graph.getNodes(false);
        Node nLoc = nodesLoc.get(0);
        Location loc = new Location();
        loc.setName(nLoc.getId().getId());
        loc.setDescription(nLoc.getAttribute("description"));
        return loc;
    }

    /** parse the path */
    private static void parsePath(Graph graph, ArrayList<Location> location)
    {
        ArrayList<Edge> edges = graph.getEdges();
        for (Edge e : edges) {
            String sourceName = e.getSource().getNode().getId().getId();
            String targetName = e.getTarget().getNode().getId().getId();
            for (int i = 0; i < location.size(); i++) {
                if (location.get(i).getName().equals(sourceName)) {
                    location.get(i).addOutPath(targetName);
                }
                if (location.get(i).getName().equals(targetName)) {
                    location.get(i).addInPath(sourceName);
                }
            }
        }
    }
}
