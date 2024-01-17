import java.io.Serializable;

abstract public class AuctionItem implements Serializable {

    private String title;
    private double startingPrice;
    private String description;
    private Person seller;

    public AuctionItem(String title, double startingPrice, String description, Person seller) {
        this.title = title;
        this.startingPrice = startingPrice;
        this.description = description;
        this.seller = seller;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return title + " " + Double.toString(startingPrice) + "€ " + description + ". Seller: " + seller.getName();
    }

    public Person getSeller() {
        return seller;
    }

    public void setSeller(Person seller) {
        this.seller = seller;
    }
}
