package models;

import exceptions.InvalidInputException;
import interfaces.Identifiable;
import utility.ValidationUtil;

import java.util.UUID;

public class Ward implements Identifiable {
    private UUID wardId;
    private String wardName;
    private String wardNumber;

    public Ward(String wardName, String wardNumber) throws InvalidInputException {
        ValidationUtil.validateWardNumber(wardNumber);
        ValidationUtil.validateWardName(wardName);
        this.wardId = UUID.randomUUID();
        this.wardName = wardName;
        this.wardNumber = wardNumber;
    }


    @Override
    public String toString() {
        return String.format("Ward %s [%s]",
                wardNumber, wardName);
    }

    public UUID getWardId() {
        return wardId;
    }

    public void setWardId(UUID wardId) {
        this.wardId = wardId;
    }

    public void setWardName(String wardName) {
        this.wardName = wardName;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    @Override
    public UUID getId() { return wardId; }
    public String getWardNumber() { return wardNumber; }
    public String getWardName() { return wardName; }
}