package org.geogebra.common.kernel.arithmetic.vector;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.MyVecNDNode;
import org.geogebra.common.kernel.printing.printer.expression.ExpressionPrinter;
import org.geogebra.common.kernel.printing.printer.Printer;

public class GiacPrinter implements Printer {

    private MyVecNDNode vector;

    public GiacPrinter(MyVecNDNode vector) {
        this.vector = vector;
    }

    @Override
    public String print(StringTemplate tpl, ExpressionPrinter expressionPrinter) {
        StringBuilder sb = new StringBuilder();
        sb.append(vector.isCASVector() ? "ggbvect[" : "point(");
        printReGiac(sb, vector.getX(), expressionPrinter, tpl);
        sb.append(",");
        printReGiac(sb, vector.getY(), expressionPrinter, tpl);
        sb.append(vector.isCASVector() ? "]" : ")");
        return sb.toString();
    }

    private static void printReGiac(
            StringBuilder sb,
            ExpressionValue expressionValue,
            ExpressionPrinter printer,
            StringTemplate tpl) {

        if (expressionValue.unwrap() instanceof Command) {
            sb.append("re(");
        }
        sb.append(printer.print(expressionValue, tpl));
        if (expressionValue.unwrap() instanceof Command) {
            sb.append(")");
        }

    }
}
