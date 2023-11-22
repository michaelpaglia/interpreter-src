import java.util.HashSet;

public class LocalVariableNode {
    HashSet<String> localVariableName; // identifier name

    public Token.SYMBOL type;
    public LocalVariableNode(HashSet<String> p_localVariableName, Token.SYMBOL p_type) {
        this.localVariableName = p_localVariableName;
        this.type = p_type;
    }
    public Token.SYMBOL getType() {
        return type;
    }

    @Override
    public String toString() {
        return "LocalVariableNode{" + "" + localVariableName + "" + ": " + type + "}";
    }
}
