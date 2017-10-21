package goticks._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// AbstractActorを継承
public class BoxOffice extends AbstractActor {
    // アクターを生成するためにファクトリメソッドを定義
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

    // ログ出力用にLoggingAdapterを定義
    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private ActorRef createTicketSeller1() {
        return getContext().actorOf(TicketSeller1.props(), "ticketSeller1");
    }
    private ActorRef createTicketSeller2() {
        return getContext().actorOf(TicketSeller2.props(), "ticketSeller2");
    }

    // 引数なしのコンストラクター
    public BoxOffice() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> {
                    // BoxOfficeからTicketSeller1にメッセージを送信
                    ActorRef ticketSeller = createTicketSeller1();
                    ticketSeller.tell("RHCP", getSelf());            // String型
                    ticketSeller.tell(2, getSelf());                 // Integer型
                    ticketSeller.tell(new TicketSeller1.Order("RHCP", 2), getSelf());  // Order型

                    // BoxOfficeからTicketSeller2にメッセージを送信
                    ActorRef ticketSeller2 = createTicketSeller2();
                    ticketSeller2.tell("RHCP", getSelf());            // String型
                    ticketSeller2.tell(2, getSelf());                 // Integer型
                    ticketSeller2.tell(new TicketSeller1.Order("RHCP", 2), getSelf());  // Order型
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}
