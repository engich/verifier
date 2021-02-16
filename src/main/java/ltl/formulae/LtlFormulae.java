package ltl.formulae;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class LtlFormulae {

    public LtlFormulae() {
    }

    abstract public boolean isNegationNormalForm();

    abstract public LtlFormulae getNegationNormalForm();

    abstract public LtlFormulae getNegation();

    public final Set<LtlFormulae> getClosure() {
        final Set<LtlFormulae> result = new HashSet<>();
        result.add(False.getInstance());
        result.add(True.getInstance());

        final Consumer<LtlFormulae> inner = new Consumer<LtlFormulae>() {
            @Override
            public void accept(LtlFormulae ltlExpression) {
                result.add(ltlExpression);
                result.add(ltlExpression.getNegation().getNegationNormalForm());

                if (ltlExpression instanceof Not) {
                    accept(((Not) ltlExpression).getFormulae());
                } else if (ltlExpression instanceof And) {
                    accept(((And) ltlExpression).getLeft());
                    accept(((And) ltlExpression).getRight());
                } else if (ltlExpression instanceof Or) {
                    accept(((Or) ltlExpression).getLeft());
                    accept(((Or) ltlExpression).getRight());
                } else if (ltlExpression instanceof Next) {
                    accept(((Next) ltlExpression).getFormulae());
                } else if (ltlExpression instanceof Future) {
                    accept(((Future) ltlExpression).getFormulae());
                } else if (ltlExpression instanceof Globally) {
                    accept(((Globally) ltlExpression).getFormulae());
                } else if (ltlExpression instanceof Until) {
                    accept(((Until) ltlExpression).getLeft());
                    accept(((Until) ltlExpression).getRight());
                } else if (ltlExpression instanceof Release) {
                    accept(((Release) ltlExpression).getLeft());
                    accept(((Release) ltlExpression).getRight());
                }
            }
        };

        inner.accept(this);
        return result;
    }
}
