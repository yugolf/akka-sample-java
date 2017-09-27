package goticks._6_supervisor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

// カフェアクター
class Shop extends AbstractActor {
    static public Props props() {
        return Props.create(Shop.class, () -> new Shop());
    }

    // メッセージプロトコルの定義
    public static class Initialize {
        public Initialize() {
        }
    }

    public static class Shutdown {
        public Shutdown() {
        }
    }

    public static class Order {
        private final Product product;
        private final int count;

        public Order(Product product, int count) {
            this.product = product;
            this.count = count;
        }

        public Product getProduct() {
            return product;
        }

        public int getCount() {
            return count;
        }
    }

    // 商品リスト
    public interface Product {
    }

    public static class Sports implements Product {
        public Sports() {
        }
    }

    public static class Music implements Product {
        public Music() {
        }
    }

    public static class SportsAndMusic implements Product {
        public SportsAndMusic() {
        }
    }


    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public Shop() {
    }

    private ActorRef ticketSeller = getContext().actorOf(TicketSeller.props(), "ticketSeller");
    private ActorRef boxOffice = getContext().actorOf(BoxOffice.props(ticketSeller), "boxOffice");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Initialize.class, initialize -> log.info("starting go ticks"))
                .match(Order.class, order -> order.getProduct() instanceof Sports, order -> boxOffice.forward(new BoxOffice.OrderSports(order.getCount()), getContext()))
                .match(Order.class, order -> order.getProduct() instanceof Music, order -> boxOffice.forward(new BoxOffice.OrderMusic(order.getCount()), getContext()))
                .match(Order.class, order -> order.getProduct() instanceof SportsAndMusic, order -> boxOffice.forward(new BoxOffice.OrderSportsAndMusic(order.getCount(), order.getCount()*2), getContext()))
                .match(Shutdown.class, shutdown -> {
                    log.info("terminating go ticks");
                    getContext().getSystem().terminate();
                })
                .build();
    }
}
