package ltl.formulae;

public final class Next extends LtlFormulae {
    private final LtlFormulae formulae;

    public Next(final LtlFormulae formulae) {
        this.formulae = formulae;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return formulae.isNegationNormalForm();
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Next(formulae.getNegationNormalForm());
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
        return "N (" + formulae + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Next)) {
            return false;
        } else {
            return formulae.equals(((Next) other).formulae);
        }
    }

    @Override
    public int hashCode() {
        return formulae.hashCode();
    }
}
