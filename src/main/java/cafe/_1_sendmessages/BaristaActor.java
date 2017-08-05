package cafe._1_sendmessages;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BaristaActor extends AbstractActor {
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
                .match(String.class, s -> log.info("Received your order: {}", s))   // String型のメッセージを受信した場合
                .match(Integer.class, s -> log.info("Received your order: {}", s))  // Int型のメッセージを受信した場合
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }


//  override def receive: Receive = {
//    case product: String => // String型のメッセージを受信した場合
//      log.info(s"Received your order: $product")
//    case count: Int =>      // Int型のメッセージを受信した場合
//      log.info(s"Received your order: $count")
//    case _ =>               // String型、Int型以外のメッセージを受信した場合
//      log.info("Received your order.")
//  }
}

//// バリスタアクターのコンパニオンオブジェクト
//object BaristaActor {
////  val props: Props = Props[BaristaActor]
//
//  // メッセージプロトコルの定義
//  case class Order(product: String, count: Int)
//}
