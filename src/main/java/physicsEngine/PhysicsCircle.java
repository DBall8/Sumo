package physicsEngine;

import customMath.CustomMath;
import customMath.Vec2;

import java.util.List;

public class PhysicsCircle extends PhysicsObject{

    private float radius;

    public PhysicsCircle(float x, float y, float radius)
    {
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

        if (distSquared < (radialSum * radialSum))
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
    public float getCrossSection(Vec2 direction)
    {
        return radius * 2;
    }
}
