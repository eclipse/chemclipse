/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.xic.ExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignalExtractor;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.exceptions.NoExtractedWavelengthSignalStoredException;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignalExtractor;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.nebula.visualization.xygraph.linearscale.Range;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class ChromatogramHeatmapUI extends Composite {

	private static final Logger logger = Logger.getLogger(ChromatogramHeatmapUI.class);
	private LightweightSystem lightweightSystem;
	private IntensityGraphFigure intensityGraphFigure;
	private Label heatmapTitel;
	private Composite header;

	public ChromatogramHeatmapUI(Composite parent, int style) {
		super(parent, style);
		initialize(parent);
	}

	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			update((IChromatogramSelectionMSD)chromatogramSelection);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			update((IChromatogramSelectionWSD)chromatogramSelection);
		} else {
			clear();
		}
	}

	private void update(IChromatogramSelectionMSD chromatogramSelection) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogram();
		IExtractedIonSignalExtractor extractedIonSignalExtractor;
		try {
			extractedIonSignalExtractor = new ExtractedIonSignalExtractor(chromatogram);
			IExtractedIonSignals extractedIonSignals = extractedIonSignalExtractor.getExtractedIonSignals(chromatogramSelection);
			//
			heatmapTitel.setText("Run: " + chromatogram.getName());
			//
			int startScan = extractedIonSignals.getStartScan();
			int stopScan = extractedIonSignals.getStopScan();
			//
			int startIon = extractedIonSignals.getStartIon();
			int stopIon = extractedIonSignals.getStopIon();
			//
			int dataHeight = stopScan - startScan + 1; // y -> scans
			int dataWidth = stopIon - startIon + 1; // x -> m/z values
			/*
			 * The data height and width must be >= 1!
			 */
			if(dataHeight < 1 || dataWidth < 1) {
				return;
			}
			/*
			 * Parse the heatmap data
			 */
			float maxIntensity = 0;
			float[] heatmapData = new float[dataWidth * dataHeight * 2];
			/*
			 * Y-Axis: Scans
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				int xScan = scan - startScan; // xScan as zero based heatmap scan array index
				IExtractedIonSignal extractedIonSignal;
				try {
					extractedIonSignal = extractedIonSignals.getExtractedIonSignal(scan);
					for(int ion = startIon; ion <= stopIon; ion++) {
						/*
						 * X-Axis: m/z values
						 */
						int xIon = ion - startIon; // xIon as zero based heatmap ion array index
						float abundance = extractedIonSignal.getAbundance(ion);
						/*
						 * Max Intensity
						 */
						if(abundance > maxIntensity) {
							maxIntensity = abundance;
						}
						/*
						 * XY data
						 */
						heatmapData[xScan * dataWidth + xIon] = abundance;
					}
				} catch(NoExtractedIonSignalStoredException e) {
					logger.warn(e);
				}
			}
			/*
			 * Set the range and min/max values.
			 */
			intensityGraphFigure.getXAxis().setRange(new Range(startIon, stopIon));
			intensityGraphFigure.getYAxis().setRange(new Range(stopScan, startScan));
			//
			intensityGraphFigure.setMin(0);
			intensityGraphFigure.setMax(maxIntensity / (dataHeight / 5.0d)); // Important when zooming in.
			//
			intensityGraphFigure.setDataHeight(dataHeight);
			intensityGraphFigure.setDataWidth(dataWidth);
			//
			intensityGraphFigure.getXAxis().setTitle("m/z");
			//
			intensityGraphFigure.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
			/*
			 * Set the heatmap data
			 */
			lightweightSystem.setContents(intensityGraphFigure);
			intensityGraphFigure.setDataArray(heatmapData);
		} catch(ChromatogramIsNullException e1) {
			logger.warn(e1);
		}
	}

	private void update(IChromatogramSelectionWSD chromatogramSelection) {

		IChromatogramWSD chromatogram = chromatogramSelection.getChromatogram();
		IExtractedWavelengthSignalExtractor extractedWavelengthSignalExtractor;
		try {
			extractedWavelengthSignalExtractor = new ExtractedWavelengthSignalExtractor(chromatogram);
			IExtractedWavelengthSignals extractedWavelengthSignals = extractedWavelengthSignalExtractor.getExtractedWavelengthSignals(chromatogramSelection);
			//
			heatmapTitel.setText("Run: " + chromatogram.getName());
			//
			int startScan = extractedWavelengthSignals.getStartScan();
			int stopScan = extractedWavelengthSignals.getStopScan();
			//
			int startSignal = extractedWavelengthSignals.getStartWavelength();
			int stopSignal = extractedWavelengthSignals.getStopWavelength();
			//
			int dataHeight = stopScan - startScan + 1; // y -> scans
			int dataWidth = stopSignal - startSignal + 1; // x -> m/z values
			/*
			 * The data height and width must be >= 1!
			 */
			if(dataHeight < 1 || dataWidth < 1) {
				return;
			}
			/*
			 * Parse the heatmap data
			 */
			float maxIntensity = 0;
			float[] heatmapData = new float[dataWidth * dataHeight * 2];
			/*
			 * Y-Axis: Scans
			 */
			for(int scan = startScan; scan <= stopScan; scan++) {
				int xScan = scan - startScan; // xScan as zero based heatmap scan array index
				IExtractedWavelengthSignal extractedWavelengthSignal;
				try {
					extractedWavelengthSignal = extractedWavelengthSignals.getExtractedWavelengthSignal(scan);
					for(int signal = startSignal; signal <= stopSignal; signal++) {
						/*
						 * X-Axis: m/z values
						 */
						int xIon = signal - startSignal; // xIon as zero based heatmap ion array index
						float abundance = extractedWavelengthSignal.getAbundance(signal);
						/*
						 * Max Intensity
						 */
						if(abundance > maxIntensity) {
							maxIntensity = abundance;
						}
						/*
						 * XY data
						 */
						heatmapData[xScan * dataWidth + xIon] = abundance;
					}
				} catch(NoExtractedWavelengthSignalStoredException e) {
					logger.warn(e);
				}
			}
			/*
			 * Set the range and min/max values.
			 */
			intensityGraphFigure.getXAxis().setRange(new Range(startSignal, stopSignal));
			intensityGraphFigure.getYAxis().setRange(new Range(stopScan, startScan));
			//
			intensityGraphFigure.setMin(0);
			intensityGraphFigure.setMax(maxIntensity / (dataHeight / 5.0d)); // Important when zooming in.
			//
			intensityGraphFigure.setDataHeight(dataHeight);
			intensityGraphFigure.setDataWidth(dataWidth);
			//
			intensityGraphFigure.getXAxis().setTitle("wavelength");
			//
			intensityGraphFigure.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
			/*
			 * Set the heatmap data
			 */
			lightweightSystem.setContents(intensityGraphFigure);
			intensityGraphFigure.setDataArray(heatmapData);
		} catch(ChromatogramIsNullException e1) {
			logger.warn(e1);
		}
	}

	public void clear() {

		float[] heatmapData = new float[0];
		intensityGraphFigure.setDataArray(heatmapData);
	}

	private void initialize(Composite parent) {

		GridData layoutData;
		Display display = Display.getCurrent();
		setLayout(new FillLayout());
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		/*
		 * Header
		 */
		layoutData = new GridData(GridData.FILL_HORIZONTAL);
		header = new Composite(composite, SWT.NONE);
		header.setLayout(new GridLayout(1, true));
		header.setLayoutData(layoutData);
		//
		layoutData = new GridData(GridData.FILL_BOTH);
		layoutData.grabExcessHorizontalSpace = true;
		heatmapTitel = new Label(header, SWT.CENTER);
		heatmapTitel.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		heatmapTitel.setLayoutData(layoutData);
		Font font = new Font(display, "Arial", 12, SWT.BOLD);
		heatmapTitel.setFont(font);
		heatmapTitel.setText("");
		font.dispose();
		/*
		 * Heatmap
		 */
		layoutData = new GridData(GridData.FILL_BOTH);
		Canvas canvas = new Canvas(composite, SWT.FILL | SWT.BORDER);
		canvas.setLayoutData(layoutData);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		//
		lightweightSystem = new LightweightSystem(canvas);
		lightweightSystem.getRootFigure().setBackgroundColor(display.getSystemColor(SWT.COLOR_WHITE));
		//
		intensityGraphFigure = new IntensityGraphFigure();
		intensityGraphFigure.setForegroundColor(display.getSystemColor(SWT.COLOR_BLACK));
		intensityGraphFigure.getXAxis().setTitle("signal");
		intensityGraphFigure.getYAxis().setTitle("scan");
	}
}
