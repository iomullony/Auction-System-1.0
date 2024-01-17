import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.lang.Math;

public class Seller {

	private static Scanner sc = new Scanner(System.in);
	private static DecimalFormat df = new DecimalFormat("0.00"); 
	private static Person seller = new Person();

	/**
	 * Get choice for what the client wants to do
	 * 
	 * @return return the integer of the choice validated
	 */
	private static int getChoice(int boundary) {

		System.out.print("Choose an option: ");
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
			input = sc.nextLine();

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
	private static int getSellerID(Map<Integer, Person> sellers) {
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
			return getSellerID(sellers);
		}
	}

	/**
	 * Checks the inputs of the user for a forward and reverse auction
	 * 
	 * @param type the type that the input should be
	 * @param message the message you want to show for the request
	 * @return returns a string with the validated input
	 */
	private static String simpleAuction(String type, String message) {
		
		System.out.print(message);
		String input = "";
	
		// Check if it is a double
		if (type.equals("double")) {
			double number = -1;
			while (!sc.hasNextDouble()) {
				System.out.print("Wrong input, try again: ");
				sc.next();
			}
			number = sc.nextDouble();
			input = df.format(Math.abs(number));
		}
		// If it is the item's name
		else if (type.equals("name")) {
			sc.nextLine();
			input = sc.nextLine(); // Consume the newline character
			return input;
		}
		// If it is the item's description
		else if (type.equals("description")) {
			sc.nextLine(); // Consume the newline character
			input = sc.nextLine();
			return input;
		}
	
		return input;
	}

	/**
	 * To get a choice about what you want to do when you choose first to add an item in a double auction
	 * 
	 * @return the choice you have chosen
	 */
	static int getDoubleAuctionChoice() {

		System.out.println("\nWhat would you like to do?");
		
		System.out.println("1. Join existing one");
		System.out.println("2. Create new one");
		System.out.println("3. Cancel action");

		return getChoice(3);
	}

	/**
	 * Get the information to put an item in a double auction
	 * 
	 * @param type to know what check it has to do
	 * @param message the message you want to display to get the input
	 * @return a string with the information
	 */
	static String doubleAuction(String type, String message) {
		
		System.out.print(message);
		String input = "";
	
		if (type.equals("double auction")) {
			sc.nextLine();
			input = sc.nextLine();

			if (input.isEmpty()) {
				System.out.println("The field can't be empty");
				return doubleAuction(type, message);
			}

			return input;
		} else if (type.equals("name")) {
			sc.nextLine();
			input = sc.nextLine();

			if (input.isEmpty()) {
				System.out.println("The field can't be empty");
				return doubleAuction(type, message);
			}
			
			return input;
		} else if (type.equals("else")) {
			input = sc.nextLine();

			if (input.isEmpty()) {
				System.out.println("The field can't be empty");
				return doubleAuction(type, message);
			}
			
			return input;
		} else if (type.equals("double")) {
			double number = -1;
			while (!sc.hasNextDouble()) {
				System.out.print("Wrong input, try again: ");
				sc.next();
			}
			number = sc.nextDouble();
			input = df.format(Math.abs(number));
		}
	
		return input;
	}

	/**
	 * Choose a double auction to join
	 * 
	 * @param doubleAuctions current double auctions
	 * @return the Id of the auction you want to join
	 */
	static int checkDoubleAuction(Map<Integer, String> doubleAuctions) {
		int id = -1;
		System.out.print("Which double auction would you like to join? ");

		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		// Check if the ID exists
		if (doubleAuctions.containsKey(id)) {
			return id;
		} else {
			System.out.println("That ID doesn't exist. Please try again.");
			return checkDoubleAuction(doubleAuctions);
		}
	}
	
	/**
	 * Check the seller can close the auction
	 * 
	 * @param yourAuctions
	 * @return
	 */
	static int checkCloseAuction(Map<Integer, AuctionItem> yourAuctions) {
		int id = -1;
		System.out.print("Which one do you want to close? ");

		while (!sc.hasNextInt()) {
			System.out.print("Wrong input, try again: ");
			sc.next();
		}
		id = sc.nextInt();

		// Check if the ID exists
		if (yourAuctions.containsKey(id)) {
			return id;
		} else {
			System.out.println("Wrong ID. Make sure the ID exists and it is your item.");
			return checkCloseAuction(yourAuctions);
		}
	}

