package customMath;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
        float area = 0;
        for (int i=0; i<numTriangles; i++)
        {
            Triangle t = new Triangle(points[0], points[i+1], points[i+2]);
            Vec2 triangleMidpoint = t.getCenter();
            float triangleArea = t.getArea();

            averageMidpoint.addX(triangleMidpoint.getX() * triangleArea);
            averageMidpoint.addY(triangleMidpoint.getY() * triangleArea);

            area += triangleArea;

//            Color color;
//            if (i==0) color = Color.GREEN;
//            else color = Color.ORANGE;
//            SumoGame.ADD_DEBUG_DOT(points[0].getX(), points[0].getY(), 5, color);
//            SumoGame.ADD_DEBUG_DOT(points[i+1].getX(), points[i+1].getY(), 5, color);
//            SumoGame.ADD_DEBUG_DOT(points[i+2].getX(), points[i+2].getY(), 5, color);
//            SumoGame.ADD_DEBUG_DOT(triangleMidpoint.getX(), triangleMidpoint.getY(), triangleArea/2000, color);
        }

        averageMidpoint.div(area);

//        SumoGame.ADD_DEBUG_DOT(averageMidpoint.getX(), averageMidpoint.getY(), 20, Color.RED);

        return averageMidpoint;
    }

    public Vec2 getPoint(int index)
    {
        if (index >= numPoints) return null;
        return points[index];
    }

    public Line2 getLine(int index)
    {
        if (index >= numPoints) return null;
        int endIndex = ((index + 1) >= numPoints) ? 0 : (index + 1);
        return new Line2(points[index], points[endIndex]);
    }
}