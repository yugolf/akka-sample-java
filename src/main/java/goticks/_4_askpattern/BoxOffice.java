package goticks._4_askpattern;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.UnitMatch;
import akka.japi.pf.UnitPFBuilder;
import akka.pattern.AskTimeoutException;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class BoxOffice extends AbstractActor {
    static public Props props() {
        return Props.create(BoxOffice.class, () -> new BoxOffice());
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

    public BoxOffice() {
    }

    ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> {

                    Timeout t = new Timeout(Duration.create(2, TimeUnit.SECONDS)); // タイムアウトの設定
                    CompletionStage<Object> orderCompleted =
                            ask(ticketSeller, new TicketSeller.Order("RHCP", 2), t); // 「?」でメッセージを送信
                    log.info("---" + orderCompleted.getClass().toString());
//                    CompletionStage<Object> future =
//                            ask(ticketSeller, new TicketSeller.Order("RHCP", 2), t); // 「?」でメッセージを送信
                    orderCompleted
                            .thenAccept(msg -> {
                                Order o = (Order) orderCompleted;
                                log.info("result: {}", ((OrderCompleted) msg).getMessage());
                            })
                            .exceptionally(ex -> {
                                UnitPFBuilder builder = UnitMatch
                                        .match(AskTimeoutException.class, e -> log.info("時間内に処理されませんでした：" + e.getMessage()))
                                        .matchAny(e -> log.info("例外が発生しました:" + e));
                                UnitMatch.create(builder).match(ex.getCause());
                                return null;
                            });

                    //pipe(future, getContext().getSystem().dispatcher()).to(getSelf());
                })
//                .match(OrderCompleted.class, result ->log.info("result--: {}", result.getMessage()) )
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
//                .matchAny(a -> log.info("any ; {}", a))
                .build();

    }
}

