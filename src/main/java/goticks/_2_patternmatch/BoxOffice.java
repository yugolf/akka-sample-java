package goticks._2_patternmatch;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class BoxOffice extends AbstractActor {
    static public Props props() {
        return Props.create(BoxOffice.class, () -> new BoxOffice());
    }

    public static class Initialize {
        public Initialize() {
        }
    }

    public static class Shutdown {
        public Shutdown() {
        }
    }

    public static class Order {
        public Order() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");

    public BoxOffice() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> {
                    // BoxOfficeからTicketSellerにメッセージを送信
                    ticketSeller.tell(1, getSelf());  // Int型
                    ticketSeller.tell(2, getSelf());  // Int型
                    ticketSeller.tell(3, getSelf());  // Int型
                    ticketSeller.tell(new TicketSeller.Order("RHCP", 2), getSelf());  /// メッセージをケースクラスで送信
                    ticketSeller.tell(new TicketSeller.Order("X", 1), getSelf());  // メッセージをケースクラスで送信
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}
