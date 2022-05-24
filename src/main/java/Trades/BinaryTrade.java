package Trades;
import Markets.MarketSimulator;

import java.util.Calendar;
public class BinaryTrade extends Trade {
  private Calendar experationDate;

  public BinaryTrade (double purchasePrice, int quantity, MarketSimulator marketSimulator,
                      int tradeLength, Calendar activeDay) {
    super(purchasePrice, quantity, marketSimulator);
    
    // Set experationdate ---------------------
    experationDate = (Calendar)activeDay.clone();
    experationDate.add(Calendar.DATE, tradeLength);
    // ----------------------------------------
  }
  public Calendar getExperationDate() {
    return experationDate;
  }
}