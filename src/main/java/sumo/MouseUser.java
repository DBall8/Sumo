package sumo;

import customMath.CustomMath;
import customMath.Vec2;
import gameEngine.Entity;
import gameEngine.userInput.MouseBinding;
import gameEngine.userInput.UserInputHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class MouseUser extends Entity {

    boolean active = false;
    MouseBinding mouseBinding;

    Character[] characters;
    Character selecteCharacter = null;
    Line l;

    public MouseUser(UserInputHandler inputHandler, Character[] characters)
    {
        mouseBinding = inputHandler.createMouseListener(MouseButton.PRIMARY);
        this.characters = characters;

        l = new Line(0, 0, 0, 0);
        l.setStrokeWidth(2);
        l.setStroke(Color.RED);
        addVisual(l);
    }

    @Override
    public void update()
    {
        if (!active && mouseBinding.isPressed())
        {
            mouseBinding.consumeClick();
            for (Character c: characters)
            {
                float distanceSquared = CustomMath.getDistSquared(
                        c.getWorldX(),
                        c.getWorldY(),
                        mouseBinding.getMouseX(),
                        mouseBinding.getMouseY());

                if (distanceSquared < c.getRadius() * c.getRadius())
                {
                    selecteCharacter = c;
                    active = true;
                    break;
                }
            }
        }

        if (active && !mouseBinding.isPressed())
        {
            Vec2 force = new Vec2(
                    selecteCharacter.getWorldX() - mouseBinding.getMouseX(),
                    selecteCharacter.getWorldY() - mouseBinding.getMouseY());

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
            l.setStartX(mouseBinding.getMouseX());
            l.setStartY(mouseBinding.getMouseY());
            l.setEndX(selecteCharacter.getWorldX());
            l.setEndY(selecteCharacter.getWorldY());
        }
    }
}
