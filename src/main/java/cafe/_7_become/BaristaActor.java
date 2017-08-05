package cafe._7_become;


import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActorWithStash {
    static public Props props() {
        return Props.create(BaristaActor.class, () -> new BaristaActor());
    }

    public static class Order {
        private final String product;
        private final int count;

        public Order(String product, int count) {
            this.product = product;
            this.count = count;
        }

        public String getProduct() {
            return product;
        }

        public int getCount() {
            return count;
        }
    }

    public static class Open {
        private final Bean bean;

        public Open(Bean bean) {
            this.bean = bean;
        }

        public Bean getBean() {
            return bean;
        }
    }

    public static class Close {
        public Close() {
        }
    }

    // コーヒー豆の種類
    public static class Bean {
        private final String name;

        public Bean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount = 0; // 注文数


    final AbstractActor.Receive open(final Bean bean) {
        // オープ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.getCount();
                    log.info("Receive your order: {}, {}. The number of orders: {} ", order.getProduct(), order.getCount(), orderCount);
                    getSender().tell(new CashierActor.OrderCompleted("Received your order. Today's coffee is " + bean.name + "."), getSelf());
                })
                .match(Close.class, c -> {
                    getContext().become(close());
                })
                .build();
    }

    final AbstractActor.Receive close() {
        // クローズ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    stash();  // オーダーを退避しておく
                    log.info("I'm closed.");
                })
                .match(Open.class, open -> {
                    unstashAll();
                    getContext().become(open(open.bean));
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return close();
    }

    public BaristaActor() {
    }
}
