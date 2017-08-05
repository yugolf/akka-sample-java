package cafe._7_become;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import cafe._7_become.CashierActor.*;

class ApplicationMain {
  public static void main(String args[]) throws Exception {

    final ActorSystem system = ActorSystem.create("CafeActorSystem", ConfigFactory.load("cafe"));
    final ActorRef cashierActor = system.actorOf(CashierActor.props(), "cashierActor");

    cashierActor.tell(new Initialize(), ActorRef.noSender());



    // コーヒー豆は「モカ」でオープン
    cashierActor.tell(new Open(new BaristaActor.Bean("Mocha")), ActorRef.noSender());

    // 5秒後にクローズ
    system.scheduler().scheduleOnce(Duration.create(5, "seconds"), cashierActor, new CashierActor.Close(), system.dispatcher(), ActorRef.noSender());
    // 10秒後にコーヒー豆を「キリマンジャロ」でオープン
    system.scheduler().scheduleOnce(Duration.create(10, "seconds"), cashierActor, new CashierActor.Open(new BaristaActor.Bean("Kilimanjaro")), system.dispatcher(), ActorRef.noSender());

    // 15秒後にシャットダウン
    system.scheduler().scheduleOnce(Duration.create(15, "seconds"), cashierActor, new CashierActor.Shutdown(), system.dispatcher(), ActorRef.noSender());

    // 1秒毎にランダム杯のコーヒーを注文
    cashierActor.tell(new Order("Coffee", 4), ActorRef.noSender());
    Thread.sleep(3000);
    cashierActor.tell(new Order("Coffee", 1), ActorRef.noSender());
    Thread.sleep(3000);
    cashierActor.tell(new Order("Coffee", 2), ActorRef.noSender());
    Thread.sleep(3000);
    cashierActor.tell(new Order("Coffee", 3), ActorRef.noSender());
    Thread.sleep(3000);
    cashierActor.tell(new Order("Coffee", 5), ActorRef.noSender());

    Await.result(system.whenTerminated(), Duration.Inf());

  }
}




//  // コーヒー豆は「モカ」でオープン
//  cashierActor ! Open(BaristaActor.Bean("Mocha"))
//  // 1秒毎にランダム杯のコーヒーを注文
//  system.scheduler.schedule(0 seconds, 1 seconds)(cashierActor ! CashierActor.Order("Coffee", Random.nextInt(9) + 1))
//  // 5秒後にクローズ
//  system.scheduler.scheduleOnce( 5 seconds, cashierActor, CashierActor.Close)
//  // 10秒後にコーヒー豆を「キリマンジャロ」でオープン
//  system.scheduler.scheduleOnce(10 seconds, cashierActor, Open(BaristaActor.Bean("Kilimanjaro")))
//  // 15秒後にシャットダウン
//  system.scheduler.scheduleOnce(15 seconds, cashierActor, CashierActor.Shutdown)
//
//  Await.result(system.whenTerminated, Duration.Inf)
//}
