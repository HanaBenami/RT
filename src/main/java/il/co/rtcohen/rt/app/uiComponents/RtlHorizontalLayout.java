package il.co.rtcohen.rt.app.uiComponents;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import il.co.rtcohen.rt.app.LanguageSettings;

public class RtlHorizontalLayout extends HorizontalLayout {
    public RtlHorizontalLayout() {
        super();
        this.setWidth(this.getWidth(), this.getWidthUnits());
        this.setDefaultComponentAlignment(
                (LanguageSettings.isHebrew() ? Alignment.MIDDLE_RIGHT : Alignment.MIDDLE_LEFT)
        );
    }

    @Override
    public void addComponent(Component c) {
        if (LanguageSettings.isHebrew()) {
            super.addComponentAsFirst(c);
        } else {
            super.addComponent(c);
        }
    }
}
