public class ConstantNode extends Node {
    String constantName; // identifier name
    Node floatOrInt; // either a FloatNode or IntegerNode
    public ConstantNode(String p_constantName, Node p_floatOrInt) {
        this.constantName = p_constantName;
        this.floatOrInt = p_floatOrInt;
    }
    public ConstantNode(String p_constantName) {
        this.constantName = p_constantName;
    }
    @Override
    public String toString() {
        if (floatOrInt == null) {
            return "" + constantName;
        } else if (constantName == null) {
            return "" + floatOrInt;
        } else {
            return "ConstantNode{" + "'" + constantName + "'" + ": " + floatOrInt + "}";
        }
    }
}
