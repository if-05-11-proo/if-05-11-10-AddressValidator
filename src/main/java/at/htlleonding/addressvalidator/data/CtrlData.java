package at.htlleonding.addressvalidator.data;

import java.io.Serializable;
import java.util.UUID;

public class CtrlData implements Serializable {
    public enum Command {
        HELLO,
        NEGOTIATE_TOKEN,
        GOOD_BY
    };

    private Command mCommand;
    private UUID mToken;

    public CtrlData(Command cmd) {
        mCommand = cmd;
    }

    public Command getCommand() {
        return mCommand;
    }

    public void setToken(UUID token) {
        mToken = token;
    }

    public UUID getToken() {
        return mToken;
    }
}
