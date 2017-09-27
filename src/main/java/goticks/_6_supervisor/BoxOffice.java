package goticks._6_supervisor;


import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class BoxOffice extends AbstractActor {
    static public Props props(ActorRef kitchen) {
        return Props.create(BoxOffice.class, () -> new BoxOffice(kitchen));
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

    public static class Shutdown {
        public Shutdown() {
        }
    }

    public interface Order {
    }

    public static class OrderSports implements Order {
        private final int count;

        public OrderSports(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class OrderMusic implements Order {
        private final int count;

        public OrderMusic(int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }

    public static class OrderSportsAndMusic implements Order {
        private final int sports;
        private final int music;

        public OrderSportsAndMusic(int sports, int music) {
            this.sports = sports;
            this.music = music;
        }

        public int getSports() {
            return sports;
        }

        public int getMusic() {
            return music;
        }
    }

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private static ActorRef ticketSeller;

    public BoxOffice(ActorRef ticketSeller) {
        this.ticketSeller = ticketSeller;
    }

    private ActorRef sports = getContext().actorOf(Sports.props(0), "sports");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderSports.class, order -> {
                    ticketSeller.tell(new TicketSeller.RequestSportsTicket(order.getCount()), getSelf());
                })
                .match(OrderMusic.class, order -> {
                    ticketSeller.tell(new TicketSeller.RequestMusicTicket(order.getCount()), getSelf());
                })
                .match(OrderSportsAndMusic.class, order -> {
                    ticketSeller.tell(new TicketSeller.RequestMultiTickets(order.getSports(), order.getMusic()), getSelf());
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .match(TicketSeller.Result.class, result -> log.info("result: {}", result.getResult()))
                .build();
    }
}
