import java.io.Serializable;
import java.util.ArrayList;
/**
 * 位置信息类
 * 包括当前位置包含的角色、工具、家具、出路径、入路径
 *
 * @author lily
 * */
public class Location extends Entity implements Serializable
{
    private ArrayList<String> inPath;
    private ArrayList<String> outPath;
    private ArrayList<Character> characters;
    private ArrayList<Artefact> artefacts;
    private ArrayList<Furniture> furniture;

    public Location()
    {
        inPath = new ArrayList<>();
        outPath = new ArrayList<>();
        characters = new ArrayList<>();
        artefacts = new ArrayList<>();
        furniture = new ArrayList<>();
    }

    /** add entity according to the type, name, and description of the entity */
    public void addEntity(String type, String name, String desc)
    {
        if (type.equals("Artefact")) {
            Artefact a = new Artefact();
            a.setName(name);
            a.setDescription(desc);
            artefacts.add(a);
        }
        else if (type.equals("Furniture")) {
            Furniture a = new Furniture();
            a.setName(name);
            a.setDescription(desc);
            furniture.add(a);
        }
        else if (type.equals("Character")) {
            Character a = new Character();
            a.setName(name);
            a.setDescription(desc);
            characters.add(a);
        }
    }

    /** set the path into this location */
    public void addInPath(String path){ inPath.add(path); }

    /** @return the location that can be reached from this location */
    public ArrayList<String> getOutPath() { return outPath; }

    /** set the location that can be reached from this location */
    public void addOutPath(String path){ outPath.add(path); }

    /** @return all characters in the current location */
    public ArrayList<Character> getCharacter() { return characters; }

    /** add a character */
    public void addCharacter(Character chara){ characters.add(chara); }

    /** delete a character */
    public boolean deleteCharacter(Character chara)
    {
        for(Character ch: characters) {
            if (ch.equals(chara)) {
                characters.remove(ch);
                return true;
            }
        }
        return false;
    }

    /** @return all artefacts in the current location */
    public ArrayList<Artefact> getArtefact() { return artefacts; }

    /** add a artefact */
    public void addArtefact(Artefact arte){ artefacts.add(arte); }

    /** delete a artefact */
    public boolean deleteArtefact(Artefact arte)
    {
        for(Artefact art: artefacts) {
            if (art.equals(arte)) {
                artefacts.remove(art);
                return true;
            }
        }
        return false;
    }

    /** @return all characters in the current location */
    public ArrayList<Furniture> getFurniture() { return furniture; }

    /** add a furniture */
    public void addFurniture(Furniture furn){ furniture.add(furn); }

    /** delete a furniture */
    public boolean deleteFurniture(String furn)
    {
        for(Furniture f: furniture) {
            if (f.getName().equals(furn)) {
                furniture.remove(f);
                return true;
            }
        }
        return false;
    }

}
