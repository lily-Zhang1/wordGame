import java.util.ArrayList;
/**
 * 该类存储通过JsonParser解析出的所有可以进行的操作信息
 *
 * @author lily
 * */
public class Action
{
    /** store the trigger information */
    private ArrayList<String> triggers;

    /** store the subjects information */
    private ArrayList<String> subjects;

    /** store the consumed information */
    private ArrayList<String> consumed;

    /** store the produced information */
    private ArrayList<String> produced;

    private String narration;

    public Action()
    {
        triggers = new ArrayList<>();
        subjects = new ArrayList<>();
        consumed = new ArrayList<>();
        produced = new ArrayList<>();
    }

    /** add information according to the key */
    public void addAction(String key, String keyWord)
    {
        if(key.equals("triggers")) { triggers.add(keyWord); }
        if(key.equals("subjects")) { subjects.add(keyWord); }
        if(key.equals("consumed")) { consumed.add(keyWord); }
        if(key.equals("produced")) { produced.add(keyWord); }
    }

    public ArrayList<String> getTriggers() { return triggers; }

    public ArrayList<String> getSubjects() { return subjects; }

    public ArrayList<String> getConsumed() { return consumed; }

    public ArrayList<String> getProduced() { return produced; }

    public String getNarration() { return narration; }

    public void setNarration(String nar) { narration = nar; }

}
