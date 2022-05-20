import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

import Markets.*;
import Trades.*;

public class Game {
  private MarketSimulator m1;
  private Player[] players;
  private Scanner s1 = new Scanner(System.in);

  public Game(MarketSimulator market, Player[] players) throws IOException {
    // Start the trading market
    m1 = market;
    this.players = players;
  }

  public boolean nextPeriod() {

    boolean nextDayResult = m1.nextDay(); // Also affect the MarketSimulator class
    // Adjust the players' money counts. New Trades need to be made before the
    // nextPeriod method is called by Main class
    for (Player player : players) {
      for (Trade trade : player.getTrades()) {
        double activeTradeProfitLoss = m1.getProfitLossOnTrade(trade);
        double changeInTradeProftiLoss = activeTradeProfitLoss - trade.getTradeProfitLoss();
        player.updateBalance(changeInTradeProftiLoss);
        trade.setTradeProfitLoss(activeTradeProfitLoss);
      }
    }
    return nextDayResult;
  }

  public void makeTrade(Player player, int quantity) {
    // CHECK INSTANCE OF FOR THE MARKETSIMULATOR FOR THE TYPE OF TRADE TODO
    // TODO: Check if a trade closes another trade, and dont place a new order
    Trade newTrade = new Trade(m1.getActivePrice(), quantity);
    player.addTrade(newTrade);
    player.updateBalance(-newTrade.getInitialTradeAmount());
  }
}