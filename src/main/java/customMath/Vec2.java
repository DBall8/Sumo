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

    public void addX(float x)
    {
        this.x += x;
    }

    public void addY(float y)
    {
        this.y += y;
    }

    public void div(float div)
    {
        this.x /= div;
        this.y /= div;
    }
}
