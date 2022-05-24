package Trades;

import Markets.MarketSimulator;

public class Trade {
  private double purchasePrice;
  protected int quantity;
  private double profitLoss;
  private MarketSimulator marketSimulator;
  private boolean active;
  public Trade(double purchasePrice, int quantity, MarketSimulator marketSimulator) {
    this.purchasePrice = purchasePrice;
    this.quantity = quantity;
    active = true;
    this.marketSimulator = marketSimulator;
  }
  public double getPurchasePrice() {
    return purchasePrice;
  }
  public int getQuantity() {
    return quantity;
  }
  public void changeQuantity(int quantity) {
    this.quantity += quantity;
  }
  public void changePurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }
  public double getTradeAmount() {
    return getQuantity() * getPurchasePrice();
  }
  public double getTradeProfitLoss() {
    return profitLoss;
  }
  public void setTradeProfitLoss(double profitLoss) {
    this.profitLoss = profitLoss;
  }
  public double getTradeValue() {
    return getTradeAmount() + getTradeProfitLoss();
  }
  public void close() {
    active = false;
  }
  public boolean getActive() {
    return active;
  }
  public void deactivate() {
    active = false;
  }
  public MarketSimulator getMarketSimulator() {
    return marketSimulator;
  }
}