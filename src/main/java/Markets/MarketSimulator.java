package Markets;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import java.util.List;

import Trades.*;

import javax.swing.*;

public class MarketSimulator {
  private int gameLength;
  
  private List<HistoricalQuote> priceData;
  private int day;

  public MarketSimulator(String symbol, int gameLength) throws IOException {
    this.gameLength = gameLength;

    // Create the stock data
    Stock stock = YahooFinance.get(symbol); // Can throw IOException
    // Generate a random starting date
    Calendar start = MarketSimulator.getRandomStartingDate(stock, gameLength);
    // Set the end date to [length]
    Calendar end = Calendar.getInstance();
    end.setTime(start.getTime());
    // 20 days after
    end.add(Calendar.DATE, gameLength + 1);

    // Needed to update the library to version 3.16 to get this to work
    priceData = stock.getHistory(start, end, Interval.DAILY);

    day = 0;
  }
  private static Calendar getRandomStartingDate(Stock stock, int gameLength) {
    // Handle case when the stock ipo'd in the past 2 years; not in range TODO
    Calendar today = Calendar.getInstance();
    int randomDay = (int) (Math.random() * (365 * 2));
    int randomDayAdjustedForGameLength = Math.max(randomDay, gameLength);
    today.add(Calendar.DATE, -randomDayAdjustedForGameLength);
    return today;
  }

  public boolean nextDay() {
    if (day == gameLength - 1) return false;
    else {
      day++;
      return true;
    }
  }

  public double getActivePrice() {
    return priceData.get(day).getClose().doubleValue();
  }

  public double getProfitLossOnTrade(Trade trade) {
    double currentPrice = getActivePrice();
    double tradedPrice = trade.getPurchasePrice();
    double priceDifference = currentPrice - tradedPrice;
    return priceDifference * trade.getQuantity();
  }

  public Calendar getActiveDate() {
    return priceData.get(day).getDate();
  }
  public void getTradeStatus() {
    // TODO
  }
  public String getSummary(){
    return "Close: " + getActivePrice() + "\n" +
            "Day: " + (day + 1) + "\n";
  }
}