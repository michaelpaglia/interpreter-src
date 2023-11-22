import java.util.Random;
import java.util.List;

public class GetRandom extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return false;
    }

    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.get(0) instanceof IntDataType) {
            Random rand = new Random();
            // upper bound of 100
            ((IntDataType) p_collectionOfInterpreterDataTypeObjects.get(0)).value = rand.nextInt(100);
        } else {
            throw new Exception("GetRandom: not the correct data type, must be int");
        }
    }
}
