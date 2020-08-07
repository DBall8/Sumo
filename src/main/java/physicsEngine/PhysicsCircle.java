package physicsEngine;

import customMath.CustomMath;
import customMath.Vec2;
import physicsEngine.material.Material;

import java.util.List;

public class PhysicsCircle extends PhysicsObject{

    private float radius;

    public PhysicsCircle(float x, float y, float radius, Material material, float gravity, float drag)
    {
        super(material, gravity, drag);
        position = new Vec2(x, y);
        this.radius = radius;

        mass = (float)Math.PI * radius * radius * MASS_FACTOR * material.getDensity();

        if (mass == 0)
        {
            invertedMass = 0;
        }
        else {
            invertedMass = 1.0f / mass;
        }
    }

    @Override
    public boolean checkCollision(PhysicsCircle circle, List<Collision> newCollisions)
    {
        float distSquared = CustomMath.getDistSquared(position, circle.position);
        float radialSum = radius + circle.radius;

        if (distSquared <= ((radialSum * radialSum) + (COLLISION_GRACE * COLLISION_GRACE)))
        {
            // Collision
            float penetration = radialSum - (float)Math.sqrt(distSquared);

            Vec2 collisionVector = position.sub(circle.position);
            collisionVector.normalize();

            Collision collision = new Collision(this, circle, collisionVector, penetration);
            collision.addContactPoint(new Vec2(
                    position.getX() + (collisionVector.getX() * radius),
                    position.getY() + (collisionVector.getY() * radius)
            ));

            newCollisions.add(collision);
            return true;
        }

        return false;
    }

    @Override
    public float getEarliestCollision(PhysicsCircle circle, float time) {

        if (this.mass <=0 && circle.mass <=0) return time;

        // Circle 1's velocity if circle 2 is the reference point
        Vec2 relativeVelocity = getVelocity().sub(circle.getVelocity());

        // If not moving relative to each other, no collision will happen
        if (relativeVelocity.getMagnitudeSquared() == 0) return time;

        Vec2 velocityNormal = relativeVelocity.copy().normalize();

        float radiusSum = radius + circle.radius;

        // Cast a ray from Circle 1 in the direction of Circle 1's relative velocity
        // To treat Circle 1 as a point, let Circle 2's radius be the sum of both radii
        CustomMath.RayCastResult result = CustomMath.rayCastToCircle(position, velocityNormal, circle.position, radiusSum);
        if (!result.intersects)
        {
            // Will not collide at current trajectories
            return time;
        }

        if (result.intersectionDistance < 0)
        {
            // Overlapping, need to cause a collision NOW
            return 0;
        }

        float collisionTime = result.intersectionDistance / relativeVelocity.getMagnitude();
        if (collisionTime < timeUntilNextCollision)
        {
            timeUntilNextCollision = collisionTime;
            timeUntilCollisonReady = true;
        }

        if (collisionTime < time)
        {
            return collisionTime;
        }

        return time;
    }

    public boolean checkCollision(PhyicsAABox box, List<Collision> newCollisions)
    {
        return box.checkCollision(this, newCollisions);
    }
    public float getEarliestCollision(PhyicsAABox box, float time)
    {
        return box.getEarliestCollision(this, time);
    }

    @Override
    public float getCrossSection(Vec2 direction)
    {
        return radius * 2;
    }

    public float getRadius(){ return radius; }
}
