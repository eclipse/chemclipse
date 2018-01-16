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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;

import javafx.scene.paint.Color;

public class Chart3DSettings {

	public static void setAxes(Chart3DSettings settings) {

		double absMaximum = Arrays.stream(new double[]{settings.getMinX(), settings.getMaxX(), settings.getMinY(), settings.getMaxY(), settings.getMinZ(), settings.getMaxZ()}).map(d -> Math.abs(d)).max().getAsDouble();
		double numberDigits = Math.floor(Math.log10(absMaximum));
		double round = Math.pow(10, numberDigits);
		double lineSpacing = (((Math.round(absMaximum / round) * round) / settings.getMaxNumberLine()));
		BiFunction<Double, Double, Double> getAbsMax = (min, max) -> {
			double absMax = (Math.abs(min) > Math.abs(max) ? Math.abs(min) : Math.abs(max));
			return Math.ceil(absMax / lineSpacing) * lineSpacing;
		};
		settings.axisMaxX = getAbsMax.apply(settings.getMinX(), settings.getMaxX()) + lineSpacing;
		settings.axisMaxY = getAbsMax.apply(settings.getMinY(), settings.getMaxY()) + lineSpacing;
		settings.axisMaxZ = getAbsMax.apply(settings.getMinZ(), settings.getMaxZ()) + lineSpacing;
		settings.axisMinX = -settings.axisMaxX;
		settings.axisMinY = -settings.axisMaxY;
		settings.axisMinZ = -settings.axisMaxZ;
		settings.lineSpacing = lineSpacing;
	}

	public static void setSettings(Chart3DSettings settings, IPcaResults pcaResults, int scale) {

		IPcaSettings pcaSettings = pcaResults.getPcaSettings();
		int pcX = pcaSettings.getPcX() - 1;
		int pcY = pcaSettings.getPcY() - 1;
		int pcZ = pcaSettings.getPcZ() - 1;
		settings.setPcX(pcX);
		settings.setPcY(pcY);
		settings.setPcZ(pcZ);
		Set<String> groupNames = PcaUtils.getGroupNames(pcaResults.getPcaResultList());
		Map<String, Color> groupNameColore = PcaColorGroup.getColorJavaFx(groupNames);
		settings.getColorGroup().clear();
		Iterator<Entry<String, Color>> it = groupNameColore.entrySet().iterator();
		//
		while(it.hasNext()) {
			Map.Entry<String, Color> entry = it.next();
			if(entry.getKey() == null) {
				settings.getColorGroup().put("------", entry.getValue());
			} else {
				settings.getColorGroup().put(entry.getKey(), entry.getValue());
			}
		}
		/*
		 * set min and max
		 */
		settings.minX = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getEigenSpace()[pcX], d2.getEigenSpace()[pcX])).get().getEigenSpace()[pcX];
		settings.minY = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getEigenSpace()[pcY], d2.getEigenSpace()[pcY])).get().getEigenSpace()[pcY];
		settings.minZ = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getEigenSpace()[pcZ], d2.getEigenSpace()[pcZ])).get().getEigenSpace()[pcZ];
		settings.maxX = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getEigenSpace()[pcX], d2.getEigenSpace()[pcX])).get().getEigenSpace()[pcX];
		settings.maxY = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getEigenSpace()[pcY], d2.getEigenSpace()[pcY])).get().getEigenSpace()[pcY];
		settings.maxZ = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getEigenSpace()[pcZ], d2.getEigenSpace()[pcZ])).get().getEigenSpace()[pcZ];
		settings.setScale(scale);
		settings.getColorGroup().clear();
		settings.getColorGroup().putAll(groupNameColore);
	}

	private double axisMaxX;
	private double axisMaxY;
	private double axisMaxZ;
	private double axisMinX;
	private double axisMinY;
	private double axisMinZ;
	private Map<String, Color> groups = new LinkedHashMap<>();
	private double lineSpacing;
	private int maxNumberLine = 10;
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	private int pcX;
	private int pcY;
	private int pcZ;
	private double scale;

	public Chart3DSettings(int scale) {
		maxX = 100;
		maxY = 100;
		maxZ = 100;
		minX = -100;
		minY = -100;
		minZ = -100;
		pcX = 1;
		pcY = 2;
		pcZ = 3;
		setScale(scale);
		setAxes(this);
	}

	private String createAxisLabel(int componentNumber) {

		if(componentNumber > 0) {
			return "PC " + componentNumber;
		} else {
			return "";
		}
	}

	public double getAxisMaxX() {

		return axisMaxX;
	}

	public double getAxisMaxY() {

		return axisMaxY;
	}

	public double getAxisMaxZ() {

		return axisMaxZ;
	}

	public double getAxisMinX() {

		return axisMinX;
	}

	public double getAxisMinY() {

		return axisMinY;
	}

	public double getAxisMinZ() {

		return axisMinZ;
	}

	public double getAxisXlenght() {

		return Math.abs(axisMaxX - axisMinX);
	}

	public double getAxisYlenght() {

		return Math.abs(axisMaxY - axisMinY);
	}

	public double getAxisZlenght() {

		return Math.abs(axisMaxZ - axisMinZ);
	}

	public Map<String, Color> getColorGroup() {

		return groups;
	}

	public Map<String, Color> getGroup() {

		return groups;
	}

	public String getLabelAxisX() {

		return createAxisLabel(pcX);
	}

	public String getLabelAxisY() {

		return createAxisLabel(pcY);
	}

	public String getLabelAxisZ() {

		return createAxisLabel(pcZ);
	}

	public double getLineSpacing() {

		return lineSpacing;
	}

	public int getMaxNumberLine() {

		return maxNumberLine;
	}

	public double getMaxX() {

		return maxX;
	}

	public double getMaxY() {

		return maxY;
	}

	public double getMaxZ() {

		return maxZ;
	}

	public double getMinX() {

		return minX;
	}

	public double getMinY() {

		return minY;
	}

	public double getMinZ() {

		return minZ;
	}

	public int getPcX() {

		return pcX;
	}

	public int getPcY() {

		return pcY;
	}

	public int getPcZ() {

		return pcZ;
	}

	public double getScale() {

		return scale;
	}

	public void setAxiMinY(double axisMinY) {

		this.axisMinY = axisMinY;
	}

	public void setAxisMaxX(double axisMaxX) {

		this.axisMaxX = axisMaxX;
	}

	public void setAxisMaxY(double axisMaxY) {

		this.axisMaxY = axisMaxY;
	}

	public void setAxisMaxZ(double axisMaxZ) {

		this.axisMaxZ = axisMaxZ;
	}

	public void setAxisMinX(double axisMinX) {

		this.axisMinX = axisMinX;
	}

	public void setAxisMinZ(double axisMinZ) {

		this.axisMinZ = axisMinZ;
	}

	public void setLineSpacing(double lineSpacing) {

		this.lineSpacing = lineSpacing;
	}

	public void setMaxNumberLine(int maxNumberLine) {

		this.maxNumberLine = maxNumberLine;
	}

	public void setPcX(int pcX) {

		this.pcX = pcX;
	}

	public void setPcY(int pcY) {

		this.pcY = pcY;
	}

	public void setPcZ(int pcZ) {

		this.pcZ = pcZ;
	}

	public void setScale(double scale) {

		this.scale = scale;
	}

	public void setScale(int point) {

		double maxDis = Math.max(Math.abs(maxX - minX), Math.max(Math.abs(maxY - minY), Math.abs(maxZ - minZ)));
		setScale(point / maxDis);
	}
}
