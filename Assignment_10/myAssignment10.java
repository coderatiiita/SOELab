package myAssignment10;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shubhvash
 */
public class myAssignment10 {

    ArrayList<Operator> operators = new ArrayList<>();
    ArrayList<Operand> operands = new ArrayList<>();

    public static void main(String[] args) {
        PrintStream out;
        try {
            out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        new myAssignment10().run("input.c");
    }

    private void run(String filename) {
        String data = Helper.getInputFromFile(filename);
        Helper.removeQuotes(data);
        Helper.removeMultilineComments(data);
        Helper.removeSinglelineComments(data);

        Pattern functionPattern = Pattern.compile("(void|int|double|float|char|\\w+)\\s+(\\w+)\\s*\\((.*?)\\)\\s*\\{");
        Matcher functionMatcher = functionPattern.matcher(data);
        while (functionMatcher.find()) {
            int openingIndex = functionMatcher.end();
            int closingIndex = Helper.findMatching(data, openingIndex);
            findHalsteadComplexity(data.substring(openingIndex, closingIndex));
        }
    }

    private void countOperators(String data) {
        String arithmeticOperators = "([\\s\\w]?\\+[\\s\\w]|[\\s\\w]-[\\s\\w]?|\\*|/|%|";
        String assignmentOperators = "[\\s\\w]=[\\s\\w]|\\+=|-=|\\*=|/=|%=|<<=|>>=|&=|\\|=|\\^=|";
        String unaryOperators = "\\+\\+|--|!|~|&|sizeof|";
        String relationalOperators = "<|>|==|<=|>=|!=|";
        String bitwiseOperators = "<<|>>|\\||";                 // didn't added binary and i.e &
        String logicalOperators = "&&|\\|\\||";
        String ternaryOperator = ("\\?|");
        String punctuationOperator = ",|;|\\(|\\)|\\[(.+?)\\]|\\{|\\}|for|if|int|return)";

        String megaString = arithmeticOperators + assignmentOperators + unaryOperators + relationalOperators
                + bitwiseOperators + logicalOperators + ternaryOperator + punctuationOperator;
        Pattern operatorPattern = Pattern.compile(megaString);
        Matcher operatorMatcher = operatorPattern.matcher(data);
        while (operatorMatcher.find()) {
            String operator = operatorMatcher.group(0);
//            System.out.println(operator);
            operator = operator.trim();
            if (operator.contains("[")) {
                operator = "[]";
            } else if (operator.contains("=")) {
                Pattern p = Pattern.compile("[\\s\\w]?=[\\s\\w]?");
                Matcher m = p.matcher(operator);
                if (m.find()) {
                    operator = "=";
                }
            } else if (operator.contains("+")) {
                if (!operator.equals("+=") && !operator.equals("++")) {
                    operator = "+";
                }
            } else if (operator.contains("-")) {
                if (!operator.equals("-=") && !operator.equals("--")) {
                    operator = "-";
                }
            }
            operator = operator.trim();
            boolean found = false;
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i).name.equals(operator)) {
                    operators.get(i).count++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                operators.add(new Operator(operator, 1));
            }
        }
        System.out.println("S.No.\t\tOperator\t\tCount");
        for (int i = 0; i < operators.size(); i++) {
            System.out.println(i + 1 + "\t\t" + operators.get(i).name + "\t\t\t" + operators.get(i).count);
        }
    }

    private void countOperands(String data) {
        String [] reservedWords = {"int", "char", "auto", "const",
                                    "double", "float", "short", "struct",
                                    "unsigned", "break", "continue",
                                    "else", "for", "long", "signed", "switch",
                                    "void", "case", "default", "enum", "goto", "register",
                                    "sizeof", "typedef", "volatile", "do", "extern",
                                    "if", "return", "static", "union", "while" };
        
        Pattern variablePattern = Pattern.compile("\\b(\\w+)\\b");
        Matcher variableMatcher = variablePattern.matcher(data);
        while(variableMatcher.find()) {
            String operand = variableMatcher.group(1);
            boolean isReserved = false;
            for(String s : reservedWords) {
                if(s.equals(operand)) {
                    isReserved = true;
                    break;
                }
            }
            if(isReserved) {
                continue;
            }
//            System.out.println(operand);
            boolean found = false;
            for (int i = 0; i < operands.size(); i++) {
                if (operands.get(i).name.equals(operand)) {
                    operands.get(i).count++;
                    found = true;
                    break;
                }
            }
            if (!found) {
                operands.add(new Operand(operand, 1));
            }
        }
        System.out.println("\n\nS.No.\t\tOperand\t\t\tCount");
        for (int i = 0; i < operands.size(); i++) {
            System.out.println(i + 1 + "\t\t" + operands.get(i).name + "\t\t\t" + operands.get(i).count);
        }
    }

    private void findHalsteadComplexity(String functionData) {
        countOperators(functionData);
        countOperands(functionData);
        
        int n1 = 0, n2 = 0;
        for (int i = 0; i < operators.size(); i++) {
            n1 += operators.get(i).count;
        }
        for (int i = 0; i < operands.size(); i++) {
            n2 += operands.get(i).count;
        }
        
        System.out.println("\n\n\t\tTotal\t\tUnique");
        System.out.println("Operators\tN1 = " + n1 + "\t\tn1 = " + operators.size());
        System.out.println("Operands\tN2 = " + n2 + "\t\tn2 = " + operands.size());
        operands.clear();
        operators.clear();
    }

}
