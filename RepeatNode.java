import java.util.ArrayList;
import java.util.HashSet;

public class RepeatNode extends StatementNode {
    BooleanExpressionNode booleanExpression;
    ArrayList<StatementNode> statementNodes;
    RepeatNode(BooleanExpressionNode p_booleanExpression, ArrayList<StatementNode> p_statementNode) {
        this.booleanExpression = p_booleanExpression;
        this.statementNodes = p_statementNode;
    }
    @Override
    public String toString() {
        return "RepeatNode{{" + statementNodes + " until " + booleanExpression + "}}";
    }
}
