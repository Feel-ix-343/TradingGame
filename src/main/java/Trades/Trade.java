package Trades;
public class Trade {
  private final double purchasePrice;
  private final int quantity;
  private double profitLoss;
  public Trade(double p, int q) {
    purchasePrice = p;
    quantity = q;
  }
  public double getPurchasePrice() {
    return purchasePrice;
  }
  public int getQuantity() {
    return quantity;
  }
  public double getInitialTradeAmount() {
    return getQuantity() * getPurchasePrice();
  }
  public double getTradeProfitLoss() {
    return profitLoss;
  }
  public void setTradeProfitLoss(double profitLoss) {
    this.profitLoss = profitLoss;
  }
  public double getTradeValue() {
    return getInitialTradeAmount() + getTradeProfitLoss();
  }
}