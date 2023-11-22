import java.util.ArrayList;
import java.util.HashSet;

public class ElseNode extends IfNode {
    ElseNode(ArrayList<StatementNode> statementNodes) {
        super(statementNodes);
    }
    @Override
    public String toString() {
        return "ElseNode(" + statementNodes + ")";
    }
}
