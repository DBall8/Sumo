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
        Vec2 velocityNormal = relativeVelocity.copy().normalize();

        float relativeVelocityMagnitude = relativeVelocity.getMagnitude();
        if (relativeVelocityMagnitude <= 0)
        {
//            System.out.println("Mag 0");
            return time;
        }

        float radiusSum = radius + circle.radius;

        // Distance from point to perpendicular intersection of circle
        float normalDot = relativePosition.dot(velocityNormal);

        Vec2 tangent = velocityNormal.getTangent();
        float tangentDot = tangent.dot(relativePosition);
        if (Math.abs(tangentDot) > radiusSum ||
            normalDot < 0)
        {
            // Will not collide
            return time;
        }

        // Will eventually collide

        // Distance to intersection on the circle
        float normalDist = normalDot - (float)Math.sqrt((radiusSum * radiusSum) - (tangentDot * tangentDot));

        // NOT GOOD
        if (normalDist < 0)
        {
            // Overlapping, need to cause a collision NOW
            return 0;
        }

        float collisionTime = normalDist / relativeVelocityMagnitude;

//        System.out.println(collisionTime);

//        System.out.format("Dist: %.2f, V: %.2f, Time: %.5f\n", normalDist,relativeVelocityMagnitude, collisionTime);

        if (collisionTime < time)
        {
            System.out.println("CLOSE -------------------------------");


            return collisionTime;
        }

//        System.out.format("Large time: %.5f\n", collisionTime);

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
