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

import java.text.NumberFormat;
import java.util.List;

import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class Chart3DScatter {

	private Chart3DData data;
	final private NumberFormat format = ValueFormat.getNumberFormatEnglish();
	private final Group mainGroup = new Group();
	private double radius;

	public Chart3DScatter(Chart3DData chart3dData) {
		this.data = chart3dData;
		this.radius = 15;
	}

	public Group getScarter() {

		return mainGroup;
	}

	public void update() {

		mainGroup.getChildren().clear();
		List<Chart3DSampleData> data = this.data.getData();
		for(Chart3DSampleData d : data) {
			Color color = d.getColor();
			String name = d.getSample().getName();
			/*
			 * create sphere
			 */
			Sphere sphere = new Sphere();
			sphere.setTranslateX(d.getPcaXData(true));
			sphere.setTranslateY(d.getPcaYData(true));
			sphere.setTranslateZ(d.getPcaZData(true));
			sphere.setRadius(radius);
			/*
			 * set material
			 */
			PhongMaterial material = new PhongMaterial();
			material.setDiffuseColor(color);
			material.setSpecularColor(color);
			sphere.setMaterial(material);
			/*
			 * add tooltip
			 */
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append('\n');
			String labelX = this.data.getLabelAxisX();
			if(!labelX.isEmpty()) {
				sb.append(labelX);
				sb.append(" = ");
				sb.append(format.format(d.getPcaXData(false)));
				sb.append("; ");
			}
			String labelY = this.data.getLabelAxisY();
			if(!labelY.isEmpty()) {
				sb.append(labelY);
				sb.append(" = ");
				sb.append(format.format(d.getPcaYData(false)));
				sb.append("; ");
			}
			String labelZ = this.data.getLabelAxisZ();
			if(!labelZ.isEmpty()) {
				sb.append(labelZ);
				sb.append(" = ");
				sb.append(format.format(d.getPcaZData(false)));
				sb.append("; ");
			}
			Tooltip t = new Tooltip(sb.toString());
			Tooltip.install(sphere, t);
			/*
			 * highlight sphere with mouse entered event
			 */
			sphere.setOnMouseEntered(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent event) {

					material.setDiffuseColor(color.darker());
					material.setSpecularColor(color.darker());
				}
			});
			sphere.setOnMouseExited(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {

					material.setDiffuseColor(color);
					material.setSpecularColor(color);
				}
			});
			mainGroup.getChildren().addAll(sphere);
		}
	}
}
