/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.util.Iterator;
import java.util.Map.Entry;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ChartLegend {

	private double radius = 5;
	private Chart3DSettings setting;
	private final VBox vbox = new VBox();

	public ChartLegend(Chart3DSettings settings) {
		this.setting = settings;
		vbox.setMinWidth(120);
		Label legend = new Label("Legend:");
		vbox.setStyle("-fx-padding: 10;");
		vbox.setSpacing(5);
		vbox.getChildren().add(legend);
		update();
	}

	public VBox getLegend() {

		return vbox;
	}

	private void update() {

		vbox.getChildren().clear();
		Label legend = new Label("Legend:");
		vbox.getChildren().add(legend);
		Iterator<Entry<String, Color>> it = setting.getGroup().entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Color> entry = it.next();
			String name = entry.getKey();
			Color color = entry.getValue();
			if(name == null) {
				name = "-----";
			}
			Circle circle = new Circle(radius, color);
			Label label = new Label(name);
			label.setGraphic(circle);
			vbox.getChildren().add(label);
		}
	}
}
