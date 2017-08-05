package cafe._4_askpattern;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import static akka.pattern.PatternsCS.ask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
                    Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS)); // タイムアウトの設定
                    CompletableFuture<Object> future =
                            ask(barista, new BaristaActor.Order("coffee", 2), t).toCompletableFuture(); // 「?」でメッセージを送信
                    CompletableFuture.allOf(future)
                            .thenAccept(v -> {
                                OrderCompleted result = (OrderCompleted) future.join();
                                log.info("result: {}", result.getMessage());
                            })
                            .exceptionally(ex -> {
                                log.info(ex.getMessage());  // TODO Exceptionが発生したときの動作を確認する。
                                return null;
                            });
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating akka cafe");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}


//import akka.actor.{Actor, ActorLogging, ActorRef, Props}
//
//import scala.util.{Failure, Success}
//
//class CashierActor extends Actor with ActorLogging {
//  import CashierActor._
//
//  val barista: ActorRef = context.actorOf(BaristaActor.props, "baristaActor")

//  def receive: Receive = {
//  	case Initialize =>
//	    log.info("starting akka cafe")
//    case Order =>
//      import scala.concurrent.duration._
//      import akka.util.Timeout
//      import akka.pattern.ask
//      import context.dispatcher
//      import scala.language.postfixOps
//
//      implicit val timeout = Timeout(5 seconds)                // タイムアウトの設定
//      val response = barista ? BaristaActor.Order("coffee", 2)  // 「?」でメッセージを送信
// 応答があった後の未来の処理
//      response.mapTo[OrderCompleted]onComplete{
//              case Success(result)=>
//              log.info(s"result: ${result.message}")
//              case Failure(t)=>
//              log.info(t.getMessage)
//              }
//    case Shutdown =>
//      log.info("terminating akka cafe")
//      context.system.terminate()
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
