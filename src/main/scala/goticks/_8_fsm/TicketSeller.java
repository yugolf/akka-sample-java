package goticks._8_fsm;


import akka.actor.AbstractFSMWithStash;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import static goticks._8_fsm.State.Break;
import static goticks._8_fsm.State.Open;
import static goticks._8_fsm.State.Close;


// states
enum State {
    Close, Open, Break
}

// state data
interface Data {
}

final class StateData implements Data {
    private final int orderCount;

    public StateData(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getOrderCount() {
        return orderCount;
    }
}

// チケット販売員
public class TicketSeller extends AbstractFSMWithStash<State, Data> {
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    {
        startWith(Close, new StateData(0));


        when(Open,
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            final int count = state.getOrderCount() + order.getCount();
                            log.info("Receive your order: {}, {}. The number of orders: {} ", order.getEvent(), order.getCount(), count);
                            getSender().tell(new BoxOffice.OrderCompleted("Received your order."), getSelf());
                            return stay().using(new StateData(count));
                        }
                ).event(Close.class, StateData.class, (close, state) -> goTo(Close))
        );

        onTransition(
                matchState(Open, Break, () -> {
                }).state(Break, Open, () -> {
                    unstashAll();
                }).state(Open, Close, () -> {
                }));

        when(Break,
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            log.info("I'm closed.");
                            stash();
                            return stay();
                        }).event(Open.class, StateData.class, (order, state) -> goTo(Open)));

        whenUnhandled(
                matchEvent(Order.class, StateData.class,
                        (order, state) -> {
                            log.info("receive unhandled order. [{}, {}]", order, state);
                            stash();
                            return stay();
                        }).
                        anyEvent((event, state) -> {
                            log.info("receive unhandled message.");
                            return stay();
                        }));

        initialize();
    }

    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    public static class Order {
        private final String event;
        private final int count;

        public Order(String event, int count) {
            this.event = event;
            this.count = count;
        }

        public String getEvent() {
            return event;
        }

        public int getCount() {
            return count;
        }
    }

    public static class Open {
        private final EventType eventType;

        public Open(EventType eventType) {
            this.eventType = eventType;
        }

        public EventType getEventType() {
            return eventType;
        }
    }

    public static class Close {
        public Close() {
        }
    }

    // イベントの種類
    public static class EventType {
        private final String name;

        public EventType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public TicketSeller() {
    }
}
