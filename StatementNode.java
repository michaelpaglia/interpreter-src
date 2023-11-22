public abstract class StatementNode extends Node {
    VariableReferenceNode target;
    Node expression;
    @Override
    public abstract String toString();
}
