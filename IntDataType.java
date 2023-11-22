public class IntDataType extends InterpreterDataType {
    int value;
    @Override
    public String toString() { return "" + value + ""; }
    @Override
    public void fromString(String input) {
        this.value = Integer.parseInt(input); // set data type to integer
    }
}
