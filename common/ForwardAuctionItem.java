public class ForwardAuctionItem extends AuctionItem {

    private double minimumPrice;

    public ForwardAuctionItem(String title, double startingPrice, String description, double minimumPrice,
        Person seller) {
        super(title, startingPrice, description, seller);
        this.minimumPrice = minimumPrice;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
    
}
