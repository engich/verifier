package automata;

import ltl.formulae.LtlFormulae;

import java.util.HashSet;
import java.util.Set;

public class Node {
    private final String name;
    private final Set<Node> incoming;
    private final Set<LtlFormulae> now;
    private final Set<LtlFormulae> next;

    public Node(final String name, final Set<Node> incoming, final Set<LtlFormulae> now, final Set<LtlFormulae> next) {
        this.name = name;
        this.incoming = incoming;
        this.now = now;
        this.next = next;
    }

    public Node(final String name) {
        this.name = name;
        this.incoming = new HashSet<>();
        this.now = new HashSet<>();
        this.next = new HashSet<>();
    }

    public final String getName() {
        return this.name;
    }

    public final Set<Node> getIncoming() {
        return this.incoming;
    }

    public final Set<LtlFormulae> getNow() {
        return this.now;
    }

    public final Set<LtlFormulae> getNext() {
        return this.next;
    }

    @Override
    public String toString() {
        return "Node(name=" + this.name + ')';
    }
}