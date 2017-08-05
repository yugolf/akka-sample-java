package cafe._2_patternmatch;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import cafe._2_patternmatch.CashierActor.*;

class ApplicationMain {
  public static void main(String args[]) throws Exception {

    final ActorSystem system = ActorSystem.create("CafeActorSystem", ConfigFactory.load("cafe"));
    final ActorRef cashierActor = system.actorOf(CashierActor.props(), "cashierActor");

    cashierActor.tell(new Initialize(), ActorRef.noSender());
    cashierActor.tell(new Order(), ActorRef.noSender());

    // 2秒後にシャットダウン
    system.scheduler().scheduleOnce(Duration.create(2, "seconds"), cashierActor, new CashierActor.Shutdown(), system.dispatcher(), ActorRef.noSender());
    Await.result(system.whenTerminated(), Duration.Inf());

  }
}
