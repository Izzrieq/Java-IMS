
/**
 * To define the attribute the instance variable and instance method of the flower class
 *
 * @author NikoChan
 * date : 2 Jul 2024
 */
public class Product
{
    protected String productId;
    protected String name;
    private String supplierName;
    private double price;
    private int quantity;
    
    public Product(){
        productId = "";
        name = "";
        supplierName = "";
        price = 0.0;
        quantity = 0;
    }
    
    public Product(String productId, String name, String supplierName, double price, int quantity){
        this.productId = productId;
        this.name = name;
        this.supplierName = supplierName;
        this.price = price;
        this.quantity = quantity;
    }
    
    //Accessor
    public String getProductId()
    {
        return productId;
    }
    public String getName()
    {
        return name;
    }
    public String getSupplierName()
    {
        return supplierName;
    }
    public double getPrice()
    {
        return price;
    }
    public int getQuantity()
    {
        return quantity;
    }
    
    //Mutator
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }
    public void setPrice(double price)
    {
        this.price = price;
    }
    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }
    
    //Overide
    @Override
    public String toString() {
        return String.format("| %-16s | %-16s | %-16s | %-8s | %-8.2f | %-8d |\n",
        "Products", productId, name, supplierName, price, quantity);
    }
}
