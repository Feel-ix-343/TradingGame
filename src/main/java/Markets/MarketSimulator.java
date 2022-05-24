package Markets;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import Trades.*;

public class MarketSimulator {
  private int gameLength;
  
  private List<HistoricalQuote> priceData;
  private List<HistoricalQuote> visualizationData;
  private int day;
  private int lookBackPeriod;

  public MarketSimulator(String symbol, int gameLength) throws IOException {
    // TODO: Make immutable, getting too confusing here
    this.gameLength = gameLength;

    // Create the stock data
    Stock stock = YahooFinance.get(symbol); // Can throw IOException
    // Generate a random starting date
    Calendar start = MarketSimulator.getRandomStartingDate(stock, gameLength);
    // Set the end date to [length]
    Calendar end = (Calendar) start.clone(); end.add(Calendar.DATE, daysForBusinessDays(start, gameLength + 50, 1)); // TODO: Implement tracking holidays (I dont care to rn so I just added 30)

    // Needed to update the library to version 3.16 to get this to work
    priceData = stock.getHistory(start, end, Interval.DAILY);

    day = 0;

    lookBackPeriod = 200;
    visualizationData = loadVisualizationData(daysForBusinessDays(start, lookBackPeriod, -1), stock);
  }

  /**
   * @param startDate The start date. The startdate object is not changed
   * @param days The number of business days
   * @param direction The direction to go: Either 1 or -1
   * @return The total number of days
   */
  private static int daysForBusinessDays(Calendar startDate, int days, int direction) {
    if (direction != 1 && direction != -1) direction = 1;

    List<Integer> NON_BUSINESS_DAYS = Arrays.asList(
            Calendar.SATURDAY,
            Calendar.SUNDAY
    );
    Calendar end = (Calendar) startDate.clone();
    int i = 0;
    int count = 0;
    while (i < days) {
      end.add(Calendar.DATE, direction);
      if (!NON_BUSINESS_DAYS.contains(end.get(Calendar.DAY_OF_WEEK))) i++;
      count++;
    }
    return count;
  }
  private static Calendar getRandomStartingDate(Stock stock, int gameLength) {
    // Handle case when the stock ipo'd in the past 2 years; not in range TODO
    Calendar randomDay = Calendar.getInstance();
    int randomDaysBack = (int) (Math.random() * (365 * 2));

    Calendar testCalendar = Calendar.getInstance();
    testCalendar.add(Calendar.DATE, -randomDaysBack);

    int randomDayAdjustedForGameLength = Math.max(randomDaysBack, daysForBusinessDays(testCalendar, gameLength, 1));
    randomDay.add(Calendar.DATE, -randomDayAdjustedForGameLength);
    return randomDay;
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
  public List<HistoricalQuote> loadVisualizationData(int days, Stock stock) throws IOException {
    Calendar visualizationStartDate = (Calendar)priceData.get(0).getDate().clone();
    visualizationStartDate.add(Calendar.DATE, -days);

    Calendar visualizationEndDate = priceData.get(0).getDate();

    List<HistoricalQuote> newData = stock.getHistory(visualizationStartDate, visualizationEndDate, Interval.DAILY);

    newData.addAll(priceData);
    return newData;
  }
  public String getSummary() throws IOException, InterruptedException {
    sendPriceDataToJSON();
    Process p;
    // p = Runtime.getRuntime().exec(new String[]{"bash", "./cli-candlestick-chart --mode=csv-file --file=Price_Data.csv"}, null);
    String workingDirectory = System.getProperty("user.dir");

    p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "-l",
            "alacritty -e" + workingDirectory + "/plot"});
    p.waitFor();
    BufferedReader reader=new BufferedReader(new InputStreamReader(
            p.getErrorStream()));
    String line;
    while((line = reader.readLine()) != null) {
      System.out.println(line);
    }

    if (day >= 1)
      return "Day: " + (day + 1) + " of " + gameLength +  "\n" +
              "Close: " + getActivePrice() + "\n" +
              "Change per share: " + (getActivePrice() - priceData.get(day - 1).getClose().doubleValue()) + "\n";
    else
      return "Day: " + (day + 1) + " of " + gameLength +  "\n" +
              "Close: " + getActivePrice() + "\n";

  }
  private void sendPriceDataToJSON() throws IOException {

    int activeCloseIndex = visualizationData.indexOf(priceData.get(day));

    PrintWriter writer = new PrintWriter("Price_Data.csv", StandardCharsets.UTF_8);
    writer.println("open,high,low,close,volume");
    for (HistoricalQuote h : visualizationData.subList(activeCloseIndex - lookBackPeriod + 10, activeCloseIndex + 1)) { // TODO: Make this better, rn it just works
      writer.println(h.getOpen() + "," + h.getHigh() + "," + h.getLow() + "," + h.getClose() + "," + h.getVolume());
    }
    writer.close();
  }
  public String getTradeSummary(Trade trade) {
    return trade.getQuantity() + " shares exchanged at " + trade.getPurchasePrice() + " for a total of " + trade.getTradeAmount();
  }
  public int getDay() {
    return day;
  }

  public int getGameLength() {
    return gameLength;
  }
  public boolean getTradeActive(Trade trade) {
    return trade.getActive(); // This will be set to false when a new trade closes this trade from the game object
  }
  public Optional<Trade> getAssociatedTrade(ArrayList<Trade> previousTrades) {
    for (Trade trade : previousTrades) {
      if (trade.getMarketSimulator() == this && trade.getActive()) return Optional.of(trade);
    }
    return Optional.empty();
  }
  // TODO: Add a trade summary (specifically for binary and futures)
}
