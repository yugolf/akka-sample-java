package goticks._5_actorandthread;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class TicketSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(TicketSeller.class, () -> new TicketSeller(offset));
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

    private int orderCount; // 注文数

    public TicketSeller(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.count;  // 受信した注文数を加算
                    log.info("Receive your order: {}, {}. The number of orders: {}", order.getEvent(), order.getCount(), orderCount);
                    getSender().tell(new BoxOffice.OrderCompleted("Received your order."), getSender());
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}
