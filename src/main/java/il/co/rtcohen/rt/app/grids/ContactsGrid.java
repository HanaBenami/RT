package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.CustomCheckBoxColumn;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class ContactsGrid extends AbstractTypeFilterGrid<Contact> {
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

    protected void addActiveColumn() {
        CustomCheckBoxColumn.addToGrid(
                Contact::isActive,
                Contact::setActive,
                "activeColumn",
                "active",
                Boolean.TRUE,
                this
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
