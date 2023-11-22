public class BooleanNode extends Node {
    private boolean bool;

    public BooleanNode(boolean p_bool) {
        this.bool = p_bool;
    }

    public boolean getBoolean() {
        return this.bool;
    }

    @Override
    public String toString() {
        return "BooleanNode" + "(" + getBoolean() + ")";
    }
}
