public class DoubleAuctionItem extends AuctionItem{

    private int doubleAuction;

    public DoubleAuctionItem(String title, double startingPrice, String description, int doubleAuction,
        Person seller, Person buyer) {
        super(title, startingPrice, description, seller);
        this.doubleAuction = doubleAuction;
    }

    public int getDoubleAuction() {
        return doubleAuction;
    }

    public void setDoubleAuction(int doubleAuction) {
        this.doubleAuction = doubleAuction;
    }
    
}
