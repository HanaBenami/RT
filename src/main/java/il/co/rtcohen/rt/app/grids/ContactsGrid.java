package il.co.rtcohen.rt.app.ui.grids;

import com.vaadin.data.ValueProvider;
import com.vaadin.server.Setter;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Component;
import il.co.rtcohen.rt.app.UIComponents;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class ContactsGrid extends AbstractFilterGrid<Contact> {
    private final Site site;

    public ContactsGrid(Site site,
                        ContactRepository contactRepository) {
        super(contactRepository, () -> {
                    Contact contact = new Contact();
                    contact.setSiteId(site.getId());
                    return contact;
                },
                "contacts",
                contact -> null == site || !contact.getSiteId().equals(site.getId()));
        this.site = site;
        this.initGrid();
    }

    protected void addColumns() {
        addActiveColumn();
        addNotesColumn();
        addPhoneColumn();
        addNameColumn();
        addIdColumn();
    }

    protected void sort() {
        this.sort("nameColumn", SortDirection.ASCENDING);
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

    private void addIdColumn() {
        this.addNumericColumn(
                Contact::getId,
                null,
                80,
                "idColumn",
                "id"
        );
    }
}
