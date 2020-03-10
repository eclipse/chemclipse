/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - getting rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultsPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.ColorSupport;
import org.eclipse.chemclipse.support.text.ValueFormat;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class ScorePlot3DScatter {

	private static final NumberFormat FORMAT = ValueFormat.getNumberFormatEnglish();
	//
	private static EventType<UpdateSelectionEvent> SELECTION_UPDATE = new EventType<>("SELECTION_UPDATE");
	private final List<IResultPCA> pcaResultList = new ArrayList<>();
	private final Group mainGroup = new Group();
	private double radius;
	private ScorePlot3DSettings settings;

	private class UpdateSelectionEvent extends Event {

		private static final long serialVersionUID = -2717902232561677870L;

		public UpdateSelectionEvent() {
			super(SELECTION_UPDATE);
		}
	}

	public ScorePlot3DScatter(ScorePlot3DSettings settings) {
		this.settings = settings;
		this.radius = 15;
		pcaResultList.clear();
		update();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public ScorePlot3DScatter(ScorePlot3DSettings settings, IResultsPCA resultsPCA) {
		this(settings);
		pcaResultList.clear();
		//
		if(resultsPCA != null) {
			pcaResultList.addAll(resultsPCA.getPcaResultList());
		}
		//
		update();
	}

	public Group getScarter() {

		return mainGroup;
	}

	public void updateSelection() {

		for(Node node : mainGroup.getChildren()) {
			node.fireEvent(new UpdateSelectionEvent());
		}
	}

	private void update() {

		double sX = settings.getScaleX();
		double sY = settings.getScaleY();
		double sZ = settings.getScaleZ();
		double shiftX = settings.getShiftX() * sX;
		double shiftY = settings.getShiftY() * sY;
		double shiftZ = settings.getShiftZ() * sZ;
		//
		for(IResultPCA resultPCA : pcaResultList) {
			/*
			 * Colors
			 */
			Map<String, Color> colorMap = ColorSupport.getGroupNameColorMapFX(pcaResultList);
			String name = resultPCA.getName();
			String groupName = resultPCA.getGroupName();
			/*
			 * create sphere
			 */
			Sphere sphere = new Sphere();
			double pcX = resultPCA.getScoreVector()[settings.getPcX()];
			double pcY = resultPCA.getScoreVector()[settings.getPcY()];
			double pcZ = resultPCA.getScoreVector()[settings.getPcZ()];
			sphere.setTranslateX(pcX * sX + shiftX);
			sphere.setTranslateY(pcY * sY + shiftY);
			sphere.setTranslateZ(pcZ * sZ + shiftZ);
			sphere.setRadius(radius);
			/*
			 * set material
			 */
			PhongMaterial material = new PhongMaterial();
			Color color = colorMap.get(groupName);
			material.setDiffuseColor(color);
			material.setSpecularColor(color);
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
				sb.append(FORMAT.format(pcX));
				sb.append("; ");
			}
			String labelY = this.settings.getLabelAxisY();
			if(!labelY.isEmpty()) {
				sb.append(labelY);
				sb.append(" = ");
				sb.append(FORMAT.format(pcY));
				sb.append("; ");
			}
			String labelZ = this.settings.getLabelAxisZ();
			if(!labelZ.isEmpty()) {
				sb.append(labelZ);
				sb.append(" = ");
				sb.append(FORMAT.format(pcZ));
				sb.append("; ");
			}
			Tooltip t = new Tooltip(sb.toString());
			Tooltip.install(sphere, t);
			/*
			 * highlight sphere with mouse entered event
			 */
			sphere.setOnMouseEntered(e -> {
				Color darkerColor = colorMap.get(groupName).darker();
				material.setDiffuseColor(darkerColor);
				material.setSpecularColor(darkerColor);
			});
			sphere.setOnMouseExited(e -> {
				material.setDiffuseColor(colorMap.get(groupName));
				material.setSpecularColor(colorMap.get(groupName));
				t.hide();
			});
			sphere.setOnMouseClicked(e -> {
				if(e.getButton().equals(MouseButton.PRIMARY)) {
					if(e.getClickCount() == 2) {
						if(e.isControlDown()) {
							resultPCA.getSample().setSelected(!resultPCA.getSample().isSelected());
						} else {
							//
						}
					}
				}
			});
			sphere.addEventFilter(SELECTION_UPDATE, event -> {
				material.setDiffuseColor(colorMap.get(groupName));
				material.setSpecularColor(colorMap.get(groupName));
				sphere.setVisible(resultPCA.isDisplayed());
			});
			mainGroup.getChildren().addAll(sphere);
		}
		updateSelection();
	}
}
