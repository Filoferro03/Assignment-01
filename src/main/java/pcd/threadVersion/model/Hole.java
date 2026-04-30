package pcd.threadVersion.model;

public class Hole {
    private final V2d pos;
    private final double radius;

    public Hole(V2d pos, double radius) {
        this.pos = pos;
        this.radius = radius;
    }

    /**
     * Controlla se la pallina è caduta dentro questa specifica buca.
     */
    public boolean checkCollision(Ball b) {
        double distance = Math.hypot(b.getPos().x() - pos.x(), b.getPos().y() - pos.y());

        return distance < radius;
    }

    public V2d getPos() {
        return pos;
    }

    public double getRadius() {
        return radius;
    }
}