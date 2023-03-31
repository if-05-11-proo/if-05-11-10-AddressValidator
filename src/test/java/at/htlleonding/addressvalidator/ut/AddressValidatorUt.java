package at.htlleonding.addressvalidator.ut;

import at.htlleonding.addressvalidator.App;
import at.htlleonding.addressvalidator.data.AddressData;
import at.htlleonding.addressvalidator.receiver.ReceptionException;
import at.htlleonding.addressvalidator.receiver.supplier.IAddressSupplier;
import at.htlleonding.addressvalidator.sender.Sender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AddressValidatorUt {

	private static List<AddressData> mAddresses = new LinkedList<>();
	private static UUID mApiToken;
	private static List<String> mPhoneNumbers = new LinkedList<>();
	private static List<String> mUniquePhoneNumbers = new LinkedList<>();
	private static List<String> mEMailAddresses = new LinkedList<>();
	private static List<String> mUniqueEMailAddresses = new LinkedList<>();
	@BeforeAll
	static void runTransmission() {
		Sender.getMe().enableFullSpeed();
		Sender.getMe().setFailureRate(256);
		App.getMe().startTransmission(addr-> mAddresses.add(addr), e -> mAddresses.add(new AddressData(null, e.getClass().getName())));
		mApiToken = Sender.getMe().getValidApiToken();
		saveSupplies(mPhoneNumbers, App.getMe().getPhoneNumberSupplier());
		saveSupplies(mUniquePhoneNumbers, App.getMe().getUniquePhoneNumberSupplier());
		saveSupplies(mEMailAddresses, App.getMe().getEMailAddressSupplier());
		saveSupplies(mUniqueEMailAddresses, App.getMe().getUniqueEMailAddressSupplier());
	}

	private static void saveSupplies(List<String> destination, IAddressSupplier supplier) {
		String address = supplier.getAddress();
		while(address != null) {
			destination.add(address);
			address = supplier.getAddress();
		}
	}

	private void assertReception(UUID expectedApiToken, List<AddressData> addresses, boolean expectFailures) {
		String[] srcData = Sender.getMe().getData();
		int sentAddressCnt = Sender.getMe().getMaxTransmissionCount();
		int receivedAddressCnt = 0;
		boolean hasFailure = false;
		for (AddressData address : addresses) {
			if (address.getApiToken() != null) {
				assertEquals(expectedApiToken, address.getApiToken(),"Wrong API token accepted");
				assertEquals(address.getAddress(), srcData[receivedAddressCnt % srcData.length], "Wrong address");
			} else {
				assertEquals( ReceptionException.class.getName(), address.getAddress(),"Wrong exception");
				hasFailure = true;
			}
			receivedAddressCnt++;
		}
		assertEquals(sentAddressCnt, receivedAddressCnt, "Number of received addresses is incorrect");
		if (!expectFailures) {
			assertFalse(hasFailure, "Detected unexpected transmission failures");
		} else {
			assertTrue(hasFailure, "Expected transmission failures, but did not detect some. Because failures occur randomly, this may be OK but it is very unlikely.");
		}
	}

	void assertAddresses(String[] expectedAddresses, List<String> actualAddresses) {
		assertEquals(expectedAddresses.length, actualAddresses.size(), "Wrong number of addresses");
		for(int i = 0; i < expectedAddresses.length; i++) {
			assertNotNull(actualAddresses.get(i), "Expected " + expectedAddresses[i] + ", but received none");
			assertEquals(expectedAddresses[i], actualAddresses.get(i), "Address #"+ i + " is unexpected");
		}
	}

	@Test
	@Order(1)
	void testReceptionWithFailures() {
		Sender.getMe().enableFullSpeed();
		Sender.getMe().setFailureRate(1);
		List<AddressData> addresses = new LinkedList<>();
		App.getMe().startTransmission(addr-> addresses.add(addr), e -> addresses.add(new AddressData(null, e.getClass().getName())));
		UUID apiToken = Sender.getMe().getValidApiToken();
		assertReception(apiToken, addresses,  true);
	}

	@Test
	@Order(2)
	void testReception() {
		assertReception(mApiToken, mAddresses, false);
		/*
		String[] srcData = Sender.getMe().getData();
		int sentAddressCnt = Sender.getMe().getMaxTransmissionCount();
		int receivedAddressCnt = 0;
		UUID apiToken = Sender.getMe().getValidApiToken();
		for (AddressData address : mAddresses) {
			if (address.getApiToken() != null) {
				assertEquals(apiToken, address.getApiToken(),"Wrong API token accepted");
				assertEquals(address.getAddress(), srcData[receivedAddressCnt % srcData.length], "Wrong address");
			} else {
				assertEquals( ReceptionException.class.getName(), address.getAddress(),"Wrong exception");
			}
			 receivedAddressCnt++;
		}
		assertEquals(sentAddressCnt, receivedAddressCnt, "Number of received addresses is incorrect");

		 */
	}


	@Test
	void testValidPhoneNumbers() {
		String[] validPn = {
				"+43 877 877 877",
				"+43 39431 3465",
				"+49 123 55 66",
				"+1 22 33 44444",
				"+43 66112298765",
				"+43 877 877 877",
				"+43 39431 3465",
				"+49 123 55 66",
				"+1 22 33 44444",
				"+43 66112298765",
		};
		assertAddresses(validPn, mPhoneNumbers);
	}
	@Test
	void testUniquePhoneNumbers() {
		String[] uniquePn = {
				"+43 877 877 877",
				"+43 39431 3465",
				"+49 123 55 66",
				"+1 22 33 44444",
				"+43 66112298765",
		};
		assertAddresses(uniquePn, mUniquePhoneNumbers);
	}
	@Test
	void testValidEMailAddresses() {
		String[] validEMailAddress = {
				"abc.edf@example.com",
				"bob.sponge@seaworld.au",
				"pearl@open.water",
				"q.tentacles@squidward.com",
				"karen@plankt.on",
				"sheld.on@plankt.on",
				"abc.edf@example.com",
				"bob.sponge@seaworld.au",
				"pearl@open.water",
				"q.tentacles@squidward.com",
				"karen@plankt.on",
				"sheld.on@plankt.on",
		};
		assertAddresses(validEMailAddress, mEMailAddresses);
	}
	@Test
	void testUniqueEMailAddresses() {
		String[] uniqueEMailAddress = {
				"abc.edf@example.com",
				"bob.sponge@seaworld.au",
				"pearl@open.water",
				"q.tentacles@squidward.com",
				"karen@plankt.on",
				"sheld.on@plankt.on",
		};
		assertAddresses(uniqueEMailAddress, mUniqueEMailAddresses);
	}
}
