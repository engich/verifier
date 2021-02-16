package ltl.formulae;

public final class True extends LtlFormulae {
    private static final True INSTANCE;

    static {
        INSTANCE = new True();
    }

    private True() {
    }

    public static True getInstance() {
        return INSTANCE;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return true;
    }

    @Override
    public final LtlFormulae getNegation() {
        return False.getInstance();
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = this;
        assert result.isNegationNormalForm();
        return result;
    }

    @Override
    public String toString() {
        return "true";
    }
}
