package cafe._7_become;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import cafe._7_become.BaristaActor.Bean;

public class CashierActor extends AbstractActor {
    static public Props props() {
        return Props.create(CashierActor.class, () -> new CashierActor());
    }

    public static class Initialize {
        public Initialize() {
        }
    }

    public static class Shutdown {
        public Shutdown() {
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

    public static class OrderCompleted {
        private final String message;

        public OrderCompleted(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public CashierActor() {
    }

    private ActorRef barista = getContext().actorOf(BaristaActor.props(), "baristaActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting akka cafe"))
                .match(Open.class, open -> {
                    barista.tell(new BaristaActor.Open(open.getBean()), getSelf());
                })
                .match(Close.class, o -> {
                    barista.tell(new BaristaActor.Close(), getSelf());
                })
                .match(Order.class, order -> {
                    barista.tell(new BaristaActor.Order(order.getProduct(), order.getCount()), getSelf()); // 「!」でメッセージを送信
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}
