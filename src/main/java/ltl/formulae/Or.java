package ltl.formulae;

public final class Or extends LtlFormulae {
    private final LtlFormulae left;
    private final LtlFormulae right;

    public Or(final LtlFormulae left, final LtlFormulae right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public final boolean isNegationNormalForm() {
        return left.isNegationNormalForm() && right.isNegationNormalForm();
    }

    @Override
    public final LtlFormulae getNegationNormalForm() {
        final LtlFormulae result = new Or(left.getNegationNormalForm(), right.getNegationNormalForm());
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
        return "(" + left + " || " + right + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Or)) {
            return false;
        } else {
            return left.equals(((Or) other).left) && right.equals(((Or) other).right);
        }
    }

    @Override
    public int hashCode() {
        return 31 * left.hashCode() + right.hashCode();
    }
}
