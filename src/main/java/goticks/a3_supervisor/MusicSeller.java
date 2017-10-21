package goticks.a3_supervisor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/** 音楽チケット担当 */
class MusicSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(MusicSeller.class, () -> new MusicSeller(offset));
    }

    /** チケットのリクエスト・メッセージ */
    public static class RequestTicket {
        private final int count;

        public RequestTicket(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount; // 注文数

    public MusicSeller(int offset) {
        this.orderCount = offset;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTicket.class, order -> {
                    orderCount += order.getCount();  // 受信した注文数を加算
                    log.info("order:{}/{}", order.getCount(), orderCount);
                    if (order.getCount() > 5)
                        throw new TicketSeller.ExceededLimitException("The number of your orders: " + order.getCount());
                    getSender().tell(new BoxOffice.OrderCompleted(
                            "I'm a charge of Music events. Received your order!"), getSelf());
                })
                .build();
    }
}
