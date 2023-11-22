import java.util.ArrayList;
import java.util.HashSet;

public class FunctionNode extends CallableNode {
    String functionName;
    ArrayList<VariableNode> variableNode;
    HashSet<ConstantNode> constantNode;
    HashSet<LocalVariableNode> localVariableNode;
    ArrayList<StatementNode> statementNodes;
    public FunctionNode(String p_functionName, ArrayList<VariableNode> p_variableNode, HashSet<ConstantNode> p_constantNodes, HashSet<LocalVariableNode> p_localVariableNode, ArrayList<StatementNode> p_statementNode) {
        this.functionName = p_functionName;
        this.variableNode = p_variableNode;
        this.constantNode = p_constantNodes;
        this.localVariableNode = p_localVariableNode;
        this.statementNodes = p_statementNode;
    }
    @Override
    public String toString() {
        return "FunctionNode(" + functionName + ", " + variableNode.toString().replace("[", "").replace("]", "") + ")" + ":\n" +
                constantNode.toString().replace("[", "").replace("]", "").replace(", ", "") + "\n" +
                localVariableNode.toString().replace("[", "").replace("]", "") + "\n" +
                statementNodes.toString().replace("[", "").replace("]", "") + "\n";
    }
}
