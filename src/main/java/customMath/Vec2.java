package customMath;

public class Vec2 {

    private float x;
    private float y;

    public Vec2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getMagnitude()
    {
        return (float)Math.sqrt((x*x) + (y*y));
    }

    public float getMagnitudeSquared()
    {
        return (x*x) + (y*y);
    }

    public void addX(float x)
    {
        this.x += x;
    }

    public void addY(float y)
    {
        this.y += y;
    }

    public Vec2 add(Vec2 v2)
    {
        return new Vec2(x + v2.x, y + v2.y);
    }

    public Vec2 sub(Vec2 v2)
    {
        return new Vec2(x - v2.getX(), y - v2.getY());
    }

    public void mult(float mult)
    {
        this.x *= mult;
        this.y *= mult;
    }

    public void div(float div)
    {
        this.x /= div;
        this.y /= div;
    }

    public float dot(Vec2 v2)
    {
        return (x * v2.x) + (y * v2.y);
    }

    public float cross(Vec2 v2)
    {
        return (x*v2.getY()) - (y * v2.getX());
    }

    public Vec2 getTangentTowards(Vec2 dir)
    {
        Vec2 tangent = new Vec2(y, -x);

        // If the tangent is opposite the direction given, flip it
        if (tangent.dot(dir) < 0)
        {
            tangent.mult(-1);
        }

        return tangent;
    }

    public Vec2 getDir()
    {
        float mag = getMagnitude();
        if (mag == 0) return new Vec2(0, 0);
        return new Vec2(x / mag, y / mag);
    }

    /**
     * Normalizes the vector AND returns reference to self
     * @return reference to self
     */
    public Vec2 normalize()
    {
        div(getMagnitude());
        return this;
    }

    public void zero()
    {
        x = 0;
        y = 0;
    }

    public Vec2 copy()
    {
        return new Vec2(x, y);
    }
}
