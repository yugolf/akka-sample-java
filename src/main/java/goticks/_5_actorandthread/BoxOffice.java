package goticks._5_actorandthread;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

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

    private ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(0), "ticketSeller");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> {
                    ticketSeller.tell(new TicketSeller.Order(order.getEvent(), order.getCount()), getSelf()); // 「!」でメッセージを送信
                })
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .match(OrderCompleted.class, result -> log.info("result: {}", result.getMessage()))
                .build();
    }
}
