package customMath;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import sumo.SumoGame;

public class Polygon {
    int numPoints;
    Vec2[] points;

    public Polygon(int numPoints, Vec2[] points)
    {
        this.numPoints = numPoints;

        this.points = new Vec2[numPoints];

        for (int i=0; i < numPoints; i++)
        {
            this.points[i] = points[i];
        }
    }

    public Polygon(int numPoints, double[] doublePoints)
    {
        this.numPoints = numPoints;

        this.points = new Vec2[numPoints];

        for (int i=0; i < numPoints; i++)
        {
            this.points[i] = new Vec2((float)doublePoints[i*2], (float)doublePoints[i*2 + 1]);
        }
    }

    public float[] getPointsAsFloats()
    {
        float[] floatPoints = new float[numPoints * 2];
        for (int i=0; i < numPoints; i++)
        {
            floatPoints[i*2] = points[i].getX();
            floatPoints[i*2 + 1] = points[i].getY();
        }

        return floatPoints;
    }

    public Vec2 getCenter()
    {
        // The number of distinct triangles that the polygon can be split into
        int numTriangles = numPoints - 2;

        // Calculate the average midpoint of each composite triangle
        Vec2 averageMidpoint = new Vec2(0, 0);
        for (int i=0; i<numTriangles; i++)
        {
            Vec2 triangleMidpoint = getTriangleMidpoint(points[0], points[i+1], points[i+2]);
            averageMidpoint.addX(triangleMidpoint.getX());
            averageMidpoint.addY(triangleMidpoint.getY());

            Color color;
            if (i==0) color = Color.GREEN;
            else color = Color.ORANGE;
            SumoGame.ADD_DEBUG_DOT(points[0].getX(), points[0].getY(), 5, color);
            SumoGame.ADD_DEBUG_DOT(points[i+1].getX(), points[i+1].getY(), 5, color);
            SumoGame.ADD_DEBUG_DOT(points[i+2].getX(), points[i+2].getY(), 5, color);
            SumoGame.ADD_DEBUG_DOT(triangleMidpoint.getX(), triangleMidpoint.getY(), 10, color);
        }

        averageMidpoint.div(numTriangles);

        return averageMidpoint;
    }

    private Vec2 getTriangleMidpoint(Vec2 p1, Vec2 p2, Vec2 p3)
    {
        // Simple case for triangle
        float midX = (p1.getX() + p2.getX()) / 2.0f;
        float midY = (p1.getY() + p2.getY()) / 2.0f;

        float dx = (p3.getX() - midX) / 3;
        float dy = (p3.getY() - midY) / 3;

        return new Vec2(midX + dx, midY + dy);
    }

    private float getTriangleArea(Vec2 p1, Vec2 p2, Vec2 p3)
    {
        float base = CustomMath.getDist(p1, p2);
    }
}
