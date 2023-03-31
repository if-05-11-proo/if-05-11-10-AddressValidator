package at.htlleonding.addressvalidator.data;

import java.util.UUID;

public class AddressData {

    private UUID mApiToken;
    private String mValue;

    public AddressData(UUID apiToken, String address) {
        mApiToken = apiToken;
        mValue = address;
    }

    public UUID getApiToken() {
        return mApiToken;
    }

    public String getAddress() {
        return mValue;
    }

    @Override
    public String toString() {
        return String.format("%s { apiToken: %s, value: %s }", getClass().getName(), mApiToken, mValue);
    }
}
