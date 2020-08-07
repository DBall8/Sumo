package sumo;

import customMath.Vec2;
import gameEngine.GameEngine;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import physicsEngine.PhysicsWorld;

import java.util.ArrayList;
import java.util.List;

public class SumoGame extends GameEngine {

    private int WINDOW_WIDTH = 1000;
    private int WINDOW_HEIGHT = 500;

    private final static float ORIGINAL_FRAME_RATE = 60.0f;
    private final static int FRAME_RATE = 60;

    private final static int NUM_CHARACTERS = 1;

    PhysicsWorld physicsWorld;

    Player p1;
    Player p2;

    MouseUser mouseControl;

    List<Character> characters = new ArrayList<>();

    static SumoGame debugInstance;

    public float getFrameRateFactor()
    {
        return getFramesPerSecond() / ORIGINAL_FRAME_RATE;
    }

    public Vec2 getWindowDim()
    {
        return new Vec2(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public PhysicsWorld getPhysicsWorld(){ return physicsWorld; }

    @Override
    protected void onInitialize()
    {
        setWindowWidth(WINDOW_WIDTH);
        setWindowHeight(WINDOW_HEIGHT);
        setFramesPerSecond(FRAME_RATE);

        debugInstance = this;

        physicsWorld = new PhysicsWorld(FRAME_RATE, 10f, 0.8f);
    }

    @Override
    protected void onStart()
    {
        p1 = new Player(1, 20, 20, getUserInputHandler(), this);
        addEntity(p1);

        p2 = new Player(2, 500, 300, getUserInputHandler(), this);
        addEntity(p2);

//        createRandomCharacters(NUM_CHARACTERS);
//
        mouseControl = new MouseUser(getUserInputHandler(), characters, this);
        addEntity(mouseControl);

        Character ground = new Character(WINDOW_WIDTH/2, WINDOW_HEIGHT - 2, WINDOW_WIDTH, 4, true, this);
        addEntity(ground);
        characters.add(ground);

        Character wall1 = new Character(0, WINDOW_HEIGHT / 2, 4, WINDOW_HEIGHT, true, this);
        addEntity(wall1);
        characters.add(wall1);

        Character wall2 = new Character(WINDOW_WIDTH, WINDOW_HEIGHT / 2, 4, WINDOW_HEIGHT, true, this);
        addEntity(wall2);
        characters.add(wall2);

        Character cieling = new Character(WINDOW_WIDTH/2, 0, WINDOW_WIDTH, 4, true, this);
        addEntity(cieling);
        characters.add(cieling);
//        createGround(10);

//        Viewer viewer = new Viewer(getUserInputHandler(), Viewer.ViewerShape.Circle, 5, this);
//        addEntity(viewer);
    }

    @Override
    protected void onUpdateStart()
    {
        RESET_DBUG();
        float alpha = physicsWorld.update(1.0f / getFramesPerSecond());
    }

    @Override
    protected void onUpdateFinish(){}

    @Override
    protected void onClose(){}

    // For debugging
    public static void ADD_DEBUG(Node n)
    {
        debugInstance.addDebugVisual(n);
    }

    public static void RESET_DBUG()
    {
        debugInstance.clearDebugVisuals();
    }

    public static void ADD_DEBUG_DOT(float x, float y, float radius, Color color)
    {
        Circle c = new Circle(x, y, radius);
        c.setFill(color);
        ADD_DEBUG(c);
    }

    private void createRandomCharacters(int numCharacters)
    {
        for (int i=0; i<numCharacters; i++)
        {
            float x = (float) (Math.random() * getWindowDim().getX());
            float y = (float) (Math.random() * getWindowDim().getY());
            float size = (float) (Math.random() * 45 + 5);

            Character c;

            float shape = (float)Math.random();
            if (shape < 10.5)
            {
                c = new Character(x, y, size, false, this);
            }
            else
            {
                float height = (float) (Math.random() * 45 + 5);
                c = new Character(x, y, size, height, false, this);
            }

            characters.add(c);
            addEntity(c);
        }
    }

    public void addCharacter(float x, float y)
    {
        float size = (float)(Math.random() * 45 + 5);
        Character c = new Character(x, y, size, false, this);
        characters.add(c);
        addEntity(c);
    }

    public void addCharacter(Character c)
    {
        characters.add(c);
        addEntity(c);
    }

    public List<Character> getCharacters(){ return characters; }

    private void createGround(float radius)
    {
        float x = 0;
        while(x < WINDOW_WIDTH)
        {
            Character c = new Character(x + radius, WINDOW_HEIGHT, radius, true, this);
            addEntity(c);
            characters.add(c);
            x += 2*radius;
        }
    }
}
