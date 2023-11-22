import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class Interpreter {
    HashMap<String, CallableNode> mapFunctions = new HashMap<>();
    HashMap<String, InterpreterDataType> storeVariables = new HashMap<>();

    public Float resolve(Node p_node) {
        // if float node -> return value
        if (p_node instanceof FloatNode) {
            return ((FloatNode) p_node).getFloat();
        }
        // if int node -> return value (cast as float)
        if (p_node instanceof IntegerNode) {
            return (float) ((IntegerNode) p_node).getInteger();
        }
        // if mathopnode -> it should call resolve() on the left and right sides.
        if (p_node instanceof VariableReferenceNode) {
            if (storeVariables.get(((VariableReferenceNode) p_node).variableName) instanceof IntDataType) {
                return (float) ((IntDataType) storeVariables.get(((VariableReferenceNode) p_node).variableName)).value;
            } else {
                return ((FloatDataType) storeVariables.get(((VariableReferenceNode) p_node).variableName)).value;
            }
        }
        if (p_node instanceof MathOpNode) {
            // recursively calls left and right nodes to get the value so that math can be performed
            float left = resolve(((MathOpNode) p_node).left);
            float right = resolve(((MathOpNode) p_node).right);
            // examines the operator on the MathOpNode and uses it to perform math on left and right nodes
            if (((MathOpNode) p_node).operation == MathOpNode.OPERATION.ADD) {
                return left + right;
            } else if (((MathOpNode) p_node).operation == MathOpNode.OPERATION.SUBTRACT) {
                return left - right;
            } else if (((MathOpNode) p_node).operation == MathOpNode.OPERATION.MULTIPLY) {
                return left * right;
            } else {
                return left / right;
            }
        }
        return null;
    }

    // almost an identical method to resolve(), instead it returns a string and is able to handle string concatenation
    public String resolveString(Node p_node) {
        if (p_node instanceof StringNode) {
            return ((StringNode) p_node).getString();
        }
        if (p_node instanceof CharNode) {
            return ((CharNode) p_node).getChar().toString();
        }
        if (p_node instanceof VariableReferenceNode) {
            if (storeVariables.get(((VariableReferenceNode) p_node).variableName) instanceof StringDataType) {
                return ((StringDataType) storeVariables.get(((VariableReferenceNode) p_node).variableName)).st;
            } else {
                return String.valueOf(((CharacterDataType) storeVariables.get(((VariableReferenceNode) p_node).variableName)).c);
            }
        }
        if (p_node instanceof MathOpNode) {
            // recursively calls left and right nodes to get the value so that math can be performed
            String left = resolveString(((MathOpNode) p_node).left);
            String right = resolveString(((MathOpNode) p_node).right);
            // examines the operator on the MathOpNode and uses it to perform math on left and right nodes
            if (((MathOpNode) p_node).operation == MathOpNode.OPERATION.ADD) {
                return left + right;
            }
        }
        return null;
    }

    public boolean resolveBoolean(Node p_node) {
        if (p_node instanceof BooleanNode) {
            return ((BooleanNode) p_node).getBoolean();
        }
        return false;
    }

    public void interpretFunction(FunctionNode p_functionNode, List<InterpreterDataType> p_parameters) throws Exception {
        // TODO: add parameters to the hashmap using the names that the function expects
        for (InterpreterDataType parameter : p_parameters) {
            storeVariables.put(parameter.toString(), parameter);
        }
        // TODO: add all of the local variables to the hashmap
        for (LocalVariableNode localVariableNode : p_functionNode.localVariableNode) {
            for (String localVariableName : localVariableNode.localVariableName) {
                if (localVariableNode.type.equals(Token.SYMBOL.integer)) {
                    storeVariables.put(localVariableName, new IntDataType());
                } else if (localVariableNode.type.equals(Token.SYMBOL.real)) {
                    storeVariables.put(localVariableName, new FloatDataType());
                } else if (localVariableNode.type.equals(Token.SYMBOL.StringContents)) {
                    storeVariables.put(localVariableName, new StringDataType());
                } else if (localVariableNode.type.equals(Token.SYMBOL.CharContents)) {
                    storeVariables.put(localVariableName, new CharacterDataType());
                } else {
                    storeVariables.put(localVariableName, new BooleanDataType());
                }
            }
        }
        // TODO: add constants to the hashmap
        for (ConstantNode constantNode : p_functionNode.constantNode) {
            if (constantNode.floatOrInt instanceof IntegerNode) { // ConstantNode(name : IntegerNode(value))
                IntDataType intDataType = new IntDataType();
                intDataType.value = ((IntegerNode) constantNode.floatOrInt).getInteger();
                storeVariables.put(constantNode.constantName, intDataType);
            } else if (constantNode.floatOrInt instanceof FloatNode) { // ConstantNode(name : FloatNode(value))
                FloatDataType floatDataType = new FloatDataType();
                floatDataType.value = ((FloatNode) constantNode.floatOrInt).getFloat();
                storeVariables.put(constantNode.constantName, floatDataType);
            } else if (constantNode.floatOrInt instanceof StringNode) {
                StringDataType stringDataType = new StringDataType();
                stringDataType.st = ((StringNode) constantNode.floatOrInt).getString();
                storeVariables.put(constantNode.constantName, stringDataType);
            } else if (constantNode.floatOrInt instanceof CharNode) {
                CharacterDataType characterDataType = new CharacterDataType();
                characterDataType.c = ((CharNode) constantNode.floatOrInt).getChar();
                storeVariables.put(constantNode.constantName, characterDataType);
            } else {
                BooleanDataType booleanDataType = new BooleanDataType();
                storeVariables.put(constantNode.constantName, booleanDataType);
            }
        }
        // System.out.println(storeVariables); // test hashmap
        // TODO: call InterpretBlock
        interpretBlock(p_functionNode.statementNodes, storeVariables);
    }

    public void interpretBlock(ArrayList<StatementNode> p_statements, HashMap<String, InterpreterDataType> p_variables) throws Exception {
        for (StatementNode statement : p_statements) {
            if (statement instanceof FunctionCallNode) {
                // TODO: STEP 1 - check if statement is a BuiltInFunction or a user-defined function
                if (mapFunctions.get(((FunctionCallNode) statement).nameOfFunction) instanceof BuiltInFunctionNode) {
                    // ... built in and NOT variadic, check number of parameters (getRandom [1], squareRoot[2], IntegerToReal[2], RealToInteger[2])
                    if (!((BuiltInFunctionNode) mapFunctions.get(((FunctionCallNode) statement).nameOfFunction)).isVariadic()) {
                        if (((FunctionCallNode) statement).nameOfFunction.equals("getRandom")) {
                            if (((FunctionCallNode) statement).listOfParameters.size() != 1) {
                                throw new Exception("InterpretBlock: getRandom requires 1 parameter");
                            }
                        }
                    }
                }
                // TODO: STEP 3 - make a collection of InterpreterDataTypes & for every parameter in invocation, add to list
                ArrayList<InterpreterDataType> collectionOfInterpreterDataTypes = new ArrayList<>();
                for (int i = 0; i < ((FunctionCallNode) statement).listOfParameters.size(); i++) {
                    // check for constant parameters, then determine if they are added as an IntDT or FloatDT
                    if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue != null) {
                        // check to see if parameter is a constant (already in the hashmap) in which case we don't need to do var <...>
                        if (p_variables.containsKey(((FunctionCallNode) statement).listOfParameters.get(i).constantValue.toString())) {
                            collectionOfInterpreterDataTypes.add(p_variables.get(((FunctionCallNode) statement).listOfParameters.get(i).constantValue.toString()));
                        }
                        if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue instanceof IntegerNode) {
                            IntDataType intDataType = new IntDataType();
                            intDataType.value = ((IntegerNode) ((FunctionCallNode) statement).listOfParameters.get(i).constantValue).getInteger();
                            collectionOfInterpreterDataTypes.add(intDataType);
                        } else if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue instanceof FloatNode) {
                            FloatDataType floatDataType = new FloatDataType();
                            floatDataType.value = ((FloatNode) ((FunctionCallNode) statement).listOfParameters.get(i).constantValue).getFloat();
                            collectionOfInterpreterDataTypes.add(floatDataType);
                        } else if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue instanceof StringNode) {
                            StringDataType stringDataType = new StringDataType();
                            stringDataType.st = ((StringNode) ((FunctionCallNode) statement).listOfParameters.get(i).constantValue).getString();
                            collectionOfInterpreterDataTypes.add(stringDataType);
                        } else if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue instanceof CharNode) {
                            CharacterDataType characterDataType = new CharacterDataType();
                            characterDataType.c = ((CharNode) ((FunctionCallNode) statement).listOfParameters.get(i).constantValue).getChar();
                            collectionOfInterpreterDataTypes.add(characterDataType);
                        } else if (((FunctionCallNode) statement).listOfParameters.get(i).constantValue instanceof BooleanNode) {
                            BooleanDataType booleanDataType = new BooleanDataType();
                            collectionOfInterpreterDataTypes.add(booleanDataType);
                        }
                        // not a constant parameter, must be var <something> (VariableReferenceNode). add current value.
                    } else {
                        collectionOfInterpreterDataTypes.add(p_variables.get(((FunctionCallNode) statement).listOfParameters.get(i).variableReferenceNode.variableName));
                    }
                }
                // TODO: STEP 4 - call the function, pass it to the collection of InterpreterDataTypes
                // ... built-in function
                if (mapFunctions.get(((FunctionCallNode) statement).nameOfFunction) instanceof BuiltInFunctionNode builtIn) {
                    // updates respective collectionofInterpreterDataTypes values
                    builtIn.execute(collectionOfInterpreterDataTypes);
                } else {
                    // ... user-defined function, call interpretFunction in interpreter
                    FunctionNode userDefined = (FunctionNode) mapFunctions.get(((FunctionCallNode) statement).nameOfFunction);
                    interpretFunction(userDefined, collectionOfInterpreterDataTypes);
                }
            } else if (statement instanceof AssignmentNode) {
                // ... original variable is of type Integer
                if (p_variables.get(statement.target.variableName) instanceof IntDataType) {
                    IntDataType intDataType = new IntDataType();
                    float result = resolve(statement.expression);
                    intDataType.value = (int) result;
                    p_variables.put(statement.target.variableName, intDataType);
                    // ... original variable is of type Float
                } else if (p_variables.get(statement.target.variableName) instanceof FloatDataType) {
                    FloatDataType floatDataType = new FloatDataType();
                    floatDataType.value = resolve(statement.expression);
                    p_variables.put(statement.target.variableName, floatDataType);
                } else if (p_variables.get(statement.target.variableName) instanceof StringDataType) {
                    StringDataType stringDataType = new StringDataType();
                    stringDataType.st = resolveString(statement.expression);
                    p_variables.put(statement.target.variableName, stringDataType);

                } else if (p_variables.get(statement.target.variableName) instanceof CharacterDataType) {
                    CharacterDataType characterDataType = new CharacterDataType();
                    characterDataType.c = resolveString(statement.expression).charAt(0);
                    p_variables.put(statement.target.variableName, characterDataType);
                } else if (p_variables.get(statement.target.variableName) instanceof BooleanDataType) {
                    BooleanDataType booleanDataType = new BooleanDataType();
                    booleanDataType.bool = resolveBoolean(statement.expression);
                    p_variables.put(statement.target.variableName, booleanDataType);
                }
                // iterators: WhileNode RepeatNode IfNode ForNode
            } else if (statement instanceof WhileNode) {
                while (evaluateBooleanExpression(((WhileNode) statement).booleanExpression, p_variables)) {
                    interpretBlock(((WhileNode) statement).statementNodes, p_variables);
                }
            } else if (statement instanceof RepeatNode) {
                do {
                    interpretBlock(((RepeatNode) statement).statementNodes, p_variables);
                } while (evaluateBooleanExpression(((RepeatNode) statement).booleanExpression, p_variables));
            } else if (statement instanceof IfNode) {
                // accounts for else (boolean is null)
                if (((IfNode) statement).booleanExpression == null) {
                    interpretBlock(((IfNode) statement).statementNodes, p_variables);
                } else if (evaluateBooleanExpression(((IfNode) statement).booleanExpression, p_variables)) {
                    interpretBlock(((IfNode) statement).statementNodes, p_variables);
                } else if (evaluateBooleanExpression(((IfNode) statement).ifNode.booleanExpression, p_variables)) {
                    interpretBlock(((IfNode) statement).ifNode.statementNodes, p_variables);
                }
            } else if (statement instanceof ForNode) {
                if (((ForNode) statement).start instanceof IntegerNode && ((ForNode) statement).end instanceof IntegerNode) {
                    int start = ((IntegerNode) ((ForNode) statement).start).getInteger();
                    int end = ((IntegerNode) ((ForNode) statement).end).getInteger();
                    for (int i = start; i < end; i++) {
                        interpretBlock(((ForNode) statement).statementNodes, p_variables);
                    }
                } else {
                    // indexes aren't integers
                    throw new Exception("Interpreter: for loop should start and end with integer values");
                }
            }
        }
    }

    public boolean evaluateBooleanExpression(BooleanExpressionNode p_booleanExpressionNode, HashMap<String, InterpreterDataType> p_variables) {
        // handles BooleanNode
        boolean value;
        if (((MathOpNode) p_booleanExpressionNode.leftExpression).left instanceof VariableReferenceNode && ((MathOpNode) p_booleanExpressionNode.leftExpression).right instanceof BooleanNode) {
            String varName = ((VariableReferenceNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).left).variableName;
            value = ((BooleanNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).right).getBoolean();
            if (p_variables.get(varName) instanceof BooleanDataType) {
                if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISEQUALTO)) {
                    return ((BooleanDataType) p_variables.get(varName)).bool == value;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISNOTEQUALTO)) {
                    return ((BooleanDataType) p_variables.get(varName)).bool != value;
                }
            }
        }
        // comparing a variable
        if (p_booleanExpressionNode.leftExpression instanceof MathOpNode) {
            // get the variable's value from the hashmap and check if the types match. if they don't, always return false
            if (p_variables.get(((VariableReferenceNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).left).variableName) instanceof IntDataType
                    && ((MathOpNode) p_booleanExpressionNode.leftExpression).right instanceof IntegerNode) {
                int leftValue = ((IntDataType) p_variables.get(((VariableReferenceNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).left).variableName)).value;
                int rightValue = ((IntegerNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).right).getInteger();
                if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISGREATERTHAN)) {
                    return leftValue > rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISLESSTHAN)) {
                    return leftValue < rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISGREATERTHANOREQUALTO)) {
                    return leftValue >= rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISLESSTHANOREQUALTO)) {
                    return leftValue <= rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISNOTEQUALTO)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }
            } else if (p_variables.get(((VariableReferenceNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).left).variableName) instanceof FloatDataType
                    && ((MathOpNode) p_booleanExpressionNode.leftExpression).right instanceof FloatNode) {
                float leftValue = ((FloatDataType) p_variables.get(((VariableReferenceNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).left).variableName)).value;
                float rightValue = ((FloatNode) ((MathOpNode) p_booleanExpressionNode.leftExpression).right).getFloat();

                if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISGREATERTHAN)) {
                    return leftValue > rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISLESSTHAN)) {
                    return leftValue < rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISGREATERTHANOREQUALTO)) {
                    return leftValue >= rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISLESSTHANOREQUALTO)) {
                    return leftValue <= rightValue;
                } else if (((MathOpNode) p_booleanExpressionNode.leftExpression).operation.equals(MathOpNode.OPERATION.ISNOTEQUALTO)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }
            } else {
                return false;
            }
        } else if (p_booleanExpressionNode.leftExpression instanceof VariableReferenceNode && p_booleanExpressionNode.rightExpression instanceof VariableReferenceNode) {
            // both the left and right expressions are variable reference nodes
            if (p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.leftExpression).variableName) instanceof IntDataType
                    && p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.rightExpression).variableName) instanceof IntDataType) {
                int leftValue = ((IntDataType) p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.leftExpression).variableName)).value;
                int rightValue = ((IntDataType) p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.rightExpression).variableName)).value;

                if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThan)) {
                    return leftValue > rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThan)) {
                    return leftValue < rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThanOrEqualTo)) {
                    return leftValue >= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThanOrEqualTo)) {
                    return leftValue <= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isNotEqualTo)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }
            } else if (p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.leftExpression).variableName) instanceof FloatDataType
                    && p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.rightExpression).variableName) instanceof FloatDataType) {
                float leftValue = ((FloatDataType) p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.leftExpression).variableName)).value;
                float rightValue = ((FloatDataType) p_variables.get(((VariableReferenceNode) p_booleanExpressionNode.rightExpression).variableName)).value;

                if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThan)) {
                    return leftValue > rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThan)) {
                    return leftValue < rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThanOrEqualTo)) {
                    return leftValue >= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThanOrEqualTo)) {
                    return leftValue <= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isNotEqualTo)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }
            } else {
                return false;
            }

        } else {
            // the left expression is not a variable reference node and the right expression isn't either, so we just compare numerical values
            if (p_booleanExpressionNode.leftExpression instanceof IntegerNode && p_booleanExpressionNode.rightExpression instanceof IntegerNode) {
                int leftValue = ((IntegerNode) p_booleanExpressionNode.leftExpression).getInteger();
                int rightValue = ((IntegerNode) p_booleanExpressionNode.rightExpression).getInteger();

                if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThan)) {
                    return leftValue > rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThan)) {
                    return leftValue < rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThanOrEqualTo)) {
                    return leftValue >= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThanOrEqualTo)) {
                    return leftValue <= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isNotEqualTo)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }

            } else if (p_booleanExpressionNode.leftExpression instanceof FloatNode && p_booleanExpressionNode.rightExpression instanceof FloatNode) {
                float leftValue = ((FloatNode) p_booleanExpressionNode.leftExpression).getFloat();
                float rightValue = ((FloatNode) p_booleanExpressionNode.rightExpression).getFloat();

                if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThan)) {
                    return leftValue > rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThan)) {
                    return leftValue < rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isGreaterThanOrEqualTo)) {
                    return leftValue >= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isLessThanOrEqualTo)) {
                    return leftValue <= rightValue;
                } else if (p_booleanExpressionNode.condition.equals(Token.SYMBOL.isNotEqualTo)) {
                    return leftValue != rightValue;
                } else {
                    // Token.SYMBOL.equals
                    return leftValue == rightValue;
                }
            } else {
                return false;
            }
        }
    }
}
