/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.marker.LabelMarker;
import org.eclipse.swt.SWT;
import org.swtchart.IPlotArea;

public class PeakLabelMarker extends LabelMarker {

	public PeakLabelMarker(BaseChart baseChart, int indexSeries, List<IPeak> peaks) {
		super(baseChart);
		IPlotArea plotArea = (IPlotArea)getBaseChart().getPlotArea();
		LabelMarker labelMarker = new LabelMarker(getBaseChart());
		List<String> labels = new ArrayList<String>();
		labels.add("2-Methoxy-4-vinylphenol");
		labels.add("Ethanone, 1-(2-hydroxy-5-methylphenyl)-");
		labels.add("4-Hydroxy-2-methylacetophenone");
		labels.add("Ethanone, 1-(2-hydroxy-5-methylphenyl)-");
		labels.add("4-Hydroxy-3-methylacetophenone");
		labels.add("3-Methoxyacetophenone");
		labels.add("3-Methyl-4-isopropylphenol");
		labels.add("Phenol, 3,4-dimethoxy-");
		labels.add("2,4-Dimethoxyphenol");
		labels.add("3-Amino-2,6-dimethoxypyridine");
		labelMarker.setLabels(labels, indexSeries, SWT.VERTICAL);
		plotArea.addCustomPaintListener(labelMarker);
	}
}
