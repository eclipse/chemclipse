/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

	private Chart3DData data;
	private double radius = 5;
	private final VBox vbox = new VBox();
	private double verticalSpace = 5;

	public ChartLegend(Chart3DData chart3dData) {
		this.data = chart3dData;
	}

	public VBox getLegend() {

		return vbox;
	}

	public void update() {

		vbox.getChildren().clear();
		vbox.setSpacing(verticalSpace);
		Label legend = new Label("Legend:");
		vbox.getChildren().add(legend);
		Iterator<Entry<String, Color>> it = data.getGroup().entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Color> entry = it.next();
			String name = entry.getKey();
			Color color = entry.getValue();
			Circle circle = new Circle(radius, color);
			Label label = new Label(name);
			label.setGraphic(circle);
			vbox.getChildren().add(label);
		}
	}
}
