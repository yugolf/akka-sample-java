package goticks._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class TicketSeller3 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller3.class, () -> new TicketSeller3());
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

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller3() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::receiveString)
                .match(Integer.class, this::receiveInteger)
                .matchAny(this::receiveAny)
                .build();
    }

    // String 型の場合
    private void receiveString(String msg) {
        log.info("Received String message: {}", msg);
    }

    // Integer 型の場合
    private void receiveInteger(Integer msg) {
        log.info("Received Integer message: {}", msg);
    }

    // それ以外の場合
    private void receiveAny(Object msg) {
        log.info("received unknown message");
    }
}
