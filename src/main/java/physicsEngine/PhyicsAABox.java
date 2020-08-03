package physicsEngine;

import customMath.CustomMath;
import customMath.Line2;
import customMath.Vec2;
import javafx.scene.paint.Color;
import physicsEngine.material.Material;
import sumo.SumoGame;

import java.util.List;

public class PhyicsAABox extends PhysicsObject {

    float width;
    float height;

    Line2[] lines = new Line2[4];

    public PhyicsAABox(float x, float y, float width, float height, Material material, float gravity, float drag) {
        super(material, gravity, drag);

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
        float dx = position.getX() - box.getX();
        float dy = position.getY() - box.getY();
        float distX = Math.abs(dx) - ((width + box.width) / 2.0f);
        float distY =Math.abs(dy) - ((height + box.height) / 2.0f);

        if (distX <= 0 && distY <= 0)
        {
            SumoGame.RESET_DBUG();
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
        }

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
