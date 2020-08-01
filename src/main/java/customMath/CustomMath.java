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
}
