package Trades;
import java.util.Calendar;
import Markets.*;
public class FuturesTrade extends Trade {
  private final double priceTarget;
  private final Calendar experationDate;
  private final futuresTradeType tradeType;
  
  public static enum futuresTradeType { BUY, SHORT };
  public FuturesTrade(double purchasePrice, int quantity, MarketSimulator marketSimulator, double priceTarget,
                      int tradeLength, futuresTradeType tradeType, Calendar activeDate) {
    super(purchasePrice, quantity, marketSimulator);
    this.priceTarget = priceTarget;
    this.tradeType = tradeType;

    // Set experationdate ---------------------
    this.experationDate = (Calendar) activeDate.clone(); // Change this to the active date
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