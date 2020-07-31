import customMath.Triangle;
import customMath.Vec2;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class PolygonTests {

    @Test
    public void TriangleTests()
    {
        assertEquals(
                50.0f,
                new Triangle(
                    new Vec2(0, 0),
                    new Vec2(10, 0),
                    new Vec2(10, 10)
                ).getArea(),
                0.001);

        assertEquals(
                50.0f,
                new Triangle(
                        new Vec2(0, 0),
                        new Vec2(10, 0),
                        new Vec2(20, 10)
                ).getArea(),
                0.001);

        assertEquals(
                50.0f,
                new Triangle(
                        new Vec2(20, 10),
                        new Vec2(0, 0),
                        new Vec2(10, 0)
                ).getArea(),
                0.001);

        assertEquals(
                50.0f,
                new Triangle(
                        new Vec2(10, 0),
                        new Vec2(20, 10),
                        new Vec2(0, 0)
                ).getArea(),
                0.001);

        assertEquals(
                50.0f,
                new Triangle(
                        new Vec2(10, 0),
                        new Vec2(0, 0),
                        new Vec2(20, 10)
                ).getArea(),
                0.001);
    }
}
