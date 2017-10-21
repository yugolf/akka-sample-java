package goticks.a4_become1;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/** チケット販売員 */
class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    /** 注文メッセージ */
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

    /** オープンメッセージ */
    public static class Open {
        private final EventType eventType;

        public Open(EventType eventType) {
            this.eventType = eventType;
        }

        public EventType getEventType() {
            return eventType;
        }
    }

    /** クローズメッセージ */
    public static class Close {
        public Close() {
        }
    }

    /** イベントの種類 */
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

    /** 注文数 */
    private int orderCount = 0;

    final Receive open(final EventType eventType) {
        // オープ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.getCount();
                    log.info("order: {}/{}, sum: {}",
                            order.getEvent(), order.getCount(), orderCount);
                    getSender().tell(new BoxOffice.OrderCompleted("Received your order:" + eventType.name),
                            getSelf());
                })
                .match(Close.class, close -> {
                    log.info("-> Close");
                    getContext().unbecome();
                })
                .build();
    }

    final Receive close() {
        // クローズ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    log.info("I'm closed.");
                })
                .match(Open.class, open -> {
                    log.info("-> Open");
                    getContext().become(open(open.eventType));
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return close();
    }  // 初期状態はクローズ

    public TicketSeller() {
    }
}
