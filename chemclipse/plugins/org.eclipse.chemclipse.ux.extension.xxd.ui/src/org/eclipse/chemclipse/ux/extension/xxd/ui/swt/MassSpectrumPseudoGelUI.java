/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class MassSpectrumPseudoGelUI extends Composite implements IExtendedPartUI {

	private LightweightSystem lightweightSystem;
	private IntensityGraphFigure intensityGraphFigure;
	//
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();
	private List<IScanMSD> scanSelections = new ArrayList<>();

	public MassSpectrumPseudoGelUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public void update() {

		scanSelections = editorUpdateSupport.getMassSpectrumSelections();
		updatePseudoGel();
	}

	private void updatePseudoGel() {

		if(scanSelections != null) {
			intensityGraphFigure = createIntensityGraphFigure(false);
			setPseudoGel(scanSelections);
		} else {
			clear();
		}
	}

	private void setPseudoGel(List<IScanMSD> scanList) {

		clear();
		//
		int dataWidth = 0;
		int dataHeight = scanList.size();
		double lowestIon = Double.MAX_VALUE;
		double highestIon = 0;
		for(IScanMSD scan : scanList) {
			if(dataWidth < scan.getNumberOfIons()) {
				dataWidth = scan.getNumberOfIons();
			}
			if(lowestIon > scan.getLowestIon().getIon()) {
				lowestIon = scan.getLowestIon().getIon();
			}
			if(highestIon < scan.getHighestIon().getIon()) {
				highestIon = scan.getHighestIon().getIon();
			}
		}
		float[] data = new float[dataWidth * dataHeight];
		double highestAbundance = 0;
		int i = 0;
		int j = 0;
		for(IScanMSD scan : scanList) {
			i++;
			if(highestAbundance < scan.getBasePeak()) {
				highestAbundance = scan.getBasePeak();
			}
			for(IIon ion : scan.getIons()) {
				data[j] = ion.getAbundance();
				j++;
			}
			j = i * dataWidth; // align
		}
		//
		intensityGraphFigure.getXAxis().setRange(new Range(lowestIon, highestIon));
		intensityGraphFigure.getYAxis().setRange(new Range(0, dataHeight));
		//
		intensityGraphFigure.setMin(0);
		intensityGraphFigure.setMax(highestAbundance);
		//
		intensityGraphFigure.setDataWidth(dataWidth);
		intensityGraphFigure.setDataHeight(dataHeight);
		//
		intensityGraphFigure.getXAxis().setTitle("m/z");
		intensityGraphFigure.getYAxis().setTitle("");
		//
		ColorMap reversedGrayScale = new ColorMap();
		reversedGrayScale.setColorMap(getReversedGrayScaleMap());
		intensityGraphFigure.setColorMap(reversedGrayScale);
		lightweightSystem.setContents(intensityGraphFigure);
		intensityGraphFigure.setDataArray(data);
		intensityGraphFigure.repaint();
	}

	private LinkedHashMap<Double, RGB> getReversedGrayScaleMap() {

		double[] values = new double[]{0, 1};
		RGB[] colors = new RGB[]{new RGB(255, 255, 255), new RGB(0, 0, 0)};
		LinkedHashMap<Double, RGB> map = new LinkedHashMap<>();
		for(int i = 0; i < values.length; i++) {
			map.put(values[i], colors[i]);
		}
		return map;
	}

	public void clear() {

		float[] heatmapData = new float[0];
		intensityGraphFigure.setMin(0);
		intensityGraphFigure.setMax(0);
		intensityGraphFigure.setDataWidth(0);
		intensityGraphFigure.setDataHeight(0);
		intensityGraphFigure.setDataArray(heatmapData);
		lightweightSystem.setContents(intensityGraphFigure);
		intensityGraphFigure.repaint();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		//
		createCanvas(composite);
	}

	private Canvas createCanvas(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.FILL | SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		lightweightSystem = createLightweightSystem(canvas);
		//
		return canvas;
	}

	private LightweightSystem createLightweightSystem(Canvas canvas) {

		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		lightweightSystem.getRootFigure().setBackgroundColor(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		//
		return lightweightSystem;
	}

	private IntensityGraphFigure createIntensityGraphFigure(boolean zoom) {

		IntensityGraphFigure intensityGraphFigure = new IntensityGraphFigure(zoom);
		intensityGraphFigure.getXAxis().setTitle("Retention Time [min]");
		intensityGraphFigure.getYAxis().setTitle("Trace");
		//
		return intensityGraphFigure;
	}
}
