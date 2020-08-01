package physicsEngine;

import customMath.CustomMath;
import customMath.Line2;
import customMath.Vec2;

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

        Vec2 p1 = new Vec2(-width/2, -width/2);
        Vec2 p2 = new Vec2(width/2, -width/2);
        Vec2 p3 = new Vec2(width/2,  width/2);
        Vec2 p4 = new Vec2(-width/2, width/2);

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
        float dx = Math.abs(circle.position.getX() - position.getX()) - width/2 - circle.getRadius();
        float dy = Math.abs(circle.position.getY() - position.getY()) - height/2 - circle.getRadius();

        if (dx <= 0 && dy <=0)
        {
            System.out.println("COLLIDE");
            // Collision
            Vec2 collisionFace1;
            Vec2 collisionFace2;
            float penetration;
            if (dx > dy)
            {
                penetration = -1.0f * dx;
                // Closest side is left or right
                if (circle.position.getX() > position.getX())
                {
                    // Closest side is right
                    collisionFace1 = new Vec2(position.getX() + width/2, position.getY() - height/2);
                    collisionFace2 = new Vec2(position.getX() + width/2, position.getY() + height/2);
                }
                else
                {
                    // closest side is left
                    collisionFace1 =  new Vec2(position.getX() - width/2, position.getY() - height/2);
                    collisionFace2 =  new Vec2(position.getX() - width/2, position.getY() + height/2);
                }
            }
            else
            {
                penetration = -1.0f * dy;
                // Closest side is top or bottom
                if (circle.position.getY() > position.getY())
                {
                    // Closest side is top
                    collisionFace1 = new  Vec2(position.getX() + width/2, position.getY() - height/2);
                    collisionFace2 = new  Vec2(position.getX() - width/2, position.getY() - height/2);
                }
                else
                {
                    // closest side is bottom
                    collisionFace1 = new  Vec2(position.getX() + width/2, position.getY() + height/2);
                    collisionFace2 = new  Vec2(position.getX() - width/2, position.getY() + height/2);
                }
            }

            Vec2 outwards = collisionFace1.sub(position);
            Vec2 collisionNormal = collisionFace2.sub(collisionFace1).getTangentTowards(outwards);
            collisionNormal.normalize();

            Collision collision = new Collision(this, circle, collisionNormal, penetration);
            collision.addContactPoint(getClosestPointOnLine(collisionFace1, collisionFace2, circle.position));
        }
    }

    @Override
    public float getEarliestCollision(PhysicsCircle circle, float time) {
        return 0;
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
