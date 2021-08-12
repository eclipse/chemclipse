/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Matthias Mailänder - add a wavelength selection mode
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapData;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramHeatmapSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IntensityScaleUI.IScaleUpdateListener;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Display;

public class ChromatogramHeatmapUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarEdit;
	private AtomicReference<Composite> toolbarEdit = new AtomicReference<>();
	private IntensityScaleUI intensityScaleMin;
	private IntensityScaleUI intensityScaleMax;
	private LightweightSystem lightweightSystem;
	private IntensityGraphFigure intensityGraphFigure;
	//
	private ChromatogramHeatmapSupport chromatogramHeatmapSupport = new ChromatogramHeatmapSupport();
	private IChromatogramSelection<?, ?> chromatogramSelection = null;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	public ChromatogramHeatmapUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	class WavelengthSelector extends MouseListener.Stub implements MouseListener {

		@Override
		public void mousePressed(MouseEvent mouseEvent) {

			IChromatogramSelectionWSD chromatogramSelectionWSD = (IChromatogramSelectionWSD)chromatogramSelection;
			if(mouseEvent.button == 1) {
				double clickedWavelength = intensityGraphFigure.getYAxis().getPositionValue(mouseEvent.y, false);
				IMarkedWavelengths markedWavelengths = new MarkedWavelengths();
				markedWavelengths.add(clickedWavelength);
				chromatogramSelectionWSD.setSelectedWavelengths(markedWavelengths);
				chromatogramSelectionWSD.fireUpdateChange(true);
				toolbarInfo.get().setText("Selected wavelength: " + Math.round(clickedWavelength) + " nm");
			} else if(mouseEvent.button == 3) {
				chromatogramSelectionWSD.populateWavelengths(chromatogramSelectionWSD.getChromatogram());
				chromatogramSelectionWSD.fireUpdateChange(true);
				toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramSelectionLabel(chromatogramSelection));
			}
			mouseEvent.consume();
		}
	}

	public void update(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(this.chromatogramSelection == chromatogramSelection) {
			return;
		}
		this.chromatogramSelection = chromatogramSelection;
		setScaleValues();
		updateHeatmap();
	}

	private void updateHeatmap() {

		if(chromatogramSelection != null) {
			/*
			 * Data Matrix
			 */
			int scaleMin = intensityScaleMin.getSelection();
			int scaleMax = intensityScaleMax.getSelection();
			Optional<ChromatogramHeatmapData> heatmapData = chromatogramHeatmapSupport.getHeatmapData(chromatogramSelection.getChromatogram(), scaleMin, scaleMax);
			if(heatmapData.isPresent()) {
				saveScaleValues(scaleMin, scaleMax);
				toolbarInfo.get().setText(ChromatogramDataSupport.getChromatogramSelectionLabel(chromatogramSelection));
				boolean isWavelengthData = chromatogramSelection instanceof IChromatogramSelectionWSD;
				intensityGraphFigure = createIntensityGraphFigure(!isWavelengthData);
				setHeatMap(heatmapData.get(), isWavelengthData);
				if(isWavelengthData) {
					WavelengthSelector wavelengthSelector = new WavelengthSelector();
					intensityGraphFigure.addMouseListener(wavelengthSelector);
				}
			} else {
				clear();
			}
		} else {
			clear();
		}
	}

	private void setScaleValues() {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			intensityScaleMin.setSelection(preferenceStore.getInt(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MIN_MSD));
			intensityScaleMax.setSelection(preferenceStore.getInt(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MAX_MSD));
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			intensityScaleMin.setSelection(preferenceStore.getInt(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MIN_WSD));
			intensityScaleMax.setSelection(preferenceStore.getInt(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MAX_WSD));
		}
	}

	private void resetScaleValues() {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			intensityScaleMin.setSelection(PreferenceConstants.DEF_HEATMAP_SCALE_INTENSITY_MIN_MSD);
			intensityScaleMax.setSelection(PreferenceConstants.DEF_HEATMAP_SCALE_INTENSITY_MAX_MSD);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			intensityScaleMin.setSelection(PreferenceConstants.DEF_HEATMAP_SCALE_INTENSITY_MIN_WSD);
			intensityScaleMax.setSelection(PreferenceConstants.DEF_HEATMAP_SCALE_INTENSITY_MAX_WSD);
		}
	}

	private void saveScaleValues(int scaleMin, int scaleMax) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			preferenceStore.setValue(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MIN_MSD, scaleMin);
			preferenceStore.setValue(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MAX_MSD, scaleMax);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			preferenceStore.setValue(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MIN_WSD, scaleMin);
			preferenceStore.setValue(PreferenceConstants.P_HEATMAP_SCALE_INTENSITY_MAX_WSD, scaleMax);
		}
	}

	private void setHeatMap(ChromatogramHeatmapData chromatogramHeatmapData, boolean isWavelengthData) {

		try {
			/*
			 * First clear the graph figure.
			 * If a previous data width or height is lower than the newer data, it could crash.
			 */
			clear();
			/*
			 * Set the range and min/max values.
			 */
			intensityGraphFigure.getXAxis().setRange(chromatogramHeatmapData.getAxisRangeWidth());
			intensityGraphFigure.getYAxis().setRange(chromatogramHeatmapData.getAxisRangeHeight());
			//
			intensityGraphFigure.setMin(chromatogramHeatmapData.getMinimum());
			intensityGraphFigure.setMax(chromatogramHeatmapData.getMaximum());
			//
			intensityGraphFigure.setDataWidth(chromatogramHeatmapData.getDataWidth());
			intensityGraphFigure.setDataHeight(chromatogramHeatmapData.getDataHeight());
			//
			intensityGraphFigure.getXAxis().setTitle("Retention Time [min]");
			intensityGraphFigure.getYAxis().setTitle(isWavelengthData ? "Wavelength [nm]" : "Trace [m/z]");
			//
			intensityGraphFigure.setColorMap(new ColorMap(PredefinedColorMap.JET, true, true));
			/*
			 * Set the heatmap data
			 */
			lightweightSystem.setContents(intensityGraphFigure);
			intensityGraphFigure.setDataArray(chromatogramHeatmapData.getArrayWrapper());
			intensityGraphFigure.repaint();
		} catch(Exception e) {
			clear();
		}
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
		createToolbarMain(composite);
		createToolbarInfo(composite);
		createToolbarEdit(composite);
		createCanvas(composite);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarEdit, buttonToolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		buttonToolbarEdit = createButtonToggleToolbar(composite, toolbarEdit, IMAGE_EDIT, TOOLTIP_EDIT);
		createColorMapComboViewer(composite);
		createButtonReset(composite);
		createButtonSettings(composite);
	}

	private Button createButtonReset(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Reset the heatmap settings.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				resetScaleValues();
				updateHeatmap();
			}
		});
		//
		return button;
	}

	private void createButtonSettings(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageChromatogram.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				setScaleValues();
				updateHeatmap();
			}
		});
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, true));
		//
		intensityScaleMin = createIntensityScale(composite, "Min Scale Intensity");
		intensityScaleMax = createIntensityScale(composite, "Max Scale Intensity");
		//
		toolbarEdit.set(composite);
	}

	private IntensityScaleUI createIntensityScale(Composite parent, String tooltip) {

		IntensityScaleUI intensityScaleUI = new IntensityScaleUI(parent, SWT.NONE);
		intensityScaleUI.setMinimum(PreferenceConstants.MIN_HEATMAP_SCALE_INTENSITY);
		intensityScaleUI.setMaximum(PreferenceConstants.MAX_HEATMAP_SCALE_INTENSITY);
		intensityScaleUI.setToolTipText(tooltip);
		intensityScaleUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		intensityScaleUI.setUpdateListener(new IScaleUpdateListener() {

			@Override
			public void update(int selection) {

				updateHeatmap();
			}
		});
		//
		return intensityScaleUI;
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
		//
		return canvas;
	}

	private LightweightSystem createLightweightSystem(Canvas canvas) {

		LightweightSystem lightweightSystem = new LightweightSystem(canvas);
		lightweightSystem.getRootFigure().setBackgroundColor(Colors.WHITE);
		//
		return lightweightSystem;
	}

	private IntensityGraphFigure createIntensityGraphFigure(boolean zoom) {

		IntensityGraphFigure intensityGraphFigure = new IntensityGraphFigure(zoom);
		intensityGraphFigure.setForegroundColor(Colors.BLACK);
		intensityGraphFigure.getXAxis().setTitle("Retention Time [min]");
		intensityGraphFigure.getYAxis().setTitle("Trace");
		//
		return intensityGraphFigure;
	}
}
