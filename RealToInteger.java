import java.util.List;

public class RealToInteger extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return false;
    }

    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.get(0) instanceof FloatDataType) {
            IntDataType intDataType = (IntDataType) p_collectionOfInterpreterDataTypeObjects.get(1); // second parameter (intDataType) is an int
            intDataType.value = (int) ((FloatDataType) p_collectionOfInterpreterDataTypeObjects.get(0)).value; // update intDataType value equal to first parameter truncated as an int
        } else {
            throw new Exception("IntegerToReal: not the correct data type, must be float");
        }
    }
}
