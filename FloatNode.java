public class FloatNode extends Node {
    private float number;

    public FloatNode(float p_float) {
        this.number = p_float;
    }

    public float getFloat() {
        return this.number;
    }

    @Override
    public String toString() {
        return "FloatNode" + "(" + getFloat() + ")";
    }
}
