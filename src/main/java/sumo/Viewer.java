package sumo;

import customMath.CustomMath;
import customMath.Line2;
import customMath.Polygon;
import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.KeyBinding;
import gameEngine.userInput.MouseBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import physicsEngine.PhyicsAABox;
import physicsEngine.PhysicsCircle;
import physicsEngine.PhysicsObject;
import physicsEngine.PhysicsWorld;
import physicsEngine.material.Material;
import physicsEngine.material.WoodMaterial;

import java.util.List;

public class Viewer extends Entity {

    final static float MOVE_SPEED = 10f;

    enum ViewerShape
    {
        Circle,
        Square,
        Polygon
    };

    MouseBinding mouseBinding;
    KeyBinding up;
    KeyBinding down;
    KeyBinding left;
    KeyBinding right;

    ViewerShape shape;
    float size;
    Shape visuals;
    Line pointer;

    Polygon polygon;

    Circle castCircle;
    Rectangle castSquare;

    SumoGame owner;

    public Viewer(UserInputHandler inputHandler, ViewerShape shape, float size, SumoGame owner)
    {
        this.owner = owner;

        mouseBinding = inputHandler.createMouseListener(MouseButton.PRIMARY);
        up = inputHandler.createKeyBinding(KeyCode.W);
        down = inputHandler.createKeyBinding(KeyCode.S);
        left = inputHandler.createKeyBinding(KeyCode.A);
        right = inputHandler.createKeyBinding(KeyCode.D);

        this.size = size;
        this.shape = shape;

        switch (shape)
        {
            default: {
                visuals = new Circle(0, 0, size, Color.PURPLE);
                addVisual(visuals);

                castCircle = new Circle(0, 0, size, Color.LAVENDER);
                addVisual(castCircle);
                break;
            }

            case Square:
            {
                visuals = new Rectangle(-size/2, -size/2, size, size);
                visuals.setFill(Color.PURPLE);
                addVisual(visuals);

                castSquare = new Rectangle(-size/2, -size/2, size, size);
                castSquare.setFill(Color.LAVENDER);
                addVisual(castSquare);

                polygon = new Polygon(4,
                    new Vec2[]
                            {
                    new Vec2(-size/2, -size/2),
                    new Vec2(size/2, -size/2),
                    new Vec2(size/2, size/2),
                    new Vec2(-size/2, size/2)
                            });
                break;
            }
        }

        pointer = new Line(0,0,0,0);
        pointer.setStroke(Color.DARKCYAN);
        pointer.setStrokeWidth(2);
        addVisual(pointer);
    }


    @Override
    public void update() {
        if (up.isPressed())
        {
            y -= MOVE_SPEED;
        }

        if (down.isPressed())
        {
            y += MOVE_SPEED;
        }

        if (left.isPressed())
        {
            x -= MOVE_SPEED;
        }

        if (right.isPressed())
        {
            x += MOVE_SPEED;
        }

        switch (shape)
        {
            default:
            {
                castAsCircle();
                break;
            }

            case Square:
            {
                castAsSquare();
                break;
            }
        }


        pointer.setEndX(mouseBinding.getMouseX() - x);
        pointer.setEndY(mouseBinding.getMouseY() - y);
    }

    private void castAsCircle()
    {
        Vec2 rayDirection = new Vec2(mouseBinding.getMouseX() - x, mouseBinding.getMouseY() - y);
        rayDirection.normalize();

        float shortestDistance = Float.MAX_VALUE;
        for (Character c: owner.getCharacters())
        {
            if (c.getPhysicsObject() instanceof PhysicsCircle)
            {
                PhysicsCircle circle = (PhysicsCircle)c.getPhysicsObject();
                float radiusSum = size + circle.getRadius();

                // Cast a ray from Circle 1 in the direction of Circle 1's relative velocity
                // To treat Circle 1 as a point, let Circle 2's radius be the sum of both radii
                CustomMath.RayCastResult result = CustomMath.rayCastToCircle(new Vec2(x,y), rayDirection, new Vec2(circle.getX(), circle.getY()), radiusSum);

                if (!result.intersects)
                {
                    // Will not collide at current trajectories
                    continue;
                }

                if (result.intersectionDistance < shortestDistance)
                {
                    shortestDistance = result.intersectionDistance;
                }
            }
            else if (c.getPhysicsObject() instanceof PhyicsAABox)
            {
                PhyicsAABox box = (PhyicsAABox)c.getPhysicsObject();
                Polygon polygon = box.getShape();

                for (int i=0; i<4; i++)
                {
                    Line2 line = polygon.getLine(i);
                    Vec2 boxPos = new Vec2(box.getX(), box.getY());
                    CustomMath.RayCastResult result = CustomMath.rayCastToSegmentWithRadius(
                            new Vec2(x, y),
                            rayDirection,
                            line.add(boxPos),
                            size);

                    if (!result.intersects)
                    {
                        // Will not collide at current trajectories
                        continue;
                    }

                    if (result.intersectionDistance < shortestDistance)
                    {
                        shortestDistance = result.intersectionDistance;
                    }
                }
            }
        }

        if (shortestDistance < Float.MAX_VALUE)
        {
            castCircle.setCenterX(shortestDistance * rayDirection.getX());
            castCircle.setCenterY(shortestDistance * rayDirection.getY());
        }
        else
        {
            castCircle.setCenterX(-size - x);
            castCircle.setCenterY(-size - y);
        }
    }

    private void castAsSquare()
    {
        Vec2 rayDirection = new Vec2(mouseBinding.getMouseX() - x, mouseBinding.getMouseY() - y);
        rayDirection.normalize();
        rayDirection.mult(-1);

        float shortestDistance = Float.MAX_VALUE;
        for (Character c: owner.getCharacters())
        {
            if (c.getPhysicsObject() instanceof PhysicsCircle)
            {
                PhysicsCircle circle = (PhysicsCircle)c.getPhysicsObject();

                for (int i=0; i<4; i++)
                {
                    Line2 line = polygon.getLine(i);
                    CustomMath.RayCastResult result = CustomMath.rayCastToSegmentWithRadius(
                            new Vec2(circle.getX(), circle.getY()),
                            rayDirection,
                            line.add(new Vec2(x, y)),
                            circle.getRadius());

                    if (!result.intersects)
                    {
                        // Will not collide at current trajectories
                        continue;
                    }

                    if (result.intersectionDistance < shortestDistance)
                    {
                        shortestDistance = result.intersectionDistance;
                    }
                }
            }
            else if (c.getPhysicsObject() instanceof PhyicsAABox)
            {
                continue;
            }
        }

        if (shortestDistance < Float.MAX_VALUE)
        {
            castSquare.setX(shortestDistance * -rayDirection.getX() - size/2);
            castSquare.setY(shortestDistance * -rayDirection.getY() - size/2);
        }
        else
        {
            castSquare.setX(-size - x);
            castSquare.setY(-size - y);
        }
    }
}
