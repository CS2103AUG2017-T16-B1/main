package seedu.address.model;

import static org.junit.Assert.fail;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.model.parcel.ReadOnlyParcel;
import seedu.address.model.parcel.exceptions.DuplicateParcelException;
import seedu.address.model.parcel.exceptions.ParcelNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.exceptions.TagNotFoundException;

/**
 * A default model stub that have all of the methods failing.
 */
public class ModelStub implements Model {
    @Override
    public void addParcel(ReadOnlyParcel parcel) throws DuplicateParcelException {
        fail("This method should not be called.");
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        fail("This method should not be called.");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        fail("This method should not be called.");
        return null;
    }

    @Override
    public void deleteParcel(ReadOnlyParcel target) throws ParcelNotFoundException {
        fail("This method should not be called.");
    }

    @Override
    public void deleteTag(Tag target) throws TagNotFoundException {
        fail("This method should not be called.");
    }

    @Override
    public void updateParcel(ReadOnlyParcel target, ReadOnlyParcel editedParcel)
            throws DuplicateParcelException {
        fail("This method should not be called.");
    }

    @Override
    public ObservableList<ReadOnlyParcel> getFilteredParcelList() {
        fail("This method should not be called.");
        return null;
    }

    @Override
    public void updateFilteredParcelList(Predicate<ReadOnlyParcel> predicate) {
        fail("This method should not be called.");
    }
}
