package physicsEngine;

import customMath.Vec2;
import physicsEngine.material.Material;
import physicsEngine.material.MetalMaterial;

import java.util.List;

public abstract class PhysicsObject {

    protected final static float MASS_FACTOR = 0.000001f;
    protected final static float GRACITY_SCALAR = 100;

    protected Vec2 position;
    protected float angle;           // In radiians

    protected Vec2 acceleration = new Vec2(0, 0);
    protected Vec2 velocity;
    protected float angularVelocity; // In radiians/s

    protected Vec2 storedForces = new Vec2(0, 0);

    protected Material material;

    protected float mass;
    protected float invertedMass;

    protected float gravity;
    protected float drag;

    public PhysicsObject(Material material, float gravity, float drag)
    {
        this.material = material;
        this.gravity = gravity;
        this.drag = drag;
    }

    public abstract void checkCollision(PhysicsCircle circle, List<Collision> newCollisions);
    public abstract float getEarliestCollision(PhysicsCircle circle, float time);

    public abstract void checkCollision(PhyicsAABox box, List<Collision> newCollisions);
    public abstract float getEarliestCollision(PhyicsAABox box, float time);

    public abstract float getCrossSection(Vec2 direction);

    /**
     * Dispatcher
     * @param object
     * @param newCollisions
     */
    public void checkCollision(PhysicsObject object, List<Collision> newCollisions)
    {
        if (object instanceof PhysicsCircle)
        {
            checkCollision((PhysicsCircle)object, newCollisions);
        }
        else if (object instanceof PhyicsAABox)
        {
            checkCollision((PhyicsAABox)object, newCollisions);
        }
    }

    /**
     * Dispatcher
     */
    public float getEarliestCollision(PhysicsObject object, float time)
    {
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

        storedForces.zero();
    }

    public void move(float time)
    {
        if (mass <= 0) return; // Do not move static objects
        position.addX(time * velocity.getX());
        position.addY(time * velocity.getY());

        angle += angularVelocity * time;

        velocity.addX(time * acceleration.getX());
        velocity.addY(time * acceleration.getY());

        // 0 velocity below a threshold
//        if (velocity.getX() < 0.01f && velocity.getX() > -0.01f)
//        {
//            velocity.setX(0);
//        }
//
//        if (velocity.getY() < 0.01f && velocity.getY() > -0.01f)
//        {
//            velocity.setY(0);
//        }
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
}
