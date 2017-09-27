package goticks._5_actorandthread;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import goticks._5_actorandthread.BoxOffice.*;

//import java.util.Random;

class ApplicationMain {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());
        boxOffice.tell(new Order("RHCP", 1), ActorRef.noSender());
        boxOffice.tell(new Order("RHCP", 2), ActorRef.noSender());
        boxOffice.tell(new Order("RHCP", 3), ActorRef.noSender());

        // 2秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(2, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), ActorRef.noSender());
        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
