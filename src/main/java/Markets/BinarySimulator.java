package Markets;
import java.io.IOException;

import Trades.*;

public class BinarySimulator extends MarketSimulator {
  public BinarySimulator(String symbol, int gameLength) throws IOException {
    super(symbol, gameLength);
  }

  /**
   * Gets the profit or loss on a Trade child BinaryTrade object
   * Will check if the stock is expired, and if above or below the current price
   * @param trade A trade object, which must be an instance of a BinaryTrade
   * @return Return the profit of loss on a Trade object,
   */
  public double getProfitLossOnTrade(Trade trade) {
    BinaryTrade binaryTrade = (BinaryTrade) trade;

    if (getTradeActive(trade)) return 0;

    double pl = super.getProfitLossOnTrade(binaryTrade);
    double tradeAmount = 0;
    if (pl > 0) {
      tradeAmount = binaryTrade.getTradeAmount();
    } else if (pl < 0) {
      tradeAmount = -binaryTrade.getTradeAmount();
    } else {
      tradeAmount = 0;
    }
    return tradeAmount;
  }

  /**
   * Determines if a BinaryTrade (passed as a Trade) is active
   * It will then set it as not active if it is not active
   * @param trade:
   */
  public boolean getTradeActive(Trade trade) {
    // TODO: Thing about making this more immutable
    if (!trade.getActive()) return false; // Other futures trades could close this trade

    BinaryTrade binaryTrade = (BinaryTrade) trade;
    if (getActiveDate().compareTo(binaryTrade.getExperationDate()) == -1) return true;
    trade.deactivate();
    return false;
  }

  /**
   * @return Randomly generated experation dates that are within the timerange left in the game
   */
  public Integer[] getExperationDateLengths() {
    int max = Math.min(getGameLength() - getDay(), 6);
    if (max <= 3) return new Integer[]{max-1};
    int nOfDates = 3;

    Integer[] out = new Integer[nOfDates];

    int lastInt = 0;
    for (int i = 0; i < nOfDates; i++) {
      int randomInt = (int) (Math.random() * (max/nOfDates)) + 1;
      lastInt += randomInt;
      out[i] = lastInt;
    }

    return out;
  }
}