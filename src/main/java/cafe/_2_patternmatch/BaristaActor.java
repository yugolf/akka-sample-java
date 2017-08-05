package cafe._2_patternmatch;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActor {
    static public Props props() {
        return Props.create(BaristaActor.class, () -> new BaristaActor());
    }
    final int two = 2;

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
                .matchEquals(1, s -> log.info("(1) received: 1"))   // String型のメッセージを受信した場合
                .matchEquals(two, s -> log.info("(2) received: {}", two))  // Int型のメッセージを受信した場合
                .match(Integer.class, count -> count < 10, count -> log.info("(3) received: {}", count))  // 10より小さい数値（Int型）の場合
                .match(Order.class, order -> order.getProduct().equals("Coffee"), order -> { // Order型で第一引数productが「Coffee」の場合
                    log.info("(4) product: {}", order.getProduct());
                    log.info("    count  : {}", order.getCount());
                })
                .match(Order.class, order -> { // Order型で第一引数productが「Coffee」の場合
                    String product = order.getProduct();
                    int count = order.getCount();
                    log.info("(5) Receive your order."); // ケースクラスを受信した時の振る舞い
                    log.info("    product: {}", product);
                    log.info("    count  : {}", count);
                })
                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
                .build();
    }
}

//    override def
//    receive:Receive =
//
//    {
        // 1の場合
//    case 1 =>
//      log.info("(1) received: 1")
        // 変数「two」の値と一致する場合
//    case `two` =>
//        log.info(s"(2) received: $two")
//    // 10より小さい数値（Int型）の場合
//    case count: Int if count < 10 =>
//      log.info(s"(3) received: $count")
        // Order型で第一引数productが「Coffee」の場合
//        case order
//            @Order("Coffee", _) =>
//            log.info(s"(4) product: ${order.product}")
//            log.info(s"    count  : ${order.count}")
            // Order型の場合
//        case Order(product, count) =>
//            log.info(s"(5) Receive your order.") // ケースクラスを受信した時の振る舞い
//            log.info(s"    product: $product")
//            log.info(s"    count  : $count")

//    // バリスタアクターのコンパニオンオブジェクト
//    object BaristaActor {
//        val props:Props=Props[BaristaActor]
//
//        // メッセージプロトコルの定義
//        case

//class Order(product:String,count:Int)
//        }
