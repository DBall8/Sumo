package physicsEngine.material;

public class BouncyMaterial extends Material {

    final static float B_DENSITY = 0.5f;
    final static float B_RESTITUTION = 0.9f;

    public BouncyMaterial() {
        super(B_DENSITY, B_RESTITUTION);
    }

    public static BouncyMaterial getInstance(){ return SingletonGaurd.instance; }

    private static class SingletonGaurd
    {
        private static BouncyMaterial instance;
    }
}
