import java.util.Optional;
import java.util.Scanner;

import Markets.*;
import Trades.*;

public class Game {
  private MarketSimulator marketSimulator;
  private Player[] players;
  private Scanner s1 = new Scanner(System.in);

  public Game(MarketSimulator market, Player[] players) {
    // Start the trading market
    marketSimulator = market;
    this.players = players;
  }

  public boolean nextPeriod() {

    boolean nextDayResult = marketSimulator.nextDay(); // Also affect the MarketSimulator class // TODO: Will need to restructure for mutliple trades
    // Adjust the players' money counts. New Trades need to be made before the
    // nextPeriod method is called by Main class
    for (Player player : players) {
      double previousPL = player.getPortfolioValue();

      for (Trade trade : player.getTrades()) {
        if (!trade.getActive()) continue;
        double activeTradeProfitLoss = marketSimulator.getProfitLossOnTrade(trade);
        trade.setTradeProfitLoss(activeTradeProfitLoss);
      }

      player.setActivePL(player.getPortfolioValue() - previousPL);
    }
    return nextDayResult;
  }

  /**
   * Places a trade using the defined marketSimulator
   * Will combine trades that are associated
   * @param player
   * @param trade
   * @return Boolean representing if the trade went through or not
   */
  public boolean makeTrade(Player player, Trade trade) {
    Optional<Trade> associatedTrade = marketSimulator.getAssociatedTrade(player.getTrades());
    if (associatedTrade.isPresent()) {
      int associatedTradeQuantity = associatedTrade.get().getQuantity();

      if (trade.getQuantity() == -associatedTradeQuantity) player.deactivateTrade(associatedTrade.get());
      else if (Math.abs(trade.getTradeAmount()) > player.getBalance()) return false;
      else {
        // Averaging purchase price and adding shares
        int totalShares = associatedTrade.get().getQuantity() + trade.getQuantity();
        double newPurchasePrice = (associatedTrade.get().getTradeAmount() + trade.getTradeAmount()) / totalShares;
        player.updateTrade(associatedTrade.get(), trade.getQuantity(), newPurchasePrice);
      }
    } else if (Math.abs(trade.getTradeAmount()) > player.getBalance()) return false;
    else {
      player.addTrade(trade);
    }
    player.updateBalance(-trade.getTradeAmount());
    return true;
  }
  public int getNetShares(Player p) {
    int netShares = 0;
    for (Trade t : p.getTrades()) {
      if (!t.getActive()) continue;
      netShares += t.getQuantity();
    }
    return netShares;
  }
}