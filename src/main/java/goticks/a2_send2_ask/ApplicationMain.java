package goticks._4_askpattern;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import goticks._4_askpattern.BoxOffice.*;

class ApplicationMain {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef boxOffice = system.actorOf(BoxOffice.props(), "boxOffice");

        boxOffice.tell(new Initialize(), ActorRef.noSender());
        boxOffice.tell(new Order(), ActorRef.noSender());
        boxOffice.tell(new Orders(), ActorRef.noSender());

        system.scheduler().scheduleOnce(Duration.create(20, "seconds"), boxOffice, new BoxOffice.Shutdown(), system.dispatcher(), null);
        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
