package ltl.formulae;

import java.util.Collections;
import java.util.Set;

public class Sym {
    private final Set<LtlFormulae> l;
    private final Set<LtlFormulae> h;

    public Sym(final Set<LtlFormulae> l, final Set<LtlFormulae> h) {
        this.l = l;
        this.h = h;
    }

    public Sym(final Set<LtlFormulae> ltlFormulaeSet) {
        this(ltlFormulaeSet, ltlFormulaeSet);
    }

    public Sym(final LtlFormulae ltlFormulae) {
        this(Collections.singleton(ltlFormulae));
    }

    public boolean subsetOf(final Sym sym) {
        return l.containsAll(sym.l) && sym.h.containsAll(h);
    }

    public Set<LtlFormulae> getL() {
        return l;
    }

    public Set<LtlFormulae> getH() {
        return h;
    }

    @Override
    public String toString() {
        return "Sym(" + l + ", " + h + ")";
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Sym)) {
            return false;
        } else {
            return l.equals(((Sym) other).l) && h.equals(((Sym) other).h);
        }
    }

    @Override
    public int hashCode() {
        return 31 * l.hashCode() + h.hashCode();
    }
}
