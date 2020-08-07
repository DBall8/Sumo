package sumo;

import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.KeyBinding;
import gameEngine.userInput.MouseBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import physicsEngine.PhysicsObject;
import physicsEngine.material.MetalMaterial;

public class Player extends Entity {

    private static final int WIDTH = 15;
    private static final int HEIGHT = 200;
    private static final float AXIS_FORCE = 1;
    private static final int FIRE_SPEED = 10000;

    private static float axisForce;

    int playerId;

    PhysicsObject object;

    KeyBinding up;
    KeyBinding down;
    KeyBinding left;
    KeyBinding right;
    KeyBinding boost;
    KeyBinding fire;
    MouseBinding mouse;

    int cooldown = 0;

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
            object = owner.getPhysicsWorld().addAABox(x, y, size*2, size*2, MetalMaterial.getInstance());
            Rectangle r = new Rectangle(-size, -size, size*2, size*2);
            r.setFill(Color.BLUE);
            addVisual(r);
        }
    }

    private void setupKeys(UserInputHandler inputHandler)
    {
        mouse = inputHandler.createMouseListener(MouseButton.NONE);
        if (playerId == 1) {
            up = inputHandler.createKeyBinding(KeyCode.W);
            down = inputHandler.createKeyBinding(KeyCode.S);
            left = inputHandler.createKeyBinding(KeyCode.A);
            right = inputHandler.createKeyBinding(KeyCode.D);
            fire = inputHandler.createKeyBinding(KeyCode.F);
        }
        else
        {
            up = inputHandler.createKeyBinding(KeyCode.UP);
            down = inputHandler.createKeyBinding(KeyCode.DOWN);
            left = inputHandler.createKeyBinding(KeyCode.LEFT);
            right = inputHandler.createKeyBinding(KeyCode.RIGHT);
            fire = inputHandler.createKeyBinding(KeyCode.SHIFT);
        }

        boost = inputHandler.createKeyBinding(KeyCode.SPACE);
    }

    @Override
    public void update()
    {
        Vec2 force = new Vec2(0, 0);
        float boostAmount = (boost.isPressed()) ? 1000 : 1;

        if (up.isPressed())
        {
            force.addY(-axisForce * boostAmount);
        }

        if (down.isPressed())
        {
            force.addY(axisForce * boostAmount);
        }

        if (left.isPressed())
        {
            force.addX(-axisForce * boostAmount);
        }

        if (right.isPressed())
        {
            force.addX(axisForce * boostAmount);
        }

        if (object.getX() < 0)
        {
            object.setX(owner.getWindowDim().getX());
        }
        else if (object.getX() > owner.getWindowDim().getX())
        {
            object.setX(0);
        }

        if (fire.isPressed() && cooldown <= 0)
        {
            Vec2 direction = new Vec2(mouse.getMouseX() - x, mouse.getMouseY() - y);
            direction.normalize();
            Character c = new Character(x + WIDTH*2, y, 5, false, owner);
            c.getPhysicsObject().applyForce(new Vec2(direction.getX() * FIRE_SPEED, direction.getY() * FIRE_SPEED));
            owner.addCharacter(c);

            cooldown = 30;
        }

        if (cooldown > 0) cooldown--;

//        if (object.getY() < 0)
//        {
//            object.setY(owner.getWindowDim().getY());
//        }
//        else if (object.getY() > owner.getWindowDim().getY())
//        {
//            object.setY(0);
//        }


        this.x = object.getX();
        this.y = object.getY();
        this.orientation = object.getAngleRads() * 180 / (float)Math.PI;

        //System.out.println(y);

        object.applyForce(force);
    }
}
