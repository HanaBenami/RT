package il.co.rtcohen.rt.app.grids;

import il.co.rtcohen.rt.app.uiComponents.columns.CustomTextColumn;
import il.co.rtcohen.rt.dal.dao.Contact;
import il.co.rtcohen.rt.dal.dao.Site;
import il.co.rtcohen.rt.dal.repositories.*;

public class ContactsGrid extends AbstractTypeWithNameAndActiveFieldsGrid<Contact> {
    private final Site site;

    public ContactsGrid(Site site,
                        ContactRepository contactRepository) {
        super(
                contactRepository, () -> {
                    Contact contact = new Contact();
                    contact.setSite(site);
                    return contact;
                },
                "contactsOfSites",
                contact -> null == site || !contact.getSite().getId().equals(site.getId())
        );
        this.site = (null == site || site.isDraft() ? null : site);
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
        if (null == this.site) {
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

    private void addNotesColumn() {
        CustomTextColumn<Contact> column = CustomTextColumn.addToGrid(
                Contact::getNotes,
                Contact::setNotes,
                false, 230,
                "notesColumn",
                "notes",
                false,
                true,
                false,
                this
        );
    }

    private void addPhoneColumn() {
        CustomTextColumn<Contact> column = CustomTextColumn.addToGrid(
                Contact::getPhone,
                Contact::setPhone,
                false, 230,
                "phoneColumn",
                "phone",
                false,
                true,
                false,
                this
        );
    }
}
