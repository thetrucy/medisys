package com.medisys.controller;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
public class ButtonController {
   public static void onClickEffect(@SuppressWarnings("exports") Button button) {
	    // Change background color and add effect
	    button.setStyle("-fx-background-color: #3c83c6; -fx-text-fill: white;");
	    DropShadow shadow = new DropShadow();
	    shadow.setColor(Color.DARKBLUE);
	    button.setEffect(shadow);
    }
}
