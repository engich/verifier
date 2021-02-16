package automata;

public final class Event {
    private final String name;
    private final String comment;

    public Event(final String name, final String comment) {
        this.name = name;
        this.comment = comment;
    }

    public final String getName() {
        return this.name;
    }

    public final String getComment() {
        return this.comment;
    }

    @Override
    public String toString() {
        return "Event(name=" + this.name + ", comment=" + this.comment + ")";
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + comment.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Event)) {
            return false;
        } else {
            return name.equals(((Event) other).name) && comment.equals(((Event) other).comment);
        }
    }
}
