package goticks._4_askpattern;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/** スポーツチケット担当 */
class SportsSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(SportsSeller.class, () -> new SportsSeller(offset));
    }

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

    public SportsSeller(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTicket.class, order -> {
                    orderCount += order.getCount();  // 受信した注文数を加算
                    log.info("Receive your order: Sports {} tickets. The number of orders: {} ", order.getCount(), orderCount);
                    getSender().tell(new BoxOffice.OrderCompleted("I'm a charge of Sports events. Received your orders!"), getSender());
                })
                .build();
    }
}
