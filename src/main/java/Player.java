import java.util.ArrayList;
import Trades.*;
public class Player {
  private String name;
  private double balance;
  private final ArrayList<Trade> listOfTrades; // Do we need a list of trades if it is only one stock?
  private double activePL;
  public Player(String name) {
    this.name = name;
    balance = 10000;
    listOfTrades = new ArrayList<Trade>();
    activePL = 0;
  }
  public double getBalance(){
    return balance;
  }
  public double getPortfolioValue() {
    double portfolioValue = balance;
    for (Trade trade: listOfTrades) {
      if (!trade.getActive()) continue;
      portfolioValue += trade.getTradeValue();
    }
    return portfolioValue;
  }
  public void addTrade(Trade newTrade) {
    listOfTrades.add(newTrade);
  }
  public void updateBalance(double change){
    balance += change;
  }

  /**
   * @return A list of trades for different market simulator objects
   */
  public ArrayList<Trade> getTrades() {
    return listOfTrades;
  }
  public void deactivateTrade(Trade trade) {
    listOfTrades.get(listOfTrades.indexOf(trade)).deactivate();
  }
  public void updateTrade(Trade trade, int quantity, double newPurchasePrice) {
    listOfTrades.get(listOfTrades.indexOf(trade)).changeQuantity(quantity);
    listOfTrades.get(listOfTrades.indexOf(trade)).changePurchasePrice(newPurchasePrice);
  }
  public String getName() {
    return name;
  }
  public double getActivePL() {
    return activePL;
  }
  public void setActivePL(double daysPL) {
    activePL = daysPL;
  }
}