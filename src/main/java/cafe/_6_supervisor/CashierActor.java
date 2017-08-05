package cafe._6_supervisor;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class CashierActor extends AbstractActor {
    static public Props props(ActorRef kitchen) {
        return Props.create(CashierActor.class, () -> new CashierActor(kitchen));
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

    public static class Shutdown {
        public Shutdown() {
        }
    }

    public interface Order {
    }

    public static class OrderCoffee implements Order {
        private final int count;

        public OrderCoffee(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class OrderCake implements Order {
        private final int count;

        public OrderCake(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static ActorRef kitchen;

    public CashierActor(ActorRef kitchen) {
        this.kitchen = kitchen;
    }

    private ActorRef barista = getContext().actorOf(BaristaActor.props(0), "baristaActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderCoffee.class, order -> {
                    kitchen.tell(new KitchenActor.DripCoffee(order.getCount()), getSelf());
                })
                .match(OrderCake.class, order -> {
                    kitchen.tell(new KitchenActor.BakeCake(order.getCount()), getSelf());
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}


//import akka.actor.{Actor, ActorLogging, ActorRef, Props}
//
//class CashierActor(kitchen: ActorRef) extends Actor with ActorLogging {
//  import CashierActor._
//
//  def receive: Receive = {
//    case OrderCoffee(count) =>
//      kitchen ! KitchenActor.DripCoffee(count)
//    case OrderCake(count) =>
//      kitchen ! KitchenActor.BakeCake(count)
//    case result: OrderCompleted =>
//      log.info(s"result: ${result.message}")
//  }
//}

//object CashierActor {
//  def props(kitchen: ActorRef) = Props(classOf[CashierActor], kitchen)

//  // メッセージプロトコルの定義
//  case class OrderCompleted(message: String)
//
//  sealed trait Order
//  case class OrderCoffee(count: Int) extends Order
//  case class OrderCake(count: Int) extends Order
//}
