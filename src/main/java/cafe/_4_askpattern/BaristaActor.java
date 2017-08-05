package cafe._4_askpattern;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActor {
    static public Props props() {
        return Props.create(BaristaActor.class, () -> new BaristaActor());
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

    public BaristaActor() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    // Baristaからの応答を受け取ったときの振る舞い
                    log.info("Your order has been completed. (product: {}, count: {})", order.getProduct(), order.getCount());
                    // 送信元に注文に対する調理が完了したことを返す
                   getSender().tell(new CashierActor.OrderCompleted("ok"), getSender());
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}


//import akka.actor.{Actor, ActorLogging, Props}
//
// バリスタアクター
//class BaristaActor extends Actor with ActorLogging {
//  import BaristaActor._

//  override def receive: Receive = {
//    case Order(product, count) =>
//      // Baristaからの応答を受け取ったときの振る舞い
//      log.info(s"Your order has been completed. (product: $product, count: $count)")
//      // 送信元に注文に対する調理が完了したことを返す
//      sender() ! CashierActor.OrderCompleted("ok")
//  }
//}

//// バリスタアクターのコンパニオンオブジェクト
//object BaristaActor {
//  val props: Props = Props[BaristaActor]
//
//  // メッセージプロトコルの定義
//  case class Order(product: String, count: Int)
//}
