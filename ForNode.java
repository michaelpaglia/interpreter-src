import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.HashSet;

public class ForNode extends StatementNode {
    VariableReferenceNode variableReference;
    Node start;
    Node end;
    ArrayList<StatementNode> statementNodes;

    ForNode(VariableReferenceNode p_variableReference, Node p_start, Node p_end, ArrayList<StatementNode> p_statementNode) {
        this.variableReference = p_variableReference;
        this.start = p_start;
        this.end = p_end;
        this.statementNodes = p_statementNode;
    }
    @Override
    public String toString() {
        return "ForNode{{" + variableReference + " starts at " + start + " & ends at " + end + " then: " + statementNodes + "}}\n";
    }
}
