package physicsEngine;

import customMath.Vec2;

public class Collision {

    private final static float POS_CORECTION_PERCENT = 0.4f;

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

        // Relative velocity (object2's velocity relative to object1)
        Vec2 relativeVelocity = object2.velocity.sub(object1.velocity);

        // Relative velocity along the collision vector
        float normalVelocity = relativeVelocity.dot(collisionVector);

        // Return if already moving apart
        if (normalVelocity >= 0) return;

        float restitution = (object1.material.getRestitution() + object2.material.getRestitution()) / 2.0f;
        float responseVelocity = (1.0f + restitution) * normalVelocity;

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
