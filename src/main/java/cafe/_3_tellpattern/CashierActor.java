package cafe._3_tellpattern;

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

    ActorRef barista = getContext().actorOf(BaristaActor.props(), "baristaActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting akka cafe"))
                .match(Order.class, order -> {
                    barista.tell(new BaristaActor.Order("Coffee", 2), getSelf()); // 「!」でメッセージを送信
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}

//class CashierActor extends Actor with ActorLogging {
//  import CashierActor._
//
//  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

//  def receive: Receive = {
//  	case Initialize =>
//	    log.info("starting akka cafe")
//    case Order =>
//      barista ! BaristaActor.Order("Coffee", 2)  // 「!」でメッセージを送信
//    case Shutdown =>
//      log.info("terminating akka cafe")
//      context.system.terminate()
//    case result: OrderCompleted =>
//      log.info(s"result: ${result.message}")
//  }
//}

//object CashierActor {
//  val props: Props = Props[CashierActor]
//
//  // メッセージプロトコルの定義
//  case object Initialize
//  case object Shutdown
//  case object Order
//  case class OrderCompleted(message: String)
//}
