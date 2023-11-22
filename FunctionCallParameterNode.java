public class FunctionCallParameterNode extends Node {
    VariableReferenceNode variableReferenceNode;
    Node constantValue;

    public FunctionCallParameterNode (VariableReferenceNode p_variableReferenceNode) {
        this.variableReferenceNode = p_variableReferenceNode;
    }
    public FunctionCallParameterNode (Node p_constantValue) {
        this.constantValue = p_constantValue;
    }

    @Override
    public String toString() {
        if (variableReferenceNode == null) { // a constant value
            return "" + constantValue;
        } else {
            return "" + variableReferenceNode; // a variable reference node
        }
    }
}
