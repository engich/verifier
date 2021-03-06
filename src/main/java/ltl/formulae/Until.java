package ltl.formulae;

public final class Until extends LtlFormulae {
    private final LtlFormulae left;
    private final LtlFormulae right;

    public Until(final LtlFormulae left, final LtlFormulae right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return left.isNegationNormalForm() && right.isNegationNormalForm();
    }

    @Override
    public final LtlFormulae getNegation() {
        return new Not(this);
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Until(left.getNegationNormalForm(), right.getNegationNormalForm());
        assert result.isNegationNormalForm();
        return result;
    }

    public final LtlFormulae getLeft() {
        return left;
    }

    public final LtlFormulae getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " U " + right + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Until)) {
            return false;
        } else {
            return left.equals(((Until) other).left) && right.equals(((Until) other).right);
        }
    }

    @Override
    public int hashCode() {
        return 31 * left.hashCode() + right.hashCode();
    }
}
