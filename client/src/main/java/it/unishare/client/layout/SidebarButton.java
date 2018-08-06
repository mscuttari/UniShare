package it.unishare.client.layout;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.NamedArg;
import javafx.scene.control.ToggleButton;

public class SidebarButton extends ToggleButton {

    /**
     * Constructor
     *
     * @param   iconName    Font Awesome icon name
     */
    public SidebarButton(@NamedArg(value = "icon") String iconName) {
        setMaxWidth(Double.MAX_VALUE);  // Full width
        getStyleClass().add("menus");   // Menu button style
        setMnemonicParsing(false);      // Disable mnemonic parsing
        setIcon(iconName);              // Set icon
    }


    /**
     * Set icon
     *
     * @param   iconName    Font Awesome icon name
     */
    public void setIcon(String iconName) {
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

}
