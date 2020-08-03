package physicsEngine.material;

public abstract class Material {

    protected float density;
    protected float restitution;

    public Material(float density, float restitution)
    {
        this.density = density;
        this.restitution = restitution;
    }

    public float getDensity() {
        return density;
    }

    public float getRestitution() {
        return restitution;
    }
}
