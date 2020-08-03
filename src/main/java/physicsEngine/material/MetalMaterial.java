package physicsEngine.material;

public class MetalMaterial extends Material{
    final static float M_DENSITY = 2.0f;
    final static float M_RESTITUTION = 0.8f;

    private MetalMaterial() {
        super(M_DENSITY, M_RESTITUTION);
    }

    private static MetalMaterial instance = new MetalMaterial();

    public static MetalMaterial getInstance(){ return instance; }

}
