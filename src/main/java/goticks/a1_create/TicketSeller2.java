package goticks._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class TicketSeller2 extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller2.class, () -> new TicketSeller2());
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

    public TicketSeller2() {
    }

    @Override
    public Receive createReceive() {
        ReceiveBuilder builder = ReceiveBuilder.create();

        // String 型の場合
        builder.match(String.class, msg -> {
            log.info("Received String message: {}", msg);
        });

        // Integer 型の場合
        builder.match(Integer.class, msg -> {
            log.info("Received Integer message: {}", msg);
        });

        // それ以外の場合
        builder.matchAny(msg -> log.info("received unknown message"));

        // build()メソッドによりReceive 型を返す
        return builder.build();
    }
}
