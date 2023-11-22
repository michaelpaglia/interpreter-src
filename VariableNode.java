import java.util.List;

public class VariableNode extends Node {
    List<String> initialValue; // is a List because multiple parameters can map to the same type, e.g. "one two : integer"
    public DATATYPE dataType;
    private boolean isConstant;

    public boolean isConstant() {
        return isConstant;
    }

    public enum DATATYPE {
        integer, real
    }
    public VariableNode(List<String> p_initialValue, DATATYPE p_dataType) {
        this.initialValue = p_initialValue;
        this.dataType = p_dataType;
    }

    @Override
    public String toString() {
        return "VariableNode{" + dataType + ": " + initialValue.toString().replace("[", "").replace("]", "") + "}";
    }
}
