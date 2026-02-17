package models;

import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;

import java.util.UUID;

public class Medicine implements Identifiable {
    private UUID medicineId;
    private String name;
    private double pricePerUnit;
    private int stockQuantity;

    public Medicine(String name, double pricePerUnit, int stockQuantity) throws InvalidInputException{
        ValidationUtil.validateName(name);
        ValidationUtil.validatePrice(pricePerUnit);
        ValidationUtil.validateQuantity(stockQuantity);
        this.medicineId = UUID.randomUUID();
        this.name = name;
        this.pricePerUnit = pricePerUnit;
        this.stockQuantity = stockQuantity;
    }

    @Override
    public UUID getId() {
        return medicineId;
    }

    public void setMedicineId(UUID medicineId) {
        this.medicineId = medicineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        ValidationUtil.validateName(name);
        this.name = name;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) throws InvalidInputException{
        ValidationUtil.validatePrice(pricePerUnit);
        this.pricePerUnit = pricePerUnit;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) throws InvalidInputException{
        ValidationUtil.validateQuantity(stockQuantity);
        this.stockQuantity = stockQuantity;
    }
}