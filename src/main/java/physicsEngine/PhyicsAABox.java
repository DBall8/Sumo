package physicsEngine;

import customMath.CustomMath;
import customMath.Line2;
import customMath.Polygon;
import customMath.Vec2;
import javafx.scene.paint.Color;
import physicsEngine.material.Material;
import sumo.SumoGame;

import java.util.List;

public class PhyicsAABox extends PhysicsObject {

    final static int NUM_SIDES = 4;

    float width;
    float height;

    Polygon shape;

    public PhyicsAABox(float x, float y, float width, float height, Material material, float gravity, float drag) {
        super(material, gravity, drag);

        position = new Vec2(x, y);
        this.width = width;
        this.height = height;

        Vec2 p1 = new Vec2(-width/2, -height/2);
        Vec2 p2 = new Vec2(width/2, -height/2);
        Vec2 p3 = new Vec2(width/2,  height/2);
        Vec2 p4 = new Vec2(-width/2, height/2);

        shape = new Polygon(
                NUM_SIDES,
                new Vec2[]
                {
                        p1, p2, p3, p4
                }
        );

        mass = width * height * MASS_FACTOR * material.getDensity();
        if (mass <= 0)
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
        // Find the closest point to the circle across all sides
        float radiusSqr = circle.getRadius() * circle.getRadius();
        float closestDistanceSqr = Float.MAX_VALUE;
        Vec2 closestPoint = null;
        for (int i=0; i < NUM_SIDES; i++)
        {
            Line2 line = shape.getLine(i);
            Vec2 p = getClosestPointOnLine(line.getP1().add(position), line.getP2().add(position), circle.position);
            float distanceToLineSqr = CustomMath.getDistSquared(p, circle.position);
            if (distanceToLineSqr < closestDistanceSqr)
            {
                closestDistanceSqr = distanceToLineSqr;
                closestPoint = p;
            }
        }

        // Collision if the closest point is closer than the radius
        if (closestDistanceSqr <= (radiusSqr + (COLLISION_GRACE*COLLISION_GRACE)))
        {
            // Collision
            float penetration = circle.getRadius() - (float)Math.sqrt(closestDistanceSqr);
            Vec2 collisionNormal = circle.position.sub(closestPoint);
            collisionNormal.normalize();

            Collision collision = new Collision(circle, this, collisionNormal, penetration);
            collision.addContactPoint(closestPoint);

            newCollisions.add(collision);

            return true;
        }

        return false;
    }

    @Override
    public float getEarliestCollision(PhysicsCircle circle, float time) {

        if (this.mass <=0 && circle.mass <=0) return time;

        Vec2 relativeVelocity = circle.getVelocity().sub(getVelocity());

        // If not moving relative to each other, no collision will happen
        if (relativeVelocity.getMagnitudeSquared() == 0) return time;

        Vec2 velocityNormal = relativeVelocity.copy().normalize();

        float shortestDistance = Float.MAX_VALUE;
        for (int i=0; i<NUM_SIDES; i++) {

            // Check if there will be an intersection between the circle and each side of the box
            Line2 line = shape.getLine(i);
            CustomMath.RayCastResult result = CustomMath.rayCastToSegmentWithRadius(
                    circle.position,
                    velocityNormal,
                    line.add(position),
                    circle.getRadius());

            if (!result.intersects) {
                // Will not collide at current trajectories
                continue;
            }

            if (result.intersectionDistance < shortestDistance) {
                shortestDistance = result.intersectionDistance;
            }
        }

        if (shortestDistance < Float.MAX_VALUE) {
            float collisionTime = shortestDistance / relativeVelocity.getMagnitude();

            if (collisionTime < timeUntilNextCollision)
            {
                timeUntilNextCollision = collisionTime;
                timeUntilCollisonReady = true;
            }

            return CustomMath.min(collisionTime, time);
        }

        return time;
    }

    @Override
    public float getCrossSection(Vec2 direction) {
        return 0;
    }

    public boolean checkCollision(PhyicsAABox box, List<Collision> newCollisions)
    {
        float dx = position.getX() - box.getX();
        float dy = position.getY() - box.getY();
        float distX = Math.abs(dx) - ((width + box.width) / 2.0f);
        float distY =Math.abs(dy) - ((height + box.height) / 2.0f);

        if (distX <= 0 && distY <= 0)
        {
            // Collision
            if (distX > distY)
            {
                // less penetration on th x axis
                float normalX = (dx > 0) ? 1 : -1;
                Collision collision = new Collision(this, box, new Vec2(normalX, 0), -1.0f * distX);
                newCollisions.add(collision);

                float collisionY1 =  CustomMath.clamp(
                        position.getY() - (height/2.0f),
                        box.position.getY() - (box.height / 2.0f),
                        box.position.getY() + (box.height / 2.0f)
                );
                float collisionY2 =  CustomMath.clamp(
                        position.getY() + (height/2.0f),
                        box.position.getY() - (box.height / 2.0f),
                        box.position.getY() + (box.height / 2.0f)
                );

                float collisionX = (dx > 0) ?
                        position.getX() - (width / 2.0f) :
                        position.getX() + (width / 2.0f);

                collision.addContactPoint(new Vec2(collisionX, collisionY1));
                collision.addContactPoint(new Vec2(collisionX, collisionY2));

//                SumoGame.ADD_DEBUG_DOT(collisionX, collisionY1, 5, Color.RED);
//                SumoGame.ADD_DEBUG_DOT(collisionX, collisionY2, 5, Color.RED);
            }
            else
            {
                // less penetration on the y axis
                float normalY = (dy > 0) ? 1 : -1;
                Collision collision = new Collision(this, box, new Vec2(0, normalY), -1.0f * distY);
                newCollisions.add(collision);

                float collisionX1 =  CustomMath.clamp(
                        position.getX() - (width/2.0f),
                        box.position.getX() - (box.width / 2.0f),
                        box.position.getX() + (box.width / 2.0f)
                );
                float collisionX2 =  CustomMath.clamp(
                        position.getX() + (width/2.0f),
                        box.position.getX() - (box.width / 2.0f),
                        box.position.getX() + (box.width / 2.0f)
                );

                float collisionY = (dy > 0) ?
                        position.getY() - (height / 2.0f) :
                        position.getY() + (height / 2.0f);

//                SumoGame.ADD_DEBUG_DOT(collisionX1, collisionY, 5, Color.RED);
//                SumoGame.ADD_DEBUG_DOT(collisionX2, collisionY, 5, Color.RED);

                collision.addContactPoint(new Vec2(collisionX1, collisionY));
                collision.addContactPoint(new Vec2(collisionX2, collisionY));
            }

            return true;
        }

        return false;
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

    // Delete later
    public Polygon getShape() { return shape; }
}
