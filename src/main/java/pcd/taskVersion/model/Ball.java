package pcd.taskVersion.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Ball {
    
    private volatile P2d pos;
    private volatile V2d vel;
    private final double radius;
    private final double mass;
    private final int id;
    private final Lock lock = new ReentrantLock();
    private volatile int lastHitter = 0;

    private static int idGenerator = 0;
    private static final double FRICTION_FACTOR = 0.25; 	/* 0 minimum */
    private static final double RESTITUTION_FACTOR = 1;

    public Ball(P2d pos, double radius, double mass, V2d vel){
       this.id = synchronizedGetNextId();
       this.pos = pos;
       this.radius = radius;
       this.mass = mass;
       this.vel = vel;
    }

    public void updateState(long dt, Board ctx){
        double speed = vel.abs();
        double dt_scaled = dt*0.001;
    	if (speed > 0.001) {
            double dec    = FRICTION_FACTOR * dt_scaled;
            double factor = Math.max(0, speed - dec) / speed;
            vel = vel.mul(factor);
        } else {
        	vel = new V2d(0,0);
        }
        pos = pos.sum(vel.mul(dt_scaled));
     	applyBoundaryConstraints(ctx);
    }

    public void kick(V2d impulse) {
        this.vel = this.vel.sum(impulse);
    }

    /**
     * 
     * Keep the ball inside the boundaries, updating the velocity in the case of bounces
     *
     */
    private void applyBoundaryConstraints(Board ctx){
        Boundary bounds = ctx.getBounds();
        if (pos.x() + radius > bounds.x1()){
            pos = new P2d(bounds.x1() - radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.x() - radius < bounds.x0()){
            pos = new P2d(bounds.x0() + radius, pos.y());
            vel = vel.getSwappedX();
        } else if (pos.y() + radius > bounds.y1()){
            pos = new P2d(pos.x(), bounds.y1() - radius);
            vel = vel.getSwappedY();
        } else if (pos.y() - radius < bounds.y0()){
            pos = new P2d(pos.x(), bounds.y0() + radius);
            vel = vel.getSwappedY();
        }
    }

    /**
     * 
     * Resolving collision between 2 balls, updating their position and velocity
     *
     */
    public static boolean resolveCollision(Ball a, Ball b) {
        double dx = b.pos.x() - a.pos.x();
        double dy = b.pos.y() - a.pos.y();
        double dist = Math.hypot(dx, dy);
        double minD = a.radius + b.radius;
        if (dist >= minD) {
            return false;
        }
        Ball first = (a.id < b.id) ? a : b;
        Ball second = (a.id < b.id) ? b : a;
        first.getLock().lock();
        try {
            second.getLock().lock();
            try {
                dx = b.pos.x() - a.pos.x();
                dy = b.pos.y() - a.pos.y();
                dist = Math.hypot(dx, dy);

                if (dist < minD && dist > 1e-6) {
                    double nx = dx / dist;
                    double ny = dy / dist;
                    double overlap = minD - dist;
                    double totalM = a.mass + b.mass;

                    double a_factor = overlap * (b.mass / totalM);
                    a.pos = new P2d(a.pos.x() - nx * a_factor, a.pos.y() - ny * a_factor);

                    double b_factor = overlap * (a.mass / totalM);
                    b.pos = new P2d(b.pos.x() + nx * b_factor, b.pos.y() + ny * b_factor);

                    double dvx = b.vel.x() - a.vel.x();
                    double dvy = b.vel.y() - a.vel.y();
                    double dvn = dvx * nx + dvy * ny;

                    if (dvn <= 0) {
                        double imp = -(1 + RESTITUTION_FACTOR) * dvn / (1.0/a.mass + 1.0/b.mass);
                        a.vel = new V2d(a.vel.x() - (imp / a.mass) * nx, a.vel.y() - (imp / a.mass) * ny);
                        b.vel = new V2d(b.vel.x() + (imp / b.mass) * nx, b.vel.y() + (imp / b.mass) * ny);
                    }
                    return true;
                }
                return false;
            } finally {
                second.getLock().unlock();
            }
        } finally {
            first.getLock().unlock();
        }
    }

    
    public P2d getPos(){
    	return pos;
    }

    public double getRadius() {
    	return radius;
    }

    public Lock getLock() {
        return lock;
    }

    public synchronized int synchronizedGetNextId() {
        return idGenerator++;
    }

    public V2d getVel() {
        return vel;
    }

    public int getLastHitter() {
        return lastHitter;
    }

    public void setLastHitter(int lastHitter) {
        this.lastHitter = lastHitter;
    }
}
