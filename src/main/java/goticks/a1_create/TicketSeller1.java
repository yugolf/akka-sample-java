package goticks._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TicketSeller1 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller1.class, () -> new TicketSeller1());
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

    public TicketSeller1() {
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                // String 型のメッセージを受信した場合
                .match(String.class, msg-> log.info("Received String message: {}", msg))
                // Int 型のメッセージを受信した場合
                .match(Integer.class, msg -> log.info("Received Integer message: {}", msg))
                // String 型、Integer 型以外のメッセージを受信した場合
                .matchAny(msg -> log.info("Received unknown message."))
                .build();
    }
}
