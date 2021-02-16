package ltl.formulae;

public final class Var extends LtlFormulae {
    private final String id;

    public Var(final String id) {
        this.id = id;
    }

    @Override
    public boolean isNegationNormalForm() {
        return true;
    }

    @Override
    public final LtlFormulae getNegation() {
        return new Not(this);
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = this;
        assert result.isNegationNormalForm();
        return result;
    }

    public final String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Var(" + id + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Var)) {
            return false;
        } else {
            return id.equals(((Var) other).id);
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}