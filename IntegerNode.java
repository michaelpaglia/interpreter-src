public class IntegerNode extends Node {
    private int number;

    public IntegerNode(int p_number) {
        this.number = p_number;
    }

    public int getInteger() {
        return this.number;
    }

    @Override
    public String toString() {
        return "IntegerNode" + "(" + getInteger() + ")";
    }
}
