import java.util.List;

public class Write extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return true;
    }
    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.size() == 0) throw new Exception("Write: size of list is 0");
        for (InterpreterDataType p_collectionOfInterpreterDataTypeObject : p_collectionOfInterpreterDataTypeObjects) {
            System.out.println(p_collectionOfInterpreterDataTypeObject);
        }
    }
}
