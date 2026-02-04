package groupProject;

public class Entity {
    private String name;

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Overriding toString makes debugging collisions much easier
    @Override
    public String toString() {
        return "Entity{name='" + name + "'}";
    }
}