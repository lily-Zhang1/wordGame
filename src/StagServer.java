import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * 服务器端
 *
 * @author lily
 * */
class StagServer
{
    /** store the map information */
    private ArrayList<Location> location;

    /** store the action information */
    private ArrayList<Action> actions;

    /** the controller of current player */
    private Controller ctrl;

    private ExecutorService pool=null;

    public static void main(String args[]) throws Exception
    {
        if(args.length != 2) System.out.println("Usage: java StagServer <entity-file> <action-file>");
        else new StagServer(args[0], args[1], 8888);
    }

    public StagServer(String entityFilename, String actionFilename, int portNumber) throws Exception
    {
        try {
            location = new ArrayList<>();
            new DotParser(entityFilename, location);
            actions = new ArrayList<>();
            new JsonParser(actionFilename, actions);
            ctrl = new Controller(location, actions);
            // 监听指定的端口
            ServerSocket ss = new ServerSocket(portNumber);
            
            // server将一直等待连接的到来
            System.out.println("Server Listening");

            //如果使用多线程，那就需要线程池，防止并发过高时创建过多线程耗尽资源,固定大小的线程池：
            pool = Executors.newFixedThreadPool(5);

            while(true) acceptNextConnection(ss);
        } catch(IOException ioe) {
            System.err.println(ioe);
        }
    }

    private void acceptNextConnection(ServerSocket ss) throws Exception
    {
        try {
            // 下一行将阻塞，直到接收到连接
            //accept()等待连接，该方法阻塞当前线程直到建立连接为止
            Socket socket = ss.accept();
            ServerThread st = new ServerThread(socket);
            pool.execute(st);
        } catch(IOException ioe) {
            System.err.println(ioe);
            pool.shutdown();
        }

    }

    class ServerThread implements Runnable {
        Socket socket;

        public ServerThread(Socket socket) {
            super();
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                processNextCommand(in, out);
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try{
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 处理接收到的客户端发送的信息
     * */
    private void processNextCommand(BufferedReader in, BufferedWriter out) throws IOException
    {
        String line = in.readLine();
        line = line.toLowerCase();
        ctrl.setLine(line);
        String curPlayerName = ctrl.getCurPlayerName();
        Player curPlayer = ctrl.getCurPlayer(curPlayerName);
        //Process input from the current player
        if(!handleKeyWord(out, line, curPlayer) && !handleActions(out,curPlayer)) {
            out.write("Sorry, I can't understand what you mean.\nYou can type 'look' to " +
                    "look the entities in the current location and paths to other locations");
        }
        //check the health of current player
        if (curPlayer.getHealth() <= 0) {
            out.write("\nYou run out of your health, you must return the start location '"
            +location.get(0).getName()+"'.\nAll the artifacts in your inventory have droped off.");
            //delete the current player from current location
            curPlayer.getCurLocation().deleteCharacter(curPlayer);
            //drop off all the artefacts from the inventory of current player to current place
            curPlayer.getInventory().dropAllArtefact(curPlayer.getCurLocation());
            //go back to start location
            curPlayer.gotoLocation(curPlayer, location.get(0));
            //reset the health level
            curPlayer.resetHealth();
        }
    }

    /**
     * determine if it is key word and handle it accordingly
     * */
    private boolean handleKeyWord(BufferedWriter out, String line, Player curPlayer) throws IOException
    {
        if (line.contains("inventory") || line.contains("inv")) {
            String s = ctrl.showInventory(curPlayer);
            printOut(out, s, "There is nothing in your inventory!");
            return true;
        }
        if (line.contains("get")) {
            String s = ctrl.pickUP(curPlayer);
            printOut(out, s, "What do you want to get? please type such as 'get key'.");
            return true;
        }
        if (line.contains("drop")) {
            String s = ctrl.putDown(curPlayer);
            printOut(out, s, "What do you want to drop? please type such as 'drop key'.");
            return true;
        }
        if (line.contains("goto")){
            String s = ctrl.gotoReturn(curPlayer);
            printOut(out, s, "The path to this location isn't exist.");
            return true;
        }
        if (line.contains("look")) {
            String s = ctrl.lookReturn(curPlayer);
            printOut(out, s,"There is nothing in this location.");
            return true;
        }
        if (line.contains("health")) {
            String health = Integer.toString(curPlayer.getHealth());
            out.write("Your current health level is "+ health + ".\n");
            return true;
        }
        return false;
    }

    /** print s1 or s2 */
    private void printOut(BufferedWriter out, String s1, String s2) throws IOException
    {
        if (s1 != null) { out.write(s1); }
        else { out.write(s2); }
    }

    /** determine if it is a legal action and handle it accordingly */
    private boolean handleActions(BufferedWriter out, Player curPlayer) throws IOException
    {
        ArrayList<Action> action = ctrl.getAction();
        boolean success = false;
        if (action.size() != 0) {
            for (Action a : action) {
                if(ctrl.hasSubject(a, curPlayer) && ctrl.hasConsumed(a, curPlayer)) {
                    if (ctrl.produce(a, curPlayer) == false) { out.write("You can't do that!"); }
                    else { out.write(a.getNarration()); }
                    success = true;
                }
            }
            if (success == false) {
                out.write("You don't have enough materials or are not in correct loction.");
            }
            return true;
        }
        return false;
    }
}
