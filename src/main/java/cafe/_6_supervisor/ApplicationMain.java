package cafe._6_supervisor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import cafe._6_supervisor.CafeActor.*;

class ApplicationMain {
  public static void main(String args[]) throws Exception {

    final ActorSystem system = ActorSystem.create("CafeActorSystem", ConfigFactory.load("cafe"));
    final ActorRef cafeActor = system.actorOf(CafeActor.props(), "cafeActor");

    cafeActor.tell(new Initialize(), ActorRef.noSender());

    // 顧客からの注文
    cafeActor.tell(new Order(new CafeActor.Coffee(), 2), ActorRef.noSender());
    cafeActor.tell(new Order(new CafeActor.Cake(), 4), ActorRef.noSender());
    cafeActor.tell(new Order(new CafeActor.Coffee(), 1), ActorRef.noSender());
    cafeActor.tell(new Order(new CafeActor.Cake(), 2), ActorRef.noSender());

    // 2秒後にシャットダウン
    system.scheduler().scheduleOnce(Duration.create(2, "seconds"), cafeActor, new CafeActor.Shutdown(), system.dispatcher(), ActorRef.noSender());
    Await.result(system.whenTerminated(), Duration.Inf());

  }
}



//object ApplicationMain extends App {
//  val system = ActorSystem("CafeActorSystem", ConfigFactory.load("cafe"))
//  val cafeActor = system.actorOf(CafeActor.props, "cafeActor")

//  cafeActor ! CafeActor.Initialize
//
//  // 顧客からの注文
//  cafeActor ! CafeActor.Order(CafeActor.Coffee, 2)
//  cafeActor ! CafeActor.Order(CafeActor.Cake, 4)
//  cafeActor ! CafeActor.Order(CafeActor.Coffee, 1)
//  cafeActor ! CafeActor.Order(CafeActor.Cake, 2)
