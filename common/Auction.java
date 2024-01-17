import java.io.Serializable;

public class Auction implements Serializable {
    private int id;
    private int state;
    private AuctionItem item;
    private double price = -1;
    private double bid = -1;
    private int buyerID = -1;

    public Auction (int id, int state, AuctionItem item) {
        this.id = id;
        this.state = state;
        this.item = item;
    }

    public Auction (int id, int state, AuctionItem item, double price, double bid, int buyerID) {
        this.id = id;
        this.state = state;
        this.item = item;
        this.price = price;
        this.bid = bid;
        this.buyerID = buyerID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public AuctionItem getItem() {
        return item;
    }

    public void setItem(AuctionItem item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public int getBuyerID() {
        return buyerID;
    }

    public void setBuyerID(int buyerID) {
        this.buyerID = buyerID;
    }

    public double getProfit() {
        return (this.bid - this.price);
    }
}
