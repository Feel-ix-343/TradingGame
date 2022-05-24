package Markets;
import java.util.EnumMap;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import Trades.*;

public class FuturesSimulator extends MarketSimulator {
  public FuturesSimulator(String symbol, int gameLength) throws IOException {
    super(symbol, gameLength);
  }

  public double getProfitLossOnTrade(FuturesTrade trade) {
    if (trade.getTradeType() == FuturesTrade.futuresTradeType.BUY && getActivePrice() > trade.getPriceTarget() || 
        trade.getTradeType() == FuturesTrade.futuresTradeType.SHORT && getActivePrice() < trade.getPriceTarget() ||
        getActiveDate().compareTo(trade.getExperationDate()) == 1 ||
        getActiveDate().compareTo(trade.getExperationDate()) == 0) {
      return 0;
    } else {
      Trade upcastedTrade = trade;
      return getProfitLossOnTrade(upcastedTrade);
    }
  }
  public enum tradeStatusTypes {
    DAYSLEFT,
    DISTANCEFROMTARGET 
  }
  /**
    Dont call this method after the trade closes
  */
  public EnumMap<tradeStatusTypes, Double> getTradeStatus(Trade trade) {
    FuturesTrade futuresTrade = (FuturesTrade) trade;
    EnumMap<tradeStatusTypes, Double> tradeStatus = new EnumMap<>(tradeStatusTypes.class);

    int activeDate = (int) getActiveDate().getTimeInMillis();
    int experationDate = (int) futuresTrade.getExperationDate().getTimeInMillis();
    int difference = (int)TimeUnit.MILLISECONDS.toDays(experationDate - activeDate);

    double activePrice = getActivePrice();
    double targetPrice = futuresTrade.getPriceTarget();

    tradeStatus.put(tradeStatusTypes.DAYSLEFT, (double)difference);
    tradeStatus.put(tradeStatusTypes.DISTANCEFROMTARGET, (double)(targetPrice - activePrice));

    return tradeStatus;
  }

  public Integer[] getExperationDateLengths() {
    int max = Math.min(getGameLength() - getDay(), 15);
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