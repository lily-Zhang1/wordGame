import java.io.*;
import java.util.ArrayList;

/**
 * 使用控制器控制当前玩家的操作
 *
 * @author lily
 * */

public class Controller {
    /** store the map information */
    private ArrayList<Location> location;

    /** store the action information */
    private ArrayList<Action> actions;

    /** store the information of all players */
    private ArrayList<Player> players;

    /** the information of player input */
    private String line;

    /** the unplaced location stores the item which will be produced */
    private Location unPlaced;

    public Controller(ArrayList<Location> loc, ArrayList<Action> act)
    {
        location = loc;
        actions = act;
        players = new ArrayList<>();
        for (Location l: location) {
            if (l.getName().equals("unplaced")) {
                unPlaced = l;
            }
        }
    }

    //当前玩家信息序列化并存档
    public void save(Player curPlayer){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(
                    "/Users/jeaneason/Desktop/term2/JAVA/4-STAG/data/"+curPlayer.getName()+".txt"
            )));
            oos.writeObject(curPlayer);
            System.out.println("序列化成功！");
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //反序列化
    public void deserialize(String name){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                    new File("/Users/jeaneason/Desktop/term2/JAVA/4-STAG/data/"+name+".txt")
            ));
            Player curPlayer = (Player) ois.readObject();
            System.out.println("name: "+curPlayer.getName());
            System.out.println("health: "+curPlayer.getHealth());
            System.out.println("inv: "+curPlayer.getInventory().getGoods().size());
            System.out.println("反序列化成功！！！");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** set the input */
    public void setLine(String input) { line = input; }

    /** return the name of current player */
    public String getCurPlayerName() { return line.substring(0,line.indexOf(":")); }

    /** return the current player */
    public Player getCurPlayer(String curPlayerName)
    {
        Player curPlayer;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getName().equals(curPlayerName)) {
                curPlayer = players.get(i);
                return curPlayer;
            }
        }
        curPlayer = new Player();
        curPlayer.setName(curPlayerName);
        //new player will start with the initial position
        curPlayer.gotoLocation(curPlayer, location.get(0));
        players.add(curPlayer);
        return curPlayer;
    }

    /** return all the artefacts in the inventory of current player */
    public String showInventory(Player curPlayer)
    {
        if (curPlayer.getInventory().getGoods().size() == 0) { return null; }
        String str = "There are some artefacts in your inventory : \n";
        for (Artefact a: curPlayer.getInventory().getGoods()) {
            str += a.getName() + ":\t" + a.getDescription() + "\n";
        }

        //序列化测试
        save(curPlayer);
        deserialize(curPlayer.getName());

        return str;
    }

    /** the current player pick up a artefact */
    public String pickUP(Player curPlayer)
    {
        for (Artefact a: curPlayer.getCurLocation().getArtefact()) {
            if (line.contains(a.getName())) {
                curPlayer.getInventory().addArtefact(a);
                curPlayer.getCurLocation().deleteArtefact(a);
                return "You pick up " + a.getName()
                        + ". It is " + a.getDescription();
            }
        }
        return null;
    }

    /** drop a artefact in current position */
    public String putDown(Player curPlayer)
    {
        for (Artefact a: curPlayer.getInventory().getGoods()) {
            if (line.contains(a.getName())) {
                curPlayer.getInventory().deleteArtefact(a.getName());
                curPlayer.getCurLocation().addArtefact(a);
                return "You put down " + a.getName()
                        + ". It is " + a.getDescription();
            }
        }
        return null;
    }

    /** goto a new location */
    public String gotoReturn(Player curPlayer)
    {
        for (String s: curPlayer.getCurLocation().getOutPath()) {
            if (line.contains(s)) {
                for (Location l: location) {
                    if (l.getName().equals(s)) {
                        curPlayer.gotoLocation(curPlayer,l);
                        return "Now you are in the " + l.getName()
                                + ". It is " + l.getDescription();
                    }
                }
            }
        }
        return null;
    }

    /** return all information of current location */
    public String lookReturn(Player curPlayer)
    {   //the information of position
        String reString = "You are at the " + curPlayer.getCurLocation().getName() +
            ", this is " + curPlayer.getCurLocation().getDescription() + ".\n";
        //artefact information for the current location
        for (Artefact a: curPlayer.getCurLocation().getArtefact()) {
            reString = reString + "There is a " + a.getName() +
                ", which you can collect. It is a " + a.getDescription() + "\n";
        }
        //furniture information for the current location
        for (Furniture f: curPlayer.getCurLocation().getFurniture()) {
            reString = reString + "There is a " + f.getName() +
                ", It is a " + f.getDescription() + "\n";
        }
        //character information for the current location
        for (Character c: curPlayer.getCurLocation().getCharacter()) {
            if (!c.equals(curPlayer)) {
                reString = reString + c.getName() + " is here. ";
                if (c.getDescription() != null) {
                    reString = reString + "It is " + c.getDescription() + ".\n";
                } else { reString = reString + "\n"; }
            }
        }
        //the path information of current location
        for (String s: curPlayer.getCurLocation().getOutPath()) {
            reString = reString + "There is a path to " + s + ".\n";
        }
        return reString;
    }

    /** return the corresponding action */
    public ArrayList<Action> getAction()
    {
        ArrayList<Action> act = new ArrayList<>();
        for (Action a: actions) {
            for (String s: a.getTriggers()) {
                if(line.contains(s)) { act.add(a); }
            }
        }
        return act;
    }

    /** determine if the subject satisfies the condition */
    public boolean hasSubject(Action action, Player curPlayer)
    {
        if (action.getSubjects().size() == 0) { return true; }
        int count = 0;
        int include = 0;
        for (String s: action.getSubjects()) {
            if (line.contains(s)) { include = 1; }
            for (Furniture f: curPlayer.getCurLocation().getFurniture()) {
                if (f.getName().equals(s)) { count++; }
            }
            for (Character c: curPlayer.getCurLocation().getCharacter()) {
                if (c.getName().equals(s)) { count++; }
            }
            for (Artefact a: curPlayer.getInventory().getGoods()) {
                if (a.getName().equals(s)) { count++; }
            }
        }
        if (include == 1 && count == action.getSubjects().size()) { return true; }
        return false;
    }

    /** determine if the consumed satisfies the condition */
    public boolean hasConsumed(Action action, Player curPlayer)
    {
        if (action.getConsumed().size() == 0) { return true; }
        int count = 0;
        for (String s: action.getConsumed()) {
            if (s.equals("health")) {
                curPlayer.decreaseHealth();
                count++;
            }
            if ( curPlayer.getInventory().deleteArtefact(s) == true ) {
                count++;
            }
            if ( curPlayer.getCurLocation().deleteFurniture(s) == true ) {
                count++;
            }
        }
        if (count == action.getConsumed().size()) { return true; }
        return false;
    }

    /** produce the corresponding things */
    public boolean produce(Action action, Player curPlayer)
    {
        if (action.getProduced().size() == 0) { return true; }
        ArrayList<String> produced = action.getProduced();
        for (String s: produced) {
            int flag = 0;
            for (Location l: location) {
                if (l.getName().equals(s)) {
                    curPlayer.getCurLocation().addOutPath(s);
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) { continue; }
            if (s.equals("health")) { curPlayer.increaseHealth(); }
            else {
                if (produceEntity(curPlayer, unPlaced, s) == false ) {
                    return false;
                }
            }
        }
        return true;
    }

    /** produce the item */
    private boolean produceEntity(Player curPlayer, Location loc, String name)
    {
        for (Artefact a: loc.getArtefact()) {
            if (a.getName().equals(name)) {
                loc.deleteArtefact(a);
                curPlayer.getInventory().addArtefact(a);
                return true;
            }
        }
        for (Furniture f: loc.getFurniture()) {
            if (f.getName().equals(name)) {
                loc.deleteFurniture(f.getName());
                curPlayer.getCurLocation().addFurniture(f);
                return true;
            }
        }
        for (Character c: loc.getCharacter()) {
            if (c.getName().equals(name)) {
                loc.deleteCharacter(c);
                curPlayer.getCurLocation().addCharacter(c);
                return true;
            }
        }
        return false;
    }
}
