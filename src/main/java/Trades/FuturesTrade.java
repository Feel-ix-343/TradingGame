package Trades;
import java.util.Calendar;
import Markets.*;
public class FuturesTrade extends Trade {
  private double priceTarget;
  private Calendar experationDate;
  private futuresTradeType tradeType;
  
  public static enum futuresTradeType { BUY, SHORT };
  public FuturesTrade(double purchasePrice, int quantity, double priceTarget, int tradeLength, futuresTradeType tradeType, Calendar activeDay) {
    super(purchasePrice, quantity);
    this.priceTarget = priceTarget;
    this.tradeType = tradeType;

    // Set experationdate ---------------------
    experationDate = activeDay; // Change this to the active date
    experationDate.add(Calendar.DATE, tradeLength);
    // ----------------------------------------
  }
  public Calendar getExperationDate() {
    return experationDate;
  }
  public double getPriceTarget() {
    return priceTarget;
  }
  public futuresTradeType getTradeType() {
    return tradeType;
  }
}