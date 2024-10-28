package projectHS.DBminiPTL.Customer;

public class SetMenu {
    SingleMenu burger;
    SingleMenu side;
    SingleMenu drink;
    int price;
    int count;

    public SetMenu(SingleMenu burger, SingleMenu side, SingleMenu drink, int count) {
        this.burger = burger;
        this.side = side;
        this.drink = drink;
        this.count = count;
        this.price = (getBurger().price + getSide().price + getDrink().price) - 1200;
    }

    public void changeCount(int cnt) {
        this.count += cnt;
    }

    public void setBurger(SingleMenu burger) {
        this.burger = burger;
    }

    public void setSide(SingleMenu side) {
        this.side = side;
    }

    public void setDrink(SingleMenu drink) {
        this.drink = drink;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public SingleMenu getBurger() {
        return burger;
    }

    public SingleMenu getSide() {
        return side;
    }

    public SingleMenu getDrink() {
        return drink;
    }
}
