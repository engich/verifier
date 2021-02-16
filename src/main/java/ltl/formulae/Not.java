package ltl.formulae;

public final class Not extends LtlFormulae {
    private final LtlFormulae formulae;

    public Not(final LtlFormulae formulae) {
        this.formulae = formulae;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return formulae instanceof Var;
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result;

        if (formulae instanceof False) {
            result = True.getInstance();
        } else if (formulae instanceof True) {
            result = False.getInstance();
        } else if (formulae instanceof Var) {
            result = this;
        } else if (formulae instanceof Not) {
            result = ((Not) formulae).getFormulae().getNegationNormalForm();
        } else if (formulae instanceof And) {
            result = new Or(new Not(((And) formulae).getLeft()).getNegationNormalForm(), new Not(((And) formulae).getRight()).getNegationNormalForm());
        } else if (formulae instanceof Or) {
            result = new And(new Not(((Or) formulae).getLeft()).getNegationNormalForm(), new Not(((Or) getFormulae()).getRight()).getNegationNormalForm());
        } else if (formulae instanceof Next) {
            result = new Next(new Not(((Next) formulae).getFormulae())).getNegationNormalForm();
        } else if (formulae instanceof Future) {
            result = new Not(new Until(True.getInstance(), ((Future) formulae).getFormulae())).getNegationNormalForm();
        } else if (formulae instanceof Globally) {
            result = new Not(new Release(False.getInstance(), ((Globally) formulae).getFormulae())).getNegationNormalForm();
        } else if (formulae instanceof Until) {
            result = new Release(new Not(((Until) formulae).getLeft()).getNegationNormalForm(), new Not(((Until) formulae).getRight()).getNegationNormalForm());
        } else if (formulae instanceof Release) {
            result = new Until(new Not(((Release) formulae).getLeft()).getNegationNormalForm(), new Not(((Release) formulae).getRight()).getNegationNormalForm());
        } else {
            throw new IllegalArgumentException("ltl.expression: " + formulae + " doesn't belong to any known class");
        }

        assert result.isNegationNormalForm();
        return result;
    }

    @Override
    public final LtlFormulae getNegation() {
        return formulae;
    }

    public final LtlFormulae getFormulae() {
        return formulae;
    }

    @Override
    public String toString() {
        return "!(" + formulae + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Not)) {
            return false;
        } else {
            return formulae.equals(((Not) other).formulae);
        }
    }

    @Override
    public int hashCode() {
        return formulae.hashCode();
    }
}
