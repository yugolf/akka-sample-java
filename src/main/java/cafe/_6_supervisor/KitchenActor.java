package cafe._6_supervisor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.actor.SupervisorStrategy;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;

import akka.actor.OneForOneStrategy;
import scala.concurrent.duration.Duration;

// キッチンアクター
public class KitchenActor extends AbstractActor {
    static public Props props(int offset) {
        return Props.create(KitchenActor.class, () -> new KitchenActor(offset));
    }

    // メッセージプロトコルの定義
    public interface Request {
    }

    ;

    public static class DripCoffee implements Request {
        private final int count;

        public DripCoffee(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class BakeCake implements Request {
        private final int count;

        public BakeCake(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    // 例外クラスの定義
    public static class ExceededLimitException extends RuntimeException {
        public ExceededLimitException(String message) {
            super(message);
        }
    }

    private ActorRef barista = getContext().actorOf(BaristaActor.props(0), "baristaActor");
    private ActorRef patissier = getContext().actorOf(PatissierActor.props(0), "patissierActor");

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private int orderCount; // 注文数

    public KitchenActor(int offset) {
        this.orderCount = offset;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DripCoffee.class, dripCoffee ->
                        barista.forward(new BaristaActor.DripCoffee(dripCoffee.getCount()), getContext())
                )
                .match(BakeCake.class, bakeCake ->
                        patissier.forward(new PatissierActor.BakeCake(bakeCake.getCount()), getContext())
                )
                .build();
    }


    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(ArithmeticException.class, e -> resume()).
                    match(ExceededLimitException.class, e -> restart()).
                    match(IllegalArgumentException.class, e -> stop()).
                    matchAny(o -> escalate()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}


// キッチンアクターのコンパニオンオブジェクト
//object KitchenActor {
//  def props(offset: Int) = Props(classOf[KitchenActor], offset)

//  // メッセージプロトコルの定義
//  sealed trait Request
//  case class DripCoffee(count: Int) extends Request
//  case class BakeCake(count: Int) extends Request

//}
