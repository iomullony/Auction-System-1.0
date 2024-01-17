import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IAuction extends Remote{
  String[] getCategories() throws RemoteException;
  int addAuctionItem(AuctionItem item) throws RemoteException;
  Map<Integer, AuctionItem> getAuctionItems() throws RemoteException;
  void addReverseCategory(int categoryID) throws RemoteException;
  Map<Integer, String> getReverseCategories() throws RemoteException;
  int addDoubleCategory(String name) throws RemoteException;
  Map<Integer, ForwardAuctionItem> getForwardAuctionItems() throws RemoteException;
  Map<Integer, ReverseAuctionItem> getReverseAuctionItems(int categoryID) throws RemoteException;
  Map<Integer, String> getDoubleAuctions() throws RemoteException;
  int addSeller(String name, String email) throws RemoteException;
  Map<Integer, Person> getSellers() throws RemoteException;
  int addBuyer(String name, String email) throws RemoteException;
  Map<Integer, Person> getBuyers() throws RemoteException;
  Person getPerson(int id, String type) throws RemoteException;
  String auctionBid(int buyerID, int itemID, double bid) throws RemoteException;
  String doubleAuctionBid(int buyerID, int categoryID, double bid) throws RemoteException;
  Auction checkAuctionState(int itemID) throws RemoteException;
  String closeAuction(Auction auction) throws RemoteException;

  byte[] signData(String data) throws RemoteException;
  boolean verifySignature(String data, byte[] signature) throws RemoteException;

}