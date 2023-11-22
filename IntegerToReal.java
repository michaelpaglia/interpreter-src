import java.util.List;

public class IntegerToReal extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return false;
    }

    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.get(0) instanceof IntDataType) {
            FloatDataType floatDataType = (FloatDataType) p_collectionOfInterpreterDataTypeObjects.get(1); // second parameter (floatDataType) is a float
            floatDataType.value = ((IntDataType) p_collectionOfInterpreterDataTypeObjects.get(0)).value; // set floatDataType's value equal to first parameter promoted as a float
        } else {
            throw new Exception("IntegerToReal: not the correct data type, must be integer");
        }
    }
}
