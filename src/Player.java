/**
 * 玩家类
 * 属性包括：玩家的仓库信息，当前位置信息，健康值
 *
 * @author lily
 * */

public class Player extends Character
{
    /** Save inventory information about the current player */
    private Inventory inventory;
    private Location curLocation;
    private int health;

    private static final long serialVersionUID = 2L;

    public Player()
    {
        inventory = new Inventory();
        health = 3;
    }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append(this.getName()+","+this.health);
        return sb.toString();
    }

    public int getHealth() { return health; }

    /** reset health = 3 */
    public void resetHealth() { health = 3; }

    public void increaseHealth() { health++; }

    public void decreaseHealth() { health--; }

    public Location getCurLocation() { return curLocation; }

    /** goto a location */
    public void gotoLocation(Player curPlayer, Location targetLocation)
    {
        //delete current player from the current location
        if (curLocation != null) { curLocation.deleteCharacter(curPlayer); }
        curLocation = targetLocation;
        curLocation.addCharacter(curPlayer);
    }

    public Inventory getInventory() { return inventory; }


}
