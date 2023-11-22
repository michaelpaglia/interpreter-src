import java.util.ArrayList;
import java.util.HashSet;

public class IfNode extends StatementNode {
    BooleanExpressionNode booleanExpression;
    ArrayList<StatementNode> statementNodes;
    IfNode ifNode;

    IfNode(ArrayList<StatementNode> p_statementNode) {
        this.statementNodes = p_statementNode;
    }

    IfNode(BooleanExpressionNode p_booleanExpression, ArrayList<StatementNode> p_statementNode) {
        this.booleanExpression = p_booleanExpression;
        this.statementNodes = p_statementNode;
    }
    IfNode(BooleanExpressionNode p_booleanExpression, ArrayList<StatementNode> p_statementNode, IfNode p_ifNode) {
        this.booleanExpression = p_booleanExpression;
        this.statementNodes = p_statementNode;
        this.ifNode = p_ifNode;
    }
    @Override
    public String toString() {
        if (booleanExpression == null) {
            return "IfNode{{ NULL BOOLEAN: " + statementNodes + "}}\n";
        }
        if (ifNode == null) {
            return "IfNode{{" + booleanExpression + " then " + statementNodes + "}}\n";
        } else {
            return "IfNode{{" + booleanExpression + " then " + statementNodes + ": " + ifNode + "}}\n";
        }
    }
}
