import java.util.HashSet;

public abstract class CallableNode extends Node {
    String functionName;
    HashSet<VariableNode> variableNode;
}
