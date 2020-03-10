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

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaVisualization;

import javafx.scene.paint.Color;

public class Chart3DSettings {

	public static void setAxes2(Chart3DSettings settings, int scale) {

		int numberLines = settings.getMaxNumberLine();
		double[] X = setAxes(settings.minX, settings.maxX, numberLines);
		double[] Y = setAxes(settings.minY, settings.maxY, numberLines);
		double[] Z = setAxes(settings.minZ, settings.maxZ, numberLines);
		settings.axisMaxX = X[0];
		settings.axisMaxY = Y[0];
		settings.axisMaxZ = Z[0];
		settings.axisMinX = X[1];
		settings.axisMinY = Y[1];
		settings.axisMinZ = Z[1];
		settings.lineSpacingX = X[2];
		settings.lineSpacingY = Y[2];
		settings.lineSpacingZ = Z[2];
		setScale(settings, scale);
	}

	public static void setAxes(Chart3DSettings settings, int scale) {

		int numberLines = settings.getMaxNumberLine();
		double[] X = setAxesSquared(settings.minX, settings.maxX, numberLines);
		double[] Y = setAxesSquared(settings.minY, settings.maxY, numberLines);
		double[] Z = setAxesSquared(settings.minZ, settings.maxZ, numberLines);
		settings.axisMaxX = X[0];
		settings.axisMaxY = Y[0];
		settings.axisMaxZ = Z[0];
		settings.axisMinX = X[1];
		settings.axisMinY = Y[1];
		settings.axisMinZ = Z[1];
		settings.lineSpacingX = X[2];
		settings.lineSpacingY = Y[2];
		settings.lineSpacingZ = Z[2];
		setScale(settings, scale);
	}

	private static void setScale(Chart3DSettings settings, int point) {

		double maxDisX = Math.abs(settings.axisMaxX - settings.axisMinX);
		double maxDisY = Math.abs(settings.axisMaxY - settings.axisMinY);
		double maxDisZ = Math.abs(settings.axisMaxZ - settings.axisMinZ);
		settings.scaleX = point / maxDisX;
		settings.scaleY = point / maxDisY;
		settings.scaleZ = point / maxDisZ;
		settings.shiftX = getShift(settings.axisMinX, settings.axisMaxX);
		settings.shiftY = getShift(settings.axisMinY, settings.axisMaxY);
		settings.shiftZ = getShift(settings.axisMinZ, settings.axisMaxZ);
	}

	private static double getShift(double x, double y) {

		double min = Math.min(x, y);
		double max = Math.max(x, y);
		if(min > 0) {
			return -(min + (max - min) / 2);
		} else if(max < 0) {
			return (-max + (-min + max) / 2);
		} else {
			if(-min < max) {
				return -(max + min) / 2;
			} else {
				return -(max + min) / 2;
			}
		}
	}

	private static double[] setAxesSquared(double x, double y, int numberLines) {

		double absMax = Math.max(Math.abs(x), Math.abs(y));
		double numberDigits = Math.floor(Math.log10(absMax));
		double round = Math.pow(10, numberDigits);
		double lineSpacing = (((Math.round(absMax / round) * round) / numberLines));
		double maxAxis = Math.ceil(absMax / lineSpacing) * lineSpacing;
		double minAxis = Math.floor(-absMax / lineSpacing) * lineSpacing;
		return new double[]{maxAxis, minAxis, lineSpacing};
	}

	private static double[] setAxes(double x, double y, int numberLines) {

		double max = Math.max(x, y);
		double min = Math.min(x, y);
		double absMax = Math.abs(x - y);
		double numberDigits = Math.floor(Math.log10(absMax));
		double round = Math.pow(10, numberDigits);
		double lineSpacing = (((Math.round(absMax / round) * round) / numberLines));
		double maxAxis = Math.ceil(max / (2 * lineSpacing)) * 2 * lineSpacing;
		double minAxis = Math.floor(min / (2 * lineSpacing)) * 2 * lineSpacing;
		if((maxAxis - max) < (lineSpacing / 2) || (min - minAxis) < (lineSpacing / 2)) {
			maxAxis = maxAxis + lineSpacing;
			minAxis = minAxis - lineSpacing;
		}
		return new double[]{maxAxis, minAxis, lineSpacing};
	}

	public static void setSettings(Chart3DSettings settings, IPcaResultsVisualization pcaResults) {

		IPcaVisualization analysisSettings = pcaResults.getPcaVisualization();
		int pcX = analysisSettings.getPcX() - 1;
		int pcY = analysisSettings.getPcY() - 1;
		int pcZ = analysisSettings.getPcZ() - 1;
		settings.setPcX(pcX);
		settings.setPcY(pcY);
		settings.setPcZ(pcZ);
		/*
		 * set min and max
		 */
		settings.minX = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getScoreVector()[pcX], d2.getScoreVector()[pcX])).get().getScoreVector()[pcX];
		settings.minY = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getScoreVector()[pcY], d2.getScoreVector()[pcY])).get().getScoreVector()[pcY];
		settings.minZ = pcaResults.getPcaResultList().stream().min((d1, d2) -> Double.compare(d1.getScoreVector()[pcZ], d2.getScoreVector()[pcZ])).get().getScoreVector()[pcZ];
		settings.maxX = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getScoreVector()[pcX], d2.getScoreVector()[pcX])).get().getScoreVector()[pcX];
		settings.maxY = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getScoreVector()[pcY], d2.getScoreVector()[pcY])).get().getScoreVector()[pcY];
		settings.maxZ = pcaResults.getPcaResultList().stream().max((d1, d2) -> Double.compare(d1.getScoreVector()[pcZ], d2.getScoreVector()[pcZ])).get().getScoreVector()[pcZ];
	}

	private double axisMaxX;
	private double axisMaxY;
	private double axisMaxZ;
	private double axisMinX;
	private double axisMinY;
	private double axisMinZ;
	private Map<String, Color> groups = new LinkedHashMap<>();
	private double lineSpacingX;
	private double lineSpacingY;
	private double lineSpacingZ;
	private int maxNumberLine = 5;
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	private int pcX;
	private int pcY;
	private int pcZ;
	private double scaleX;
	private double scaleY;
	private double scaleZ;
	private double shiftX;
	private double shiftY;
	private double shiftZ;

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
		setAxes(this, scale);
	}

	private String createAxisLabel(int componentNumber) {

		componentNumber++;
		return "PC " + componentNumber;
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

	public double getLineSpacingX() {

		return lineSpacingX;
	}

	public double getLineSpacingY() {

		return lineSpacingY;
	}

	public double getLineSpacingZ() {

		return lineSpacingZ;
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

	public double getScaleX() {

		return scaleX;
	}

	public double getScaleY() {

		return scaleY;
	}

	public double getScaleZ() {

		return scaleZ;
	}

	public double getShiftX() {

		return shiftX;
	}

	public double getShiftY() {

		return shiftY;
	}

	public double getShiftZ() {

		return shiftZ;
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
}
