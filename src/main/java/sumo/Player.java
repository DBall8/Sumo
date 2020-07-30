package sumo;

import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.KeyBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class Player extends Entity {

    private static final int WIDTH = 100;
    private static final int HEIGHT = 200;
    private static final float AXIS_ACCEL = 1;
    private static final float MAX_AXIS_VEL = 15;

    private static float axisAcceleration;
    private static float axisSlow;

    int playerId;
    Vec2 position;
    Vec2 velocity;
    float angleRads;

    KeyBinding up;
    KeyBinding down;
    KeyBinding left;
    KeyBinding right;

    SumoGame owner;

    public Player(int id, int x, int y, UserInputHandler inputHandler, SumoGame owner)
    {
        this.owner = owner;

        playerId = id;
        position = new Vec2(x, y);
        velocity = new Vec2(0, 0);
        angleRads = 0;

        axisAcceleration = AXIS_ACCEL * owner.getFrameRateFactor();

        setupKeys(inputHandler);

        Vec2 windowDim = owner.getWindowDim();
        double[] points = new double[]{
                Math.random() * windowDim.getX(), Math.random() * windowDim.getY(),
                Math.random() * windowDim.getX(), Math.random() * windowDim.getY(),
                Math.random() * windowDim.getX(), Math.random() * windowDim.getY(),
                Math.random() * windowDim.getX(), Math.random() * windowDim.getY()
        };
        Polygon visuals = new Polygon(points);

        addVisual(visuals);

        customMath.Polygon polygon = new customMath.Polygon(
                points.length/2,
                points
        );

        Vec2 midPoint = polygon.getCenter();
        Circle c = new Circle(midPoint.getX(), midPoint.getY(), 5);
        c.setFill(Color.RED);

        addVisual(c);
    }

    private void setupKeys(UserInputHandler inputHandler)
    {
        up = inputHandler.createKeyBinding(KeyCode.W);
        down = inputHandler.createKeyBinding(KeyCode.S);
        left = inputHandler.createKeyBinding(KeyCode.A);
        right = inputHandler.createKeyBinding(KeyCode.D);
    }

    @Override
    public void update()
    {
        if (up.isPressed() &&
            (velocity.getY() > -MAX_AXIS_VEL))
        {
            velocity.addY(-axisAcceleration);
        }

        if (down.isPressed() &&
                (velocity.getY() < MAX_AXIS_VEL))
        {
            velocity.addY(axisAcceleration);
        }

        if (left.isPressed() &&
                (velocity.getX() > -MAX_AXIS_VEL))
        {
            velocity.addX(-axisAcceleration);
        }

        if (right.isPressed() &&
                (velocity.getX() < MAX_AXIS_VEL))
        {
            velocity.addX(axisAcceleration);
        }

        position.addX(velocity.getX());
        position.addY(velocity.getY());

        this.x = position.getX();
        this.y = position.getY();
    }
}
