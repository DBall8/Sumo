package physicsEngine;

import customMath.CustomMath;
import customMath.Line2;
import customMath.Vec2;
import javafx.scene.paint.Color;
import sumo.SumoGame;

import java.util.List;

public class PhyicsAABox extends PhysicsObject {

    float width;
    float height;

    Line2[] lines = new Line2[4];

    public PhyicsAABox(float x, float y, float width, float height, float gravity, float drag) {
        super(gravity, drag);

        position = new Vec2(x, y);
        this.width = width;
        this.height = height;

        Vec2 p1 = new Vec2(-width/2, -height/2);
        Vec2 p2 = new Vec2(width/2, -height/2);
        Vec2 p3 = new Vec2(width/2,  height/2);
        Vec2 p4 = new Vec2(-width/2, height/2);

        lines[0] = new Line2(p1, p2);
        lines[1] = new Line2(p2, p3);
        lines[2] = new Line2(p3, p4);
        lines[3] = new Line2(p4, p1);

        velocity = new Vec2(0, 0);
        angularVelocity = 0;

        mass = width * height * MASS_FACTOR;
        invertedMass = 1.0f / mass;
    }

    @Override
    public void checkCollision(PhysicsCircle circle, List<Collision> newCollisions)
    {
        // Find the closest point to the circle across all sides
        float radiusSqr = circle.getRadius() * circle.getRadius();
        float closestDistanceSqr = Float.MAX_VALUE;
        Vec2 closestPoint = null;
        for (Line2 line: lines)
        {
            Vec2 p = getClosestPointOnLine(line.getP1().add(position), line.getP2().add(position), circle.position);
            float distanceToLineSqr = CustomMath.getDistSquared(p, circle.position);
            if (distanceToLineSqr < closestDistanceSqr)
            {
                closestDistanceSqr = distanceToLineSqr;
                closestPoint = p;
            }
        }

        // Collision if the closest point is closer than the radius
        if (closestDistanceSqr < radiusSqr)
        {
            // Collision
            float penetration = circle.getRadius() - (float)Math.sqrt(closestDistanceSqr);
            Vec2 collisionNormal = circle.position.sub(closestPoint);
            collisionNormal.normalize();

            Collision collision = new Collision(circle, this, collisionNormal, penetration);
            collision.addContactPoint(closestPoint);

            newCollisions.add(collision);
        }
    }

    @Override
    public float getEarliestCollision(PhysicsCircle circle, float time) {
        return time;
    }

    @Override
    public float getCrossSection(Vec2 direction) {
        return 0;
    }

    public void checkCollision(PhyicsAABox box, List<Collision> newCollisions)
    {
        return;
    }
    public float getEarliestCollision(PhyicsAABox box, float time)
    {
        return time;
    }

    private Vec2 getClosestPointOnLine(Vec2 l1, Vec2 l2, Vec2 point)
    {
        Vec2 lineVector = l2.sub(l1);
        Vec2 pointVector = point.sub(l1);

        float lineLength = lineVector.getMagnitude();
        float normalizeDotProduct = lineVector.dot(pointVector) / (lineLength * lineLength);

        if (normalizeDotProduct < 0)
        {
            return l1;
        }
        else if (normalizeDotProduct > 1)
        {
            return l2;
        }
        else
        {
            return new Vec2(
                    l1.getX() + (lineVector.getX() * normalizeDotProduct),
                    l1.getY() + (lineVector.getY() * normalizeDotProduct)
            );
        }
    }
}
