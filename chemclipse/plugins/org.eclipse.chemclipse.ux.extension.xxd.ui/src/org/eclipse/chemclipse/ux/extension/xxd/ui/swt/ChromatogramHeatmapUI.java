/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapData;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap;
import org.eclipse.nebula.visualization.widgets.datadefinition.ColorMap.PredefinedColorMap;
import org.eclipse.nebula.visualization.widgets.figures.IntensityGraphFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramHeatmapUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private LightweightSystem lightweightSystem;
	private IntensityGraphFigure intensityGraphFigure;
	//
	private ChromatogramHeatmapSupport chromatogramHeatmapSupport = new ChromatogramHeatmapSupport();

	public ChromatogramHeatmapUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void update(IChromatogramSelection<?, ?> chromatogramSelection) {

		clear();
		toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramSelectionLabel(chromatogramSelection));
		//
		if(chromatogramSelection != null) {
			/*
			 * Data Matrix
			 */
			Optional<ChromatogramHeatmapData> heatmapData = chromatogramHeatmapSupport.getHeatmapData(chromatogramSelection.getChromatogram());
			if(heatmapData.isPresent()) {
				boolean isWavelengthData = chromatogramSelection instanceof IChromatogramSelectionWSD;
				setHeatMap(heatmapData.get(), isWavelengthData);
			}
		}
	}

	private void setHeatMap(ChromatogramHeatmapData chromatogramHeatmap, boolean isWavelengthData) {

		/*
		 * Set the range and min/max values.
		 */
		intensityGraphFigure.getXAxis().setRange(chromatogramHeatmap.getAxisRangeWidth());
		intensityGraphFigure.getYAxis().setRange(chromatogramHeatmap.getAxisRangeHeight());
		//
		intensityGraphFigure.setMin(chromatogramHeatmap.getMinimum());
		intensityGraphFigure.setMax(chromatogramHeatmap.getMaximum());
		//
		intensityGraphFigure.setDataWidth(chromatogramHeatmap.getDataWidth());
		intensityGraphFigure.setDataHeight(chromatogramHeatmap.getDataHeight());
		//
		intensityGraphFigure.getXAxis().setTitle("Retention Time [min]");
		intensityGraphFigure.getYAxis().setTitle(isWavelengthData ? "Trace [nm]" : "Trace [m/z]");
		//
		intensityGraphFigure.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
		/*
		 * Set the heatmap data
		 */
		lightweightSystem.setContents(intensityGraphFigure);
		intensityGraphFigure.setDataArray(chromatogramHeatmap.getArrayWrapper());
		intensityGraphFigure.repaint();
	}

	public void clear() {

		float[] heatmapData = new float[0];
		intensityGraphFigure.setDataArray(heatmapData);
		intensityGraphFigure.repaint();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.FILL);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createCanvas(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createColorMapComboViewer(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private ComboViewer createColorMapComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ColorMap) {
					ColorMap colorMap = (ColorMap)element;
					return colorMap.getPredefinedColorMap().name();
				}
				return null;
			}
		});
		//
		combo.setToolTipText("Select a color scheme.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ColorMap) {
					ColorMap colorMap = (ColorMap)object;
					intensityGraphFigure.setColorMap(colorMap);
					intensityGraphFigure.repaint();
				}
			}
		});
		//
		ColorMap[] input = new ColorMap[6];
		input[0] = new ColorMap(PredefinedColorMap.JET, true, true);
		input[1] = new ColorMap(PredefinedColorMap.ColorSpectrum, true, true);
		input[2] = new ColorMap(PredefinedColorMap.Cool, true, true);
		input[3] = new ColorMap(PredefinedColorMap.GrayScale, true, true);
		input[4] = new ColorMap(PredefinedColorMap.Hot, true, true);
		input[5] = new ColorMap(PredefinedColorMap.Shaded, true, true);
		comboViewer.setInput(input);
		combo.select(0);
		//
		return comboViewer;
	}

	private Canvas createCanvas(Composite parent) {

		Canvas canvas = new Canvas(parent, SWT.FILL | SWT.BORDER);
		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas.setBackground(Colors.WHITE);
		//
		lightweightSystem = createLightweightSystem(canvas);
		intensityGraphFigure = createIntensityGraphFigure();
		//
		return canvas;
	}

	private LightweightSystem createLightweightSystem(Canvas canvas) {

		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		lightweightSystem.getRootFigure().setBackgroundColor(Colors.WHITE);
		//
		return lightweightSystem;
	}

	private IntensityGraphFigure createIntensityGraphFigure() {

		IntensityGraphFigure intensityGraphFigure = new IntensityGraphFigure();
		intensityGraphFigure.setForegroundColor(Colors.BLACK);
		intensityGraphFigure.getXAxis().setTitle("Retention Time [min]");
		intensityGraphFigure.getYAxis().setTitle("Trace");
		//
		return intensityGraphFigure;
	}
}
