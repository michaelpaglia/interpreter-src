import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Parser {
    List<Token> token; // reference list of tokens passed from lexer

    public Parser(List<Token> p_token) {
        this.token = p_token;
    }

    public List<FunctionNode> parse() throws Exception {
        // calls expression() and matchAndRemove() a newLine
        // Node savedExpression = expression();
        // return savedExpression;

        // calls functionDefinition()
        return functionDefinition();
    }
    public List<FunctionNode> functionDefinition() throws Exception {
        List<String> functionParameters = new ArrayList<>();  // handles param values in a function, it's a list since there can be multiple
        String valueOfFunctionName;
        ArrayList<VariableNode> parameterNode = new ArrayList<>();
        HashSet<ConstantNode> constantNode = new HashSet<>();
        HashSet<LocalVariableNode> variableNode = new HashSet<>();
        ArrayList<StatementNode> statementNode = new ArrayList<>();
        List<FunctionNode> functions = new ArrayList<>();

        while (matchAndRemove(Token.SYMBOL.define) != null) {
            // we find a function name (identifier), get the token value, and convert it to a string; will be used to build FunctionNode
            Token functionName = matchAndRemove(Token.SYMBOL.identifier);
            valueOfFunctionName = functionName.getValue();

            if (matchAndRemove(Token.SYMBOL.LPAREN) != null) {
                if (matchAndRemove(Token.SYMBOL.RPAREN) != null) {
                    functionParameters.add("No parameters");
                    parameterNode.add(new VariableNode(functionParameters, null));
                    functions.add(new FunctionNode(valueOfFunctionName, parameterNode, constantNode, variableNode, statementNode));
                    return functions;
                }
                // look for variable declarations (can be multiple)
                while (matchAndRemove(Token.SYMBOL.colon) == null) {
                    // keep iterating if we encounter a comma because we don't want to include it as an identifier
                    if (matchAndRemove(Token.SYMBOL.comma) != null) {
                        continue;
                    }
                    // we find an identifier, get the token value, convert to a string, and add it to our param list
                    Token newParameter = matchAndRemove(Token.SYMBOL.identifier);
                    functionParameters.add(newParameter.getValue());
                }
                // find and set data type of parameter variable (for now, either integer or real)
                if (matchAndRemove(Token.SYMBOL.integer) != null) {
                    parameterNode.add(new VariableNode(functionParameters, VariableNode.DATATYPE.integer));
                } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                    parameterNode.add(new VariableNode(functionParameters, VariableNode.DATATYPE.real));
                } else {
                    throw new Exception("Missing a data type for your identifier or not a valid one (integer/real)");
                }
                if (matchAndRemove(Token.SYMBOL.semicolon) != null) {
                    // re-initialize our list of variables
                    functionParameters = new ArrayList<>();
                    // essentially repeat the same process as we did above
                    while (matchAndRemove(Token.SYMBOL.colon) == null) {
                        if (matchAndRemove(Token.SYMBOL.comma) != null) {
                            continue;
                        }
                        Token anotherNewParameter = matchAndRemove(Token.SYMBOL.identifier);
                        functionParameters.add(anotherNewParameter.getValue());
                    }
                    if (matchAndRemove(Token.SYMBOL.integer) != null) {
                        parameterNode.add(new VariableNode(functionParameters, VariableNode.DATATYPE.integer));
                    } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                        parameterNode.add(new VariableNode(functionParameters, VariableNode.DATATYPE.real));
                    } else {
                        throw new Exception("Missing a data type for your identifier or not a valid one (integer/real)");
                    }
                }
                // header is done if ) is reached
                if (matchAndRemove(Token.SYMBOL.RPAREN) != null) {
                    if (matchAndRemove(Token.SYMBOL.EndOfLine) != null) {
                        constantNode = constants(); // look for constants and variables
                        variableNode = variables();
                        matchAndRemove(Token.SYMBOL.EndOfLine);
                        if (body()) { // if there is a body, execute statements, else body() throws an exception
                            matchAndRemove(Token.SYMBOL.begin);
                            matchAndRemove(Token.SYMBOL.EndOfLine);
                            statementNode = statements();
                        }
                    }
                } else {
                    throw new Exception("Missing a right parenthesis");
                }
            }
            functions.add(new FunctionNode(valueOfFunctionName, parameterNode, constantNode, variableNode, statementNode));
            if (token.size() > 1) {
                matchAndRemove(Token.SYMBOL.EndOfLine);
                parameterNode = new ArrayList<>();
                functionParameters = new ArrayList<>();
            }
        }
        matchAndRemove(Token.SYMBOL.EndOfLine);
        return functions;
    }
    public HashSet<ConstantNode> constants() throws Exception {
        HashSet<ConstantNode> constantNodes = new HashSet<>();

        // look for constants token, if found, call processConstants() function to look for tokens
        if (matchAndRemove(Token.SYMBOL.constants) != null) {
            if (matchAndRemove(Token.SYMBOL.EndOfLine) != null) {
                constantNodes = processConstants();
            }
        }
        matchAndRemove(Token.SYMBOL.EndOfLine);
        return constantNodes;
    }
    public HashSet<ConstantNode> processConstants() throws Exception {
        // looks for tokens in the format: "identifier equals number endOfLine" and creates a ConstantNode for each of them
        // loops until it doesn't find an identifier anymore
        HashSet<ConstantNode> constantList = new HashSet<>();

        // create new constant tokens as long as we don't hit the 'begin' token
        matchAndRemove(Token.SYMBOL.EndOfLine);
        while (matchAndRemove(Token.SYMBOL.begin) == null) {
            Token identifierName = matchAndRemove(Token.SYMBOL.identifier);
            if (identifierName == null) {
                throw new Exception("Missing an identifier name in the constants section or there is no begin symbol");
            }
            if (matchAndRemove(Token.SYMBOL.equals) != null) {
                Token numberValue = matchAndRemove(Token.SYMBOL.NUMBER);
                if (numberValue == null) {
                    Token character = matchAndRemove(Token.SYMBOL.CharContents);
                    if (character == null) {
                        Token string = matchAndRemove(Token.SYMBOL.StringContents);
                        constantList.add(new ConstantNode(identifierName.getValue(), new StringNode(string.getValue())));
                    } else {
                        constantList.add(new ConstantNode(identifierName.getValue(), new CharNode(character.getValue().charAt(0))));
                    }
                } else {
                    // check if number is a float or an int and add it as its respective Node value
                    Float f = Float.parseFloat(numberValue.getValue());
                    if(isFloat(f)) {
                        constantList.add(new ConstantNode(identifierName.getValue(), new FloatNode(f)));
                    } else {
                        Integer i = Integer.parseInt(numberValue.getValue());
                        constantList.add(new ConstantNode(identifierName.getValue(), new IntegerNode(i)));
                    }
                }
                // if we get to the variables section, clear the EndOfLine token and break
                matchAndRemove(Token.SYMBOL.EndOfLine);
                matchAndRemove(Token.SYMBOL.EndOfLine);

                // if next token is a variable, break and move to variables function
                if (token.get(0).getTokenName().equals(Token.SYMBOL.variables)) {
                    break;
                }
            } else {
                throw new Exception("Missing an equals sign in the constants declaration");
            }
        }
        // if we get to begin token, remove extra whitespace to help with parsing statements
        return constantList;
    }
    public HashSet<LocalVariableNode> variables() throws Exception {
        // looks for tokens in the format: "identifier : dataType endOfLine" and creates a LocalVariableNode for each of them
        // loops until it doesn't find an identifier anymore
        HashSet<LocalVariableNode> localVariableNodes = new HashSet<>();
        HashSet<String> listOfVariables = new HashSet<>();
        HashSet<String> anotherVariable = new HashSet<>();
        HashSet<String> aThirdVariable = new HashSet<>();
        HashSet<String> aFourthVariable = new HashSet<>();
        // no variables, return empty collection
        if (matchAndRemove(Token.SYMBOL.variables) == null) {
            return localVariableNodes;
        }
        // create new constant tokens as long as we don't hit the 'begin' token
        matchAndRemove(Token.SYMBOL.EndOfLine);
        while (matchAndRemove(Token.SYMBOL.begin) == null) {
            Token identifierName = matchAndRemove(Token.SYMBOL.identifier);
            if (identifierName == null) {
                throw new Exception("Missing an identifier name in the variables section or there is no begin symbol");
            } else {
                listOfVariables.add(identifierName.getValue());
                while (matchAndRemove(Token.SYMBOL.colon) == null) {
                    if (matchAndRemove(Token.SYMBOL.comma) != null) {
                        Token anotherIdentifier = matchAndRemove(Token.SYMBOL.identifier);
                        listOfVariables.add(anotherIdentifier.getValue());
                    }
                }
                // we get to a colon, so we create a new node
                if (matchAndRemove(Token.SYMBOL.integer) != null) {
                    localVariableNodes.add(new LocalVariableNode(listOfVariables, Token.SYMBOL.integer));
                } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                    matchAndRemove(Token.SYMBOL.real);
                    localVariableNodes.add(new LocalVariableNode(listOfVariables, Token.SYMBOL.real));
                } else if (matchAndRemove(Token.SYMBOL.CharContents) != null) {
                    matchAndRemove(Token.SYMBOL.CharContents);
                    localVariableNodes.add(new LocalVariableNode(listOfVariables, Token.SYMBOL.CharContents));
                } else {
                    matchAndRemove(Token.SYMBOL.StringContents);
                    localVariableNodes.add(new LocalVariableNode(listOfVariables, Token.SYMBOL.StringContents));
                }
                if (matchAndRemove(Token.SYMBOL.semicolon) != null) {
                    // re-initialize our list of variables;
                    // essentially repeat the same process as we did above
                    while (matchAndRemove(Token.SYMBOL.colon) == null) {
                        if (matchAndRemove(Token.SYMBOL.comma) != null) {
                            continue;
                        }
                        Token anotherNewVariable = matchAndRemove(Token.SYMBOL.identifier);
                        anotherVariable.add(anotherNewVariable.getValue());
                    }
                    if (matchAndRemove(Token.SYMBOL.integer) != null) {
                        localVariableNodes.add(new LocalVariableNode(anotherVariable, Token.SYMBOL.integer));
                    } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                        localVariableNodes.add(new LocalVariableNode(anotherVariable, Token.SYMBOL.real));
                    } else if (matchAndRemove(Token.SYMBOL.CharContents) != null) {
                        localVariableNodes.add(new LocalVariableNode(anotherVariable, Token.SYMBOL.CharContents));
                    } else if (matchAndRemove(Token.SYMBOL.StringContents) != null) {
                        localVariableNodes.add(new LocalVariableNode(anotherVariable, Token.SYMBOL.StringContents));
                    } else {
                        throw new Exception("Missing a data type for your identifier or not a valid one (integer/real/char/string)");
                    }
                }
                if (matchAndRemove(Token.SYMBOL.semicolon) != null) {
                    // re-initialize our list of variables;
                    // essentially repeat the same process as we did above
                    while (matchAndRemove(Token.SYMBOL.colon) == null) {
                        if (matchAndRemove(Token.SYMBOL.comma) != null) {
                            continue;
                        }
                        Token anotherNewVariable = matchAndRemove(Token.SYMBOL.identifier);
                        aThirdVariable.add(anotherNewVariable.getValue());
                    }
                    if (matchAndRemove(Token.SYMBOL.integer) != null) {
                        localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.integer));
                    } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                        localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.real));
                    }
                    if (token.get(0).getTokenName().equals(Token.SYMBOL.identifier)) {
                        if (token.get(0).getValue().equals("string")) {
                            localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.StringContents));
                            matchAndRemove(Token.SYMBOL.identifier);
                        } else if (token.get(0).getValue().equals("char")) {
                            localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.CharContents));
                            matchAndRemove(Token.SYMBOL.identifier);
                        }
                    }
                }
                if (matchAndRemove(Token.SYMBOL.semicolon) != null) {
                    // re-initialize our list of variables;
                    // essentially repeat the same process as we did above
                    while (matchAndRemove(Token.SYMBOL.colon) == null) {
                        if (matchAndRemove(Token.SYMBOL.comma) != null) {
                            continue;
                        }
                        Token anotherNewVariable = matchAndRemove(Token.SYMBOL.identifier);
                        aFourthVariable.add(anotherNewVariable.getValue());
                    }
                    if (matchAndRemove(Token.SYMBOL.integer) != null) {
                        localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.integer));
                    } else if (matchAndRemove(Token.SYMBOL.real) != null) {
                        localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.real));
                    }
                    if (token.get(0).getTokenName().equals(Token.SYMBOL.identifier)) {
                        if (token.get(0).getValue().equals("string")) {
                            localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.StringContents));
                            matchAndRemove(Token.SYMBOL.identifier);
                        } else if (token.get(0).getValue().equals("char")) {
                            localVariableNodes.add(new LocalVariableNode(aThirdVariable, Token.SYMBOL.CharContents));
                            matchAndRemove(Token.SYMBOL.identifier);
                        } else if (token.get(0).getValue().equals("boolean")) {
                            localVariableNodes.add(new LocalVariableNode(aFourthVariable, Token.SYMBOL.BOOLEAN));
                            matchAndRemove(Token.SYMBOL.identifier);
                        }
                    }
                }
            }
            matchAndRemove(Token.SYMBOL.EndOfLine);
        }
        return localVariableNodes;
    }
    public boolean body() throws Exception {
        // tests for an end token, if there isn't one, throw an exception before proceeding
        int i = 0;
        while(!(token.get(i).getTokenName().equals(Token.SYMBOL.end))) {
            i++;
        }
        if (token.get(i).getTokenName().equals(Token.SYMBOL.end)) {
            return true;
        } else {
            throw new Exception("Missing an end token");
        }
    }
    public AssignmentNode assignment() throws Exception {
        AssignmentNode assignmentNodes = null;
        VariableReferenceNode variableToAssign;
        // create new assignment until we reach an EndOfLine token
        while (matchAndRemove(Token.SYMBOL.EndOfLine) == null) {
            Token identifierName = matchAndRemove(Token.SYMBOL.identifier);
            if (identifierName == null) {
                throw new Exception("Missing an identifier name in the body section");
            } else {
                // store variable name as a new VariableReferenceNode
                variableToAssign = new VariableReferenceNode(identifierName.getValue());
            }
            if (matchAndRemove(Token.SYMBOL.assignment) != null) {
                // make use of our expression() method to assign an expression to a variable, create a new node, and break
                Node expressionToAssign = expression();
                if (expressionToAssign == null) {
                    throw new Exception("Missing an expression to assign");
                }
                assignmentNodes = new AssignmentNode(variableToAssign, expressionToAssign);
                matchAndRemove(Token.SYMBOL.EndOfLine);
                break;
            } else {
                // if assignment fails to execute, add back the identifier that was matched and removed
                // that way, functionCall() has an identifier to parse
                token.add(0, identifierName);
                throw new Exception("Missing an assignment in the body section");
            }
        }
        return assignmentNodes;
    }
    // TODO
    public BooleanExpressionNode booleanExpression() throws Exception {
        Node leftExpression = expression();
        Token.SYMBOL condition = null;
        if (matchAndRemove(Token.SYMBOL.isGreaterThan) != null) {
            condition = Token.SYMBOL.isGreaterThan;
        } else if (matchAndRemove(Token.SYMBOL.isLessThan) != null) {
            condition = Token.SYMBOL.isLessThan;
        } else if (matchAndRemove(Token.SYMBOL.isGreaterThanOrEqualTo) != null) {
            condition = Token.SYMBOL.isGreaterThanOrEqualTo;
        } else if (matchAndRemove(Token.SYMBOL.isLessThanOrEqualTo) != null) {
            condition = Token.SYMBOL.isLessThanOrEqualTo;
        } else if (matchAndRemove(Token.SYMBOL.isNotEqualTo) != null) {
            condition = Token.SYMBOL.isNotEqualTo;
        } else if (matchAndRemove(Token.SYMBOL.equals) != null) {
            condition = Token.SYMBOL.equals;
        }
        Node rightExpression = expression();
        return new BooleanExpressionNode(leftExpression, condition, rightExpression);
    }
    public WhileNode whileNode() throws Exception {
        WhileNode whileNode;
        ArrayList<StatementNode> statementNode = null;
        if (matchAndRemove(Token.SYMBOL.WHILE) != null) {
            // generate new boolean expression, as per WhileNode parameters
            BooleanExpressionNode booleanExpression = booleanExpression();
            matchAndRemove(Token.SYMBOL.EndOfLine);
            if (matchAndRemove(Token.SYMBOL.begin) != null) {
                matchAndRemove(Token.SYMBOL.EndOfLine);
                while (matchAndRemove(Token.SYMBOL.end) == null) {
                    // create new StatementNodes until we get to end of while loop
                    statementNode = statements();
                    matchAndRemove(Token.SYMBOL.EndOfLine);
                    break;
                    // assuming there is more code after this, get out of the loop
                }
                // create new while loop
                whileNode = new WhileNode(booleanExpression, statementNode);
            } else {
                throw new Exception("Missing a begin keyword in the while loop!");
            }
        } else {
            return null;
        }
        matchAndRemove(Token.SYMBOL.EndOfLine);
        return whileNode;
    }
    // TODO
    public ForNode forNode() throws Exception {
        ForNode forNode;
        ArrayList<StatementNode> statementNode = null;
        // VariableReferenceNode p_variableReference, Node p_start, Node p_end, HashSet<StatementNode> p_statementNode
        VariableReferenceNode variableReference;
        Node start;
        Node end;
        if (matchAndRemove(Token.SYMBOL.FOR) != null) {
            Token identifierName = matchAndRemove(Token.SYMBOL.identifier);
            // store identifier as new variable reference node
            variableReference = new VariableReferenceNode(identifierName.getValue());
            if (matchAndRemove(Token.SYMBOL.FROM) != null) {
                Token startNumber = matchAndRemove(Token.SYMBOL.NUMBER);
                if (startNumber == null) {
                    throw new Exception("Starting value is undefined in for loop");
                } else {
                    // store starting index of for loop iteration
                    Integer startingIndex = Integer.parseInt(startNumber.getValue());
                    start = new IntegerNode(startingIndex);
                    if (matchAndRemove(Token.SYMBOL.TO) != null) {
                        Token endNumber = matchAndRemove(Token.SYMBOL.NUMBER);
                        if (endNumber == null) {
                            throw new Exception("End value is undefined in for loop");
                        } else {
                            // store ending index of for loop iteration
                            Integer endingIndex = Integer.parseInt(endNumber.getValue());
                            end = new IntegerNode(endingIndex);

                            // now we start the collection of statement nodes, since this is a valid for loop header. check for begin
                            matchAndRemove(Token.SYMBOL.EndOfLine);
                            if (matchAndRemove(Token.SYMBOL.begin) != null) {
                                matchAndRemove(Token.SYMBOL.EndOfLine);
                                while (matchAndRemove(Token.SYMBOL.end) == null) {
                                    // create new StatementNodes until we get to end of while loop
                                    statementNode = statements();
                                    matchAndRemove(Token.SYMBOL.EndOfLine);
                                    // assuming there is more code after this, get out of the loop
                                    if (token.size() > 1) break;
                                }
                                forNode = new ForNode(variableReference, start, end, statementNode);
                            } else {
                                throw new Exception("Missing a begin token in for loop");
                            }
                        }
                    } else {
                        throw new Exception("'To' is missing from the for loop iteration");
                    }
                }
            } else {
                throw new Exception("'From' is missing from the for loop initialization");
            }
        } else {
            return null;
        }
        return forNode;
    }
    // TODO
    public IfNode ifNode() throws Exception {
        IfNode ifNode = null;
        // maintain order of if, elsif, elsif ... else with a linked hashset
        ArrayList<StatementNode> statementNode = null;
        IfNode elsIf = null;
        BooleanExpressionNode booleanExpression;

        // same process applies if token is IF or ELSIF
        if (matchAndRemove(Token.SYMBOL.IF) != null || matchAndRemove(Token.SYMBOL.ELSIF) != null) {
            booleanExpression = booleanExpression();
            if (matchAndRemove(Token.SYMBOL.THEN) != null) {
                matchAndRemove(Token.SYMBOL.EndOfLine);
                if (matchAndRemove(Token.SYMBOL.begin) != null) {
                    matchAndRemove(Token.SYMBOL.EndOfLine);
                    while (matchAndRemove(Token.SYMBOL.end) == null) {
                        // corner case, if the only character left is an EndOfLife
                        if (token.get(0).getTokenName().equals(Token.SYMBOL.EndOfLine)) break;
                        statementNode = statements();
                        matchAndRemove(Token.SYMBOL.EndOfLine);
                        if (token.get(0).getTokenName().equals(Token.SYMBOL.ELSIF)) {
                            // recursively call ifNode() and save result to add to IfNode
                            elsIf = ifNode();
                            ifNode = new IfNode(booleanExpression, statementNode, elsIf);
                            return ifNode;
                        } else {
                            // if there are no elsif/else tokens
                            ifNode = new IfNode(booleanExpression, statementNode);
                            return ifNode;
                        }
                    }
                } else {
                    throw new Exception("Missing a begin symbol in the IF statement");
                }
            } else {
                throw new Exception("Missing a then symbol in the IF statement");
            }
        } else if (matchAndRemove(Token.SYMBOL.ELSE) != null) {
            // ELSE doesn't check for 'then'
            matchAndRemove(Token.SYMBOL.EndOfLine);
            if (matchAndRemove(Token.SYMBOL.begin) != null) {
                matchAndRemove(Token.SYMBOL.EndOfLine);
                while (matchAndRemove(Token.SYMBOL.end) == null) {
                    if (token.get(0).getTokenName().equals(Token.SYMBOL.EndOfLine)) break;
                    statementNode = statements();
                }
                ifNode = new ElseNode(statementNode);
            }
        } else {
            return null;
        }
        matchAndRemove(Token.SYMBOL.EndOfLine);
        return ifNode;
    }
    public RepeatNode repeatNode() throws Exception {
        RepeatNode repeatNode = null;
        ArrayList<StatementNode> statementNode = null;
        BooleanExpressionNode booleanExpression = null;
        if (matchAndRemove(Token.SYMBOL.REPEAT) != null) {
            if (matchAndRemove(Token.SYMBOL.EndOfLine) != null) {
                if (matchAndRemove(Token.SYMBOL.begin) != null) {
                    matchAndRemove(Token.SYMBOL.EndOfLine);
                    while (matchAndRemove(Token.SYMBOL.end) == null) {
                        statementNode = statements();
                        matchAndRemove(Token.SYMBOL.EndOfLine);
                        break;
                    }
                    // save boolean expression
                    if (matchAndRemove(Token.SYMBOL.UNTIL) != null) {
                        booleanExpression = booleanExpression();
                    }
                    repeatNode = new RepeatNode(booleanExpression, statementNode);
                }
            } else {
                throw new Exception("Missing an EndOfLine following your REPEAT symbol");
            }
        } else {
            return null;
        }
        matchAndRemove(Token.SYMBOL.EndOfLine);
        return repeatNode;
    }
    
    public FunctionCallNode functionCall() throws Exception {
        List<FunctionCallParameterNode> listOfParameters = new ArrayList<>();
        Token functionName = matchAndRemove(Token.SYMBOL.identifier);
        Token stringContents = matchAndRemove(Token.SYMBOL.StringContents);
        while (matchAndRemove(Token.SYMBOL.EndOfLine) == null) {
            matchAndRemove(Token.SYMBOL.comma); // each iteration, remove the comma if there is one
            if (matchAndRemove(Token.SYMBOL.VAR) != null) { // parameter is a var (e.g. var test)
                Token variableName = matchAndRemove(Token.SYMBOL.identifier);
                listOfParameters.add(new FunctionCallParameterNode(new VariableReferenceNode(variableName.getValue())));
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.identifier)) { // parameter is a constant value (e.g. test)
                Token constantValue = matchAndRemove(Token.SYMBOL.identifier);
                listOfParameters.add(new FunctionCallParameterNode(new ConstantNode(constantValue.getValue())));
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.NUMBER)) { // parameter is a value (e.g. 10)
                Token numberValue = matchAndRemove(Token.SYMBOL.NUMBER);
                if (numberValue.getValue().contains(".")) {
                    Float f = Float.parseFloat(numberValue.getValue());
                    listOfParameters.add(new FunctionCallParameterNode(new FloatNode(f)));
                } else {
                    Integer i = Integer.parseInt(numberValue.getValue());
                    listOfParameters.add(new FunctionCallParameterNode(new IntegerNode(i)));
                }
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.StringContents)) {
                String value = stringContents.getValue();
                listOfParameters.add(new FunctionCallParameterNode(new StringNode(value)));
            } else {
                throw new Exception("Function call is missing a variable, a name, and/or a number");
            }
        }
        return new FunctionCallNode(functionName.getValue(), listOfParameters);
    }
    public StatementNode statement() throws Exception {
        // processes one assignment statement or one functionCall statement
        matchAndRemove(Token.SYMBOL.EndOfLine);
        try {
            return assignment();
        } catch (Exception e) {
        } try {
            return functionCall();
        } catch (Exception e) {
        }
        return null;
    }
    public ArrayList<StatementNode> statements() throws Exception {
        // maintain order with a linked hashset
        ArrayList<StatementNode> statementNodes = new ArrayList<>();
        // add statement to collection until we reach an end (i.e. processes multiple statements)
        while (matchAndRemove(Token.SYMBOL.end) == null) {
            // if we get to the end of the token list and all we have is an EndOfLine, remove it and break
            if(matchAndRemove(Token.SYMBOL.EndOfLine) != null) break;
            // if token is WHILE, do not run statement() since assignment() can't process the token.
            // instead, run whileNode() and generate new WhileNode to add to collection of StatementNodes
            if (token.get(0).getTokenName().equals(Token.SYMBOL.WHILE)) {
                WhileNode resultFromWhile = whileNode();
                statementNodes.add(resultFromWhile);
            // same idea for other symbols
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.FOR)){
                ForNode resultFromFor = forNode();
                statementNodes.add(resultFromFor);
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.IF)){
                IfNode resultFromIf = ifNode();
                statementNodes.add(resultFromIf);
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.ELSIF)){
                IfNode resultFromElseIf = ifNode();
                statementNodes.add(resultFromElseIf);
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.ELSE)) {
                IfNode resultFromElse = ifNode();
                statementNodes.add(resultFromElse);
            } else if (token.get(0).getTokenName().equals(Token.SYMBOL.REPEAT)) {
                RepeatNode resultFromRepeat = repeatNode();
                statementNodes.add(resultFromRepeat);
            } else {
                // default
                StatementNode resultFromStatement = statement();
                statementNodes.add(resultFromStatement);
            }
        }
        return statementNodes;
    }
    public Node expression() throws Exception {
        Node left = term();

        // check for + or - token and sets operator, if none, return left node
        // merges boolean operators to right side of expression
        MathOpNode.OPERATION operator;
        if (matchAndRemove(Token.SYMBOL.PLUS) != null) {
            operator = MathOpNode.OPERATION.ADD;
        } else if (matchAndRemove(Token.SYMBOL.MINUS) != null) {
            operator = MathOpNode.OPERATION.SUBTRACT;
        } else if (matchAndRemove(Token.SYMBOL.isGreaterThan) != null) {
            operator = MathOpNode.OPERATION.ISGREATERTHAN;
        } else if (matchAndRemove(Token.SYMBOL.isLessThan) != null) {
            operator = MathOpNode.OPERATION.ISLESSTHAN;
        } else if (matchAndRemove(Token.SYMBOL.isGreaterThanOrEqualTo) != null) {
            operator = MathOpNode.OPERATION.ISGREATERTHANOREQUALTO;
        } else if (matchAndRemove(Token.SYMBOL.isLessThanOrEqualTo) != null) {
            operator = MathOpNode.OPERATION.ISLESSTHANOREQUALTO;
        } else if (matchAndRemove(Token.SYMBOL.isNotEqualTo) != null) {
            operator = MathOpNode.OPERATION.ISNOTEQUALTO;
        } else if (matchAndRemove(Token.SYMBOL.equals) != null) {
            operator = MathOpNode.OPERATION.ISEQUALTO;
        } else {
            return left;
        }
        Node right = term();
        // if right returns null, we have to throw an error
        if(right == null) {
            throw new Exception("INITIAL Right operand is missing, statement is invalid, current expression is: " + left);
        }
        // checks for repeated + or - token as long as we haven't reached end of token list
        while(matchAndRemove(Token.SYMBOL.EndOfLine) == null) {
            // saves the right operand by overwriting the left operand
            left = new MathOpNode(operator, left, right);
            // iterate forwards and account for extra operators
            if (matchAndRemove(Token.SYMBOL.PLUS) != null) {
                operator = MathOpNode.OPERATION.ADD;
            } else if (matchAndRemove(Token.SYMBOL.MINUS) != null) {
                operator = MathOpNode.OPERATION.SUBTRACT;
            } else {
                return left;
            }
            right = term();
            // if right returns null, we have to throw an error
            if(right == null) {
                throw new Exception("Right operand is missing, statement is invalid, current expression is: " + left);
            }
        }
        // new node with all data
        return new MathOpNode(operator, left, right);
    }
    public Node term() throws Exception {
        Node left = factor();

        // check for + or - token and sets operator, if none, return left node
        MathOpNode.OPERATION operator;
        if (matchAndRemove(Token.SYMBOL.TIMES) != null) {
            operator = MathOpNode.OPERATION.MULTIPLY;
        } else if (matchAndRemove(Token.SYMBOL.DIVIDE) != null) {
            operator = MathOpNode.OPERATION.DIVIDE;
        } else if (matchAndRemove(Token.SYMBOL.MOD) != null) {
            operator = MathOpNode.OPERATION.MODULO;
        } else {
            return left;
        }
        Node right = factor();
        // if right returns null, we have to throw an error
        if(right == null) {
            throw new Exception("INITIAL Right operand is missing, statement is invalid, current expression is " + left);
        }
        // checks for repeated + or - token as long as we haven't reached end of token list
        while(matchAndRemove(Token.SYMBOL.EndOfLine) == null) {
            // saves the right operand by overwriting the left operand
            left = new MathOpNode(operator, left, right);
            // iterate forwards and account for extra operators
            if (matchAndRemove(Token.SYMBOL.TIMES) != null) {
                operator = MathOpNode.OPERATION.MULTIPLY;
            } else if (matchAndRemove(Token.SYMBOL.DIVIDE) != null) {
                operator = MathOpNode.OPERATION.DIVIDE;
            } else if (matchAndRemove(Token.SYMBOL.MOD) != null) {
                operator = MathOpNode.OPERATION.MODULO;
            } else {
                return left;
            }
            right = factor();

            if(right == null) {
                throw new Exception("Right operand is missing, statement is invalid, current expression is: " + left);
            }
        }
        // new node with all data
        return new MathOpNode(operator, left, right);
    }
    public Node factor() throws Exception {
        // checks for number
        // determines if float or integer and returns IntegerNode, FloatNode, or return value from expression

        // returns a CharNode or a StringNode from expression
        Token savedCharContents = matchAndRemove(Token.SYMBOL.CharContents);
        if (savedCharContents != null) {
            char c = savedCharContents.getValue().charAt(0);
            return new CharNode(c);
        }
        Token savedStringContents = matchAndRemove(Token.SYMBOL.StringContents);
        if (savedStringContents != null) {
            return new StringNode(savedStringContents.getValue());
        }
        // returns a BooleanNode when it finds a true and false token
        if (matchAndRemove(Token.SYMBOL.TRUE) != null) {
            return new BooleanNode(true);
        } else if (matchAndRemove(Token.SYMBOL.FALSE) != null) {
            return new BooleanNode(false);
        }

        Token savedNumericalToken = matchAndRemove(Token.SYMBOL.NUMBER);
        if(savedNumericalToken != null) {
            Float f = Float.parseFloat(savedNumericalToken.getValue());
            if(isFloat(f)) {
                return new FloatNode(f);
            } else {
                Integer i = Integer.parseInt(savedNumericalToken.getValue());
                return new IntegerNode(i);
            }
        }
        // factor now accepts an identifier by creating a variableReferenceNode, e.g. a := b + 1
        Token savedIdentifier = matchAndRemove(Token.SYMBOL.identifier);
        if (savedIdentifier != null) {
            return new VariableReferenceNode(savedIdentifier.getValue());
        }
        // checks to see if there is a (, if so, saves a new expression to work with. there must be a matching ), otherwise, throws error
        if(matchAndRemove(Token.SYMBOL.LPAREN) != null) {
            Node savedExpression = expression();

            if(matchAndRemove(Token.SYMBOL.RPAREN) != null) {
                return savedExpression;
            } else {
                throw new Exception("Missing a right parenthesis, current expression is: " + savedExpression);
            }
        }
        return null;
    }

    /**
     * Helper function that determines if a given token matches the token at the front of the list. If so, it is returned and removed
     * @param p_token A specific token that will be compared to the front of the list
     * @return The token at the front of the list if it matches, else, null
     */
    public Token matchAndRemove(Token.SYMBOL p_token) {
        // looks at token list, if next token matches the type, remove and return token, if not, return null
        if(p_token == token.get(0).getTokenName()) {
            return token.remove(0);
        } else {
            return null;
        }
    }

    /**
     * Helper function that determines if a given number is a float or not
     * @param p_float A specific number, as a float
     * @return Boolean value specifying whether the number is a float or not
     */
    public boolean isFloat(float p_float) {
        // if input is 0, is not a float, rather an int
        if(p_float == 0) {
            return false;
        }
        int truncatedTestFloat = (int) p_float;
        // subtracting the float from its truncated value should equal some decimal, meaning it is a float
        return p_float - truncatedTestFloat != 0;
    }
}
