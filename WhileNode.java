import java.util.ArrayList;
import java.util.HashSet;

public class WhileNode extends StatementNode {
    BooleanExpressionNode booleanExpression;
    ArrayList<StatementNode> statementNodes;
    WhileNode(BooleanExpressionNode p_booleanExpression, ArrayList<StatementNode> p_statementNode) {
        this.booleanExpression = p_booleanExpression;
        this.statementNodes = p_statementNode;
    }
    @Override
    public String toString() {
        return "WhileNode{{" + booleanExpression + " then: " + statementNodes + "}}\n";
    }
}
