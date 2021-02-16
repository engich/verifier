package ltl.formulae;

public final class Globally extends LtlFormulae {
    private final LtlFormulae formulae;

    public Globally(final LtlFormulae formulae) {
        this.formulae = formulae;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return false;
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Release(False.getInstance(), formulae).getNegationNormalForm();
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
        return "G (" + formulae + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Globally)) {
            return false;
        } else {
            return formulae.equals(((Globally) other).formulae);
        }
    }

    @Override
    public int hashCode() {
        return formulae.hashCode();
    }
}
