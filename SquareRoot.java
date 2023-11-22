import java.util.List;

public class SquareRoot extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return false;
    }
    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.get(0) instanceof FloatDataType) {
            FloatDataType dataType = (FloatDataType) p_collectionOfInterpreterDataTypeObjects.get(0); // for readability
            ((FloatDataType) p_collectionOfInterpreterDataTypeObjects.get(1)).value = (float) Math.sqrt(dataType.value); // set dataType's value equal to its sqrt
        } else {
            throw new Exception("SquareRoot: not the correct data type, must be float");
        }
    }
}
