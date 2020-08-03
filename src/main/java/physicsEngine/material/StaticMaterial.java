package physicsEngine.material;

public class StaticMaterial extends Material{
    public StaticMaterial() {
        super(0, 0.5f);
    }

    private static StaticMaterial instance = new StaticMaterial();

    public static StaticMaterial getInstance() {
        return instance;
    }
}
