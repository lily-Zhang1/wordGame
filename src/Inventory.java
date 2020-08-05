import java.util.ArrayList;
/**
 * 仓库信息
 *
 * @author lily
 * */

public class Inventory extends Entity
{
    /** Save inventory information about the current player */
    private ArrayList<Artefact> inventory;

    public Inventory() { inventory = new ArrayList<>(); }

    public ArrayList<Artefact> getGoods () { return inventory; }

    public void addArtefact(Artefact art) { inventory.add(art); }

    public boolean deleteArtefact(String art)
    {
        for(Artefact a: inventory) {
            if (a.getName().equals(art)) {
                inventory.remove(a);
                return true;
            }
        }
        return false;
    }

    /** delete all artefacts from the inventory */
    public void dropAllArtefact(Location location)
    {
        for (Artefact a : inventory) {
            location.addArtefact(a);
        }
        inventory.removeAll(inventory);
    }
}
