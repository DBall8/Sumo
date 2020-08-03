package sumo;

import customMath.Vec2;
import gameEngine.Entity;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import physicsEngine.PhysicsObject;
import physicsEngine.material.MetalMaterial;
import physicsEngine.material.StaticMaterial;
import physicsEngine.material.WoodMaterial;

public class Character extends Entity {

    private SumoGame owner;
    private float size;

    private PhysicsObject object;
    public Character(float x, float y, float size, SumoGame owner)
    {
        this.owner = owner;
        this.size = size;

        object = owner.getPhysicsWorld().addCircle(x, y, size, WoodMaterial.getInstance());
        Circle c = new Circle(0, 0, size);
        c.setFill(Color.GREEN);
        addVisual(c);

        Line l = new Line(0, 0, 0, -size);
        l.setStroke(Color.BLACK);
        l.setStrokeWidth(2);
        addVisual(l);
    }

    public Character(float x, float y, float width, float height, boolean isStatic, SumoGame owner)
    {
        this.owner = owner;
        this.size = width;

        object = owner.getPhysicsWorld().addAABox(x, y, width, height, (isStatic ? StaticMaterial.getInstance() : WoodMaterial.getInstance()));
        Rectangle r = new Rectangle(-width/2, -height/2, width, height);
        r.setFill(Color.GREEN);
        addVisual(r);

        Line l = new Line(0, 0, 0, -height/2);
        l.setStroke(Color.BLACK);
        l.setStrokeWidth(2);
        addVisual(l);
    }

    public float getRadius() { return size; }

    public void applyForce(Vec2 force)
    {
        object.applyForce(force);
    }

    @Override
    public void update() {

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
    }
}
