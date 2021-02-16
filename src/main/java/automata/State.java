package automata;

import java.util.List;
import java.util.Objects;

public final class State {
    private final int id;
    private final String name;
    private final int type;
    private final List<Integer> incoming;
    private final List<Integer> outgoing;

    public State(final int id, final String name, final int type, final List<Integer> incoming, final List<Integer> outgoing) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.incoming = incoming;
        this.outgoing = outgoing;
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final int getType() {
        return this.type;
    }

    public final List<Integer> getIncoming() {
        return this.incoming;
    }

    public final List<Integer> getOutgoing() {
        return this.outgoing;
    }

    @Override
    public String toString() {
        return "State(id=" + this.id + ", name=" + this.name + ", type=" + this.type + ", incoming=" + this.incoming + ", outgoing=" + this.outgoing + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, incoming, outgoing);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof State)) {
            return false;
        } else {
            return id == ((State) other).id &&
                    name.equals(((State) other).name) &&
                    type == ((State) other).type &&
                    incoming.equals(((State) other).incoming) &&
                    outgoing.equals(((State) other).outgoing);
        }
    }
}
