package automata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Automata {
    private static final String STATE_MACHINE_TAG_NAME = "Statemachine";
    private static final String EVENT_TAG_NAME = "event";
    private static final String WIDGET_TAG_NAME = "widget";
    private static final String ATTRIBUTES_TAG_NAME = "attributes";
    private static final String INCOMING_TAG_NAME = "incoming";
    private static final String OUTGOING_TAG_NAME = "outgoing";
    private static final String CODE_TAG_NAME = "code";
    private static final String GUARD_TAG_NAME = "guard";
    private static final String ACTION_TAG_NAME = "action";

    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String COMMENT_ATTRIBUTE_NAME = "comment";
    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String TYPE_ATTRIBUTE_NAME = "type";

    private static final String STATE_TYPE_NAME = "State";
    private static final String TRANSITION_TYPE_NAME = "Transition";

    private final List<Event> eventList;
    private final List<State> stateList;
    private final List<Transition> transitionList;

    public Automata(final List<Event> eventList,
                    final List<State> stateList,
                    final List<Transition> transitionList) {
        this.eventList = eventList;
        this.stateList = stateList;
        this.transitionList = transitionList;
    }

    public static Automata fromFile(final String filename) throws IOException, ParserConfigurationException, SAXException {
        final List<Event> events = new ArrayList<>();
        final List<State> states = new ArrayList<>();
        final List<Transition> transitions = new ArrayList<>();

        final Document document = DocumentBuilderFactory.
                newInstance().
                newDocumentBuilder().
                parse(new File(filename));
        document.getDocumentElement().normalize();

        final NodeList eventElements = ((Element) document.
                getElementsByTagName(STATE_MACHINE_TAG_NAME).
                item(0)).
                getElementsByTagName(EVENT_TAG_NAME);

        for (int i = 0; i < eventElements.getLength(); ++i) {
            final Element eventElement = (Element) eventElements.item(i);
            events.add(new Event(eventElement.getAttribute(NAME_ATTRIBUTE_NAME), eventElement.getAttribute(COMMENT_ATTRIBUTE_NAME)));
        }

        final NodeList widgetElements = document.getElementsByTagName(WIDGET_TAG_NAME);

        for (int i = 0; i < widgetElements.getLength(); ++i) {
            final org.w3c.dom.Node widgetNode = widgetElements.item(i);

            if (widgetNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE) {
                continue;
            }

            final Element widgetElement = (Element) widgetNode;
            final int widgetId = Integer.parseInt(widgetElement.getAttribute(ID_ATTRIBUTE_NAME));
            final String widgetType = widgetElement.getAttribute(TYPE_ATTRIBUTE_NAME);

            if (widgetType.equals(STATE_TYPE_NAME)) {
                final Element stateWidgetAttributes = (Element) widgetElement.getElementsByTagName(ATTRIBUTES_TAG_NAME).item(0);
                final String stateWidgetName = stateWidgetAttributes.getElementsByTagName(NAME_ATTRIBUTE_NAME).item(0).getTextContent();
                final String stateWidgetType = stateWidgetAttributes.getElementsByTagName(TYPE_ATTRIBUTE_NAME).item(0).getTextContent();
                final List<Integer> stateWidgetIncoming = getConnections(stateWidgetAttributes, INCOMING_TAG_NAME);
                final List<Integer> stateWidgetOutgoing = getConnections(stateWidgetAttributes, OUTGOING_TAG_NAME);
                states.add(new State(widgetId, stateWidgetName, Integer.parseInt(stateWidgetType), stateWidgetIncoming, stateWidgetOutgoing));
            } else if (widgetType.equals(TRANSITION_TYPE_NAME)) {
                final Element transitionWidgetAttributes = (Element) widgetElement.getElementsByTagName(ATTRIBUTES_TAG_NAME).item(0);
                final Element transitionWidgetEvent = (Element) transitionWidgetAttributes.getElementsByTagName(EVENT_TAG_NAME).item(0);
                final String transitionWidgetEventName = transitionWidgetEvent.getAttribute(NAME_ATTRIBUTE_NAME);
                final String transitionWidgetCode = transitionWidgetAttributes.getElementsByTagName(CODE_TAG_NAME).item(0).getTextContent();
                final String transitionWidgetGuard = transitionWidgetAttributes.getElementsByTagName(GUARD_TAG_NAME).item(0).getTextContent();
                final List<String> actionNames = new ArrayList<>();
                final NodeList actionsElements = transitionWidgetAttributes.getElementsByTagName(ACTION_TAG_NAME);

                for (int j = 0; j < actionsElements.getLength(); ++j) {
                    final Element actionElement = (Element) actionsElements.item(j);
                    final String actionName = actionElement.getAttribute(NAME_ATTRIBUTE_NAME);
                    actionNames.add(actionName);
                }

                transitions.add(new Transition(widgetId, transitionWidgetEventName, transitionWidgetGuard, actionNames, transitionWidgetCode));
            }
        }

        return new Automata(events, states, transitions);
    }

    private static List<Integer> getConnections(final Element stateAttributes, final String tagName) {
        final List<Integer> result = new ArrayList<>();
        final NodeList elementList = stateAttributes.getElementsByTagName(tagName);

        for (int j = 0; j < elementList.getLength(); ++j) {
            final String elementId = ((Element) elementList.item(j)).getAttribute(ID_ATTRIBUTE_NAME);
            result.add(Integer.valueOf(elementId));
        }

        return result;
    }

    public final List<Event> getEventList() {
        return this.eventList;
    }

    public final List<State> getStateList() {
        return this.stateList;
    }

    public final List<Transition> getTransitionList() {
        return this.transitionList;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Automata)) {
            return false;
        } else {
            return eventList.equals(((Automata) other).eventList) &&
                    stateList.equals(((Automata) other).stateList) &&
                    transitionList.equals(((Automata) other).transitionList);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventList, stateList, transitionList);
    }
}
