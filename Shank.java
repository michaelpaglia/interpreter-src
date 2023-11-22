import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Shank {
    public static void main(String[] args) throws Exception {
        Lexer lexer = new Lexer(); // Initializes Lexer class to use the lex() method
        Interpreter interpreter = new Interpreter(); // Initializes Interpreter class to use the resolve() method on the Nodes from Parser
        List<Token> masterList = new ArrayList<>();
        List<FunctionNode> listOfFunctions = new ArrayList<>();
        SemanticAnalysis semanticAnalysis = new SemanticAnalysis();
        try {
            List<String> readLine = Files.readAllLines(Paths.get("test.txt"), Charset.defaultCharset());
            for (String line : readLine) { masterList.addAll(lexer.lex(line));}
            Parser parser = new Parser(masterList);
            //System.out.println((parser.parse()));
            //accesses function name and adds function to hashmap (start)
            for (FunctionNode functions : parser.parse()) {
                interpreter.mapFunctions.put(functions.functionName, functions);
                listOfFunctions.add(functions);
            }
            // add built-in functions to hashmap
            interpreter.mapFunctions.put("getRandom", new GetRandom());
            interpreter.mapFunctions.put("intToReal", new IntegerToReal());
            interpreter.mapFunctions.put("realToInt", new RealToInteger());
            interpreter.mapFunctions.put("squareRoot", new SquareRoot());
            interpreter.mapFunctions.put("read", new Read());
            interpreter.mapFunctions.put("write", new Write());
            interpreter.mapFunctions.put("substring", new Substring());
            interpreter.mapFunctions.put("left", new Left());
            interpreter.mapFunctions.put("right", new Right());

            // call InterpretFunction on "start" and create storage for a collection of InterpreterDataTypes
            semanticAnalysis.checkAssignments(listOfFunctions);
            if (interpreter.mapFunctions.containsKey("start")) {
                interpreter.interpretFunction((FunctionNode) interpreter.mapFunctions.get("start"), new ArrayList<>());
            } else {
                throw new Exception("Missing a start definition in your function!");
            }
        } catch (IOException e) {
            throw new IOException("Couldn't read file", e);
        }
    }
}