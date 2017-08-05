package cafe._5_actorandthread;


import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(BaristaActor.class, () -> new BaristaActor(offset));
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

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount; // 注文数

    public BaristaActor(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.count;  // 受信した注文数を加算
                    log.info("Receive your order: {}, {}. The number of orders: {}", order.getProduct(), order.getCount(), orderCount);
                    getSender().tell(new CashierActor.OrderCompleted("Received your order."), getSender());
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}


//import akka.actor.{Actor, ActorLogging, Props}
//
//// バリスタアクター
//class BaristaActor(offset: Int) extends Actor with ActorLogging {
//  import BaristaActor._

//  private var orderCount = offset // 注文数
//  override def receive: Receive = {
//    case Order(product, count) =>
//      orderCount += count // 受信した注文数を加算
//      log.info(s"Receive your order: $product, $count. The number of orders: $orderCount ")
//      sender() ! CashierActor.OrderCompleted(s"Received your order.")
//  }
//}

//// バリスタアクターのコンパニオンオブジェクト
//object BaristaActor {
//  def props(offset: Int) = Props(classOf[BaristaActor], offset)
//
//  // メッセージプロトコルの定義
//  case class Order(product: String, count: Int)
//}
