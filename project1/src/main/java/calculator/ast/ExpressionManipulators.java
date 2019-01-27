package calculator.ast;

import calculator.interpreter.Environment;

import static org.junit.Assert.fail;

import calculator.errors.EvaluationError;
import calculator.gui.ImageDrawer;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;

/**
 * All of the static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the TODOs in the 'toDoubleHelper' method.
        return new AstNode(toDoubleHelper(env.getVariables(), node.getChildren().get(0)));
    }
    
    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases.
        if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            String variable = node.getName();
            AstNode newNode;
            if (variables.containsKey(variable)) {
                newNode = variables.get(variable);
            } else {
                throw new EvaluationError("undefined variable");
            }
            return toDoubleHelper(variables, newNode);
        } else {
            String name = node.getName();
            IList<AstNode> childs = node.getChildren();
            if (childs.size() == 2) {
                double value1 = toDoubleHelper(variables, childs.get(0));
                double value2 = toDoubleHelper(variables, childs.get(1));
                if (name.equals("+")) {
                    return value1 + value2;
                } else if (name.equals("-")) {
                    return value1 - value2;
                } else if (name.equals("*")) {
                    return value1 * value2;
                } else if (name.equals("/")) {
                    return value1 / value2;
                } else if (name.equals("^")){
                    return Math.pow(value1, value2);
                }
            } else if (childs.size() == 1) {
                double value = toDoubleHelper(variables, childs.get(0));
                if (name.equals("negate")) {
                    return -value;
                } else if (name.equals("sin")) {
                    return Math.sin(value);
                } else if (name.equals("cos")){
                    return Math.cos(value);
                } 
            }
            // when none of the valid operators are found
            throw new EvaluationError("unknown operation");
        }
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // Try writing this one on your own!
        // Hint 1: Your code will likely be structured roughly similarly
        //         to your "handleToDouble" method
        // Hint 2: When you're implementing constant folding, you may want
        //         to call your "handleToDouble" method in some way

        return simplifyHelper(env.getVariables(), node.getChildren().get(0));
    }

    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) {
        if (node.isNumber()) {
            return new AstNode(node.getNumericValue());
        } else if (node.isVariable()) {
            String variable = node.getName();
            AstNode newNode;
            if (variables.containsKey(variable)) {
                newNode = variables.get(variable);
                return simplifyHelper(variables, newNode);
            } else {
                newNode = new AstNode(variable);
                return newNode;
            }
        } else {
            String name = node.getName();
            IList<AstNode> childs = node.getChildren();
            IList<AstNode> newChilds = new DoubleLinkedList<AstNode>();
            if (childs.size() == 2) {
                AstNode child1 = childs.get(0);
                AstNode child2 = childs.get(1);
                if (child1.isNumber() & child2.isNumber()) {
                    double value1 = toDoubleHelper(variables, child1);
                    double value2 = toDoubleHelper(variables, child2);
                    if (name.equals ("+")) {
                        return new AstNode(value1 + value2);
                    } else if (name.equals("-")) {
                        return new AstNode(value1 - value2);
                    } else if (name.equals("*")) {
                        return new AstNode(value1 * value2);
                    }
                }
                newChilds.add(simplifyHelper(variables, child1));
                newChilds.add(simplifyHelper(variables, child2));
            } else if (childs.size() == 1){
                newChilds.add(simplifyHelper(variables, childs.get(0)));
            }
            return new AstNode(name, newChilds);
        }
    }
    
    /**
     * Accepts a 'plot(exprToPlot, var, varMin, varMax, step)' AstNode and
     * generates the corresponding plot. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
        IDictionary<String, AstNode> variables = env.getVariables();
        IList<AstNode> childs = node.getChildren();
        AstNode varMin = childs.get(2);
        AstNode varMax = childs.get(3);
        AstNode step = childs.get(4);
        if ((varMin.isVariable() && !variables.containsKey(varMin.getName())) |
            (varMax.isVariable() && !variables.containsKey(varMax.getName())) |
            (step.isVariable() && !variables.containsKey(step.getName()))) {
            throw new EvaluationError("undefined variable");
        }
        double max = toDoubleHelper(variables, varMax);
        double s = toDoubleHelper(variables, step);
        double min = toDoubleHelper(variables, varMin);
        if ((min > max) | (s <= 0)) {
            throw new EvaluationError("out of bound");
        }
        AstNode var = childs.get(1);
        AstNode function = childs.get(0);
        if (variables.containsKey(var.getName())) {
            throw new EvaluationError("variable already defined");
        }
        IList<Double> xValues = new DoubleLinkedList<Double>();
        IList<Double> yValues = new DoubleLinkedList<Double>();
        for (double i = min; i <= max; i += s) {
            xValues.add(i);
            variables.put(var.getName(), new AstNode(i));
            double y = toDoubleHelper(variables, function);
            yValues.add(y);
            variables.remove(var.getName());
        }
        ImageDrawer pen = env.getImageDrawer();
        pen.drawScatterPlot(function.toString(), var.getName(), "output", xValues, yValues);
        
        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
}
