package cafe._2_patternmatch;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class CashierActor extends AbstractActor{
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

    ActorRef barista = getContext().actorOf(BaristaActor.props(), "baristaActor");

    public CashierActor() {
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(Initialize.class, initialize -> log.info("starting akka cafe"))
            .match(Order.class, order -> {
              // CashierからBaristaにメッセージを送信
              barista.tell(1, getSelf());  // Int型
              barista.tell(2, getSelf());  // Int型
              barista.tell(3, getSelf());  // Int型
              barista.tell(new BaristaActor.Order("Coffee", 2), getSelf());  /// メッセージをケースクラスで送信
              barista.tell(new BaristaActor.Order("Cake", 1), getSelf());  // メッセージをケースクラスで送信
            })
            .match(Shutdown.class, shutdown -> {
              log.info("terminating akka cafe");
              getContext().getSystem().terminate();
            })
            .build();
  }
}


//  def receive: Receive = {
//  	case Initialize =>
//	    log.info("starting akka cafe")
//    case Order =>
//      barista ! 1                   // Int型
//      barista ! 2                   // Int型
//      barista ! 3                   // Int型
//
//      barista ! BaristaActor.Order("Coffee", 2)  // メッセージをケースクラスで送信
//      barista ! BaristaActor.Order("Cake", 1)    // メッセージをケースクラスで送信
//    case Shutdown =>
//      log.info("terminating akka cafe")
//      context.system.terminate()
//  }

//object CashierActor {
//  val props: Props = Props[CashierActor]
//
//  // メッセージプロトコルの定義
//  case object Initialize
//  case object Shutdown
//  case object Order
//}
