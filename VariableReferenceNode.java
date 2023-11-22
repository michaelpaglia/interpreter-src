public class VariableReferenceNode extends Node {
    String variableName;
    boolean isConstant;
    public VariableReferenceNode(String p_variableName) {
        this.variableName = p_variableName;
    }

    @Override
    public String toString() {
        return "VariableReferenceNode(" + variableName + ")";
    }
}
