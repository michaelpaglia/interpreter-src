import java.util.List;

public class Substring extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return false;
    }
    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.get(0) instanceof StringDataType) {
            if (p_collectionOfInterpreterDataTypeObjects.get(1) instanceof IntDataType) {
                int index = ((IntDataType) p_collectionOfInterpreterDataTypeObjects.get(1)).value;
                int length = ((IntDataType) p_collectionOfInterpreterDataTypeObjects.get(2)).value;
                if (p_collectionOfInterpreterDataTypeObjects.get(3) instanceof StringDataType) {
                    String substring = ((StringDataType) p_collectionOfInterpreterDataTypeObjects.get(0)).st.substring(index, length);
                    ((StringDataType) p_collectionOfInterpreterDataTypeObjects.get(3)).st = substring;
                } else {
                    throw new Exception("Substring: fourth parameter must be of type String");
                }
            } else {
                throw new Exception("Substring: second parameter must be of type Integer");
            }
        } else {
            throw new Exception("Substring: first parameter must be of type String");
        }
    }
}
