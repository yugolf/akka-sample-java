package goticks._4_askpattern;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// チケット販売員
class TicketSeller extends AbstractActor {
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
                .match(Order.class, order -> {
                    // BoxOfficeからの応答を受け取ったときの振る舞い
                    log.info("Your order has been completed. (event: {}, count: {})", order.getEvent(), order.getCount());
                    // 送信元に注文に対する調理が完了したことを返す
                    getSender().tell(new BoxOffice.OrderCompleted("ok"), getSender());
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}
