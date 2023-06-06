package il.co.rtcohen.rt.app.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UiComponents.UIComponents;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class ContactsGrid extends AbstractFilterGrid<Contact> {
    private final Site site;

    public ContactsGrid(Site site,
                        ContactRepository contactRepository) {
        super(contactRepository, () -> {
                    Contact contact = new Contact();
                    contact.setSite(site);
                    return contact;
                },
                "contactsOfSites",
                contact -> null == site || !contact.getSite().getId().equals(site.getId()));
        this.site = site;
        this.initGrid();
    }

    @Override
    protected void setTitle() {
        super.setTitle();
        if (null != this.site) {
            this.title += " " + site.getName();
        }
    }

    @Override
    protected void changeErrorMessage() {
        String errorMessageKey = null;
        String warningMessageKey = null;
        if (null == site) {
            errorMessageKey = "noSite";
        } else if (0 == this.getItemsCounter()) {
            warningMessageKey = "noContactsToSite";
        }
        this.setErrorMessage(errorMessageKey);
        this.setWarningMessage(warningMessageKey);
    }

    protected void addColumns() {
        addActiveColumn();
        addNotesColumn();
        addPhoneColumn();
        addNameColumn();
        addIdColumn();
    }

    private void addActiveColumn() {
        this.addBooleanColumn(
                (ValueProvider<Contact, Component>) contact -> UIComponents.checkBox(contact.isActive(),true),
                (ValueProvider<Contact, Boolean>) Contact::isActive,
                (Setter<Contact, Boolean>) Contact::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE
        );
    }

    private void addNotesColumn() {
        this.addTextColumn(
                Contact::getNotes,
                Contact::setNotes,
                230,
                "notesColumn",
                "notes"
        );
    }

    private void addPhoneColumn() {
        this.addTextColumn(
                Contact::getPhone,
                Contact::setPhone,
                230,
                "phoneColumn",
                "phone"
        );
    }

    private void addNameColumn() {
        this.addTextColumn(
                Contact::getName,
                Contact::setName,
                230,
                "nameColumn",
                "name"
        );
    }
}
