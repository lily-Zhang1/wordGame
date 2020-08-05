import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
/**
 * 解析json文件
 *
 * @author lily
 * */
public class JsonParser
{
    public JsonParser(String actionFilename, ArrayList<Action> actions)
    {
        try {
            //parse() to parse the document
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(actionFilename);
            //Parse the file and convert the parsed file format into JSON Object
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            JSONArray jsonArray = (JSONArray) jsonObject.get("actions");
            //Parse each set of data
            for(int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                Action action = new Action();
                setAction(jsonObject1, action, "triggers");
                setAction(jsonObject1, action, "subjects");
                setAction(jsonObject1, action, "consumed");
                setAction(jsonObject1, action, "produced");
                action.setNarration(jsonObject1.get("narration").toString());
                actions.add(action);
            }
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe);
        } catch (ParseException pe) {
            System.out.println(pe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Parse and store the information of the corresponding action */
    private void setAction(JSONObject jsonObject, Action action, String key)
    {
        JSONArray jsonArray = (JSONArray) jsonObject.get(key);
        for(int j = 0; j < jsonArray.size(); j++) {
            action.addAction(key, (String)jsonArray.get(j));
        }
    }

}