	public static void main(String[] args) {

		if (args.length != 0) {
			System.out.println("Usage: java Seller");
			return;
       	}

        try {
			String name = "myserver";
			Registry registry = LocateRegistry.getRegistry("localhost");
			IAuction server = (IAuction) registry.lookup(name);

			String[] categories = server.getCategories();

			// Log in
			System.out.println("WELCOME!\nYou signed as a seller.");
			System.out.println("Are you new?");
			System.out.println("1. Yes");
			System.out.println("2. No");

			int newUser = getChoice(2);
			if (newUser == 1) {
				System.out.println("\nLet's create an account then. We will need some personal information.");
				String sellerName = getPersonalInformation("name", "What's your name? ");
				String sellerEmail = getPersonalInformation("email", "What's your email? ");
				int id = server.addSeller(sellerName, sellerEmail);
				System.out.println("\nYour account was successfully created!");
				System.out.println("Remember your ID is: " + id);
			}

			System.out.println("\nLog in to AUCTIONS.");
			int sellerID = getSellerID(server.getSellers());

			byte[] signature = server.signData(Integer.toString(sellerID));

			if (server.verifySignature(Integer.toString(sellerID), signature)) {
                seller = server.getPerson(sellerID, "Seller");
                System.out.println("\nDigital signature successfully verified.");
            } else {
                System.out.println("\nSignature verification failed.");
				System.exit(0);
            }
			
			System.out.print("\nWelcome " + seller.getName() + "!\n");

			while (true) {

				// Options
				System.out.println("\nWhat would you like to do?");
				System.out.println("1. Get list of current auctions");
				System.out.println("2. Add an auction for an item");
				System.out.println("3. Close an auction");
				System.out.println("4. Exit\n");

				// What do you want to do?
				int choice = getChoice(4);

				switch(choice) {

					// Get current auctions
					case 1:
						Map<Integer, AuctionItem> auctionItems = server.getAuctionItems();
						Map<Integer, AuctionItem> otherItems = new HashMap<>();
						Map<Integer, AuctionItem> yourItems = new HashMap<>();

						if (!auctionItems.isEmpty()) {
							for (Map.Entry<Integer, AuctionItem> entry : auctionItems.entrySet()) {
								Integer key = entry.getKey();
								AuctionItem value = entry.getValue();
						
								if (value.getSeller().getId() == seller.getId()) {
									yourItems.put(key, value);
								} else otherItems.put(key, value);
							}
						}
						
						// Get all your items
						System.out.println("\nThese are your current auctions:");
						if (!yourItems.isEmpty()) {
							for (Map.Entry<Integer, AuctionItem> entry : yourItems.entrySet()) {
								Integer key = entry.getKey();
								AuctionItem value = entry.getValue();
						
								System.out.println(key + ". " + value.getTitle() + " (" + df.format(value.getStartingPrice()) + "€)");
								System.out.println("    Type of auction: " + value.getClass().getSimpleName());
								System.out.println("    " + value.getDescription());
							}
						} else System.out.println("You don't have any item yet.");
						
						// Get items from other sellers
						System.out.println("\nThese are other current auctions:");
						if (!otherItems.isEmpty()) {
							for (Map.Entry<Integer, AuctionItem> entry : otherItems.entrySet()) {
								Integer key = entry.getKey();
								AuctionItem value = entry.getValue();
						
								System.out.println(key + ". " + value.getTitle() + " (" + df.format(value.getStartingPrice()) + "€)");
								System.out.println("    Type of auction: " + value.getClass().getSimpleName());
								System.out.println("    " + value.getDescription());
							}
						} else System.out.println("There are no auctions yet from other sellers.");

						break;

					// Add auction for an item
					case 2:
						System.out.println("\nYou chose to put an item for auction. What type of auction is it going to be?");
						System.out.println("1. Forward Auction");
						System.out.println("2. Reverse Auction");
						System.out.println("3. Double Auction");
						System.out.println("4. Cancel action");
						
						int auction = getChoice(4);

						switch (auction) {
							// Forward Auction
							case 1:
								System.out.println("\nYou chose to create a Forward Auction, we will need some details.");

								String itemName1 = simpleAuction("name", "Set a name: ");
								double startingPrice1 = Double.parseDouble(simpleAuction("double", "Set a starting price: "));
								String description1 = simpleAuction("description", "Set a description: ");
								double minimumPrice1 = Double.parseDouble(simpleAuction("double", "Set a minimum price: "));
								while (minimumPrice1 > startingPrice1) {
									System.out.println("The minimum price has to be smaller or equal than the starting price.");
									minimumPrice1 = Double.parseDouble(simpleAuction("double", "Set a minimum price: "));
								}

								AuctionItem item1 = new ForwardAuctionItem(itemName1, startingPrice1, description1, minimumPrice1, seller);

								int id1 = -1;

								byte[] signature2 = server.signData(item1.toString());

								if (server.verifySignature(item1.toString(), signature2)) {
									id1 = server.addAuctionItem(item1);
									System.out.println("\nDigital signature successfully verified.");
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}

								System.out.println("\nSuccessfully item added to auction with ID " + id1);
								
								break;

							// Reverse Auction
							case 2:
								System.out.println("\nYou chose to create a Reverse Auction, we will need some details.");

								String itemName2 = simpleAuction("name", "Set a name: ");
								double startingPrice2 = Double.parseDouble(simpleAuction("double", "Set a starting price: "));
								String description2 = simpleAuction("description", "Set a description: ");
								double minimumPrice2 = Double.parseDouble(simpleAuction("double", "Set a minimum price: "));
								while (minimumPrice2 > startingPrice2) {
									System.out.println("The minimum price has to be smaller or equal than the starting price. Try again.");
									minimumPrice2 = Double.parseDouble(simpleAuction("double", "Set a minimum price: "));
								}
								System.out.println("Choose a category for the item: ");
								for (int i = 0; i < categories.length; i++) {
									System.out.println("  " + Integer.toString(i + 1) + ". " + categories[i]);
								}
								int categoryID2 = getChoice(categories.length);
								categoryID2--;

								AuctionItem item2 = new ReverseAuctionItem(itemName2, startingPrice2, description2, minimumPrice2, categories[categoryID2], seller);
								server.addReverseCategory(categoryID2);

								int id2 = -1;

								byte[] signature3 = server.signData(item2.toString());

								if (server.verifySignature(item2.toString(), signature3)) {
									id2 = server.addAuctionItem(item2);
									System.out.println("\nDigital signature successfully verified.");
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}

								System.out.println("\nSuccessfully item added to auction with ID " + id2);
								
								break;

							// Double Auction
							case 3:
								Map<Integer, String> doubleAuctions = server.getDoubleAuctions();
								int doubleAuctionID = -1;
								String title3 = "";

								System.out.println("\nYou chose to create a Double Auction");
								System.out.println("These are the current double auctions: ");

								if (!doubleAuctions.isEmpty()) {
									for (Map.Entry<Integer, String> entry : doubleAuctions.entrySet()) {
										Integer key = entry.getKey();
										String value = entry.getValue();
								
										System.out.println(key + ". " + value);
									}
								} else System.out.println("There are no double auctions right now.");

								int doubleAuctionChoice = getDoubleAuctionChoice();

								if (doubleAuctionChoice == 1 && doubleAuctions.isEmpty()) {
									System.out.println("There are no double auctions you can join.");
									break;
								} else if (doubleAuctionChoice == 3) {
									break;
								} else if (doubleAuctionChoice == 1) {
									doubleAuctionID = checkDoubleAuction(doubleAuctions);
									System.out.println("\nNow we will need some details about the item you want to add:");
									title3 = doubleAuction("name", "Name of your item: ");
								} else if (doubleAuctionChoice == 2) {
									System.out.println("\nYou chose to create a new Double Auction. We will need some details:");
									String doubleAuctionName = doubleAuction("double auction", "Name for the double auction (Choose a general name): ");
									doubleAuctionID = server.addDoubleCategory(doubleAuctionName);
									title3 = doubleAuction("else", "Name of your item: ");
								}
								
								String description3 = doubleAuction("else", "Description for your item: ");
								Double startingPrice3 = Double.parseDouble(doubleAuction("double", "Price of your item: "));

								DoubleAuctionItem item3 = new DoubleAuctionItem(title3, startingPrice3, description3, doubleAuctionID, seller, new Person());
								int id3 = -1;

								byte[] signature4 = server.signData(item3.toString());

								if (server.verifySignature(item3.toString(), signature4)) {
									id3 = server.addAuctionItem(item3);
									System.out.println("\nDigital signature successfully verified.");
								} else {
									System.out.println("\nSignature verification failed.");
									System.exit(0);
								}


								System.out.println("\nSuccessfully item added to auction with ID " + id3);
								
								break;

							// Cancel action
							case 4:
								
								break;
						
							default:
								System.out.println("Invalid choice. Please select a valid option.");
								break;
						}

						break;

					// Close an auction
					case 3:
						Map<Integer, AuctionItem> auctionItems3 = server.getAuctionItems();
						Map<Integer, AuctionItem> yourItems3 = new HashMap<>();
						
						if (!auctionItems3.isEmpty()) {
							for (Map.Entry<Integer, AuctionItem> entry : auctionItems3.entrySet()) {
								Integer key = entry.getKey();
								AuctionItem value = entry.getValue();
						
								if (value.getSeller().getId() == seller.getId()) {
									yourItems3.put(key, value);
								}
							}
						}

						System.out.println("\nYou chose to close an auction.");

						if (yourItems3.isEmpty()) {
							System.out.println("\nYou don't have any auction to close.");
							break;
						}

						int itemID = checkCloseAuction(yourItems3);
						Auction theAuction = server.checkAuctionState(itemID);
						int state = theAuction.getState();

						if (state == 1) System.out.println("You get a profit.");
						else if (state == 2) System.out.println("Between your minimum and starting price.");
						else if (state == 3) System.out.println("They are giving less than your minimum price.");
						else if (state == 4) System.out.println("Couldn't find a match.");
						else if (state == -1) System.out.println("No one bid for this item.");

						System.out.println("\nDo you want to close the auction?");
						System.out.println("1. Yes");
						System.out.println("2. No");

						int closeAuction = getChoice(2);

						if (closeAuction == 1) {
							byte[] signature2 = server.signData(theAuction.toString());

							if (server.verifySignature(theAuction.toString(), signature2)) {
								System.out.println("\nDigital signature successfully verified.");
								System.out.println("\n" + server.closeAuction(theAuction));
							} else {
								System.out.println("\nSignature verification failed.");
								System.exit(0);
							}
						}

						break;

					// Exit the program
					case 4:
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
