package cafe._7_become;


import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithStash;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// バリスタアクター
class BaristaActor extends AbstractActorWithStash {
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

    public static class Open {
        private final Bean bean;

        public Open(Bean bean) {
            this.bean = bean;
        }

        public Bean getBean() {
            return bean;
        }
    }

    public static class Close {
        public Close() {
        }
    }

    // コーヒー豆の種類
    public static class Bean {
        private final String name;

        public Bean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount = 0; // 注文数


    final AbstractActor.Receive open(final Bean bean) {
        // オープ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    orderCount += order.getCount();
                    log.info("Receive your order: {}, {}. The number of orders: {} ", order.getProduct(), order.getCount(), orderCount);
                    getSender().tell(new CashierActor.OrderCompleted("Received your order. Today's coffee is " + bean.name + "."), getSelf());
                })
                .match(Close.class, c -> {
                    getContext().become(close());
                })
                .build();
    }

    final AbstractActor.Receive close() {
        // クローズ状態の振る舞い
        return receiveBuilder()
                .match(Order.class, order -> {
                    stash();  // オーダーを退避しておく
                    log.info("I'm closed.");
                })
                .match(Open.class, open -> {
                    unstashAll();
                    getContext().become(open(open.bean));
                })
                .build();
    }

    @Override
    public Receive createReceive() {
        return close();
    }

    public BaristaActor() {
        //createReceive(close());
    }
//  public BaristaActor() {
//    open =
//            // オープ状態の振る舞い
//            receiveBuilder()
//                    .match(Order.class, order -> {
//                      orderCount += order.getCount();
//                      log.info("Receive your order: {}, $count. The number of orders: {} ", order.getCount(), orderCount);
//                      getSender().tell(new CashierActor.OrderCompleted("Received your order. Today's coffee is {}.", bean.name), getSelf());
//                    })
//                    .match(Close.class, close -> {
//                      getContext().become(close);
//                    })
//                    .build();
//
//    def close: Receive = {
//    case Order =>
//    stash()                     // オーダーを退避しておく
//    log.info("I'm closed.")
//    case Open(bean) =>
//    unstashAll()                // 退避したオーダーを引き戻す
//    context.become(open(bean))  // オープンへ状態変更
//  }
//    close =
//            // クローズ状態の振る舞い
//            receiveBuilder()
//            .match(Order.class, order -> {
//              stash();  // オーダーを退避しておく
//              log.info("I'm closed.");
//              getSender().tell("I am already happy :-)", getSelf());
//            })
//            .match(Open.class, open -> {
//              unStashAll();
//              getContext().become(open(open.bean));
//            })
//            .build();
//  }
//
//    @Override
//    public Receive createReceive() {
//        return receiveBuilder()
//                .match(Order.class, order -> {
//                    orderCount += order.count;  // 受信した注文数を加算
//                    log.info("Receive your order: {}, {}. The number of orders: {}", order.getProduct(), order.getCount(), orderCount);
//                    getSender().tell(new CashierActor.OrderCompleted("Received your order."), getSender());
//                })
//                .matchAny(c -> log.info("Received your order."))  // String型、Int型以外のメッセージを受信した場合
//                .build();
//    }
}


//import akka.actor.{Actor, ActorLogging, Props, Stash}
//
//// バリスタアクター
//class BaristaActor extends Actor with ActorLogging with Stash {
//  import BaristaActor._

// 初期状態の設定
//  override def receive:Receive=close
//  // 注文数
//  var orderCount = 0

//  // オープ状態の振る舞い
//  def open(bean: Bean): Receive = {
//    case Order(product, count) =>
//      orderCount += count         // 受信した注文数を加算
//      log.info(s"Receive your order: $product, $count. The number of orders: $orderCount ")
//      sender() ! CashierActor.OrderCompleted(s"Received your order. Today's coffee is ${bean.name}.")
//    case Close =>
//      context.become(close)       // クローズへ状態変更
//  }
//
//          // クローズ状態の振る舞い
//          def close:Receive={
//          case Order=>
//          stash()                     // オーダーを退避しておく
//          log.info("I'm closed.")
//          case Open(bean)=>
//          unstashAll()                // 退避したオーダーを引き戻す
//          context.become(open(bean))  // オープンへ状態変更
//          }
//          }
//
//// バリスタアクターのコンパニオンオブジェクト
//object BaristaActor {
//  val props: Props = Props[BaristaActor]
//
//  // メッセージプロトコルの定義
//  case class Order(product: String, count: Int)
//  case class Open(bean: Bean)
//  case object Close

//  // コーヒー豆の種類
//  case class Bean(name: String)
//}
