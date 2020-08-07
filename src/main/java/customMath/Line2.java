package customMath;

public class Line2 {
    Vec2 p1;
    Vec2 p2;

    public Line2(Vec2 p1, Vec2 p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Line2(float x1, float y1, float x2, float y2)
    {
        this.p1 = new Vec2(x1, y1);
        this.p2 = new Vec2(x2, y2);
    }

    public Vec2 getP1(){ return p1; }

    public Vec2 getP2() { return p2; }

    public Line2 add(Vec2 addVec)
    {
        return new Line2(p1.add(addVec), p2.add(addVec));
    }
}
