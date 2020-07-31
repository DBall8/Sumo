package physicsEngine.material;

public abstract class Material {

    protected float restitution;

    public Material(float restitution)
    {
        this.restitution = restitution;
    }

    public float getRestitution() {
        return restitution;
    }
}
