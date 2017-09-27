package goticks._7_become;


import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class TicketSeller extends AbstractActorWithStash {
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

    public static class Open {
        private final EventType eventType;

        public Open(EventType eventType) {
            this.eventType = eventType;
        }

        public EventType getEventType() {
            return eventType;
        }
    }

    public static class Close {
        public Close() {
        }
    }

    //イベントの種類
    public static class EventType {
        private final String name;

        public EventType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount = 0; // 注文数

    final Receive open(final EventType eventType) {
        // オープ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.getCount();
                    log.info("Receive your order: {}, {}. The number of orders: {} ", order.getEvent(), order.getCount(), orderCount);
                    getSender().tell(new BoxOffice.OrderCompleted("Received your order. Today's coffee is " + eventType.name + "."), getSelf());
                })
                .match(Close.class, close-> {
                    getContext().unbecome();
                })
                .build();
    }

    final Receive close() {
        // クローズ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    stash();  // オーダーを退避しておく
                    log.info("I'm closed.");
                })
                .match(Open.class, open -> {
                    unstashAll();
                    getContext().become(open(open.eventType));
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return close();
    }

    public TicketSeller() {
    }
}
