package ltl.formulae;

public final class Release extends LtlFormulae {
    private final LtlFormulae left;
    private final LtlFormulae right;

    public Release(final LtlFormulae left, final LtlFormulae right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return left.isNegationNormalForm() && right.isNegationNormalForm();
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Release(left.getNegationNormalForm(), right.getNegationNormalForm());
        assert result.isNegationNormalForm();
        return result;
    }

    @Override
    public final LtlFormulae getNegation() {
        return new Not(this);
    }

    public final LtlFormulae getLeft() {
        return left;
    }

    public final LtlFormulae getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " R " + right + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Release)) {
            return false;
        } else {
            return left.equals(((Release) other).left) && right.equals(((Release) other).right);
        }
    }

    @Override
    public int hashCode() {
        return 31 * left.hashCode() + right.hashCode();
    }
}
