public class Supplier {
    protected String supplierId;
    private String supplierName;
    
    public Supplier() {
        supplierId = "";
        supplierName = "";
    }
    
    public Supplier(String supplierId, String supplierName) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
    }
    
    // Accessors
    public String getSupplierId() {
        return supplierId;
    }
    
    public String getSupplierName() {
        return supplierName;
    }
    
    // Mutators
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    // Override toString method for debugging or logging
    @Override
    public String toString() {
        return String.format("| %-16s | %-16s |\n", supplierId, supplierName);
    }
}
