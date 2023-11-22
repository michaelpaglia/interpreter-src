public class Token {
    private SYMBOL tokenName;
    private String value;

    public Token(SYMBOL tokenName) {
        this.tokenName = tokenName;
    }
    public Token(SYMBOL tokenName, String value) { // Used in the Lexer class to help match a token's value to the correct enum
        this.tokenName = tokenName;
        this.value = value;
    }

    public enum SYMBOL { // Potential symbols for the lexer to use: numbers (including decimals), +, -, *, /
        NUMBER, PLUS, MINUS, TIMES, DIVIDE, EndOfLine, LPAREN, RPAREN,
        identifier, define, integer, real, begin, end, semicolon, colon,
        equals, comma, variables, constants,
        assignment,
        IF, THEN, ELSE, ELSIF, FOR, FROM, TO, WHILE, REPEAT, UNTIL, MOD,
        isGreaterThan, isLessThan, isGreaterThanOrEqualTo, isLessThanOrEqualTo, isNotEqualTo,
        VAR,
        BOOLEAN, TRUE, FALSE, CharContents, StringContents
    }

    public String toString() { // Overridden method that formats numbers differently compared to other tokens, as per assignment instructions
        if (tokenName == SYMBOL.NUMBER) {
            return "NUMBER " + "(" + value + ")";
        } else if (tokenName == SYMBOL.identifier) {
            return "identifier " + "(" + value + ")";
        } else if (tokenName == SYMBOL.CharContents) {
            return "CharContents " + "(" + value + ")";
        } else if (tokenName == SYMBOL.StringContents) {
            return "StringContents " + "(" + value + ")";
        }
        return tokenName.toString();
    }

    public SYMBOL getTokenName() { return tokenName; }
    public String getValue() {
        return value;
    }
}
