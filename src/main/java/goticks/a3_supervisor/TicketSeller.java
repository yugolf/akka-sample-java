package goticks.a3_supervisor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;

import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.stop;
import static akka.actor.SupervisorStrategy.escalate;

import scala.concurrent.duration.Duration;

/** チケット販売員 */
public class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    /** スポーツチケットのリクエスト・メッセージ */
    public static class RequestSportsTicket{
        private final int count;

        public RequestSportsTicket(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    /** 音楽チケットのリクエスト・メッセージ */
    public static class RequestMusicTicket{
        private final int count;

        public RequestMusicTicket(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    /** 例外クラスの定義 */
    public static class ExceededLimitException extends RuntimeException {
        public ExceededLimitException(String message) {
            super(message);
        }
    }

    private ActorRef sportsSeller = getContext().actorOf(SportsSeller.props(0), "sportsSeller");
    private ActorRef musicSeller = getContext().actorOf(MusicSeller.props(0), "musicSeller");

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller() {
        getContext().watch(sportsSeller);
        getContext().watch(musicSeller);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestSportsTicket.class, requestSportsTicket ->
                        sportsSeller.forward(new SportsSeller.RequestTicket(requestSportsTicket.getCount()),
                                getContext()))
                .match(RequestMusicTicket.class, requestMusicTicket ->
                        musicSeller.forward(new MusicSeller.RequestTicket(requestMusicTicket.getCount()),
                                getContext()))
                .matchEquals("killSports", msg ->
                    getContext().stop(sportsSeller)
                )
                .match(Terminated.class, t -> t.actor().equals(sportsSeller), t ->
                    log.info("A charge of sports events has terminated.")
                )
                .build();
    }

    // スーパーバイザー戦略の設定
    private static SupervisorStrategy strategy =
            new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
                    match(ArithmeticException.class, e -> resume()).
                    match(ExceededLimitException.class, e -> restart()).
                    match(IllegalArgumentException.class, e -> stop()).
                    matchAny(o -> escalate()).build());

    // スーパーバイザー戦略をオーバーライド
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}

