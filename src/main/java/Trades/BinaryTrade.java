package Trades;
import java.util.Calendar;
public class BinaryTrade extends Trade {
  private Calendar experationDate;
  
  public BinaryTrade (double purchasePrice, int quantity, int tradeLength, Calendar activeDay) {
    super(purchasePrice, quantity);
    
    // Set experationdate ---------------------
    experationDate = activeDay;
    experationDate.add(Calendar.DATE, tradeLength);
    // ----------------------------------------
  }
  public Calendar getExperationDate() {
    return experationDate;
  }
}