package physicsEngine.material;

public class WoodMaterial extends Material{
    final static float W_DENSITY = 1.0f;
    final static float W_RESTITUION = 0.5f;

    public WoodMaterial()
    {
        super(W_DENSITY, W_RESTITUION);
    }

    private static WoodMaterial instance = new WoodMaterial();
    public static WoodMaterial getInstance(){ return instance; }
}
