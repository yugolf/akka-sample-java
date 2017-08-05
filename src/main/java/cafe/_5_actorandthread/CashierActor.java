package cafe._5_actorandthread;

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
        private final String product;
        private final int count;

        public Order(String product, int count) {
            this.product = product;
            this.count = count;
        }

        public String getProduct() {
            return product;
        }

        public int getCount() {
            return count;
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

    private ActorRef barista = getContext().actorOf(BaristaActor.props(0), "baristaActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting akka cafe"))
                .match(Order.class, order -> {
                    barista.tell(new BaristaActor.Order(order.getProduct(), order.getCount()), getSelf()); // 「!」でメッセージを送信
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}


//import akka.actor.{Actor, ActorLogging, ActorRef, Props}

//class CashierActor extends Actor with ActorLogging {
//  import CashierActor._

//  val barista: ActorRef = context.actorOf(BaristaActor.props(0), "baristaActor")

//  def receive: Receive = {
//  	case Initialize =>
//	    log.info("starting akka cafe")
//    case Order(product, count) =>
//      barista ! BaristaActor.Order(product, count)  // 「!」でメッセージを送信
//    case Shutdown =>
//      log.info("terminating akka cafe")
//      context.system.terminate()
//    case result: OrderCompleted =>
//      log.info(s"result: ${result.message}")
//  }
//}
//
//object CashierActor {
//  val props: Props = Props[CashierActor]
//
//  // メッセージプロトコルの定義
//  case object Initialize
//  case object Shutdown
//  case class Order(product: String, count: Int)
//  case class OrderCompleted(message: String)
//}