import java.util.HashMap;
import java.util.List;

public class SemanticAnalysis {
    public void checkAssignments(List<FunctionNode> p_functionNodes) throws Exception {
        // add local variable names and types to new hashmap for reference
        HashMap<String, Token.SYMBOL> checkType = new HashMap<>();
        // iterate through each function node
        for (FunctionNode functionNode : p_functionNodes) {
            for (LocalVariableNode localVariableNode : functionNode.localVariableNode) {
                for (String name : localVariableNode.localVariableName) {
                    checkType.put(name, localVariableNode.getType());
                }
            }
            for (StatementNode statements : functionNode.statementNodes) {
                // check assignment
                if (statements instanceof AssignmentNode) {
                    // confirm type is correct
                    if (checkType.get(statements.target.variableName).equals(Token.SYMBOL.integer)) {
                        if (statements.expression instanceof MathOpNode) {
                            String leftName = "";
                            String rightName = "";
                            if (((MathOpNode) statements.expression).left instanceof VariableReferenceNode) {
                                leftName = ((VariableReferenceNode) ((MathOpNode) statements.expression).left).variableName;
                            }
                            if (((MathOpNode) statements.expression).right instanceof VariableReferenceNode) {
                                rightName = ((VariableReferenceNode) ((MathOpNode) statements.expression).left).variableName;
                            }

                            if (checkType.get(leftName) != checkType.get(rightName)) {
                                throw new Exception("SemanticAnalysis: MathOpNode; can only perform operations on same type (integer + integer)");
                            }
                            if (checkType.get(leftName) != checkType.get(statements.target.variableName) ||
                                    checkType.get(rightName) != checkType.get(statements.target.variableName)) {
                                throw new Exception("SemanticAnalysis: MathOpNode; must assign to the same type as target (integer)");
                            }
                        }
                        if (statements.expression instanceof FloatNode || statements.expression instanceof StringNode || statements.expression instanceof CharNode) {
                            throw new Exception("SemanticAnalysis: " + statements.expression + " is not of required type (integer)");
                        }
                    } else if (checkType.get(statements.target.variableName).equals(Token.SYMBOL.real)) {
                        if (statements.expression instanceof MathOpNode) {
                            if (checkType.get(((MathOpNode) statements.expression).left) != checkType.get(((MathOpNode) statements.expression).right)) {
                                throw new Exception("SemanticAnalysis: MathOpNode; can only perform operations on same type (integer + integer)");
                            }
                            if (checkType.get(((MathOpNode) statements.expression).left) != checkType.get(statements.target.variableName) ||
                                    checkType.get(((MathOpNode) statements.expression).right) != checkType.get(statements.target.variableName)) {
                                throw new Exception("SemanticAnalysis: MathOpNode: must assign " + ((MathOpNode) statements.expression).right + " to the same type as target:" +
                                        ((MathOpNode) statements.expression).left + "");
                            }
                        }
                        if (statements.expression instanceof IntegerNode || statements.expression instanceof StringNode || statements.expression instanceof CharNode) {
                            throw new Exception("SemanticAnalysis: " + statements.expression + " is not of required type (real/float)");
                        }
                    }
                }
            }
        }
    }
}
