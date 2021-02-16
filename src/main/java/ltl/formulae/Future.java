package ltl.formulae;

public final class Future extends LtlFormulae {
    private final LtlFormulae formulae;

    public Future(final LtlFormulae formulae) {
        this.formulae = formulae;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return false;
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Until(True.getInstance(), formulae).getNegationNormalForm();
        assert result.isNegationNormalForm();
        return result;
    }

    @Override
    public final LtlFormulae getNegation() {
        return new Not(this);
    }

    public final LtlFormulae getFormulae() {
        return formulae;
    }

    @Override
    public String toString() {
        return "F (" + formulae + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Future)) {
            return false;
        } else {
            return formulae.equals(((Future) other).formulae);
        }
    }

    @Override
    public int hashCode() {
        return formulae.hashCode();
    }
}