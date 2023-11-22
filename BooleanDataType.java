public class BooleanDataType extends InterpreterDataType {
    boolean bool;
    @Override
    public String toString() { return "" + bool + ""; }
    @Override
    public void fromString(String input) {
        this.bool = Boolean.parseBoolean(input); // set data type to character
    }
}