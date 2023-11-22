public class StringDataType extends InterpreterDataType {
    String st;
    @Override
    public String toString() { return "" + st + ""; }
    @Override
    public void fromString(String input) {
        this.st = input; // set data type to character
    }
}