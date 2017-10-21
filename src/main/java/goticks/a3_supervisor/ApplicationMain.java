package goticks._6_supervisor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import scala.concurrent.duration.Duration;
import scala.concurrent.Await;
import goticks._6_supervisor.Shop.*;

class ApplicationMain {
    public static void main(String args[]) throws Exception {

        final ActorSystem system = ActorSystem.create("main", ConfigFactory.load("goticks"));
        final ActorRef shop = system.actorOf(Shop.props(), "shop");

        // 初期化
        shop.tell(new Initialize(), ActorRef.noSender());

        // 顧客からの注文
        shop.tell(new Order(new Shop.Sports(), 2), ActorRef.noSender());
        shop.tell(new Order(new Shop.Music(), 6), ActorRef.noSender());
        shop.tell(new Order(new Shop.Sports(), 1), ActorRef.noSender());
        shop.tell(new Order(new Shop.Music(), 2), ActorRef.noSender());

        // 2秒間隔をあける
        Thread.sleep(2000);

        // SportsSellerを停止させる
        shop.tell("killSports", ActorRef.noSender());

        // 10秒後にシャットダウン
        system.scheduler().scheduleOnce(Duration.create(10, "seconds"), shop, new Shop.Shutdown(), system.dispatcher(), ActorRef.noSender());
        Await.result(system.whenTerminated(), Duration.Inf());

    }
}
