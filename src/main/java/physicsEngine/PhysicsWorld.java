package physicsEngine;

import physicsEngine.material.Material;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhysicsWorld {

    private final static boolean CCD = false;

    private List<PhysicsObject> physicsObjects = new ArrayList<>();
    private int numObjects;

    private List<Collision> newCollisions = new LinkedList<>();

    private float timeStep;
    private float gravity;
    private float drag;

    private float timeAccumulator;

    public PhysicsWorld(int updatesPerSecond, float gravity, float drag)
    {
        this.timeStep = 1.0f / updatesPerSecond;
        this.gravity = gravity;
        this.drag = drag;

        this.timeAccumulator = 0;
    }

    public PhysicsCircle addCircle(float x, float y, float radius, Material material)
    {
        PhysicsCircle circle = new PhysicsCircle(x, y, radius, material, gravity, drag);
        physicsObjects.add(circle);
        numObjects++;

        return circle;
    }

    public PhyicsAABox addAABox(float x, float y, float width, float height, Material material)
    {
        PhyicsAABox box = new PhyicsAABox(x, y, width, height, material, gravity, drag);
        physicsObjects.add(box);
        numObjects++;

        return box;
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

        if (CCD) {
            float timeLeft = time;
            while (timeLeft > (time / 1000.0f)) {
                float step = findEarliestCollision(timeLeft);
                move(time);
                checkCollisions();

                timeLeft -= step;
            }
        }
        else
        {
            checkCollisions();
            move(time);
        }
    }

    private void updateForces(float time)
    {
        for (int i=0; i<numObjects; i++)
        {
            physicsObjects.get(i).update(time);
            physicsObjects.get(i).applyConstantForces();
        }
    }

    private float findEarliestCollision(float time)
    {
        float earliestTime = time;
        // Check each object against all objects that come after it
        // Skip the last object, because it will already have checked with all objects
        for (int i=0; i<(numObjects-1); i++)
        {
            PhysicsObject o1 = physicsObjects.get(i);
            for (int j=i+1; j < numObjects; j++)
            {
                PhysicsObject o2 = physicsObjects.get(j);
                float t = o1.getEarliestCollision(o2, time);
                if (t < earliestTime) earliestTime = t;
            }
        }

        return earliestTime;
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
