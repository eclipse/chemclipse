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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;

import javafx.scene.paint.Color;

public class Chart3DData {

	final private List<Chart3DSampleData> data = new ArrayList<>();
	private Map<String, Color> groups = new LinkedHashMap<>();
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

	public Chart3DData() {
		scale = 1.0;
	}

	private String createAxisLabel(int componentNumber) {

		if(componentNumber > 0) {
			return "PC " + componentNumber;
		} else {
			return "";
		}
	}

	public List<Chart3DSampleData> getData() {

		return data;
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

	public double getMaxX(boolean isScaled) {

		return isScaled ? maxX * scale : maxX;
	}

	public double getMaxY(boolean isScaled) {

		return isScaled ? maxY * scale : maxY;
	}

	public double getMaxZ(boolean isScaled) {

		return isScaled ? maxZ * scale : maxZ;
	}

	public double getMinX(boolean isScaled) {

		return isScaled ? minX * scale : minX;
	}

	public double getMinY(boolean isScaled) {

		return isScaled ? minY * scale : minY;
	}

	public double getMinZ(boolean isScaled) {

		return isScaled ? minZ * scale : minZ;
	}

	public int getPCX() {

		return pcX;
	}

	public int getPCY() {

		return pcY;
	}

	public int getPCZ() {

		return pcZ;
	}

	public double getScale() {

		return scale;
	}

	public boolean isEmpty() {

		return data.isEmpty();
	}

	public void removeData() {

		/*
		 * clear data
		 */
		groups.clear();
		data.clear();
		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = 0;
		maxY = 0;
		maxZ = 0;
	}

	public void setScale(double scale) {

		data.forEach(d -> d.setScale(scale));
		this.scale = scale;
	}

	public void setScale(int point) {

		double maxDis = Math.max(Math.abs(maxX - minX), Math.max(Math.abs(maxY - minY), Math.abs(maxZ - minZ)));
		setScale(point / maxDis);
	}

	public void update(IPcaResults pcaResults, int pcX, int pcY, int pcZ, int scale) {

		removeData();
		/*
		 * set principal component
		 */
		this.pcX = pcX;
		this.pcY = pcY;
		this.pcZ = pcZ;
		/*
		 *
		 */
		Set<String> groupNames = PcaUtils.getGroupNames(pcaResults.getPcaResultList());
		Map<String, Color> groupNameColore = PcaColorGroup.getColorJavaFx(groupNames);
		Iterator<Entry<String, Color>> it = groupNameColore.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, Color> entry = it.next();
			if(entry.getKey() == null) {
				groups.put("------", entry.getValue());
			} else {
				groups.put(entry.getKey(), entry.getValue());
			}
		}
		/*
		 * update data
		 */
		for(IPcaResult pcaResul : pcaResults.getPcaResultList()) {
			Color color = groupNameColore.get(pcaResul.getGroupName());
			data.add(new Chart3DSampleData(pcaResul, pcX, pcY, pcZ, color));
		}
		/*
		 * set min and max
		 */
		minX = data.stream().min((d1, d2) -> Double.compare(d1.getPcaXData(false), d2.getPcaXData(false))).get().getPcaXData(false);
		minY = data.stream().min((d1, d2) -> Double.compare(d1.getPcaYData(false), d2.getPcaYData(false))).get().getPcaYData(false);
		minZ = data.stream().min((d1, d2) -> Double.compare(d1.getPcaZData(false), d2.getPcaZData(false))).get().getPcaZData(false);
		maxY = data.stream().max((d1, d2) -> Double.compare(d1.getPcaYData(false), d2.getPcaYData(false))).get().getPcaYData(false);
		maxX = data.stream().max((d1, d2) -> Double.compare(d1.getPcaXData(false), d2.getPcaXData(false))).get().getPcaXData(false);
		maxZ = data.stream().max((d1, d2) -> Double.compare(d1.getPcaZData(false), d2.getPcaZData(false))).get().getPcaZData(false);
		/*
		 * update scale
		 */
		setScale(scale);
	}
}
