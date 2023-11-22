public class CharacterDataType extends InterpreterDataType {
    char c;
    @Override
    public String toString() { return "" + c + ""; }
    @Override
    public void fromString(String input) {
        this.c = input.charAt(0); // set data type to character
    }
}
