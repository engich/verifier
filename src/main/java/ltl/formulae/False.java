package ltl.formulae;

public final class False extends LtlFormulae {
    private static final False INSTANCE;

    static {
        INSTANCE = new False();
    }

    private False() {
    }

    public static False getInstance() {
        return INSTANCE;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return true;
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = this;
        assert result.isNegationNormalForm();
        return result;
    }

    @Override
    public final LtlFormulae getNegation() {
        return True.getInstance();
    }

    @Override
    public String toString() {
        return "false";
    }
}
