package cafe._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


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

    public static class Order {
        public Order() {
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef createBarista() {
        return getContext().actorOf(BaristaActor.props(), "baristaActor");
    }

    public CashierActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting akka cafe"))
                .match(Order.class, order -> {
                    // CashierからBaristaにメッセージを送信
                    ActorRef barista = createBarista();
                    barista.tell("coffee", getSelf());            // String型
                    barista.tell(2, getSelf());                   // Integer型
                    barista.tell(new BaristaActor.Order("coffee", 2), getSelf());  // Order型
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}
