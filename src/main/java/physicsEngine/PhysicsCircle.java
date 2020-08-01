package physicsEngine;

import customMath.CustomMath;
import customMath.Vec2;

import java.util.List;

public class PhysicsCircle extends PhysicsObject{

    private float radius;

    public PhysicsCircle(float x, float y, float radius, float gravity, float drag)
    {
        super(gravity, drag);
        position = new Vec2(x, y);
        this.radius = radius;

        velocity = new Vec2(0, 0);
        angularVelocity = 0;

        mass = (float)Math.PI * radius * radius * MASS_FACTOR;
        invertedMass = 1.0f / mass;
    }

    @Override
    public void checkCollision(PhysicsCircle circle, List<Collision> newCollisions)
    {
        float distSquared = CustomMath.getDistSquared(position, circle.position);
        float radialSum = radius + circle.radius;

        if (distSquared <= (radialSum * radialSum))
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
        }
    }

    @Override
    public float getEarliestCollision(PhysicsCircle circle, float time) {

        // Circle 2's position when the origin is set to circle 1's position
        Vec2 relativePosition = circle.position.sub(position);

        // Circle 1's velocity if circle 2 is the reference point
        Vec2 relativeVelocity = velocity.sub(circle.velocity);
        Vec2 velocityNormal = relativeVelocity.normalize();

        float relativeVelocityMagnitude = relativeVelocity.getMagnitude();
        if (relativeVelocityMagnitude < 0) return time;

        float radiusSum = radius + circle.radius;

        Vec2 tangent = velocityNormal.getTangent();
        float tangentDot = tangent.dot(relativePosition);
        if (Math.abs(tangentDot) > radiusSum)
        {
            // Will not collide
            return time;
        }

        // Will eventually collide

        // Distance from point to perpendicular intersection of circle
        float normalDot = relativePosition.dot(velocityNormal);

        // Distance to intersection on the circle
        float normalDist = normalDot - (float)Math.sqrt((radiusSum * radiusSum) - (tangentDot * tangentDot));

        float collisionTime = normalDist / relativeVelocityMagnitude;

        if (collisionTime < time) return collisionTime;

        return time;
    }

    public void checkCollision(PhyicsAABox box, List<Collision> newCollisions)
    {
        box.checkCollision(this, newCollisions);
    }
    public float getEarliestCollision(PhyicsAABox box, float time)
    {
        return time;
    }

    @Override
    public float getCrossSection(Vec2 direction)
    {
        return radius * 2;
    }

    public float getRadius(){ return radius; }
}
