package physicsEngine.material;

public class MetalMaterial extends Material{
    final static float M_RESTITUTION = 0.2f;

    private MetalMaterial() {
        super(M_RESTITUTION);
    }

    private static MetalMaterial instance = new MetalMaterial();

    public static MetalMaterial getInstance(){ return instance; }

}
