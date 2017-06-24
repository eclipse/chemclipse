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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility.PcaColorGroup;

import javafx.scene.paint.Color;

public class Chart3DData {

	final private List<Chart3DSampleData> data = new ArrayList<>();
	private Map<String, Color> groups = new LinkedHashMap<>();
	private boolean isEmpty = false;
	private double maxX;
	private double maxY;
	private double maxZ;
	private double minX;
	private double minY;
	private double minZ;
	private PcaEditor pcaEditor;
	private int pcaX;
	private int pcaY;
	private int pcaZ;

	public Chart3DData(PcaEditor pcaEditor) {
		this.pcaEditor = pcaEditor;
	}

	private String createAxisName(int componentNumber) {

		if(componentNumber > 0) {
			return "PCA " + componentNumber;
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

	public String getLabelX() {

		return createAxisName(pcaX);
	}

	public String getLabelY() {

		return createAxisName(pcaY);
	}

	public String getLabelZ() {

		return createAxisName(pcaZ);
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

	public int getPcaX() {

		return pcaX;
	}

	public int getPcaY() {

		return pcaY;
	}

	public int getPcaZ() {

		return pcaZ;
	}

	public boolean isEmpty() {

		return isEmpty;
	}

	public void update(int pcaX, int pcaY, int pcaZ) {

		/*
		 * clear data
		 */
		data.clear();
		isEmpty = true;
		minX = 0;
		minY = 0;
		minZ = 0;
		maxX = 0;
		maxY = 0;
		maxZ = 0;
		/*
		 * set principal component
		 */
		this.pcaX = pcaX;
		this.pcaY = pcaY;
		this.pcaZ = pcaZ;
		IPcaResults results = pcaEditor.getPcaResults();
		if(results != null) {
			List<ISample> samples = results.getSampleList();
			int numeberSelectedSamples = 0;
			for(ISample sample : samples) {
				if(sample.isSelected()) {
					numeberSelectedSamples++;
				}
			}
			if(numeberSelectedSamples > 0) {
				/*
				 *
				 */
				Set<String> groupNames = PcaUtils.getGroupNames(samples, true);
				Map<String, Color> groupNameColore = PcaColorGroup.getColorJavaFx(groupNames);
				Iterator<Entry<String, Color>> it = groupNameColore.entrySet().iterator();
				groups.clear();
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
				isEmpty = false;
				for(ISample sample : samples) {
					if(sample.isSelected()) {
						Color color = groupNameColore.get(sample.getGroupName());
						data.add(new Chart3DSampleData(sample, pcaX, pcaY, pcaZ, color));
					}
				}
				minX = data.stream().min((d1, d2) -> Double.compare(d1.getPcaXData(), d2.getPcaXData())).get().getPcaXData();
				minY = data.stream().min((d1, d2) -> Double.compare(d1.getPcaYData(), d2.getPcaYData())).get().getPcaYData();
				minZ = data.stream().min((d1, d2) -> Double.compare(d1.getPcaZData(), d2.getPcaZData())).get().getPcaZData();
				maxY = data.stream().max((d1, d2) -> Double.compare(d1.getPcaYData(), d2.getPcaYData())).get().getPcaYData();
				maxX = data.stream().max((d1, d2) -> Double.compare(d1.getPcaXData(), d2.getPcaXData())).get().getPcaXData();
				maxZ = data.stream().max((d1, d2) -> Double.compare(d1.getPcaZData(), d2.getPcaZData())).get().getPcaZData();
			}
		}
	}
}
