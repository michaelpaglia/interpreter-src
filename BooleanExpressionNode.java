public class BooleanExpressionNode extends Node {
    Node leftExpression;
    Node rightExpression;
    public Token.SYMBOL condition;
    BooleanExpressionNode(Node p_left, Token.SYMBOL p_condition, Node p_right) {
        this.leftExpression = p_left;
        this.condition = p_condition;
        this.rightExpression = p_right;
    }

    @Override
    public String toString() {
        return "BooleanExpressionNode(" + leftExpression + " " + condition + " " + rightExpression + ")\n";
    }
}
