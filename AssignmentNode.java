public class AssignmentNode extends StatementNode {
    public AssignmentNode(VariableReferenceNode p_target, Node p_expression) {
        this.target = p_target;
        this.expression = p_expression;
    }
    @Override
    public String toString() {
        return "AssignmentNode{" + target + " := " + expression + "}";
    }
}
