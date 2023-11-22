import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
public class Lexer {
    public List<Token> lex(String input) throws Exception {
        ArrayList<Token> tokens = new ArrayList<>();
        String accumulator = ""; // Used to accumulate multiple characters of same numerical type (e.g. 123)
        String charAccumulator = ""; // Used to accumulate multiple characters of a string (e.g. "identifier")
        String charAndStringAccumulator = "";
        HashMap<String, Token.SYMBOL> reservedWords = new HashMap<>();  // List of pre-reserved words to add as a token

        // Add integer, real, begin, end, variables, constants to the HashMap with their matching token type.
        reservedWords.put("integer", Token.SYMBOL.integer);
        reservedWords.put("real", Token.SYMBOL.real);
        reservedWords.put("begin", Token.SYMBOL.begin);
        reservedWords.put("end", Token.SYMBOL.end);
        reservedWords.put("variables", Token.SYMBOL.variables);
        reservedWords.put("constants", Token.SYMBOL.constants);
        reservedWords.put("define", Token.SYMBOL.define);
        // add if, then, else, elsif, for, from, to, while, repeat, until, mod
        reservedWords.put("if", Token.SYMBOL.IF);
        reservedWords.put("then", Token.SYMBOL.THEN);
        reservedWords.put("else", Token.SYMBOL.ELSE);
        reservedWords.put("elsif", Token.SYMBOL.ELSIF);
        reservedWords.put("for", Token.SYMBOL.FOR);
        reservedWords.put("from", Token.SYMBOL.FROM);
        reservedWords.put("to", Token.SYMBOL.TO);
        reservedWords.put("while", Token.SYMBOL.WHILE);
        reservedWords.put("repeat", Token.SYMBOL.REPEAT);
        reservedWords.put("until", Token.SYMBOL.UNTIL);
        reservedWords.put("mod", Token.SYMBOL.MOD);
        // add var
        reservedWords.put("var", Token.SYMBOL.VAR);
        // add true and false keywords
        reservedWords.put("true", Token.SYMBOL.TRUE);
        reservedWords.put("false", Token.SYMBOL.FALSE);

        // Flag which represents if the lexer is in a comment state, in which case tokens are ignored
        boolean isComment = false;
        boolean isChar = false;
        boolean isString = false;

        for (int i = 0; i < input.length(); i++) {
            char referenceChar = input.charAt(i);

            switch (referenceChar) { // Switches based on the current character, whether it is an operator/number/space
                case '+':
                    if (isComment) break;
                    handleMinus(tokens, accumulator);
                    accumulator = ""; // Reset accumulator
                    if(lastCharIsOperatorOrNothing(tokens)) {
                        throw new Exception("Last character cannot be an operator: " + referenceChar); }
                    tokens.add(new Token(Token.SYMBOL.PLUS, "+"));
                    break;
                case '-':
                    if (isComment) break;
                    handleMinus(tokens, accumulator);
                    accumulator = "";
                    if (lastCharIsOperatorOrNothing(tokens)) { // Because the last token is MINUS/PLUS/etc., we have to start a new negative number
                        accumulator += "-";
                    } else {
                        tokens.add(new Token(Token.SYMBOL.MINUS, "-")); // If previous token is a number, add MINUS (e.g. 3 MINUS ...)
                    }
                    break;
                case '*':
                    if (isComment) break;
                    // If previous token is (, then we enter a comment state
                    if(tokens.get(tokens.size()-1).getTokenName().equals(Token.SYMBOL.LPAREN)) {
                        tokens.remove(tokens.size() - 1);
                        isComment = true;
                        break;
                    }
                    handleMinus(tokens, accumulator);
                    accumulator = "";
                    if(lastCharIsOperatorOrNothing(tokens)) {
                        throw new Exception("Last character cannot be an operator: " + referenceChar); }
                    tokens.add(new Token(Token.SYMBOL.TIMES, "*"));
                    break;
                case '/':
                    if (isComment) break;
                    handleMinus(tokens, accumulator);
                    accumulator = "";
                    if(lastCharIsOperatorOrNothing(tokens)) {
                        throw new Exception("Last character cannot be an operator: " + referenceChar); }
                    tokens.add(new Token(Token.SYMBOL.DIVIDE, "/"));
                    break;
                case '(':
                    if (isComment) break;
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.LPAREN, "("));
                    break;
                case ')':
                    if (isComment) break;
                    handleMinus(tokens, accumulator);
                    accumulator = "";

                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.RPAREN, ")"));

                    // We are now out of our comment state, so set flag to false
                    isComment = false;
                    break;
                case ',':
                    if (isComment) break;
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    if (!accumulator.isEmpty()) {
                        tokens.add(new Token(Token.SYMBOL.NUMBER, accumulator));
                        accumulator = "";
                    }
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.comma, ","));
                    break;
                case ':':
                    if (isComment) break;
                    tokens.add(new Token(Token.SYMBOL.colon, ":"));
                    break;
                case '=':
                    if (isComment) break;
                    // adding := assignment token
                    if (tokens.get(tokens.size() - 1).getTokenName().equals(Token.SYMBOL.colon)) {
                        tokens.remove(tokens.size() - 1);
                        tokens.add(new Token(Token.SYMBOL.assignment));
                        break;
                    } else if (tokens.get(tokens.size() - 1).getTokenName().equals(Token.SYMBOL.isGreaterThan)) {
                        tokens.remove(tokens.size() - 1);
                        tokens.add(new Token(Token.SYMBOL.isGreaterThanOrEqualTo)); // >=
                        break;
                    } else if (tokens.get(tokens.size() - 1).getTokenName().equals(Token.SYMBOL.isLessThan)) {
                        tokens.remove(tokens.size() - 1);
                        tokens.add(new Token(Token.SYMBOL.isLessThanOrEqualTo)); // <=
                        break;
                    }
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.equals, "="));
                    break;
                case ';':
                    if (isComment) break;
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.semicolon, ";"));
                    break;
                case '>':
                    if (isComment) break;
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    if (tokens.get(tokens.size() - 1).getTokenName().equals(Token.SYMBOL.isLessThan)) {
                        tokens.remove(tokens.size() - 1);
                        tokens.add(new Token(Token.SYMBOL.isNotEqualTo));
                        break;
                    }
                    tokens.add(new Token(Token.SYMBOL.isGreaterThan, ">"));
                    break;
                case '<':
                    if (isComment) break;
                    handleReservedWords(tokens, charAccumulator, reservedWords);
                    charAccumulator = "";
                    tokens.add(new Token(Token.SYMBOL.isLessThan, "<"));
                    break;
                case '%':
                    if (isComment) break;
                    handleMinus(tokens, accumulator);
                    accumulator = "";
                    if (lastCharIsOperatorOrNothing(tokens)) { // Because the last token is MINUS/PLUS/etc., we have to start a new negative number
                        accumulator += "-";
                    } else {
                        tokens.add(new Token(Token.SYMBOL.MOD, "%")); // If previous token is a number, add MINUS (e.g. 3 MINUS ...)
                    }
                    break;
                case '\'':
                    if (isComment) break;
                    isChar = true;
                    if (charAndStringAccumulator.length() == 1) { // Character can only be limited to 1 length
                        isChar = false;
                        charAndStringAccumulator = "";
                    }
                case '\"':
                    if (isComment) break;
                    isString = true;
                    if (charAndStringAccumulator.length() > 0) { // Given we reached the end quotation mark, and the string is not empty, we can make a new token
                        isString = false;
                        tokens.add(new Token(Token.SYMBOL.StringContents, charAndStringAccumulator));
                        charAndStringAccumulator = "";
                        break;
                    } else {
                        break;
                    }
                default:
                    if (Character.isDigit(referenceChar) || referenceChar == '.') {
                        if (isComment) break;
                        if (isChar && charAndStringAccumulator.length() < 1) {
                            charAndStringAccumulator += referenceChar;
                        }
                        accumulator += referenceChar;
                        break;
                    } else if(Character.isLetter(referenceChar)) {
                        if (isComment) break;
                        if (isChar && charAndStringAccumulator.length() <= 1) { // Character can only be limited to 1 length
                            charAndStringAccumulator += referenceChar;
                            tokens.add(new Token(Token.SYMBOL.CharContents, charAndStringAccumulator));
                        } else if (isString) {
                            charAndStringAccumulator += referenceChar;
                        }
                        if (!isChar && !isString) {
                            charAccumulator += referenceChar;
                        }
                        break;
                    } else if (Character.isWhitespace(referenceChar)) { // In case of a space, checks if last token is a number and current accumulator is a number
                        if (isString) { // If in string state, simply add the whitespace to the string and break
                            charAndStringAccumulator += referenceChar;
                            break;
                        } else {
                            handleTwoNumbers(tokens, accumulator);
                            handleMinus(tokens, accumulator);
                            accumulator = ""; // If character is empty space, reset accumulator, so we can create new tokens
                            handleReservedWords(tokens, charAccumulator, reservedWords);
                            charAccumulator = ""; // Reset accumulator to create new (word) tokens
                        }
                    } else {
                        throw new Exception("Not a valid character: " + referenceChar);
                    }
            }
        }
        if (!charAccumulator.isEmpty()) {
            handleReservedWords(tokens, charAccumulator, reservedWords);
        }
        if (!accumulator.isEmpty()) {
            tokens.add(new Token(Token.SYMBOL.NUMBER, accumulator)); // Adds whatever is left on the accumulator to the token list
        }
        // add an EndOfLine token if it's not a comment, else ignore
        if (!isComment) {
            tokens.add(new Token(Token.SYMBOL.EndOfLine, "EndOfLine"));
        }
        return tokens; // After the lexer is executed, tokens are returned as a list
    }
    /**
     * Helper function based on current charAccumulator that handles if the "string of chars" preceding a space matches a reserved word
     * @param tokens ArrayList of tokens so far
     * @param charAccumulator Stores current char tokens to create new ones
     * @param reservedWords HashMap of reserved words that should be used to create new reservedWord tokens
     */
    private void handleReservedWords(ArrayList<Token> tokens, String charAccumulator, HashMap<String, Token.SYMBOL> reservedWords) {
        if(!charAccumulator.isEmpty()) {
            if(reservedWords.containsKey(charAccumulator)) {
                tokens.add(new Token(reservedWords.get(charAccumulator), charAccumulator)); // If accumulator string matches the key in hashmap, add it as its respective token
            } else {
                tokens.add(new Token(Token.SYMBOL.identifier, charAccumulator)); // If it doesn't match, it is added as an "identifier" token
            }
        }
    }

    /**
     * Helper function based on current accumulator that determines if there are two separate numbers together (e.g. 7 3 2 5)
     * @param tokens ArrayList of tokens so far
     * @param accumulator Stores current tokens to create new ones
     * @throws Exception If two numbers are together, input is invalid
     */
    private static void handleTwoNumbers(ArrayList<Token> tokens, String accumulator) throws Exception {
        if (!accumulator.isEmpty() && !tokens.isEmpty() && tokens.get(tokens.size()-1).getTokenName().equals(Token.SYMBOL.NUMBER)) {
            throw new Exception("Cannot have two numbers together: " + tokens.get(tokens.size()-1).getValue()  + ", " + accumulator);
        }
    }

    /**
     * Helper function based on current accumulator that determines if the "-" sign on the accumulator should be handled as part of a
     * negative number or an actual minus sign (e.g. 1 + -2 vs. 1 - 2).
     * @param tokens ArrayList of tokens so far
     * @param accumulator Stores current tokens to create new ones
     */
    private static void handleMinus(ArrayList<Token> tokens, String accumulator) {
        if (!accumulator.isEmpty()) {
            if (accumulator.equals("-")) {
                tokens.add(new Token(Token.SYMBOL.MINUS, accumulator));
            } else {
                tokens.add(new Token(Token.SYMBOL.NUMBER, accumulator)); // If accumulator has numbers on it, add as NUMBER token
            }
        }
    }

    /**
     * Helper function based on current list of tokens that determines if the last character in the token list is an operator
     * @param tokens ArrayList of tokens so far
     * @return Boolean value indicating if last character is an operator or not
     */
    public static boolean lastCharIsOperatorOrNothing(ArrayList<Token> tokens) { // Helper function based on previous character in token list
        if (tokens.isEmpty()) {
            return true;
        }
        String endOfTokenListValue = tokens.get(tokens.size() - 1).getValue(); // Accessor for previous character in token list
        return endOfTokenListValue.equals("+") || endOfTokenListValue.equals("*") ||
                endOfTokenListValue.equals("/") || endOfTokenListValue.equals("-"); // Returns true as long as previous character is an operator
    }
}
