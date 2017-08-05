package cafe._6_supervisor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(BaristaActor.class, () -> new BaristaActor(offset));
    }

    public static class DripCoffee {
        private final int count;

        public DripCoffee(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount; // 注文数

    public BaristaActor(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DripCoffee.class, order -> {
                    orderCount += order.getCount();  // 受信した注文数を加算
                    log.info("Receive your order: Drip {} cups of coffee. The number of orders: {} ", order.getCount(), orderCount);
                    getSender().tell(new CashierActor.OrderCompleted("I'm a Barista. Received your orders!"), getSender());
                })
                .build();
    }
}
