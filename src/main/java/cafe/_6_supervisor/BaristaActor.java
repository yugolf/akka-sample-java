package cafe._6_supervisor;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActor {
  static public Props props(int offset) {
    return Props.create(BaristaActor.class, () -> new BaristaActor(offset));
  }

  public static class DripCoffee {
    private final int count;

    public DripCoffee(int count) {
      this.count = count;
    }

    public int getCount() {
      return count;
    }
  }

  private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private int orderCount; // 注文数

  public BaristaActor(int offset) {
    this.orderCount = offset;
  }


  @Override
  public Receive createReceive() {
    return receiveBuilder()
            .match(DripCoffee.class, order -> {
              orderCount += order.getCount();  // 受信した注文数を加算
              log.info("Receive your order: Drip {} cups of coffee. The number of orders: {} ", order.getCount(), orderCount);
              getSender().tell(new CashierActor.OrderCompleted("I'm a Barista. Received your orders!"), getSender());
            })
            .build();
  }
}


//import akka.actor.{Actor, ActorLogging, Props}

//// バリスタアクター
//class BaristaActor(offset: Int) extends Actor with ActorLogging {
//  import BaristaActor._

//  private var orderCount = offset // 注文数
//  override def receive: Receive = {
//    case DripCoffee(count) =>
//      orderCount += count // 受信した注文数を加算
//      log.info(s"Receive your order: Drip $count cups of coffee. The number of orders: $orderCount ")
//      sender() ! CashierActor.OrderCompleted("I'm a Barista. Received your orders!")
//  }
//}
//
//// バリスタアクターのコンパニオンオブジェクト
//object BaristaActor {
//  def props(offset: Int) = Props(classOf[BaristaActor], offset)
//
//  // ケースクラスの定義
//  case class DripCoffee(count: Int)
//}
