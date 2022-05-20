import Markets.MarketSimulator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main {
  private static Scanner s = new Scanner(System.in);
  private static final String CLEAR_LINE = "\033[K";
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
    MarketSimulator market;
    // Get the type of game the user wants to play, then assign it
    market = new MarketSimulator(stockTicker, 100);
    t.stop();
    System.out.println();
    System.out.println("Market Started");


    Game game = new Game(market, new Player[]{player1, player2});

    boolean running = true;
    while (running) {
      // TODO: Include the pervious prices in the summary
      System.out.println(market.getSummary());
      makeTrade(game, player1);
      makeTrade(game, player2);
      // TODO: game.getSummary();
      game.nextPeriod();
    }
  }
  public static void makeTrade(Game game, Player player) {
    System.out.print(player.getName() + " (b)uy/cover or (s)ell/short or (enter) to do nothing: ");
    String tradeType = s.nextLine();
    if (tradeType.equals("b")) {
      System.out.print("Enter number of shares: ");
      int nOfShares = s.nextInt();
      game.makeTrade(player, nOfShares);
    } else if (tradeType.equals("s")) {
      System.out.print("Enter number of shares: ");
      int nOfShares = s.nextInt();
      game.makeTrade(player, -nOfShares);
    }
    return;
  }

  public static String displayTurnStart(Player p1, Player p2) {
    // Display balance, name, stock being traded, ?previous stock values?
    return "";
  }
}