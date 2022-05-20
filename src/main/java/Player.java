import java.util.ArrayList;
import Trades.*;
public class Player {
  private String name;
  private double balance;
  private ArrayList<Trade> listOfTrades; // Do we need a list of trades if it is only one stock?
  public Player(String name) {
    this.name = name;
    balance = 10000;
    listOfTrades = new ArrayList<Trade>();
  }
  public double getBalance(){
    return balance;
  }
  public double getPortfolioValue() {
    double portfolioValue = balance;
    for (Trade trade: listOfTrades) {
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
  public ArrayList<Trade> getTrades() {
    return listOfTrades;
  }
  public String getName() {
    return name;
  }
}