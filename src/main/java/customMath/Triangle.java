package customMath;

public class Triangle {

    private Vec2[] points = new Vec2[3];

    public Triangle(Vec2 p1, Vec2 p2, Vec2 p3)
    {
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
    }

    public Vec2 getCenter()
    {
        // Simple case for triangle
        float midX = (points[0].getX() + points[1].getX()) / 2.0f;
        float midY = (points[0].getY() + points[1].getY()) / 2.0f;

        float dx = (points[2].getX() - midX) / 3;
        float dy = (points[2].getY() - midY) / 3;

        return new Vec2(midX + dx, midY + dy);
    }

    public float getArea()
    {
        Vec2 base = points[1].sub(points[0]);
        float baseLen = base.getMagnitude();

        // Get the distance from the base to the third point DOT the tangent of the base
        Vec2 inwardsDir = new Vec2(points[2].getX() - points[0].getX(), points[2].getY() - points[0].getY());
        Vec2 inwardsTangent = base.getTangentTowards(inwardsDir);
        inwardsTangent.normalize();

        float height = inwardsTangent.dot(inwardsDir);

        return 0.5f * baseLen * height;
    }
}
