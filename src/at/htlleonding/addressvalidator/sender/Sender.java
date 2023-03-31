package at.htlleonding.addressvalidator.sender;

import at.htlleonding.addressvalidator.data.AddressData;
import at.htlleonding.addressvalidator.data.CtrlData;

import java.io.*;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Sender {

    private static String[] DATA = {
            "abc.edf@example.com",
            "+43 877 877 877",
            "bob.sponge@seaworld.au",
            "039431 3465",
            "4531",
            "patric.star@c.u",
            "pearl@open.water",
            "0049 123 55 66",
            "0732 56 1A 16",
            "mr@krabs",
            "patric",
            "+1 22 33 44444",
            "q.tentacles@squidward.com",
            "066112298765",
            "karen@plankt.on",
            "sheld.on@plankt.on",
    };

    private static Sender mMe;
    private boolean mDisableDelay = false;

    private PipedOutputStream mDataSource;
    private UUID mApiToken;
    private AtomicBoolean mSending;
    private int mMaxTransmissionCount;

    private int mNextTransmissionId;
    private int mNextAddressIdx;
    private int mFailureRate = 1;
    private int mNextFalseTokenAt = 1;


    private Random mRand;

    private Sender() {
        mSending = new AtomicBoolean(false);
        mRand = new Random();
        mMaxTransmissionCount = 2 * DATA.length;
    }

    /** Intended for unit testing only! */
    public static String[] getData() {
        return DATA;
    }

    /** Intended for unit testing only! */
    public UUID getValidApiToken() {
        return mApiToken;
    }

    /** Intended for unit testing only! */
    public void enableFullSpeed() {
        mDisableDelay = true;
    }
    /** Intended for unit testing only! */
    public void setFailureRate(int rate) {
        mFailureRate = rate;
    }

    public static Sender getMe() {
        if (mMe == null) {
            mMe = new Sender();
        }
        return mMe;
    }

    public int getMaxTransmissionCount() {
        return mMaxTransmissionCount;
    }

    public void registerSink(PipedInputStream sink) {
        if (sink != null) {
            try {
                mDataSource = new PipedOutputStream();
                mDataSource.connect(sink);
                startSending();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void startSending() {
        mSending.set(true);
        Thread sendingThread = new Thread(() -> {
            System.out.println("--> Starting sender");
            try (ObjectOutputStream objectSink = new ObjectOutputStream(new BufferedOutputStream(mDataSource))) {

                sendCommand(objectSink, CtrlData.Command.HELLO);
                sendCommand(objectSink, CtrlData.Command.NEGOTIATE_TOKEN);

                mNextTransmissionId = 0;
                mNextAddressIdx = 0;
                mNextFalseTokenAt = mRand.nextInt(6) + mFailureRate;
                while(mSending.get()) {
                    sendAddress(objectSink);
                    mNextTransmissionId++;

                    if (mNextTransmissionId >= mMaxTransmissionCount) {
                        stopSending();
                    }
                }

                sendCommand(objectSink, CtrlData.Command.GOOD_BY);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("--> Sender terminated");
        });

        sendingThread.start();
    }

    public void stopSending() {
        mSending.set(false);
    }

    private void sendAddress(ObjectOutputStream outStream) throws IOException {
        if (outStream != null) {
            UUID apiToken = mApiToken;
            if (mNextTransmissionId == mNextFalseTokenAt) {
                apiToken = UUID.randomUUID();
                mNextFalseTokenAt += mRand.nextInt(mMaxTransmissionCount / 5) + mFailureRate;
            }
            sendAddressData(outStream, apiToken, DATA[mNextAddressIdx]);
            mNextAddressIdx = ++mNextAddressIdx % DATA.length;
        }
    }

    private void sendAddressData(ObjectOutputStream outStream, UUID token, String address) throws IOException {
        if (outStream != null && address != null) {
            System.out.printf("DBG: Sending address: %s%n", address);
            AddressData addrData = new AddressData(token, address);
            outStream.writeObject(addrData);
            outStream.flush();

            try {
                if (!mDisableDelay) {
                    Thread.sleep(mRand.nextInt(500) + 100);
                }
            } catch (InterruptedException e) { /* just ignore */ }
        }
    }
    private void sendCommand(ObjectOutputStream outStream, CtrlData.Command cmd) throws IOException {
        if (outStream != null && cmd != null) {
            CtrlData cmdData = new CtrlData(cmd);
            if (cmd == CtrlData.Command.NEGOTIATE_TOKEN) {
                mApiToken = UUID.randomUUID();
                cmdData.setToken(mApiToken);
            }
            System.out.printf("--> Sending %s%n", cmd);
            outStream.writeObject(cmdData);
            outStream.flush();

            try {
                if (!mDisableDelay) {
                    Thread.sleep(mRand.nextInt(500) + 500);
                }
            } catch (InterruptedException e) { /* just ignore */ }
        }
    }

}
