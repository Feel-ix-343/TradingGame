import Markets.BinarySimulator;
import Markets.FuturesSimulator;
import Markets.MarketSimulator;
import Trades.BinaryTrade;
import Trades.FuturesTrade;
import Trades.Trade;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Main {
  private static Scanner s = new Scanner(System.in);
  private static final String CLEAR_LINE = "\033[K";
  private static Game game;
  private static MarketSimulator marketSimulator;
  public static void main(String[] args) throws IOException, InterruptedException {
    // TODO: Deal with IO exceptions with the API
    // TODO: Restructure to trade with multiple stocks, or multiple markets, or both
    // TODO: Fix decimal output
    // TODO: possibly remove the second player?

    // Get User Input for the stock being traded

    System.out.print("Enter stock being traded (|FB|, GOOG, APPL, ...): ");
    String stockTicker = s.nextLine();
    // TODO: implement a stock name checker using IO exception
    if (stockTicker.equals("")) stockTicker = "FB";
    System.out.println("| " + stockTicker);

    System.out.print("Enter player 1 name (or return for default): ");
    String name = s.nextLine();
    if(name.equals("")) name = "player 1";
    System.out.println("| Hello " + name);
    Player player1 = new Player(name);

    System.out.print("Enter player 2 name (or return for default): ");
    name = s.nextLine();
    if(name.equals("")) name = "player 2";
    System.out.println("| Hello " + name);
    Player player2 = new Player(name);

    System.out.print("Enter the type of trading (|normal|, binary, futures): ");
    String marketType = s.nextLine();

    // Execute loader on new thread
    Thread t = new Thread(marketLoaderAnimation());
    t.start();

    int gameLength = 100;

    // Take the market type
    if (marketType.equals("binary")) {
      marketSimulator = new BinarySimulator(stockTicker, gameLength);
    } else if (marketType.equals("futures")) {
      marketSimulator = new FuturesSimulator(stockTicker, gameLength);
    } else {
      marketSimulator = new MarketSimulator(stockTicker, gameLength);
    }

    t.stop(); // Stop marketLoader thread execution


    System.out.println();
    System.out.println("Market Started");
    System.out.println();


    game = new Game(marketSimulator, new Player[]{player1, player2});

    do {
      // TODO: Include the pervious prices in the summary
      System.out.println(marketSimulator.getSummary());
      if (marketSimulator.getDay() != 0) System.out.println(displayPlayerProfitLoss(player1));
      makeTrade(player1);
      System.out.println();
      if (marketSimulator.getDay() != 0) System.out.println(displayPlayerProfitLoss(player2));
      makeTrade(player2);
      System.out.println();
    } while (game.nextPeriod());
    // TODO: Show history of trades with trade summaries
    System.out.println(player1.getName() + "'s total portfolio value: " + player1.getPortfolioValue());
    System.out.println(player2.getName() + "'s total portfolio value: " + player2.getPortfolioValue());
  }
// TODO: Make it stop at the right time
  public static Runnable marketLoaderAnimation() {
    Runnable r = new Runnable() {
      public void run() {
        System.out.print("Loading trading market ");
        int i = 0;
        while (true) {
          try {
            if (i == 10) {
              System.out.print("\b".repeat(10));
              System.out.print(CLEAR_LINE);
              i = 0;
            }
            System.out.print(".");
            i++;
            Thread.sleep(100);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    };
    return r;
  }
  public static void makeTrade(Player player) throws IOException, InterruptedException {
    System.out.println(displayPlayerSummary(player));

    System.out.print(player.getName() + " (b)uy/cover or (s)ell/short, (enter) to do nothing, or (m) to display market summary: ");
    String tradeType = s.nextLine();

    if (tradeType.equals("m")) {
      marketSimulator.getSummary();
      makeTrade(player);
    }

    if (!tradeType.equals("b") && !tradeType.equals("s")) {
      return;
    }

    System.out.print("| Enter number of shares: ");
    int nOfShares = 0;
    try {
      nOfShares = s.nextInt();
      s.nextLine();
    } catch (InputMismatchException e) {
      makeTrade(player);
    }

    if (tradeType.equals("s")) nOfShares = -nOfShares;


    Trade trade = null;
    if (marketSimulator instanceof BinarySimulator) {
      trade = makeBinaryOptionsTrade(player, nOfShares);
    } else if (marketSimulator instanceof FuturesSimulator) {
      makeFuturesTrade(player, nOfShares);
    } else {
      trade = makeNormalTrade(player, nOfShares);
    }

    assert trade != null; // Getting around the "trade might not haev been initialized error", even through it has to have been
    boolean result = game.makeTrade(player, trade);

    if (!result) {
      System.out.println("| You do not have enough money! try again");
      makeTrade(player);
    }
    System.out.println(displayTradeSummary(trade));
    System.out.println(displayPlayerSummary(player));
    System.out.println();
  }

  public static Trade makeNormalTrade(Player player, int nOfShares) {
    return new Trade(marketSimulator.getActivePrice(), nOfShares, marketSimulator);
  }

  public static Trade makeBinaryOptionsTrade(Player player, int nOfShares) {

    Integer[] possibleTradeLengths = ((BinarySimulator) marketSimulator).getExperationDateLengths();
    String possibleTradeLengthsDisplay = IntStream.range(0, possibleTradeLengths.length)
            .boxed()
            .flatMap(x -> Stream.of( "(" + (x + 1) + ")" + possibleTradeLengths[x] + " day/days" ))
            .collect(Collectors.toList())
            .toString();

    System.out.print(player.getName() + " chose trade length " + possibleTradeLengthsDisplay + "(number does not matter if closing trade): "); // TODO: dont have to enter trade length if closing trade, and this should be calculated by the BinarySimulator
    int tradeLengthInput = s.nextInt();
    s.nextLine();
    int tradeLength = possibleTradeLengths[tradeLengthInput-1];


    System.out.println("| Trade expires in " + tradeLength + " days");

    return new BinaryTrade(marketSimulator.getActivePrice(), nOfShares, marketSimulator, tradeLength, marketSimulator.getActiveDate());
  }

  public static Trade makeFuturesTrade(Player player, int nOfShares) {

    Integer[] possibleTradeLengths = ((BinarySimulator) marketSimulator).getExperationDateLengths();
    String possibleTradeLengthsDisplay = IntStream.range(0, possibleTradeLengths.length)
            .boxed()
            .flatMap(x -> Stream.of( "(" + (x + 1) + ")" + possibleTradeLengths[x] + " day/days" ))
            .collect(Collectors.toList())
            .toString();

    System.out.print(player.getName() + " chose trade length " + possibleTradeLengthsDisplay + "(number does not matter if closing trade): "); // TODO: dont have to enter trade length if closing trade, and this should be calculated by the BinarySimulator
    int tradeLengthInput = s.nextInt();
    s.nextLine();
    int tradeLength = possibleTradeLengths[tradeLengthInput-1];


    System.out.println("| Trade expires in " + tradeLength + " days");

    System.out.print(player.getName() + " chose price target (number does not matter if closing trade): ");
    double priceTarget = s.nextDouble();
    s.nextLine();



    if (nOfShares > 0) return new FuturesTrade(marketSimulator.getActivePrice(), nOfShares, marketSimulator, priceTarget,
            tradeLength, FuturesTrade.futuresTradeType.BUY, marketSimulator.getActiveDate());
    else return new FuturesTrade(marketSimulator.getActivePrice(), nOfShares, marketSimulator, priceTarget,
            tradeLength, FuturesTrade.futuresTradeType.SHORT, marketSimulator.getActiveDate());
  }


  public static String displayTradeSummary(Trade trade) {
    return "| " + marketSimulator.getTradeSummary(trade);
  }

  public static String displayPlayerSummary(Player p) {
    int netShares = game.getNetShares(p);
    return "| " + p.getName() + " porfolio summary:\n" +
            "| | Net shares: " + netShares + "\n" +
            "| | Account Balance: " +  p.getBalance() + "\n" +
            "| | Portfolio value: " + p.getPortfolioValue();
  }

  public static String displayPlayerProfitLoss(Player p) {
    double pl = p.getActivePL();
    return "| " + p.getName() + "'s daily profit/loss " + pl;
  }
}