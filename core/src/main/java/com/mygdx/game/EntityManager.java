import java.util.ArrayList;
import java.util.List;

public class EntityManager {
    private List<Entity> entityList = new ArrayList<>(); 
    public void addEntity(Entity e) {
        entityList.add(e); 
    }

    public void movement() {
        for (Entity e : entityList) {
            e.movement(); 
        }
    }

    public void draw(SpriteBatch batch, ShapeRenderer shape) {
        for (Entity e : entityList) {
            e.draw(batch); 
            e.draw(shape);
        }
    }

    public void update() {
        for (Entity e : entityList) {
            e.update();
        }
    }
}
public class GameMaster extends ApplicationAdapter {
    private EntityManager em;

    @Override
    public void create() {
        em = new EntityManager();
        // Create your objects and add them to the manager
        em.addEntity(new Circle(100, 100, 50, Color.RED, 200));
        em.addEntity(new TextureObject(bucketTex, 300, 20, 300, false));
        // Add raindrops, triangles, etc.
    }

    @Override
    public void render() {
        // 1. Update state
        em.movement(); 
        em.update();

        // 2. Draw state
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        shape.begin(ShapeType.Filled);
        
        em.draw(batch, shape);
        
        shape.end();
        batch.end();
    }
}