package customMath;

public class CustomMath {

    public static float getDistSquared(Vec2 v1, Vec2 v2)
    {
        float dx = v1.getX() - v2.getX();
        float dy = v1.getY() - v2.getY();

        return (dx*dx) + (dy*dy);
    }

    public static float getDistSquared(float x1, float y1, float x2, float y2)
    {
        float dx = x1 - x2;
        float dy = y1 - y2;

        return (dx*dx) + (dy*dy);
    }

    public static float getDist(Vec2 v1, Vec2 v2)
    {
        return (float)Math.sqrt(getDistSquared(v1, v2));
    }

    public static float getDist(float x1, float y1, float x2, float y2)
    {
        return (float)Math.sqrt(getDistSquared(x1, y1, x2, y2));
    }

    public static float[] quadratic(float a, float b, float c)
    {
        float[] result = new float[2];

        float root = (float)Math.sqrt((b*b) - (4.0f * a * c));

        result[0] = ((-1.0f * b) +  root) / (2.0f * a);
        result[1] = ((-1.0f * b) -  root) / (2.0f * a);

        return result;
    }

    public static class RayCastResult
    {
        public boolean intersects;
        public float intersectionDistance;

        public RayCastResult(boolean intersects, float intersectionDistance)
        {
            this.intersects = intersects;
            this.intersectionDistance = intersectionDistance;
        }
    }

    /**
     * Cast a ray from a point in a direction. Find the distance between the ray point and the first intersection on the
     * circle's perimeter
     * @param rayPoint      Source of ray
     * @param rayDirection  Direction ray travels in, must be normalized
     * @param circlePoint   Center of circle
     * @param circleRadius  Radius of circle
     * @return  -1 if ray does not intersect circle, otherwise, the distance point from the ray to the circle intersection
     */
    public static RayCastResult rayCastToCircle(Vec2 rayPoint, Vec2 rayDirection, Vec2 circlePoint, float circleRadius)
    {

        // Circle's position when the origin is set to the point's position
        Vec2 relativePosition = circlePoint.sub(rayPoint);

        // Distance from point to perpendicular intersection of circle
        float normalDot = relativePosition.dot(rayDirection);

        // Perpendicular distance from the circle's point to the ray
        Vec2 tangent = rayDirection.getTangent();
        float tangentDot = tangent.dot(relativePosition);

        if ((Math.abs(tangentDot) > circleRadius) || (normalDot < 0))
        {
            // Ray does not intersect
            // If the tangent dot product is larger than the circle's radius, they ray missed
            // If the normal dot product is negative, the ray is pointing away from the circle
            return new RayCastResult(false, 0);
        }

        // Ray intersects

        // Distance to intersection on the circle
        // There is a right triangle formed
        // The hypotenuse is the circle center to the point the ray first hits the circle
        // The known leg is the perpendicular distance from the circle center to the ray
        // The unknown leg is the perpendicular intersection to the first ray intersection point with the circle
        // Subtract this unknown leg from the relative position dot the ray normal to find the distance of the ray point
        // from the first intersection with the circle
        float distance = normalDot - (float)Math.sqrt((circleRadius * circleRadius) - (tangentDot * tangentDot));

        // NOTE if normalDist is negative, then the ray originates from inside the circle
        return new RayCastResult(true, distance);
    }
}
