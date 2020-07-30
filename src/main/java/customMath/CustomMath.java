package customMath;

public class CustomMath {

    public static float getDistSquared(Vec2 v1, Vec2 v2)
    {
        float dx = v1.getX() - v2.getX();
        float dy = v1.getY() - v2.getY();

        return (dx*dx) + (dy*dy);
    }

    public static float getDist(Vec2 v1, Vec2 v2)
    {
        return (float)Math.sqrt(getDist(v1, v2));
    }
}
