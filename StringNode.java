public class StringNode extends Node {
    String str;
    StringNode (String p_str) {
        this.str = p_str;
    }
    @Override
    public String toString() {
        return "StringNode(" + str + ")";
    }
    public String getString() { return this.str; }
}
