package processing;

import automata.BA;
import automata.Node;
import com.google.common.collect.Streams;
import javafx.util.Pair;
import ltl.formulae.Sym;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Answer {
    private final boolean holds;
    private final List<Sym> cePath;
    private final int ind;

    public Answer(final boolean holds, final List<Sym> cePath, final int ind) {
        this.holds = holds;
        this.cePath = cePath;
        this.ind = ind;
    }

    public Answer(final boolean holds) {
        this.holds = holds;
        this.cePath = null;
        this.ind = 0;
    }

    public static Answer build(final BA ba, final BA ltlBa) {
        final List<Pair<Node, Node>> verticesPath = new ArrayList<>();
        final Set<Pair<Node, Node>> pathSet = new LinkedHashSet<>();
        final List<Sym> transitions = new ArrayList<>();
        final Set<Pair<Node, Node>> used = new LinkedHashSet<>();
        final List<Sym> transitionsRepeated = new ArrayList<>();
        final boolean[] foundPath = {false};
        final List<Sym>[] path = new List[]{null};
        final Integer[] cycleStartIndex = {null};


        final Consumer<Pair<Node, Node>> inner1 = new Consumer<Pair<Node, Node>>() {
            @Override
            public void accept(Pair<Node, Node> nodePair) {
                if (foundPath[0]) {
                    return;
                }

                used.add(nodePair);
                final Map<Sym, List<Node>> baMap = ba.getDelta().
                        getOrDefault(nodePair.getKey(), transform(nodePair.getKey()));
                final Map<Sym, List<Node>> ltlMap = ltlBa.getDelta().
                        getOrDefault(nodePair.getValue(), transform(nodePair.getValue()));

                for (final Sym baSym : baMap.keySet()) {
                    for (final Sym ltlSym : ltlMap.keySet()) {
                        if (baSym.subsetOf(ltlSym)) {
                            final List<Node> baNodes = baMap.
                                    getOrDefault(baSym, new ArrayList<>());
                            final List<Node> ltlNodes = ltlMap.
                                    getOrDefault(ltlSym, new ArrayList<>());

                            for (final Node baNode : baNodes) {
                                for (final Node ltlNode : ltlNodes) {
                                    final Pair<Node, Node> pair = new Pair<>(baNode, ltlNode);

                                    if (pathSet.contains(pair)) {
                                        foundPath[0] = true;
                                        path[0] = Streams.concat(transitions.stream(), transitionsRepeated.stream(), Stream.of(baSym)).
                                                collect(Collectors.toList());
                                        cycleStartIndex[0] = verticesPath.indexOf(pair);
                                        return;
                                    }

                                    if (!used.contains(pair)) {
                                        transitionsRepeated.add(baSym);

                                        accept(pair);
                                        transitionsRepeated.remove(transitionsRepeated.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        final Consumer<Pair<Node, Node>> inner2 = new Consumer<Pair<Node, Node>>() {
            @Override
            public void accept(Pair<Node, Node> nodePair) {
                if (foundPath[0]) {
                    return;
                }

                pathSet.add(nodePair);
                verticesPath.add(nodePair);
                final Map<Sym, List<Node>> baMap = ba.getDelta().
                        getOrDefault(nodePair.getKey(), transform(nodePair.getKey()));
                final Map<Sym, List<Node>> ltlMap = ltlBa.getDelta().
                        getOrDefault(nodePair.getValue(), transform(nodePair.getValue()));

                for (final Sym baSym : baMap.keySet()) {
                    for (final Sym ltlSym : ltlMap.keySet()) {
                        if (baSym.subsetOf(ltlSym)) {
                            final List<Node> baNodes = baMap.getOrDefault(baSym, new ArrayList<>());
                            final List<Node> ltlNodes = ltlMap.getOrDefault(ltlSym, new ArrayList<>());

                            for (final Node baNode : baNodes) {
                                for (Node ltlNode : ltlNodes) {
                                    final Pair<Node, Node> pair = new Pair<>(baNode, ltlNode);
                                    if (!pathSet.contains(pair)) {
                                        transitions.add(baSym);

                                        accept(pair);
                                        transitions.remove(transitions.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                }

                if (ba.getEnd().contains(nodePair.getKey()) && ltlBa.getEnd().contains(nodePair.getValue())) {
                    used.clear();
                    inner1.accept(nodePair);
                }

                verticesPath.remove(verticesPath.size() - 1);
                pathSet.remove(nodePair);
            }
        };

        for (final Node baStart : ba.getStart()) {
            for (final Node ltlStart : ltlBa.getStart()) {
                inner2.accept(new Pair<>(baStart, ltlStart));

                if (foundPath[0]) {
                    return new Answer(false, path[0], cycleStartIndex[0]);
                }
            }
        }

        return new Answer(true);
    }

    private static Map<Sym, List<Node>> transform(final Node o) {
        final Map<Sym, List<Node>> result = new LinkedHashMap<>();
        result.put(new Sym(new LinkedHashSet<>(),
                        new LinkedHashSet<>()),
                Collections.singletonList(o));
        return result;
    }

    public final boolean getHolds() {
        return this.holds;
    }

    public final List<Sym> getCePath() {
        return this.cePath;
    }

    public final int getInd() {
        return this.ind;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Answer)) {
            return false;
        } else {
            return holds == ((Answer) other).holds &&
                    (cePath != null && cePath.equals(((Answer) other).cePath)) &&
                    ind == ((Answer) other).ind;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(holds, cePath, ind);
    }
}
