package automata;

import java.util.List;
import java.util.Objects;

public final class Transition {
    private final int id;
    private final String eventName;
    private final String guard;
    private final List<String> actionNames;
    private final String code;

    public Transition(final int id, final String eventName, final String guard, final List<String> actionNames, final String code) {
        this.id = id;
        this.eventName = eventName;
        this.guard = guard;
        this.actionNames = actionNames;
        this.code = code;
    }

    public final int getId() {
        return this.id;
    }

    public final String getEventName() {
        return this.eventName;
    }

    public final String getGuard() {
        return this.guard;
    }

    public final List<String> getActionNames() {
        return this.actionNames;
    }

    public final String getCode() {
        return this.code;
    }

    public String toString() {
        return "Transition(id=" + this.id + ", event=" + this.eventName + ", guard=" + this.guard + ", actions=" + this.actionNames + ", code=" + this.code + ")";
    }

    public int hashCode() {
        return Objects.hash(id, eventName, guard, actionNames, code);
    }

    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Transition)) {
            return false;
        } else {
            return id == ((Transition) other).id &&
                    eventName.equals(((Transition) other).eventName) &&
                    guard.equals(((Transition) other).guard) &&
                    actionNames.equals(((Transition) other).actionNames) &&
                    code.equals(((Transition) other).code);
        }
    }
}
