public class FloatDataType extends InterpreterDataType {
    float value;
    @Override
    public String toString() {
        return "" + value + "";
    }
    @Override
    public void fromString(String input) {
        this.value = Float.parseFloat(input); // set data type to float;
    }
}
