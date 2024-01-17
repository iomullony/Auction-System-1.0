import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Map;

public class Buyer {

	private static Scanner sc = new Scanner(System.in);
	private static DecimalFormat df = new DecimalFormat("0.00"); 
	private static Person buyer = new Person();

	/**
	 * Get choice for what the client wants to do
	 * 
	 * @return return the integer of the choice validated
	 */
	private static int getChoice(int boundary) {

		System.out.print("Choose a number: ");
		int choice = -1;

		// Check it is an integer
		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		
		choice = sc.nextInt();

		// Check the choice is valid
		if ((choice < 1) ||  (choice > boundary)) {
			System.out.println("Make sure the number is in the options");
			return getChoice(boundary);
		}

		return choice;
	}

	/**
	 * Validates the personal information input
	 * 
	 * @param type to know what it the information we want to validate
	 * @param message message to show to get the input
	 * @return returns a string with the validated data
	 */
	private static String getPersonalInformation(String type, String message) {
		
		System.out.print(message);
		String input = "";
	
		// If it is the buyer's name
		if (type.equals("name")) {
			sc.nextLine();
			input = sc.nextLine(); // Consume the newline character

			// The name can't be empty
			if (input.isEmpty()) {
				System.out.println("Name cannot be empty.");
				return getPersonalInformation(type, message);
			}

			return input;
		}
		// If it is the buyer's email
		else if (type.equals("email")) {
			input = sc.next(); // email should be only one string
			return input;
		}
	
		return input;
	}

	/**
	 * Log in with an ID
	 * 
	 * @param sellers all selelrs that have already an account
	 * @return the input ID validated
	 */
	private static int getBuyerID(Map<Integer, Person> sellers) {
		int id = -1;

		System.out.print("What's your ID: ");

		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		// Check if the ID exists
		if (sellers.containsKey(id)) {
			return id;
		} else {
			System.out.println("That ID doesn't exist. Please try again.");
			return getBuyerID(sellers);
		}
	}

	/**
	 * Gets the forward item a client wants to bid on
	 * 
	 * @param auctionItems a list with all the items
	 * @return returns the ID of the item the buyer chose
	 */
	public static int getForwardItem(Map<Integer, AuctionItem> auctionItems) {
		System.out.print("Item's ID you want to bid for: ");
		int id = -1;

		// Check the input is an integer
		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		if (auctionItems.keySet().contains(id)) {
			return id;
		} else {
			System.out.println("Make sure you put a correct ID. Try again.");
			return getForwardItem(auctionItems);
		}
	}

	/**
	 * Gets the reverse item a client wants to bid on
	 * 
	 * @param auctionItems a list with all the items
	 * @return returns the ID of the item the buyer chose
	 */
	public static int getReverseItem(Map<Integer, ReverseAuctionItem> auctionItems) {
		System.out.print("Item's ID you want to bid for: ");
		int id = -1;

		// Check the input is an integer
		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		if (auctionItems.keySet().contains(id)) {
			return id;
		} else {
			System.out.println("Make sure you put a correct ID. Try again.");
			return getReverseItem(auctionItems);
		}
	}

	/**
	 * Gets the double item a client wants to bid on
	 * 
	 * @param auctionItems a list with all the items
	 * @return returns the ID of the item the buyer chose
	 */
	public static int getDoubleItem(Map<Integer, String> auctionItems) {
		System.out.print("Type of item you want to bid for: ");
		int id = -1;

		// Check the input is an integer
		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		if (auctionItems.keySet().contains(id)) {
			return id;
		} else {
			System.out.println("Make sure you put a correct ID. Try again.");
			return getDoubleItem(auctionItems);
		}
	}

