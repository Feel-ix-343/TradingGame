import Markets.MarketSimulator;
import Trades.Trade;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main {
  private static Scanner s = new Scanner(System.in);
  private static final String CLEAR_LINE = "\033[K";
  private static Game game;
  private static MarketSimulator market;
  public static void main(String[] args) throws IOException {
    // TODO: Deal with IO exceptions with the API

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

    // TODO: Chosing type of market

    // Execute on new thread
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
    Thread t = new Thread(r);
    t.start();
    // Get the type of game the user wants to play, then assign it

    market = new MarketSimulator(stockTicker, 5);
    t.stop();
    System.out.println();
    System.out.println("Market Started");
    System.out.println();


    game = new Game(market, new Player[]{player1, player2});

    boolean running = true;
    do {
      // TODO: Include the pervious prices in the summary
      System.out.println(market.getSummary());
      if (market.getDay() != 0) System.out.println(displayPlayerProfitLoss(player1));
      makeTrade(player1);
      if (market.getDay() != 0) System.out.println(displayPlayerProfitLoss(player2));
      makeTrade(player2);
      // TODO: game.getSummary();
    } while (game.nextPeriod());
    System.out.println(player1.getName() + "'s total portfolio value: " + player1.getPortfolioValue());
    System.out.println(player2.getName() + "'s total portfolio value: " + player2.getPortfolioValue());
  }
  public static void makeTrade(Player player) {
    System.out.print(player.getName() + " (b)uy/cover or (s)ell/short or (enter) to do nothing: ");
    String tradeType = s.nextLine();
    if (tradeType.equals("b")) {
      System.out.print("| Enter number of shares: ");
      int nOfShares = s.nextInt();
      s.nextLine();
      boolean result = game.makeTrade(player, nOfShares);
      if (!result) {
        System.out.println("| You do not have enough money! try again");
        makeTrade(player);
      }
      System.out.println(displayTradeSummary(player));
    } else if (tradeType.equals("s")) {
      System.out.print("| Enter number of shares: ");
      int nOfShares = s.nextInt();
      s.nextLine();
      boolean result = game.makeTrade(player, -nOfShares);
      if (!result) {
        System.out.println("| You do not have enough money! try again");
        makeTrade(player);
      }
      System.out.println(displayTradeSummary(player));
    }
    System.out.println(displayPlayerSummary(player));
    System.out.println();
  }

  public static String displayTurnStart(Player p1, Player p2) {
    // Display balance, name, stock being traded, ?previous stock values?
    return "";
  }
  public static String displayTradeSummary(Player p) {
    ArrayList<Trade> tradeArrayList = p.getTrades();
    Trade recentTrade = tradeArrayList.get(tradeArrayList.size() - 1);
    return "| " + market.getTradeSummary(recentTrade);
  }

  public static String displayPlayerSummary(Player p) {
    int netShares = game.getNetShares(p);
    return "| " + p.getName() + " porfolio summary:\n" +
            "| | Net shares: " + netShares + "\n" +
            "| | Account Balance: " +  p.getBalance() + "\n" +
            "| | Portfolio value: " + p.getPortfolioValue();
  }

  public static String displayPlayerProfitLoss(Player p) {
    double pl = 0;
    for (Trade t : p.getTrades()) {
      pl += t.getTradeProfitLoss();
    }
    return "| " + p.getName() + "'s account has total profit/loss " + pl;
  }
}