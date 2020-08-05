import java.io.Serializable;

public class Entity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;

    public String getName() { return name; }

    public void setName(String playerName) { name = playerName; }

    public String getDescription() { return description; }

    public void setDescription(String playerDesc) { description = playerDesc; }
}
