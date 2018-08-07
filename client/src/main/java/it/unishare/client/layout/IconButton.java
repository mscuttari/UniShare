package it.unishare.client.layout;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.NamedArg;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class IconButton extends Button {

    /**
     * Constructor
     *
     * @param   iconName    Font Awesome icon name
     */
    public IconButton(@NamedArg(value = "icon") String iconName, @NamedArg(value = "description") String description) {
        setIcon(iconName);
        setDescription(description);
    }


    /**
     * Set icon
     *
     * @param   iconName    Font Awesome icon name
     */
    private void setIcon(String iconName) {
        if (iconName != null) {
            try {
                FontAwesomeIcon icon = FontAwesomeIcon.valueOf(iconName.toUpperCase());
                FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
                setGraphic(iconView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Create description tooltip
     *
     * @param   description     description
     */
    private void setDescription(String description) {
        if (description != null) {
            Tooltip tooltip = new Tooltip(description);
            Tooltip.install(this, tooltip);
        }
    }

}
