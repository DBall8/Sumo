package physicsEngine;

import customMath.Vec2;
import physicsEngine.material.Material;
import physicsEngine.material.MetalMaterial;

import java.util.List;

public abstract class PhysicsObject {

    protected final static float MASS_FACTOR = 0.000001f;
    protected final static float GRACITY_SCALAR = 100;
    protected final static float COLLISION_GRACE = 0.00f; // Distance between objects that can still be considered a collision

    protected Vec2 position;
    protected float angle;           // In radiians

    protected Vec2 acceleration = new Vec2(0, 0);
    private Vec2 velocity;
    protected float angularVelocity; // In radiians/s

    protected Vec2 storedForces = new Vec2(0, 0);

    protected Material material;

    protected float mass;
    protected float invertedMass;

    protected float gravity;
    protected float drag;

    protected boolean timeUntilCollisonReady;
    protected float timeUntilNextCollision;

    public PhysicsObject(Material material, float gravity, float drag)
    {
        this.material = material;
        this.gravity = gravity;
        this.drag = drag;
        this.velocity = new Vec2(0,0);
        this.angularVelocity = 0;

        this.timeUntilCollisonReady = false;
        this.timeUntilNextCollision = Float.MAX_VALUE;
    }

    public abstract boolean checkCollision(PhysicsCircle circle, List<Collision> newCollisions);
    public abstract float getEarliestCollision(PhysicsCircle circle, float time);

    public abstract boolean checkCollision(PhyicsAABox box, List<Collision> newCollisions);
    public abstract float getEarliestCollision(PhyicsAABox box, float time);

    public abstract float getCrossSection(Vec2 direction);

    /**
     * Dispatcher
     * @param object
     * @param newCollisions
     */
    public boolean checkCollision(PhysicsObject object, List<Collision> newCollisions)
    {
        if (object.mass <= 0 && mass <= 0) return false;

        if (object instanceof PhysicsCircle)
        {
            return checkCollision((PhysicsCircle)object, newCollisions);
        }
        else if (object instanceof PhyicsAABox)
        {
            return checkCollision((PhyicsAABox)object, newCollisions);
        }

        return false;
    }

    /**
     * Dispatcher
     */
    public float getEarliestCollision(PhysicsObject object, float time)
    {
        if (object.mass <= 0 && mass <= 0) return time;

        if (object instanceof PhysicsCircle)
        {
            return getEarliestCollision((PhysicsCircle)object, time);
        }
        if (object instanceof PhyicsAABox)
        {
            return getEarliestCollision((PhyicsAABox)object, time);
        }

        return time;
    }

    public void update(float time)
    {
        // Apply stored forces
        // F = m * v/s
        velocity.addX(invertedMass * storedForces.getX() * time);
        velocity.addY(invertedMass * storedForces.getY() * time);

        velocity.addX(time * acceleration.getX());
        velocity.addY(time * acceleration.getY());

        storedForces.zero();

        resetCollisionTime();
    }

    public void move(float time)
    {
        if (mass <= 0) return; // Do not move static objects
        position.addX(time * velocity.getX());
        position.addY(time * velocity.getY());

        angle += angularVelocity * time;

        timeUntilNextCollision -= time;

        // 0 velocity below a threshold
        if (velocity.getX() < 0.01f && velocity.getX() > -0.01f)
        {
            velocity.setX(0);
        }

        if (velocity.getY() < 0.01f && velocity.getY() > -0.01f)
        {
            velocity.setY(0);
        }
    }

    /**
     * For external use
     * @param force
     */
    public void applyForce(Vec2 force)
    {
        if (invertedMass <= 0) return;
        storedForces = storedForces.add(force);
    }

    public float getX()
    {
        return position.getX();
    }

    public float getY()
    {
        return position.getY();
    }

    public float getAngleRads()
    {
        return angle;
    }

    public float getXVelocity()
    {
        return velocity.getX();
    }

    public float getYVelocity()
    {
        return velocity.getY();
    }

    public void setVelocity(Vec2 newVelocity)
    {
        this.velocity = newVelocity;

        resetCollisionTime();
    }

    public Vec2 getVelocity()
    {
        return velocity;
    }

    public void setX(float x)
    {
        position = new Vec2(x, position.getY());
    }

    public void setY(float y)
    {
        position = new Vec2(position.getX(), y);
    }

    void applyConstantForces()
    {
        acceleration = new Vec2(
                -1.0f * drag * velocity.getX(),
                (GRACITY_SCALAR * gravity) - (drag * velocity.getY())
        );
    }

    void resetCollisionTime()
    {
        timeUntilCollisonReady = false;
        timeUntilNextCollision = Float.MAX_VALUE;
    }
}
