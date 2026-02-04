import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Entity implements iMovable {
    protected float x, y, speed; // Common attributes 
    protected Color color;

    public Entity(float x, float y, Color color, float speed) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.speed = speed;
    }

    // Standard getters and setters 
    public float getX() { return x; }
    public float getY() { return y; }

    // Abstract method: Must be implemented by subclasses to display console info 
    public abstract void update();

    // These will be overridden by subclasses depending on if they use textures or shapes [cite: 114]
    public void draw(ShapeRenderer shape) {}
    public void draw(SpriteBatch batch) {}
}