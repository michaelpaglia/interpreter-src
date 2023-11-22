import java.util.List;

public class FunctionCallNode extends StatementNode {
    String nameOfFunction;
    List<FunctionCallParameterNode> listOfParameters; // VariableReferenceNode or a constant

    public FunctionCallNode (String p_nameOfFunction, List<FunctionCallParameterNode> p_listOfParameters) {
        this.nameOfFunction = p_nameOfFunction;
        this.listOfParameters = p_listOfParameters;
    }
    @Override
    public String toString() {
        if (listOfParameters.isEmpty()) { // no parameters
            return "FunctionCall{{" + nameOfFunction + "}}\n";
        } else {
            return "FunctionCall{{" + nameOfFunction + ": " + listOfParameters + "}}\n";
        }
    }
}
