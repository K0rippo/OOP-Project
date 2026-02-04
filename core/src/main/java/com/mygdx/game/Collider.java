package groupProject;

import java.util.ArrayList;
import java.util.List;

public class Collider {
    private Rectangle bounds;
    private Entity owner;
    private boolean isTrigger;
    private String tag;

    public Collider(Entity owner, Rectangle bounds, boolean isTrigger) {
        this.owner = owner;
        this.bounds = bounds;
        this.isTrigger = isTrigger;
        this.tag = ""; // Initializing as empty string
    }

    public String getTag() {
        return tag;
    }

    public Entity getOwner() {
        return owner;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        this.isTrigger = trigger;
    }

    public boolean intersects(Collider other) {
        if (other == null) return false;
        // Basic AABB (Axis-Aligned Bounding Box) intersection logic
        return this.bounds.intersects(other.getBounds());
    }

	public String getOwner1() {
		// TODO Auto-generated method stub
		return null;
	}
}