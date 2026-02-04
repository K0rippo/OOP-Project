package groupProject;

public class Rectangle {
    public float x, y, width, height;

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Checks if this rectangle overlaps with another using AABB logic.
     */
    public boolean intersects(Rectangle other) {
        // Check if the right side of A is left of the left side of B
        // Check if the left side of A is right of the right side of B
        // Check if the bottom of A is above the top of B
        // Check if the top of A is below the bottom of B
        
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
}