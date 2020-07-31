package physicsEngine.material;

public class BouncyMaterial extends Material {

    final static float B_RESTITUTION = 0.8f;

    public BouncyMaterial() {
        super(B_RESTITUTION);
    }

    public static BouncyMaterial getInstance(){ return SingletonGaurd.instance; }

    private static class SingletonGaurd
    {
        private static BouncyMaterial instance;
    }
}
