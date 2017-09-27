package goticks._6_supervisor;

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
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

// チケット販売員
public class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    // メッセージプロトコルの定義
    public interface Request {
    }

    public static class RequestSportsTicket implements Request {
        private final int count;

        public RequestSportsTicket(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class RequestMusicTicket implements Request {
        private final int count;

        public RequestMusicTicket(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class RequestMultiTickets implements Request {
        private final Integer sports;
        private final Integer music;

        public RequestMultiTickets(Integer sports, Integer music) {
            this.sports = sports;
            this.music = music;
        }

        public Integer getSports() {
            return sports;
        }

        public Integer getMusic() {
            return music;
        }
    }

    public static class Result {
        private final String sports;
        private final String music;

        public Result(String sports, String music) {

            this.sports = sports;
            this.music = music;
        }

        public Result(BoxOffice.OrderCompleted sports, BoxOffice.OrderCompleted music) {
            this.sports = sports.getMessage();
            this.music = music.getMessage();
        }

        public String getResult() {
            return sports + ":" + music;
        }
    }

    // 例外クラスの定義
    public static class ExceededLimitException extends RuntimeException {
        public ExceededLimitException(String message) {
            super(message);
        }
    }

    private ActorRef sports = getContext().actorOf(Sports.props(0), "sports");
    private ActorRef music = getContext().actorOf(Music.props(0), "music");

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller() {

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RequestSportsTicket.class, RequestSportsTicket ->
                        sports.forward(new Sports.RequestTicket(RequestSportsTicket.getCount()),
                                getContext()))
                .match(RequestMusicTicket.class, requestMusicTicket ->
                        music.forward(new Music.RequestTicket(requestMusicTicket.getCount()),
                                getContext())
                ).match(RequestMultiTickets.class, this::requestMultiTickets)
                .build();
    }

    // スポーツイベントと音楽イベントのチケットを手配するメソッド
    private void requestMultiTickets(RequestMultiTickets ticketRequests) {
        // タイムアウトの設定
        Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));

        // スポーツイベントのチケット手配
        CompletableFuture<Object> resultOfSports =
                ask(sports, new Sports.RequestTicket(ticketRequests.getSports()), t)
                        .toCompletableFuture();

        // 音楽イベントのチケット手配
        CompletableFuture<Object> resultOfMusic =
                ask(music, new Music.RequestTicket(ticketRequests.getMusic()), t)
                        .toCompletableFuture();

        // 両方のチケット手配結果を合成
        CompletableFuture<Result> results =
                CompletableFuture.allOf(resultOfSports, resultOfMusic)
                        .thenApply(v -> {
                            BoxOffice.OrderCompleted sports = (BoxOffice.OrderCompleted) resultOfSports.join();
                            BoxOffice.OrderCompleted music = (BoxOffice.OrderCompleted) resultOfMusic.join();
                            return new Result(sports, music);
                        }).exceptionally(ex -> {
                            log.info(ex.getMessage());
                            return null;
                        }
                );

        // 手配結果を売り場に通知
        pipe(results, getContext().dispatcher()).to(getSender());
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

