package physicsEngine;

import customMath.Vec2;
import physicsEngine.material.Material;
import physicsEngine.material.MetalMaterial;

import java.util.List;

public abstract class PhysicsObject {

    protected final static float MASS_FACTOR = 0.000001f;
    protected final static float DRAG_COEFF = 0.00001f;

    protected Vec2 position;
    protected float angle;           // In radiians

    protected Vec2 velocity;
    protected float angularVelocity; // In radiians/s

    protected Vec2 storedForces = new Vec2(0, 0);

    protected float mass;
    protected float invertedMass;

    protected Material material = MetalMaterial.getInstance();

    public PhysicsObject()
    {}

    public abstract void checkCollision(PhysicsCircle circle, List<Collision> newCollisions);
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
    }

    public void update(float time)
    {
        velocity.addX(invertedMass * storedForces.getX() * time);
        velocity.addY(invertedMass * storedForces.getY() * time);

        storedForces.zero();
    }

    public void move(float time)
    {
        position.addX(time * velocity.getX());
        position.addY(time * velocity.getY());

        angle += angularVelocity * time;
    }

    /**
     * For external use
     * @param force
     */
    public void applyForce(Vec2 force)
    {
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

    public void applyDragForce(float time)
    {
        float crossSection = getCrossSection(velocity);
        float vSquared = velocity.getMagnitudeSquared();

        float drag = DRAG_COEFF * vSquared * crossSection / 2.0f * time;

        Vec2 dragForce = velocity.getDir();
        dragForce.mult(-drag);

        velocity = velocity.add(dragForce);
    }
}
