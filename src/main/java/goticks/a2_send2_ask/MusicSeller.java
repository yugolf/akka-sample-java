package goticks._4_askpattern;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/** 音楽チケット担当 */
class MusicSeller extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(MusicSeller.class, () -> new MusicSeller(offset));
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

    /** 注文数 */
    private int orderCount;

    public MusicSeller(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestTicket.class, order -> {
                    orderCount += order.getCount();  // 受信した注文数を加算
                    log.info("Receive your order: Music {} tickets. The number of orders: {} ", order.getCount(), orderCount);
                    getSender().tell(new BoxOffice.OrderCompleted("I'm a charge of Music events. Received your orders!"), getSelf());
                })
                .build();
    }
}
