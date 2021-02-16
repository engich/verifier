package automata;

import javafx.util.Pair;
import ltl.formulae.LtlFormulae;
import ltl.formulae.Sym;
import ltl.formulae.Var;

import java.util.*;

public class BA {
    private final Set<String> atomicPropositions;
    private final Set<Node> states;
    private final Set<Node> start;
    private final Set<Node> end;
    private final Map<Node, Map<Sym, List<Node>>> delta;

    public BA(final Set<String> atomicPropositions, final Set<Node> states, final Set<Node> start,
              final Set<Node> end, final Map<Node, Map<Sym, List<Node>>> delta) {
        this.atomicPropositions = atomicPropositions;
        this.states = states;
        this.start = start;
        this.end = end;
        this.delta = delta;
    }

    private static void addTransition(Map<Node, Map<Sym, List<Node>>> delta,
                                      Node start,
                                      Sym sym,
                                      Node end) {
        final List<Node> endNodes = delta.
                computeIfAbsent(start, x -> new LinkedHashMap<>()).
                computeIfAbsent(sym, x -> new ArrayList<>());
        endNodes.add(end);
    }

    public static BA build(final Automata automata) {
        final Set<String> atomicPropositions = new LinkedHashSet<>();

        automata.getStateList().stream().
                map(State::getName).
                forEach(atomicPropositions::add);
        automata.getEventList().stream().
                map(Event::getName).
                forEach(atomicPropositions::add);
        automata.getTransitionList().stream().
                map(Transition::getEventName).
                forEach(atomicPropositions::add);
        automata.getTransitionList().stream()
                .flatMap(x -> x.getActionNames().stream())
                .forEach(atomicPropositions::add);

        final Set<Node> states = new LinkedHashSet<>();
        final Set<Node> start = new LinkedHashSet<>();
        final Map<Integer, Pair<Node, Node>> nodesMap = new LinkedHashMap<>();
        final Map<Node, Map<Sym, List<Node>>> delta = new LinkedHashMap<>();

        for (final State state : automata.getStateList()) {
            final Node fromNode = new Node(state.getName() + "_enter");
            final Node toNode = new Node(state.getName());
            nodesMap.put(state.getId(), new Pair<>(fromNode, toNode));
            states.add(fromNode);
            states.add(toNode);

            if (state.getType() == 1) {
                start.add(fromNode);
            }
        }

        for (Pair<Node, Node> pair : nodesMap.values()) {
            addTransition(delta,
                    pair.getKey(),
                    new Sym(new Var(pair.getValue().getName())),
                    pair.getValue());
        }

        for (Transition transition : automata.getTransitionList()) {
            final State startState = automata.getStateList().stream().
                    filter(x -> x.getOutgoing().contains(transition.getId())).
                    findFirst().
                    get();
            final State endState = automata.getStateList().stream().
                    filter(x -> x.getIncoming().contains(transition.getId())).
                    findFirst().
                    get();
            final Node fromNode = nodesMap.get(startState.getId()).getValue();
            final Node toNode = nodesMap.get(endState.getId()).getKey();
            final Var eventVar = new Var(transition.getEventName());
            final Var fromVar = new Var(fromNode.getName());
            final Set<LtlFormulae> formulaeSet = new LinkedHashSet<>();
            formulaeSet.add(fromVar);
            formulaeSet.add(eventVar);

            final Sym eventLabel = new Sym(formulaeSet);
            final Node eventEnd;

            if (transition.getActionNames().isEmpty()) {
                eventEnd = toNode;
            } else {
                eventEnd = new Node("temp_" + transition.getEventName());
                states.add(eventEnd);
            }

            addTransition(delta, fromNode, eventLabel, eventEnd);

            Node eventStart = eventEnd;

            for (int i = 0; i < transition.getActionNames().size(); ++i) {
                final Var actionNameVar = new Var(transition.getActionNames().get(i));
                final Node endNode;

                if (i == transition.getActionNames().size() - 1) {
                    endNode = toNode;
                } else {
                    endNode = new Node("temp_" + transition.getActionNames().get(i));
                    states.add(endNode);
                }

                final Set<LtlFormulae> symSet = new LinkedHashSet<>();
                symSet.add(fromVar);
                symSet.add(eventVar);
                symSet.add(actionNameVar);

                addTransition(delta, eventStart, new Sym(symSet), endNode);
                eventStart = endNode;
            }
        }

        return new BA(atomicPropositions, states, start, states, delta);
    }

    public static BA build(final GBA gba) {
        final Set<Node> nodes = new LinkedHashSet<>();
        final Set<Node> start = new LinkedHashSet<>();
        final Set<Node> end = new LinkedHashSet<>();
        final Map<Node, Map<Sym, List<Node>>> delta = new LinkedHashMap<>();

        for (final Node node : gba.getStates()) {
            for (int i = 1; i < gba.getEnd().size() + 1; ++i) {
                final Node countingNode = new InnerNode(node, i);
                nodes.add(countingNode);

                if (i == 1 && gba.getStart().contains(node)) {
                    start.add(countingNode);
                }

                if (i == 1 && gba.getEnd().get(0).contains(node)) {
                    end.add(countingNode);
                }
            }
        }

        for (final Node state : gba.getStates()) {
            final Sym sym = gba.getSyms().get(state);
            final List<Node> nodeList = gba.getDelta().getOrDefault(state, Collections.singletonList(state));

            for (int i = 0; i < gba.getEnd().size(); ++i) {
                final int n1 = i + 1;
                final int n2 = !gba.getEnd().get(i).contains(state) ? n1 : (n1 % gba.getEnd().size()) + 1;

                for (final Node node : nodeList) {
                    addTransition(delta,
                            new InnerNode(state, n1),
                            sym,
                            new InnerNode(node, n2));
                }
            }
        }

        return new BA(gba.getAtomicPropositions(), nodes, start, end, delta);
    }

    public final Set<String> getAtomicPropositions() {
        return this.atomicPropositions;
    }

    public final Set<Node> getStates() {
        return this.states;
    }

    public final Set<Node> getStart() {
        return this.start;
    }

    public final Set<Node> getEnd() {
        return this.end;
    }

    public final Map<Node, Map<Sym, List<Node>>> getDelta() {
        return this.delta;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof BA)) {
            return false;
        } else {
            return atomicPropositions.equals(((BA) other).atomicPropositions) &&
                    states.equals(((BA) other).states) &&
                    start.equals(((BA) other).start) &&
                    end.equals(((BA) other).end) &&
                    delta.equals(((BA) other).delta);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicPropositions, states, start, end, delta);
    }

    final static class InnerNode extends Node {
        private final Node node;
        private final int id;

        public InnerNode(final Node node, final int id) {
            super(node.getName(), node.getIncoming(), node.getNow(), node.getNext());
            this.node = node;
            this.id = id;
        }

        public final Node getNode() {
            return this.node;
        }

        public final int getId() {
            return this.id;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof InnerNode)) {
                return false;
            } else {
                return this.node.equals(((InnerNode) other).node) && this.id == ((InnerNode) other).id;
            }
        }

        @Override
        public int hashCode() {
            return this.node.hashCode() * 31 + this.id;
        }

        public String toString() {
            return "InnerNode(name=" + this.getName() + ", id=" + this.id + ')';
        }
    }
}
