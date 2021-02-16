package automata;

import com.google.common.collect.Sets;
import ltl.formulae.*;
import utils.QuadConsumer;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class GBA {
    private final Set<String> atomicPropositions;
    private final List<Node> states;
    private final Map<Node, Sym> syms;
    private final Map<Node, List<Node>> delta;
    private final List<Node> start;
    private final List<Set<Node>> end;

    public GBA(final Set<String> atomicPropositions, final List<Node> states,
               final Map<Node, Sym> syms, final Map<Node, List<Node>> delta,
               final List<Node> start, final List<Set<Node>> end) {
        this.atomicPropositions = atomicPropositions;
        this.states = states;
        this.syms = syms;
        this.delta = delta;
        this.start = start;
        this.end = end;
    }

    public static GBA build(final LtlFormulae ltlFormulae, final Set<String> atomicPropositions) {
        final LtlFormulae ltlFormula = ltlFormulae.getNegationNormalForm();
        final Node initNode = new Node("init");
        final List<Node> nodeList = new ArrayList<>();
        final Integer[] nodeId = {0};

        final Supplier<String> newNodeId = () -> "node_" + (++nodeId[0]);

        final QuadConsumer<Set<LtlFormulae>, Set<LtlFormulae>, Set<LtlFormulae>, Set<Node>> expand = new QuadConsumer<Set<LtlFormulae>, Set<LtlFormulae>, Set<LtlFormulae>, Set<Node>>() {
            @Override
            public void apply(Set<LtlFormulae> current,
                              Set<LtlFormulae> old,
                              Set<LtlFormulae> next,
                              Set<Node> incoming) {
                if (current.isEmpty()) {
                    final Optional<Node> r = nodeList.stream().
                            filter(x -> x.getNext().equals(next) && x.getNow().equals(old)).
                            findFirst();

                    if (r.isPresent()) {
                        r.get().getIncoming().addAll(incoming);
                        return;
                    } else {
                        final Node q = new Node(newNodeId.get());
                        nodeList.add(q);
                        q.getIncoming().addAll(incoming);
                        q.getNow().addAll(old);
                        q.getNext().addAll(next);

                        final Set<Node> nodeSet = new LinkedHashSet<>();
                        nodeSet.add(q);

                        this.apply(q.getNext(),
                                new LinkedHashSet<>(),
                                new LinkedHashSet<>(),
                                nodeSet);
                    }
                } else {
                    final LtlFormulae formulae = current.stream().
                            findFirst().
                            get();
                    final Set<LtlFormulae> currentSet = new LinkedHashSet<>(current);
                    currentSet.remove(formulae);

                    final Set<LtlFormulae> oldSet = new LinkedHashSet<>(old);
                    oldSet.add(formulae);

                    if (check(formulae)) {
                        if (formulae instanceof False || oldSet.contains(formulae.getNegation())) {
                            return;
                        }

                        this.apply(currentSet, oldSet, next, incoming);
                    } else if (formulae instanceof And) {
                        final Set<LtlFormulae> ltlFormulaeSet = new LinkedHashSet<>();
                        ltlFormulaeSet.add(((And) formulae).getLeft());
                        ltlFormulaeSet.add(((And) formulae).getRight());
                        ltlFormulaeSet.removeAll(oldSet);
                        currentSet.addAll(ltlFormulaeSet);

                        this.apply(currentSet, oldSet, next, incoming);
                    } else if (formulae instanceof Next) {
                        final Set<LtlFormulae> nextSet = new LinkedHashSet<>(next);
                        nextSet.add(((Next) formulae).getFormulae());

                        this.apply(currentSet, oldSet, nextSet, incoming);
                    } else if (formulae instanceof Or || formulae instanceof Until || formulae instanceof Release) {
                        final Set<LtlFormulae> currentSet1 = new LinkedHashSet<>(currentSet);
                        final Set<LtlFormulae> bufSet1 = transform1(formulae);
                        bufSet1.removeAll(oldSet);
                        currentSet1.addAll(bufSet1);

                        final Set<LtlFormulae> nextSet1 = new LinkedHashSet<>(next);
                        nextSet1.addAll(transform2(formulae));

                        this.apply(currentSet1, oldSet, nextSet1, incoming);

                        final Set<LtlFormulae> currentSet2 = new LinkedHashSet<>(currentSet);
                        final Set<LtlFormulae> bufSet2 = transform3(formulae);
                        bufSet2.removeAll(oldSet);
                        currentSet2.addAll(bufSet2);

                        this.apply(currentSet2, oldSet, next, incoming);
                    } else {
                        throw new AssertionError("not in negative normal form");
                    }
                }
            }
        };

        final Set<Node> nodeSet = new LinkedHashSet<>();
        nodeSet.add(initNode);

        final Set<LtlFormulae> ltlFormulaeSet = new LinkedHashSet<>();
        ltlFormulaeSet.add(ltlFormula);

        expand.apply(ltlFormulaeSet, new LinkedHashSet<>(), new LinkedHashSet<>(), nodeSet);

        return inner(ltlFormula, nodeList, initNode, atomicPropositions);
    }

    private static GBA inner(final LtlFormulae ltlFormula, final List<Node> nodes,
                             final Node init, final Set<String> atomicPropositions) {
        final Set<Var> varSet = atomicPropositions.stream().
                map(Var::new).
                collect(Collectors.toSet());
        final Map<Node, Sym> labels = new LinkedHashMap<>();

        for (final Node node : nodes) {
            final Set<LtlFormulae> min = new LinkedHashSet<>();
            final Set<LtlFormulae> max = new LinkedHashSet<>();
            Sets.intersection(node.getNow(), varSet).copyInto(min);

            varSet.stream().
                    filter(x -> !node.getNow().contains(new Not(x))).
                    forEach(max::add);
            labels.put(node, new Sym(min, max));
        }

        final Map<Node, List<Node>> delta = new LinkedHashMap<>();

        for (final Node node : nodes) {
            for (final Node fromNode : node.getIncoming()) {
                if (fromNode.equals(init)) {
                    continue;
                }

                final List<Node> nodeList = new ArrayList<>();
                nodeList.add(node);

                delta.merge(fromNode, nodeList,
                        (t, u) -> Stream.concat(t.stream(), u.stream()).
                                collect(Collectors.toList()));
            }
        }

        final List<Node> startNode = nodes.stream().
                filter(x -> x.getIncoming().contains(init)).
                collect(Collectors.toList());
        final List<Set<Node>> end = new ArrayList<>();

        for (final LtlFormulae formulae : ltlFormula.getClosure()) {
            if (!(formulae instanceof Until)) {
                continue;
            }

            final Set<Node> endNodes = nodes.stream().
                    filter(x -> x.getNow().contains(((Until) formulae).getRight()) || !x.getNow().contains(formulae)).
                    collect(Collectors.toSet());
            end.add(endNodes);
        }

        return new GBA(atomicPropositions, nodes, labels, delta, startNode, end);
    }

    private static Set<LtlFormulae> transform1(final LtlFormulae ltlFormulae) {
        final Set<LtlFormulae> result = new LinkedHashSet<>();

        if (ltlFormulae instanceof Until) {
            result.add(((Until) ltlFormulae).getLeft());
        } else if (ltlFormulae instanceof Release) {
            result.add(((Release) ltlFormulae).getRight());
        } else if (ltlFormulae instanceof Or) {
            result.add(((Or) ltlFormulae).getRight());
        }

        return result;
    }

    private static Set<LtlFormulae> transform2(final LtlFormulae ltlFormulae) {
        final Set<LtlFormulae> result = new LinkedHashSet<>();

        if (ltlFormulae instanceof Until) {
            result.add(ltlFormulae);
        } else if (ltlFormulae instanceof Release) {
            result.add(ltlFormulae);
        } else if (ltlFormulae instanceof Or) {
            result.clear();
        }

        return result;
    }

    private static Set<LtlFormulae> transform3(final LtlFormulae ltlFormulae) {
        final Set<LtlFormulae> result = new LinkedHashSet<>();

        if (ltlFormulae instanceof Until) {
            result.add(((Until) ltlFormulae).getRight());
        } else if (ltlFormulae instanceof Release) {
            result.add(((Release) ltlFormulae).getLeft());
            result.add(((Release) ltlFormulae).getRight());
        } else if (ltlFormulae instanceof Or) {
            result.add(((Or) ltlFormulae).getLeft());
        }

        return result;
    }

    private static boolean check(final LtlFormulae ltlFormulae) {
        if (ltlFormulae instanceof False || ltlFormulae instanceof True) {
            return true;
        }
        if (ltlFormulae instanceof Var) {
            return true;
        }
        return ltlFormulae instanceof Not && ((Not) ltlFormulae).getFormulae() instanceof Var;
    }

    public final Set<String> getAtomicPropositions() {
        return this.atomicPropositions;
    }

    public final List<Node> getStates() {
        return this.states;
    }

    public final Map<Node, Sym> getSyms() {
        return this.syms;
    }

    public final Map<Node, List<Node>> getDelta() {
        return this.delta;
    }

    public final List<Node> getStart() {
        return this.start;
    }

    public final List<Set<Node>> getEnd() {
        return this.end;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof GBA)) {
            return false;
        } else {
            return atomicPropositions.equals(((GBA) other).atomicPropositions) &&
                    states.equals(((GBA) other).states) &&
                    syms.equals(((GBA) other).syms) &&
                    delta.equals(((GBA) other).delta) &&
                    start.equals(((GBA) other).start) &&
                    end.equals(((GBA) other).end);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicPropositions, states, syms, delta, start, end);
    }
}
