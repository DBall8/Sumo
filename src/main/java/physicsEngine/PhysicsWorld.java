package physicsEngine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhysicsWorld {

    private List<PhysicsObject> physicsObjects = new ArrayList<>();
    private int numObjects;

    private List<Collision> newCollisions = new LinkedList<>();

    private float timeStep;
    private boolean gravity;
    private boolean slowdown;

    private float timeAccumulator;

    public PhysicsWorld(int updatesPerSecond, boolean gravity, boolean slowdown)
    {
        this.timeStep = 1.0f / updatesPerSecond;
        this.gravity = gravity;
        this.slowdown = slowdown;

        this.timeAccumulator = 0;
    }

    public PhysicsCircle addCircle(float x, float y, float radius)
    {
        PhysicsCircle circle = new PhysicsCircle(x, y, radius);
        physicsObjects.add(circle);
        numObjects++;

        return circle;
    }

    /**
     * Updates the physic engine
     * @param time  Time to move forward, in seconds
     * @return  Percent of a time step left over, for projection
     */
    public float update(float time)
    {
        timeAccumulator += time;

        while (timeAccumulator > timeStep)
        {
            step(timeStep);
            timeAccumulator -= timeStep;
        }

        return timeAccumulator / timeStep;
    }

    private void step(float time)
    {
        updateForces(time);
        checkCollisions();
        move(time);
    }

    private void updateForces(float time)
    {
        for (int i=0; i<numObjects; i++)
        {
            physicsObjects.get(i).update(time);
            if (slowdown) physicsObjects.get(i).applyDragForce(time);
        }
    }

    private void checkCollisions()
    {
        newCollisions.clear();

        // Check each object against all objects that come after it
        // Skip the last object, because it will already have checked with all objects
        for (int i=0; i<(numObjects-1); i++)
        {
            PhysicsObject o1 = physicsObjects.get(i);
            for (int j=i+1; j < numObjects; j++)
            {
                PhysicsObject o2 = physicsObjects.get(j);
                o1.checkCollision(o2, newCollisions);
            }
        }

        for (Collision c: newCollisions)
        {
            c.resolve();
        }
    }

    private void move(float time)
    {
        for (int i=0; i<numObjects; i++)
        {
            physicsObjects.get(i).move(time);
        }
    }
}
