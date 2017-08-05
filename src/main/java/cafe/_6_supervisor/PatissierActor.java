package cafe._6_supervisor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class PatissierActor extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(PatissierActor.class, () -> new PatissierActor(offset));
    }

    public static class BakeCake {
        private final int count;

        public BakeCake(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount; // 注文数

    public PatissierActor(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(BakeCake.class, order -> {
                    orderCount += order.getCount();  // 受信した注文数を加算
                    log.info("Receive your order: Bake {} pieces of cake. The number of orders: {} ", order.getCount(), orderCount);
                    if (order.getCount() > 3)
                        throw new KitchenActor.ExceededLimitException("The number of your orders: " + order.getCount());
                    getSender().tell(new CashierActor.OrderCompleted("I'm a Patissier. Received your orders!"), getSender());
                })
                .build();
    }
}
