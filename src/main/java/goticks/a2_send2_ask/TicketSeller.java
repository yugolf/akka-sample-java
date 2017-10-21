package goticks.a2_send2_ask;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;

import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/** チケット販売員 */
class TicketSeller extends AbstractActor {
    static public Props props() {
        return Props.create(TicketSeller.class, () -> new TicketSeller());
    }

    /** 注文メッセージ */
    public static class Order {
        private final String event;
        private final int count;

        public Order(String event, int count) {
            this.event = event;
            this.count = count;
        }

        public String getEvent() {
            return event;
        }

        public int getCount() {
            return count;
        }
    }

    /** 複数チケットの注文メッセージ */
    public static class RequestMultiTickets{
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

    /** 注文完了メッセージ */
    public static class OrderCompleted {
        private final String message;

        public OrderCompleted(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private ActorRef sportsSeller = getContext().actorOf(SportsSeller.props(0), "sportsSeller");
    private ActorRef musicSeller = getContext().actorOf(MusicSeller.props(0), "musicSeller");

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public TicketSeller() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Order.class, order -> {
                    log.info("Your order has been completed. (event: {}, count: {})", order.getEvent(), order.getCount());
                    getSender().tell(new BoxOffice.OrderCompleted("ok"), getSender());
                })
                .match(RequestMultiTickets.class, this::requestMultiTickets)
                .matchAny(c -> log.info("received unknown message."))
                .build();
    }

    /** スポーツイベントと音楽イベントのチケットを手配 */
    private void requestMultiTickets(RequestMultiTickets ticketRequests) {

        // タイムアウトの設定
        Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));

        // スポーツイベントのチケット手配
        CompletableFuture<Object> resultOfSports =
                ask(sportsSeller, new SportsSeller.RequestTicket(ticketRequests.getSports()),
                        t).toCompletableFuture();

        // 音楽イベントのチケット手配
        CompletableFuture<Object> resultOfMusic =
                ask(musicSeller, new MusicSeller.RequestTicket(ticketRequests.getMusic()), t)
                        .toCompletableFuture();

        // 両方のチケット手配結果を合成
        CompletableFuture<BoxOffice.Result> results =
                CompletableFuture.allOf(resultOfSports, resultOfMusic)
                        .thenApply(v -> {
                            OrderCompleted sports = (OrderCompleted) resultOfSports.join();
                            OrderCompleted music = (OrderCompleted) resultOfMusic.join();
                            return new BoxOffice.Result(
                                    new BoxOffice.OrderCompleted(sports.getMessage()),
                                    new BoxOffice.OrderCompleted(music.getMessage()));
                        });

        // メッセージの送信元に手配結果を通知
        pipe(results, getContext().dispatcher()).to(getSender());
    }
}
