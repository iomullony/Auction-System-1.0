import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.io.Serializable;

public class Server implements IAuction, Serializable {

	private KeyPair keyPair;
	private int auctionItemCounter = 0;
	private int doubleAuctionItemCounter = 0;
	private int sellerCounter = 0;
	private int buyerCounter = 0;
	private Map<Integer, AuctionItem> auctionItems = new HashMap<>(); // itemID, item>
	private Map<Integer, ForwardAuctionItem> forwardAuctionItems = new HashMap<>(); // itemID, item>
	private Map<String, Map<Integer, ReverseAuctionItem>> reverseAuctionItems = new HashMap<>(); // category <itemID, item>
	private Map<Integer, Map<Integer, DoubleAuctionItem>> doubleAuctionItems = new HashMap<>(); // categoryID <itemID, item>
	private Map<Integer, String> reverseCategories = new HashMap<>(); // categoryID, category
	private Map<Integer, String> doubleCategories = new HashMap<>(); // categoryID, category
	private Map<Integer, Map<Integer, Double>> forwardBids = new HashMap<>(); // itemID <buyerID, bid>
	private Map<Integer, Map<Integer, Double>> reverseBids = new HashMap<>(); // itemID <buyerID, bid>
	private Map<Integer, Map<Integer, Double>> doubleBids = new HashMap<>(); // categoryID <buyerID, bid>
    private Map<Integer, Person> sellers = new HashMap<>(); // sellerID, seller
    private Map<Integer, Person> buyers = new HashMap<>(); // buyerID, buyer
	private final String[] CATEGORIES = {
		"Technology",
		"Decoration",
		"Furniture",
		"Clothes",
		"Books",
		"Films",
		"Entertainment",
		"Jewelry",
		"Art",
		"Cars"
	};
	private static DecimalFormat df = new DecimalFormat("0.00"); 

	public Server() {
  		super();
		generateKeyPair();
   	}

