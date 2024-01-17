public class ReverseAuctionItem extends AuctionItem {

    private double minimumPrice;
    private String category;

    public ReverseAuctionItem(String title, double startingPrice, String description, double minimumPrice,
        String category, Person seller) {
        super(title, startingPrice, description, seller);
        this.minimumPrice = minimumPrice;
        this.category = category;
    }

    public double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
}
