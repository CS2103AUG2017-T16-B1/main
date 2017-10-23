package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.model.AddressBookChangedEvent;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.model.parcel.Parcel;
import seedu.address.model.parcel.ReadOnlyParcel;
import seedu.address.model.parcel.exceptions.DuplicateParcelException;
import seedu.address.model.parcel.exceptions.ParcelNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.exceptions.TagInternalErrorException;
import seedu.address.model.tag.exceptions.TagNotFoundException;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);
    private static boolean selected = false;
    private static Index prevIndex = Index.fromZeroBased(0);

    private final AddressBook addressBook;
    private final FilteredList<ReadOnlyParcel> filteredParcels;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        filteredParcels = new FilteredList<>(this.addressBook.getParcelList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    @Override
    public synchronized void deleteParcel(ReadOnlyParcel target) throws ParcelNotFoundException {
        addressBook.removeParcel(target);
        indicateAddressBookChanged();
    }

    /** Deletes the tag from every parcel in the address book */
    public void deleteTag(Tag target) throws TagNotFoundException, TagInternalErrorException {

        int tagsFound = 0;
        Iterator it = addressBook.getParcelList().iterator();
        while (it.hasNext()) {
            Parcel oldParcel = (Parcel) it.next();
            Parcel newParcel = new Parcel(oldParcel);
            Set<Tag> newTags = new HashSet<>(newParcel.getTags());
            if (newTags.contains(target)) {
                newTags.remove(target);
                tagsFound++;
            }

            newParcel.setTags(newTags);

            try {
                addressBook.updateParcel(oldParcel, newParcel);
            } catch (DuplicateParcelException | ParcelNotFoundException dpe) {
                throw new TagInternalErrorException();
            }
        }

        if (tagsFound == 0) {
            throw new TagNotFoundException();
        }

        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addParcel(ReadOnlyParcel parcel) throws DuplicateParcelException {
        addressBook.addParcel(parcel);
        updateFilteredParcelList(PREDICATE_SHOW_ALL_PARCELS);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addAllParcels(List<ReadOnlyParcel> parcels, List<ReadOnlyParcel> parcelsAdded,
                                           List<ReadOnlyParcel> duplicateParcels) {

        for (ReadOnlyParcel parcel : parcels) {
            ReadOnlyParcel parcelToAdd = new Parcel(parcel);
            try {
                addressBook.addParcel(parcelToAdd);
                parcelsAdded.add(parcelToAdd);
            } catch (DuplicateParcelException ive) {
                duplicateParcels.add(parcelToAdd);
            }
        }

        updateFilteredParcelList(PREDICATE_SHOW_ALL_PARCELS);
        indicateAddressBookChanged();
    }

    @Override
    public void updateParcel(ReadOnlyParcel target, ReadOnlyParcel editedParcel)
            throws DuplicateParcelException, ParcelNotFoundException {
        requireAllNonNull(target, editedParcel);

        addressBook.updateParcel(target, editedParcel);
        indicateAddressBookChanged();
    }

    //=========== Filtered Parcel List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code ReadOnlyParcel} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<ReadOnlyParcel> getFilteredParcelList() {
        return FXCollections.unmodifiableObservableList(filteredParcels);
    }

    @Override
    public void updateFilteredParcelList(Predicate<ReadOnlyParcel> predicate) {
        requireNonNull(predicate);
        filteredParcels.setPredicate(predicate);
    }

    @Override
    public void maintainSorted() {
        addressBook.sort();
        indicateAddressBookChanged();
    }

    @Override
    public boolean hasSelected() {
        return selected;
    }

    @Override
    public void select() {
        selected = true;
    }

    @Override
    public void unselect() {
        selected = false;
    }

    @Override
    public void forceSelect(Index target) {
        EventsCenter.getInstance().post(new JumpToListRequestEvent(target));
    }

    public void setPrevIndex(Index newIndex) {
        prevIndex = newIndex;
    }

    public Index getPrevIndex() {
        return prevIndex;
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredParcels.equals(other.filteredParcels);
    }
    
}