	/**
	 * Get category of a reverse version
	 * 
	 * @param categories current categories
	 * @return returns the Id of the category
	 */
	public static int getCategory(Map<Integer, String> categories) {
		System.out.print("Category ID: ");
		int id = -1;

		// Check the input is an integer
		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt() - 1;

		if (categories.keySet().contains(id)) {
			return id;
		} else {
			System.out.println("Make sure you put a correct ID. Try again.");
			return getCategory(categories);
		}
	}

	/**
	 * Valdiates the bid
	 * 
	 * @return returns how much money they have bid
	 */
	private static double getBid() {

		double bid = -1;
		System.out.print("How much would you like to bid? ");
		
		// Checks if it is a double
		while (!sc.hasNextDouble()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		bid = sc.nextDouble();

		return Double.parseDouble(df.format(bid));
	}

	public static void main(String[] args) {

		if (args.length != 0) {
			System.out.println("Usage: java Buyer");
			return;
       	}

        try {
			String name = "myserver";
			Registry registry = LocateRegistry.getRegistry("localhost");
			IAuction server = (IAuction) registry.lookup(name);
			
			String[] categories = server.getCategories();

			System.out.println("WELCOME!\nYou signed as a buyer.");
			System.out.println("Are you new?");
			System.out.println("1. Yes");
			System.out.println("2. No");

			int newUser = getChoice(2);
			if (newUser == 1) {
				System.out.println("\nLet's create an account then. We will need some personal information.");
				String buyerName = getPersonalInformation("name", "What's your name? ");
				String buyerEmail = getPersonalInformation("email", "What's your email? ");
				int id = server.addBuyer(buyerName, buyerEmail);
				System.out.println("\nYour account was successfully created!");
				System.out.println("Remember your ID is: " + id);
			}

			System.out.println("\nLog in to AUCTIONS.");
			int buyerID = getBuyerID(server.getBuyers());

			byte[] signature = server.signData(Integer.toString(buyerID));

			if (server.verifySignature(Integer.toString(buyerID), signature)) {
                buyer = server.getPerson(buyerID, "Buyer");
                System.out.println("\nDigital signature successfully verified.");
            } else {
                System.out.println("\nSignature verification failed.");
				System.exit(0);
            }
			
			System.out.print("\nWelcome " + buyer.getName() + "!\n");

			while (true) {

				System.out.println("\nWhat would you like to do?");
				System.out.println("1. Get list of current auctions");
				System.out.println("2. Bid for an item");
				System.out.println("3. Exit\n");

				int choice = getChoice(3);
				Map<Integer, AuctionItem> auctionItems = server.getAuctionItems();

				switch(choice) {

					// Get current auctions
					case 1:						
						System.out.println("\nTheses are the current auctions:");

						if (!auctionItems.isEmpty()) {
							for (Map.Entry<Integer, AuctionItem> entry : auctionItems.entrySet()) {
								Integer key = entry.getKey();
								AuctionItem value = entry.getValue();
						
								System.out.println(key + ". " + value.getTitle() + " (" + df.format(value.getStartingPrice()) + "€)");
								System.out.println("    Type of auction: " + value.getClass().getSimpleName());
								System.out.println("    " + value.getDescription());
							}
						} else System.out.println("You don't have any item yet.");

						break;

					// Bid for an auction
					case 2:
						System.out.println("\nYou chose to bid for an item. What type of auction do you want to join?");
						System.out.println("1. Forward Auction");
						System.out.println("2. Reverse Auction");
						System.out.println("3. Double Auction");
						System.out.println("4. Cancel action");

						int bidChoice = getChoice(4);

						switch (bidChoice) {
							// Forward Auction
							case 1:
								Map<Integer, ForwardAuctionItem> forwardAuctions = server.getForwardAuctionItems();
								System.out.println("\nYou chose to bid in a Forward Auction. These are the current items:");

								if (!forwardAuctions.isEmpty()) {
									for (Map.Entry<Integer, ForwardAuctionItem> entry : forwardAuctions.entrySet()) {
										Integer key = entry.getKey();
										ForwardAuctionItem value = entry.getValue();
										
										System.out.println(key + ". " + value.getTitle() + " (" + df.format(value.getStartingPrice()) + "€): " + value.getDescription());
									}
								} else {
									System.out.println("There are no items for forward auction.");
									break;
								}
								
								int itemID = getForwardItem(auctionItems);
								double bid = getBid();

								byte[] signature2 = server.signData(Integer.toString(buyerID) + Integer.toString(itemID) + Double.toString(bid));

								if (server.verifySignature(Integer.toString(buyerID) + Integer.toString(itemID) + Double.toString(bid), signature2)) {
									System.out.println("\nDigital signature successfully verified.");
									System.out.println(server.auctionBid(buyer.getId(), itemID, bid));
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}

								break;
								
							// Reverse Auction
							case 2:
								System.out.println("\nYou chose to bid in a Reverse Auction. These are the current items:");
								Map<Integer, String> reverseCategories = server.getReverseCategories();

								if (!reverseCategories.isEmpty()) {
									for (Map.Entry<Integer, String> entry : reverseCategories.entrySet()) {
										Integer key = entry.getKey();
										String value = entry.getValue();
										
										System.out.println((key + 1) + ". " + value);
									}
								} else {
									System.out.println("There are no items for reverse auction.");
									break;
								}
								
								int categoryID = getCategory(reverseCategories);
								Map<Integer, ReverseAuctionItem> reverseItems = server.getReverseAuctionItems(categoryID);

								System.out.println("\nThese are the current item for category \"" + categories[categoryID] + "\":");
								for (Map.Entry<Integer, ReverseAuctionItem> entry : reverseItems.entrySet()) {
									Integer key = entry.getKey();
									ReverseAuctionItem value = entry.getValue();
									
									System.out.println(key + ". " + value.getTitle() + " (" + df.format(value.getStartingPrice()) + "€): " + value.getDescription());
								}

								int itemID2 = getReverseItem(reverseItems);
								double bid2 = getBid();

								byte[] signature3 = server.signData(Integer.toString(buyerID) + Integer.toString(itemID2) + Double.toString(bid2));

								if (server.verifySignature(Integer.toString(buyerID) + Integer.toString(itemID2) + Double.toString(bid2), signature3)) {
									System.out.println("\nDigital signature successfully verified.");
									System.out.println(server.auctionBid(buyer.getId(), itemID2, bid2));
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}
								
								break;
								
							// Double Auction
							case 3:
								System.out.println("\nYou chose to bid in a Double Auction. These are the current items:");
								Map<Integer, String> doubleAuctions = server.getDoubleAuctions();

								if (!doubleAuctions.isEmpty()) {
									for (Map.Entry<Integer, String> entry : doubleAuctions.entrySet()) {
										Integer key = entry.getKey();
										String value = entry.getValue();
										
										System.out.println(key + ". " + value);
									}
								} else {
									System.out.println("There are no items for double auction.");
									break;
								}

								int categoryID3 = getDoubleItem(doubleAuctions);
								double bid3 = getBid();

								byte[] signature4 = server.signData(Integer.toString(buyerID) + Integer.toString(categoryID3) + Double.toString(bid3));

								if (server.verifySignature(Integer.toString(buyerID) + Integer.toString(categoryID3) + Double.toString(bid3), signature4)) {
									System.out.println("\nDigital signature successfully verified.");
									System.out.println(server.doubleAuctionBid(buyer.getId(), categoryID3, bid3));
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}
								
								break;
						
							default:
								break;
						}

						break;

					// Exit the program
					case 3:
						System.out.println("Exiting the program.");
						sc.close();
						System.exit(0);
						break;

					default:
						System.out.println("Invalid choice. Please select a valid option.");
						break;
				}
			}
		}
		catch (Exception e) {
			System.err.println("Exception:");
			e.printStackTrace();
		}
		sc.close();
	}
}
