import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.InputStreamReader;

public class Read extends BuiltInFunctionNode {
    @Override
    public boolean isVariadic() {
        return true;
    }
    @Override
    public void execute(List<InterpreterDataType> p_collectionOfInterpreterDataTypeObjects) throws Exception {
        if (p_collectionOfInterpreterDataTypeObjects.size() == 0) throw new Exception("Read: size of list is 0");
        ArrayList<InterpreterDataType> read = new ArrayList<>();
        for (InterpreterDataType p_collectionOfInterpreterDataTypeObject : p_collectionOfInterpreterDataTypeObjects) {
            read.add(p_collectionOfInterpreterDataTypeObject);
            System.out.println("Reading: " + p_collectionOfInterpreterDataTypeObject + "...");
        }
    }
}
