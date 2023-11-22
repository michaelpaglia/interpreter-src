import java.util.List;

public abstract class BuiltInFunctionNode extends CallableNode {
    public abstract boolean isVariadic();
    public abstract void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception; // if called incorrectly
    @Override
    public String toString() {
        return "BuiltInFunctionNode{" + functionName + " is variadic: " + isVariadic() + "}";
    }
}
