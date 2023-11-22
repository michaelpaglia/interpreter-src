public class CharNode extends Node {
    char c;
    CharNode (char p_c) {
        this.c = p_c;
    }
    @Override
    public String toString() {
        return "CharNode(" + c + ")";
    }

    public Character getChar() {
        return this.c;
    }
}
