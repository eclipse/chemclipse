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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility.PcaColorGroup;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class Chart3DScatter {

	private class UpdateSelectionEvent extends Event {

		/**
		 *
		 */
		private static final long serialVersionUID = -2717902232561677870L;

		public UpdateSelectionEvent() {

			super(SELECTION_UPDATE);
		}
	}

	private static EventType<UpdateSelectionEvent> SELECTION_UPDATE = new EventType<>("SELECTION_UPDATE");
	private final List<IPcaResultVisualization> data = new ArrayList<>();
	final private NumberFormat format = ValueFormat.getNumberFormatEnglish();
	private final Group mainGroup = new Group();
	private double radius;
	private Chart3DSettings settings;

	public Chart3DScatter(Chart3DSettings settings) {

		this.settings = settings;
		this.radius = 15;
		data.clear();
		update();
	}

	public Chart3DScatter(Chart3DSettings settings, IPcaResultsVisualization pcaResults) {

		this(settings);
		data.clear();
		for(IPcaResultVisualization pcaResult : pcaResults.getPcaResultList()) {
			data.add(pcaResult);
		}
		update();
	}

	private Color getColor(IPcaResultVisualization data) {

		Color color = PcaColorGroup.getSampleColorFX(data);
		if(SelectionManagerSample.getInstance().getSelection().contains(data.getSample())) {
			return PcaColorGroup.getActualSelectedColor(color);
		} else {
			if(data.getSample().isSelected()) {
				return color;
			} else {
				return PcaColorGroup.getUnselectedColor(color);
			}
		}
	}

	public Group getScarter() {

		return mainGroup;
	}

	private void update() {

		double sX = settings.getScaleX();
		double sY = settings.getScaleY();
		double sZ = settings.getScaleZ();
		double shiftX = settings.getShiftX() * sX;
		double shiftY = settings.getShiftY() * sY;
		double shiftZ = settings.getShiftZ() * sZ;
		for(IPcaResultVisualization d : data) {
			String name = d.getName();
			/*
			 * create sphere
			 */
			Sphere sphere = new Sphere();
			double pcX = d.getScoreVector()[settings.getPcX()];
			double pcY = d.getScoreVector()[settings.getPcY()];
			double pcZ = d.getScoreVector()[settings.getPcZ()];
			sphere.setTranslateX(pcX * sX + shiftX);
			sphere.setTranslateY(pcY * sY + shiftY);
			sphere.setTranslateZ(pcZ * sZ + shiftZ);
			sphere.setRadius(radius);
			/*
			 * set material
			 */
			PhongMaterial material = new PhongMaterial();
			Color c = getColor(d);
			material.setDiffuseColor(c);
			material.setSpecularColor(c);
			sphere.setMaterial(material);
			/*
			 * add tooltip
			 */
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append('\n');
			String labelX = this.settings.getLabelAxisX();
			if(!labelX.isEmpty()) {
				sb.append(labelX);
				sb.append(" = ");
				sb.append(format.format(pcX));
				sb.append("; ");
			}
			String labelY = this.settings.getLabelAxisY();
			if(!labelY.isEmpty()) {
				sb.append(labelY);
				sb.append(" = ");
				sb.append(format.format(pcY));
				sb.append("; ");
			}
			String labelZ = this.settings.getLabelAxisZ();
			if(!labelZ.isEmpty()) {
				sb.append(labelZ);
				sb.append(" = ");
				sb.append(format.format(pcZ));
				sb.append("; ");
			}
			Tooltip t = new Tooltip(sb.toString());
			Tooltip.install(sphere, t);
			/*
			 * highlight sphere with mouse entered event
			 */
			sphere.setOnMouseEntered(e -> {
				Color darkerColor = getColor(d).darker();
				material.setDiffuseColor(darkerColor);
				material.setSpecularColor(darkerColor);
			});
			sphere.setOnMouseExited(e -> {
				material.setDiffuseColor(getColor(d));
				material.setSpecularColor(getColor(d));
				t.hide();
			});
			sphere.setOnMouseClicked(e -> {
				if(e.getButton().equals(MouseButton.PRIMARY)) {
					if(e.getClickCount() == 2) {
						if(e.isControlDown()) {
							d.getSample().setSelected(!d.getSample().isSelected());
						} else {
							ObservableList<ISample> selection = SelectionManagerSample.getInstance().getSelection();
							if(!selection.contains(d.getSample())) {
								selection.setAll(d.getSample());
							} else {
								selection.remove(d.getSample());
							}
						}
					}
				}
			});
			sphere.addEventFilter(SELECTION_UPDATE, event -> {
				material.setDiffuseColor(getColor(d));
				material.setSpecularColor(getColor(d));
				sphere.setVisible(d.isDisplayed());
			});
			mainGroup.getChildren().addAll(sphere);
		}
		updateSelection();
	}

	public void updateSelection() {

		for(Node node : mainGroup.getChildren()) {
			node.fireEvent(new UpdateSelectionEvent());
		}
	}
}
