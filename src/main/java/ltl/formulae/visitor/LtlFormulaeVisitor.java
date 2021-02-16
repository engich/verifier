package ltl.formulae.visitor;

import ltl.formulae.*;
import ltl.gen.LtlBaseVisitor;
import ltl.gen.LtlParser;

public class LtlFormulaeVisitor extends LtlBaseVisitor<LtlFormulae> {

    @Override
    public LtlFormulae visitParentheses(LtlParser.ParenthesesContext parenthesesContext) {
        return visit(parenthesesContext.getChild(1));
    }

    @Override
    public LtlFormulae visitNegation(LtlParser.NegationContext negationContext) {
        return new Not(visit(negationContext.expression()));
    }

    @Override
    public LtlFormulae visitConjunction(LtlParser.ConjunctionContext conjunctionContext) {
        return new And(visit(conjunctionContext.lhs), visit(conjunctionContext.rhs));
    }

    @Override
    public LtlFormulae visitDisjunction(LtlParser.DisjunctionContext disjunctionContext) {
        return new Or(visit(disjunctionContext.lhs), visit(disjunctionContext.rhs));
    }

    @Override
    public LtlFormulae visitImplication(LtlParser.ImplicationContext implicationContext) {
        return new Or(new Not(visit(implicationContext.lhs)), visit(implicationContext.rhs));
    }

    @Override
    public LtlFormulae visitNext(LtlParser.NextContext nextContext) {
        return new Next(visit(nextContext.expression()));
    }

    @Override
    public LtlFormulae visitFuture(LtlParser.FutureContext futureContext) {
        return new Future(visit(futureContext.expression()));
    }

    @Override
    public LtlFormulae visitGlobally(LtlParser.GloballyContext globallyContext) {
        return new Globally(visit(globallyContext.expression()));
    }

    @Override
    public LtlFormulae visitUntil(LtlParser.UntilContext untilContext) {
        return new Until(visit(untilContext.lhs), visit(untilContext.rhs));
    }

    @Override
    public LtlFormulae visitRelease(LtlParser.ReleaseContext releaseContext) {
        return new Release(visit(releaseContext.lhs), visit(releaseContext.rhs));
    }

    @Override
    public LtlFormulae visitVariable(LtlParser.VariableContext variableContext) {
        return new Var(variableContext.ID().getText());
    }

    @Override
    public LtlFormulae visitBoolean(LtlParser.BooleanContext booleanContext) {
        switch (booleanContext.getText()) {
            case "false": {
                return False.getInstance();
            }
            case "true": {
                return True.getInstance();
            }
            default: {
                throw new IllegalArgumentException("Wrong boolean argument " + booleanContext.getText());
            }
        }
    }
}