	private void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
			keyPair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    }

	@Override
    public byte[] signData(String data) throws RemoteException {
        try {
            Signature signature = Signature.getInstance("SHA256withDSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(data.getBytes());
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean verifySignature(String data, byte[] signature) throws RemoteException {
        try {
            Signature sig = Signature.getInstance("SHA256withDSA");
            sig.initVerify(keyPair.getPublic());
            sig.update(data.getBytes());
            return sig.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

	@Override
	public String[] getCategories() throws RemoteException {
		return CATEGORIES;
	}

	@Override
	public int addAuctionItem(AuctionItem item) throws RemoteException {
		String dataToSign = item.toString();
        byte[] signature = signData(dataToSign);

		auctionItemCounter++;
		auctionItems.put(auctionItemCounter, item);

		if (verifySignature(dataToSign, signature)) {
			if (item.getClass().getSimpleName().equals("ForwardAuctionItem")) {
				forwardAuctionItems.put(auctionItemCounter, (ForwardAuctionItem) item);
			} else if (item.getClass().getSimpleName().equals("ReverseAuctionItem")) {
				ReverseAuctionItem reverseItem = (ReverseAuctionItem) item;
				Map<Integer, ReverseAuctionItem> categoryMap = reverseAuctionItems
					.computeIfAbsent(reverseItem.getCategory(), k -> new HashMap<>());
				categoryMap.put(auctionItemCounter, reverseItem);
			} else if (item.getClass().getSimpleName().equals("DoubleAuctionItem")) {
				DoubleAuctionItem doubleItem = (DoubleAuctionItem) item;
				Map<Integer, DoubleAuctionItem> categoryMap = doubleAuctionItems
					.computeIfAbsent(doubleItem.getDoubleAuction(), k -> new HashMap<>());
				categoryMap.put(auctionItemCounter, doubleItem);
			}

			System.out.println(item.toString()); // Just some information for me

			return auctionItemCounter;
		} else {
			return -1;
		}
	}

	@Override
	public Map<Integer, AuctionItem> getAuctionItems() throws RemoteException {
		return auctionItems;
	}

	@Override
	public void addReverseCategory(int categoryID) throws RemoteException {
		if (!reverseCategories.containsKey(categoryID)) {
			reverseCategories.put(categoryID, CATEGORIES[categoryID]);
		}
	}
	
	@Override
	public Map<Integer, String> getReverseCategories() throws RemoteException {
		return reverseCategories;
	}

	@Override
	public int addDoubleCategory(String name) throws RemoteException {
		doubleAuctionItemCounter++;
		doubleCategories.put(doubleAuctionItemCounter, name);
		return doubleAuctionItemCounter;
	}

	@Override
	public Map<Integer, ForwardAuctionItem> getForwardAuctionItems() throws RemoteException {
		return forwardAuctionItems;
	}

	@Override
	public Map<Integer, ReverseAuctionItem> getReverseAuctionItems(int categoryID) throws RemoteException {
		Map<Integer, ReverseAuctionItem> reverseAuctionItemsMap = reverseAuctionItems.get(CATEGORIES[categoryID]);
		List<Map.Entry<Integer, ReverseAuctionItem>> reverseAuctionItemsList = new ArrayList<>(reverseAuctionItemsMap.entrySet());

		reverseAuctionItemsList.sort(Comparator.comparingDouble(entry -> entry.getValue().getStartingPrice()));

		Map<Integer, ReverseAuctionItem> sortedReverseAuctionItems = new LinkedHashMap<>();
		for (Map.Entry<Integer, ReverseAuctionItem> entry : reverseAuctionItemsList) {
			sortedReverseAuctionItems.put(entry.getKey(), entry.getValue());
		}

		return sortedReverseAuctionItems;
	}
	
	@Override
	public Map<Integer, String> getDoubleAuctions() throws RemoteException {
		return doubleCategories;
	}

	@Override
	public int addSeller(String name, String email) throws RemoteException {
		sellerCounter++;
		Person person = new Person(sellerCounter, name, email, "Seller");
		sellers.put(sellerCounter, person);

		return sellerCounter;
	}

	@Override
	public Map<Integer, Person> getSellers() throws RemoteException {
		return sellers;
	}

	@Override
	public int addBuyer(String name, String email) throws RemoteException {
		buyerCounter++;
		Person person = new Person(buyerCounter, name, email, "Buyer");
		buyers.put(buyerCounter, person);

		return buyerCounter;
	}

	@Override
	public Map<Integer, Person> getBuyers() throws RemoteException {
		return buyers;
	}

	@Override
	public Person getPerson(int id, String type) throws RemoteException {
		String dataToSign = Integer.toString(id);
        byte[] signature = signData(dataToSign);

		if (verifySignature(dataToSign, signature)) {
			if (type.equals("Seller")) {
				return sellers.get(id);
			} else {
				return buyers.get(id);
			}
		} else {
			return new Person();
		}
	}
	
	@Override
	public String auctionBid(int buyerID, int itemID, double bid) throws RemoteException {
		String dataToSign = Integer.toString(buyerID) + Integer.toString(itemID) + Double.toString(bid);
        byte[] signature = signData(dataToSign);

		AuctionItem item = auctionItems.get(itemID);
		String message = "";

		if (verifySignature(dataToSign, signature)) {
			if (item.getClass().getSimpleName().equals("ForwardAuctionItem")) {
				if (forwardBids.containsKey(itemID)) {
					if (forwardBids.get(itemID).containsKey(buyerID)) {
						forwardBids.get(itemID).replace(buyerID, bid);
					} else {
						forwardBids.get(itemID).put(buyerID, bid);
					}
				} else {
					Map<Integer, Double> bids = new HashMap<>();
					bids.put(buyerID, bid);
					forwardBids.put(itemID, bids);
				}

				message = "\nThanks for bidding " + df.format(bid) +  "€ on \"" + item.getTitle() + "\"";

			} else if (item.getClass().getSimpleName().equals("ReverseAuctionItem")) {
				if (reverseBids.containsKey(itemID)) {
					if (reverseBids.get(itemID).containsKey(buyerID)) {
						reverseBids.get(itemID).replace(buyerID, bid);
					} else {
						reverseBids.get(itemID).put(buyerID, bid);
					}
				} else {
					Map<Integer, Double> bids = new HashMap<>();
					bids.put(buyerID, bid);
					reverseBids.put(itemID, bids);
				}

				message = "\nThanks for bidding " + df.format(bid) +  "€ on \"" + item.getTitle() + "\"";

			}

			return message;
		} else {
			return message;
		}
	}

	public String doubleAuctionBid(int buyerID, int categoryID, double bid) throws RemoteException {
		String dataToSign = Integer.toString(buyerID) + Integer.toString(categoryID) + Double.toString(bid);
        byte[] signature = signData(dataToSign);

		String category = doubleCategories.get(categoryID);
		
		if (verifySignature(dataToSign, signature)) {
			if (doubleBids.containsKey(categoryID)) {
				if (doubleBids.get(categoryID).containsKey(buyerID)) {
					doubleBids.get(categoryID).replace(buyerID, bid);
				} else {
					doubleBids.get(categoryID).put(buyerID, bid);
				}
			} else {
				Map<Integer, Double> bids = new HashMap<>();
				bids.put(buyerID, bid);
				doubleBids.put(categoryID, bids);
			}

			String message = "\nThanks for bidding " + df.format(bid) +  "€ on \"" + category + "\"";
			return message;
		} else {
			return "";
		}
	}

	// 1. Above starting price
	// 2. Below starting price but above minimum price
	// 3. Below minimum price
	// 4. (Double auction) couldn't find match
	// -1. No bids
	@Override
	public Auction checkAuctionState(int itemID) throws RemoteException {
		AuctionItem item = auctionItems.get(itemID);
		String auctionType = item.getClass().getSimpleName();
		Map.Entry<Integer, Double> maxBidEntry = null; // <buyerID, bid>
						
		if (auctionType.equals("ForwardAuctionItem")) {
			ForwardAuctionItem forwardItem = (ForwardAuctionItem) item;
			if (forwardBids.containsKey(itemID)) {
				Map<Integer, Double> bids = forwardBids.get(itemID);
				maxBidEntry = bids.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
			} else return new Auction(itemID, -1, forwardItem); // no bid founded for this item
			if (maxBidEntry.getValue() >= forwardItem.getStartingPrice()) {
				return new Auction(itemID, 1, forwardItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			} else if (maxBidEntry.getValue() < forwardItem.getStartingPrice() && maxBidEntry.getValue() >= forwardItem.getMinimumPrice()) {
				return new Auction(itemID, 2, forwardItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			} else return new Auction(itemID, 3, forwardItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			
		} else if (auctionType.equals("ReverseAuctionItem")) {
			ReverseAuctionItem reverseItem = (ReverseAuctionItem) item;
			if (reverseBids.containsKey(itemID)) {
				Map<Integer, Double> bids = reverseBids.get(itemID);
				maxBidEntry = bids.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .orElse(null);
			} else return new Auction(itemID, -1, reverseItem);
			if (maxBidEntry.getValue() >= reverseItem.getStartingPrice()) {
				return new Auction(itemID, 1, reverseItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			} else if (maxBidEntry.getValue() < reverseItem.getStartingPrice() && maxBidEntry.getValue() >= reverseItem.getMinimumPrice()) {
				return new Auction(itemID, 2, reverseItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			} else return new Auction(itemID, 3, reverseItem, item.getStartingPrice(), maxBidEntry.getValue(), maxBidEntry.getKey());
			
		} else if (auctionType.equals("DoubleAuctionItem")) {
			DoubleAuctionItem doubleAuctionItem = (DoubleAuctionItem) item;
			int doubleAuctionCategoryID = doubleAuctionItem.getDoubleAuction();
			Map<Integer, Double> bids = doubleBids.get(doubleAuctionCategoryID); 

			if (bids == null || bids.isEmpty()) return new Auction(itemID, -1, doubleAuctionItem);

			Map<Integer, DoubleAuctionItem> items = doubleAuctionItems.get(doubleAuctionCategoryID);
			ArrayList<Integer> orderedPrices = sortDoublePrices(items); // item IDs
			Map<Integer, Integer> orderedBids = sortDoubleBids(bids); // <index, buyerID>
			int index = orderedPrices.indexOf(itemID);
			
			if (orderedBids.size() - 1 < index) return new Auction(itemID, 4, doubleAuctionItem);

			Double price = items.get(itemID).getStartingPrice();
			int buyerID = orderedBids.get(index);
			Double bid = doubleBids.get(doubleAuctionCategoryID).get(buyerID);
			System.out.println();

			if (bid >= price) return new Auction(itemID, 1, doubleAuctionItem, price, bid, buyerID);
			else if(bid < price) return new Auction(itemID, 3, doubleAuctionItem, price, bid, buyerID);
		}

		return null;
	}

	/**
	 * Sort the prices for the items in a double auction from lowest to highest
	 * 
	 * @param items a hashmap of the items in this specific double auction
	 * @return an array list with the ids of the items once sorted
	 */
	private ArrayList<Integer> sortDoublePrices (Map<Integer, DoubleAuctionItem> items) {
        ArrayList<Map.Entry<Integer, DoubleAuctionItem>> entryList = new ArrayList<>(items.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<Integer, DoubleAuctionItem>>() {
            @Override
            public int compare(Map.Entry<Integer, DoubleAuctionItem> entry1, Map.Entry<Integer, DoubleAuctionItem> entry2) {
                return Double.compare(entry1.getValue().getStartingPrice(), entry2.getValue().getStartingPrice());
            }
        });

        ArrayList<Integer> sortedKeys = new ArrayList<>();

        for (Map.Entry<Integer, DoubleAuctionItem> entry : entryList) {
			System.out.println("Item" + entry.getKey() + ": " + entry.getValue().getStartingPrice());
            sortedKeys.add(entry.getKey());
        }

        return sortedKeys;
	}

	/**
	 * Sort the biddings for the items in a double auction from highest to lowest
	 * @param bids a hasmap with all the bids for a specific double auction
	 * @return a hashmap with key the value that represents an array and value the id of the buyers
	 */
	private Map<Integer, Integer> sortDoubleBids (Map<Integer, Double> bids) {
        ArrayList<Map.Entry<Integer, Double>> entryList = new ArrayList<>(bids.entrySet());

        Collections.sort(entryList, Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));

        Map<Integer, Integer> sortedMap = new HashMap<>();
        int index = 0;
        for (Map.Entry<Integer, Double> entry : entryList) {
			System.out.println("Buyer" + entry.getKey() + ": " + entry.getValue());
            sortedMap.put(index++, entry.getKey());
        }

        return sortedMap;
	}

	@Override
	public String closeAuction(Auction auction) throws RemoteException {
		String dataToSign = auction.toString();
        byte[] signature = signData(dataToSign);

		int itemID = auction.getId();
		AuctionItem item = auctionItems.get(itemID);
		String auctionType = item.getClass().getSimpleName();
		int state = auction.getState();

		if (verifySignature(dataToSign, signature)) {
			auctionItems.remove(itemID);

			if (auctionType.equals("ForwardAuctionItem")) {
				forwardAuctionItems.remove(itemID);
				forwardBids.remove(itemID);
			} else if (auctionType.equals("ReverseAuctionItem")) {
				ReverseAuctionItem reverseAuctionItem = (ReverseAuctionItem) item;
				reverseBids.remove(itemID);
				reverseAuctionItems.get(reverseAuctionItem.getCategory()).remove(itemID);
				if (reverseAuctionItems.get(reverseAuctionItem.getCategory()).isEmpty()) {
					reverseAuctionItems.remove(reverseAuctionItem.getCategory());
					List<String> list = Arrays.asList(CATEGORIES);
					reverseCategories.remove(list.indexOf(reverseAuctionItem.getCategory()));
				}
			} else if (auctionType.equals("DoubleAuctionItem")) {
				DoubleAuctionItem doubleAuctionItem = (DoubleAuctionItem) item;
				doubleBids.get(doubleAuctionItem.getDoubleAuction()).remove(auction.getBuyerID());
				doubleAuctionItems.get(doubleAuctionItem.getDoubleAuction()).remove(itemID);
				if (doubleAuctionItems.get(doubleAuctionItem.getDoubleAuction()).isEmpty()) {
					doubleAuctionItems.remove(doubleAuctionItem.getDoubleAuction());
					doubleCategories.remove(doubleAuctionItem.getDoubleAuction());
				}
			}
			
			if (state == 1) {
				return "The winner is buyer with ID " + auction.getBuyerID() + ". You got a profit of " + 
					df.format(auction.getProfit()) + "€";
			} else if (state == 2) {
				return "The winner is buyer with ID " + auction.getBuyerID() + ". With a bidding of " + 
					df.format(auction.getBid()) + "€. Your starting price was " + df.format(auction.getPrice());
			} else if (state == 3) {
				return "There was no winner for this item. They didn't bid enough.";
			} else if (state == 4) {
				return "There was no winner for this item. Couldn't find a match for the item.";
			} else {
				return "There was no winner for this item. No one bid for the item.";
			}
		} else {
			return "";
		}
	}

  	public static void main(String[] args) {
	  	try {
	  		Server s = new Server();
		   	String name = "myserver";
		   	IAuction stub = (IAuction) UnicastRemoteObject.exportObject(s, 0);
		   	Registry registry = LocateRegistry.getRegistry();
		   	registry.rebind(name, stub);
		   	System.out.println("Server ready");
		} catch (Exception e) {
		   	System.err.println("Exception:");
		   	e.printStackTrace();
		}
	}
}
