import Markets.MarketSimulator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class Main {
  public static void main(String[] args) throws IOException {
    // TODO: Deal with IO exceptions with the API

    // Get User Input for the stock being traded
    Scanner s = new Scanner(System.in);

    System.out.print("Enter stock being traded (FB, GOOG, APPL, ...): ");
    String stockTicker = s.nextLine();
    // TODO: implement a stock name checker using IO exception
    while(stockTicker.equals("")) {
      System.out.print("| You must enter a stock name: ");
      stockTicker = s.nextLine();
    }

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

    boolean marketDone = false;
    // TODO: Chosing type of market
    // Execute on new thread
    Runnable r = new Runnable() {
      public void run() {
        System.out.print("Loading trading market ");
        while (true) {
          try {
            System.out.print(".");
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
    market = new MarketSimulator(stockTicker, 5);
    t.stop();
    System.out.println();

    Game game = new Game(market, new Player[]{player1, player2});

    game.makeTrade(player1, 1);
    game.makeTrade(player2, -1);
    System.out.println(player1.getPortfolioValue());
    System.out.println(player2.getPortfolioValue());
    System.out.println(market.getSummary());

    game.nextPeriod();
    System.out.println(player1.getPortfolioValue());
    System.out.println(player2.getPortfolioValue());
    System.out.println(market.getSummary());
    game.makeTrade(player1, -1);
    System.out.println(player1.getBalance());

    game.nextPeriod();
    System.out.println(player1.getPortfolioValue());
    System.out.println(player2.getPortfolioValue());
    System.out.println(market.getSummary());

    game.nextPeriod();
    System.out.println(player1.getPortfolioValue());
    System.out.println(player2.getPortfolioValue());
    System.out.println(market.getSummary());

    game.nextPeriod();
    System.out.println(player1.getPortfolioValue());
    System.out.println(player2.getPortfolioValue());
    System.out.println(market.getSummary());


    // boolean running = true;
    // while (running) {
    //   // System.out.println("Player 1 action(buy/sell [quantity], or nothing to continue): ");
    //   // IO crap
// 
    //   game.nextPeriod();
    // }
    
    // System.out.println("P1 choose your stock." + Player.listOfTrades());
    // 
    // 
    // System.out.println("P2 choose your stock.");
  //per day display money, get new trades 
  }

  public static String displayTurnStart(Player p1, Player p2) {
    // Display balance, name, stock being traded, ?previous stock values?
    return "";
  }
}