package physicsEngine;

import customMath.Vec2;

public class Collision {

    private final static float POS_CORECTION_PERCENT = 0.5f;

    private PhysicsObject object1;
    private PhysicsObject object2;

    private Vec2 collisionVector; // Vector from object1 to object2
    private float penetration;

    private static final int MAX_CONTACT_POINTS = 2;
    private int numContactPoints;
    private Vec2[] contactPoints = new Vec2[MAX_CONTACT_POINTS];

    public Collision(PhysicsObject o1, PhysicsObject o2, Vec2 vector, float penetration)
    {
        object1 = o1;
        object2 = o2;
        this.collisionVector = vector;
        this.penetration = penetration;
    }

    public void addContactPoint(Vec2 point)
    {
        contactPoints[numContactPoints] = point;
        numContactPoints++;
    }

    public void resolve()
    {
        correctPosition();

        // In the linear direction of the collision:
        // Conservation of momentum:
        // m1*u1 + m2*u2 = m1*v1 + m2*v2

        // Conservation of kinetic energy
        // 0.5*m1*u1*u1 + 0.5*m2*u2*u2 = 0.5*m1*v1*v1 + 0.5*m2*v2*v2

        // Solve system of equations for velocities along normal
        // Velocities along tangent remain the same as before

        // Velocity tangential to the collision, will remain unchanged
        Vec2 tangent = collisionVector.getTangent();
        float t1 = object1.velocity.dot(tangent);
        float t2 = object2.velocity.dot(tangent);

        float u1 = object1.velocity.dot(collisionVector);
        float u2 = object2.velocity.dot(collisionVector);
        float massSum = object1.mass + object2.mass;

        float v1 = ((2.0f * object2.mass * u2) + ((object1.mass - object2.mass) * u1))
                / massSum;
        float v2 = ((2.0f * object1.mass * u1) + ((object2.mass - object1.mass) * u2))
                / massSum;

        Vec2 v1Vector = collisionVector.copy();
        v1Vector.mult(v1);
        Vec2 v2Vector = collisionVector.copy();
        v2Vector.mult(v2);

        object1.velocity = new Vec2(
                (collisionVector.getX() * v1) + (tangent.getX() * t1),
                (collisionVector.getY() * v1) + (tangent.getY() * t1)
        );

        object2.velocity = new Vec2(
                (collisionVector.getX() * v2) + (tangent.getX() * t2),
                (collisionVector.getY() * v2) + (tangent.getY() * t2)
        );
    }

    private void correctPosition()
    {
        Vec2 correctionVector = collisionVector.copy();
        correctionVector.mult(penetration * POS_CORECTION_PERCENT);

        object1.position.addX(correctionVector.getX());
        object1.position.addY(correctionVector.getY());
        object2.position.addX(-1.0f * correctionVector.getX());
        object2.position.addY(-1.0f * correctionVector.getY());
    }
}
