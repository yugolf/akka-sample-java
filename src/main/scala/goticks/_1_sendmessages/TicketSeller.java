package goticks._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class TicketSeller extends AbstractActor {
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

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class,
                        s -> log.info("Received your order: {}", s)
                )   // String型のメッセージを受信した場合
                .match(Integer.class, s -> log.info("Received your order: {}", s))  // Int型のメッセージを受信した場合
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }

//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(String.class, this::receiveString)
//                .match(Integer.class, this::receiveInteger)
//                .matchAny(this::receiveAny)
//                .build();
//    }
//
//    private void receiveString(String msg) {
//        log.info("Received String message: {}", msg);
//    }
//
//    private void receiveInteger(Integer msg) {
//        log.info("Received String message: {}", msg);
//    }
//
//    private void receiveAny(Object msg) {
//        log.info("received unknown message");
//    }
}
