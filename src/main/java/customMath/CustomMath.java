package customMath;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import sumo.SumoGame;

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

    public static float clamp(float value, float min, float max)
    {
        if (value < min) return min;
        else if (value > max) return max;
        return value;
    }

    public static float min(float value1, float value2)
    {
        if (value1 < value2) return value1;
        return value2;
    }

    public static float max(float value1, float value2)
    {
        if (value1 < value2) return value2;
        return value1;
    }

    /**
     * Cast a ray from a point in a direction. Find the distance between the ray point and the first intersection on the
     * circle's perimeter
     * @param rayPoint      Source of ray
     * @param rayDirection  Direction ray travels in
     * @param circlePoint   Center of circle
     * @param circleRadius  Radius of circle
     * @return  -1 if ray does not intersect circle, otherwise, the distance point from the ray to the circle intersection
     */
    public static RayCastResult rayCastToCircle(Vec2 rayPoint, Vec2 rayDirection, Vec2 circlePoint, float circleRadius)
    {

        // Circle's position when the origin is set to the point's position
        Vec2 relativePosition = circlePoint.sub(rayPoint);
        rayDirection.normalize();

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

    public static RayCastResult rayCastToSegment(Vec2 rayPoint, Vec2 rayDirection, Vec2 linePoint1, Vec2 linePoint2)
    {
        // Circle 2's position when the origin is set to circle 1's position
        linePoint1 = linePoint1.sub(rayPoint);
        linePoint2 = linePoint2.sub(rayPoint);

        Vec2 rayNormal = rayDirection.normalize();
        Vec2 rayTangent = rayDirection.getTangent();

        // If both points are behind, there will be no intersection
        if (rayNormal.dot(linePoint1) < 0 &&
                rayNormal.dot(linePoint2) < 0) return new RayCastResult(false, 0);

        float linePoint1Tangent = linePoint1.dot(rayTangent);
        float linePoint2Tangent = linePoint2.dot(rayTangent);

        if ((linePoint1Tangent <= 0 && linePoint2Tangent >= 0) ||
                (linePoint1Tangent >= 0 && linePoint2Tangent <= 0))
        {
            // Ray intersects segment
            float tangentSum = Math.abs(linePoint1Tangent) + Math.abs(linePoint2Tangent);

            if (tangentSum == 0)
            {
                // Line is parallel and overlaps ray
                float intersectionDistanceSqr = CustomMath.min(linePoint1.getMagnitudeSquared(), linePoint2.getMagnitudeSquared());
                return new RayCastResult(true, (float)Math.sqrt(intersectionDistanceSqr));
            }

            float tangentRatio = Math.abs(linePoint1Tangent) / tangentSum;
            Vec2 lineVector = linePoint2.sub(linePoint1);
            float adjustmentMag = tangentRatio * lineVector.getMagnitude();

            lineVector.normalize();

            Vec2 intersection =  new Vec2(linePoint1.getX() + (adjustmentMag * lineVector.getX()),
                    linePoint1.getY() + (adjustmentMag * lineVector.getY()));

            // Return null if intersection point is behind
            if (intersection.dot(rayNormal) < 0) return new RayCastResult(false, 0);;

            //Vec2 intersectionPoint = intersection.add(rayPoint);
            return new RayCastResult(true, intersection.getMagnitude());
        }

        return new RayCastResult(false, 0);
    }

    public static RayCastResult rayCastToSegmentWithRadius(Vec2 rayPoint, Vec2 rayDirection, Line2 segment, float radius)
    {
        // Normalize segment so that the ray point is at 0,0
        Vec2 linePoint1 = segment.getP1().sub(rayPoint);
        Vec2 linePoint2 = segment.getP2().sub(rayPoint);

        Vec2 towardsPoint = linePoint1.copy();
        towardsPoint.mult(-1.0f);
        Vec2 lineTangentTowardsPoint = linePoint2.sub(linePoint1).getTangentTowards(towardsPoint);
        lineTangentTowardsPoint.normalize();
        lineTangentTowardsPoint.mult(radius);

        linePoint1 = linePoint1.add(lineTangentTowardsPoint);
        linePoint2 = linePoint2.add(lineTangentTowardsPoint);

//        SumoGame.ADD_DEBUG_DOT(linePoint1.getX() + rayPoint.getX(), linePoint1.getY() + rayPoint.getY(), 5, Color.RED);
//        SumoGame.ADD_DEBUG_DOT(linePoint2.getX() + rayPoint.getX(), linePoint2.getY() + rayPoint.getY(), 5, Color.RED);

        RayCastResult lineResult = rayCastToSegment(rayPoint, rayDirection, linePoint1.add(rayPoint), linePoint2.add(rayPoint));
        if (lineResult.intersects)
        {
            return lineResult;
        }

        RayCastResult point1Result = rayCastToCircle(rayPoint, rayDirection, segment.getP1(), radius);
        RayCastResult point2Result = rayCastToCircle(rayPoint, rayDirection, segment.getP2(), radius);
        if (point1Result.intersects && point2Result.intersects)
        {
            if (point1Result.intersectionDistance < point2Result.intersectionDistance)
            {
                return  point1Result;
            }
            else
            {
                return point2Result;
            }
        }
        else if (point1Result.intersects)
        {
            return point1Result;
        }
        else
        {
            return point2Result;
        }
    }
}
