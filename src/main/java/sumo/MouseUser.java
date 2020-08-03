package sumo;

import customMath.CustomMath;
import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.MouseBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class MouseUser extends Entity {

    boolean active = false;
    MouseBinding leftMouseBinding;
    MouseBinding rightMouseBinding;

    List<Character> characters;
    Character selecteCharacter = null;
    Line l;

    SumoGame owner;

    public MouseUser(UserInputHandler inputHandler, List<Character> characters, SumoGame owner)
    {
        leftMouseBinding = inputHandler.createMouseListener(MouseButton.PRIMARY);
        rightMouseBinding = inputHandler.createMouseListener(MouseButton.SECONDARY);
        this.characters = characters;
        this.owner = owner;

        l = new Line(0, 0, 0, 0);
        l.setStrokeWidth(2);
        l.setStroke(Color.RED);
        addVisual(l);
    }

    @Override
    public void update()
    {
        if (!active && leftMouseBinding.isPressed())
        {
            leftMouseBinding.consumeClick();
            for (Character c: characters)
            {
                float distanceSquared = CustomMath.getDistSquared(
                        c.getWorldX(),
                        c.getWorldY(),
                        leftMouseBinding.getMouseX(),
                        leftMouseBinding.getMouseY());

                if (distanceSquared < c.getRadius() * c.getRadius())
                {
                    selecteCharacter = c;
                    active = true;
                    break;
                }
            }
        }

        if (active && !leftMouseBinding.isPressed())
        {
            Vec2 force = new Vec2(
                    selecteCharacter.getWorldX() - leftMouseBinding.getMouseX(),
                    selecteCharacter.getWorldY() - leftMouseBinding.getMouseY());

            selecteCharacter.applyForce(force);
            selecteCharacter = null;
            active = false;

            l.setStartX(0);
            l.setStartY(0);
            l.setEndX(0);
            l.setEndY(0);
        }

        if (active)
        {
            l.setStartX(leftMouseBinding.getMouseX());
            l.setStartY(leftMouseBinding.getMouseY());
            l.setEndX(selecteCharacter.getWorldX());
            l.setEndY(selecteCharacter.getWorldY());
        }

        if (rightMouseBinding.isClicked())
        {
            rightMouseBinding.consumeClick();

            owner.addCharacter(rightMouseBinding.getMouseX(), rightMouseBinding.getMouseY());
        }
    }
}
