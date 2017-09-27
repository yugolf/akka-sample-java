package goticks._2_patternmatch;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// チケット販売員
class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    final int two = 2;

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
                .matchEquals(1, s -> log.info("(1) received: 1"))   // String型のメッセージを受信した場合
                .matchEquals(two, s -> log.info("(2) received: {}", two))  // Int型のメッセージを受信した場合
                .match(Integer.class, count -> count < 10, count -> log.info("(3) received: {}", count))  // 10より小さい数値（Int型）の場合
                .match(Order.class, order -> order.getEvent().equals("RHCP"), order -> { // Order型で第一引数eventが「RHSP」の場合
                    log.info("(4) event : {}", order.getEvent());
                    log.info("    count : {}", order.getCount());
                })
                .match(Order.class, order -> { // Order型で第一引数eventが「RHCP」の場合
                    String event = order.getEvent();
                    int count = order.getCount();
                    log.info("(5) Receive your order."); // ケースクラスを受信した時の振る舞い
                    log.info("    event : {}", event);
                    log.info("    count : {}", count);
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}
