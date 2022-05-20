package Markets;
import java.io.IOException;
import Trades.*;
public class BinarySimulator extends MarketSimulator {
  public BinarySimulator(String symbol, int gameLength) throws IOException {
    super(symbol, gameLength);
  }
  /**
    In binary trades, you either double your trade amount if the stock goes up, lose all of it if the stock goes down, or keep it if the stock stays at the same price
  */
  public double getProfitLossOnTrade(BinaryTrade trade) {
    if (getActiveDate().compareTo(trade.getExperationDate()) == -1) return 0;
    
    double pl = super.getProfitLossOnTrade(trade);
    if (pl > 0) {
      return trade.getInitialTradeAmount();
    } else if (pl < 0) {
      return 0 - trade.getInitialTradeAmount();
    } else {
      return 0;
    }
  }
  public void getTradeStatus(BinaryTrade trade) {
    // TODO
  }
}