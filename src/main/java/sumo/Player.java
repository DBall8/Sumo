package sumo;

import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.KeyBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import physicsEngine.PhysicsObject;
import physicsEngine.material.MetalMaterial;

public class Player extends Entity {

    private static final int WIDTH = 30;
    private static final int HEIGHT = 200;
    private static final float AXIS_FORCE = 1;

    private static float axisForce;

    int playerId;

    PhysicsObject object;

    KeyBinding up;
    KeyBinding down;
    KeyBinding left;
    KeyBinding right;

    SumoGame owner;

    public Player(int id, int x, int y, UserInputHandler inputHandler, SumoGame owner)
    {
        this.owner = owner;

        playerId = id;

        axisForce = AXIS_FORCE * owner.getFrameRateFactor();

        setupKeys(inputHandler);

        float size = WIDTH;

        if (id == 1) {
            object = owner.getPhysicsWorld().addCircle(x, y, size, MetalMaterial.getInstance());
            Circle c = new Circle(0, 0, size);
            c.setFill(Color.BLUE);
            addVisual(c);
        }
        else
        {
            object = owner.getPhysicsWorld().addAABox(x, y, size, size, MetalMaterial.getInstance());
            Rectangle r = new Rectangle(-size/2, -size/2, size, size);
            r.setFill(Color.BLUE);
            addVisual(r);
        }
    }

    private void setupKeys(UserInputHandler inputHandler)
    {
        if (playerId == 1) {
            up = inputHandler.createKeyBinding(KeyCode.W);
            down = inputHandler.createKeyBinding(KeyCode.S);
            left = inputHandler.createKeyBinding(KeyCode.A);
            right = inputHandler.createKeyBinding(KeyCode.D);
        }
        else
        {
            up = inputHandler.createKeyBinding(KeyCode.UP);
            down = inputHandler.createKeyBinding(KeyCode.DOWN);
            left = inputHandler.createKeyBinding(KeyCode.LEFT);
            right = inputHandler.createKeyBinding(KeyCode.RIGHT);
        }
    }

    @Override
    public void update()
    {
        Vec2 force = new Vec2(0, 0);

        if (up.isPressed())
        {
            force.addY(-axisForce);
        }

        if (down.isPressed())
        {
            force.addY(axisForce);
        }

        if (left.isPressed())
        {
            force.addX(-axisForce);
        }

        if (right.isPressed())
        {
            force.addX(axisForce);
        }

        if (object.getX() < 0)
        {
            object.setX(owner.getWindowDim().getX());
        }
        else if (object.getX() > owner.getWindowDim().getX())
        {
            object.setX(0);
        }

        if (object.getY() < 0)
        {
            object.setY(owner.getWindowDim().getY());
        }
        else if (object.getY() > owner.getWindowDim().getY())
        {
            object.setY(0);
        }


        this.x = object.getX();
        this.y = object.getY();
        this.orientation = object.getAngleRads() * 180 / (float)Math.PI;

        object.applyForce(force);
    }
}
