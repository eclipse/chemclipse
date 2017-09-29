/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisScaleConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.swtchart.ISeries;
import org.swtchart.LineStyle;

public class ChromatogramOverlayPart extends AbstractMeasurementEditorPartSupport {

	private static final Logger logger = Logger.getLogger(ChromatogramOverlayPart.class);
	@Inject
	private EPartService partService;
	//
	private static final String OVERLAY_TYPE_TIC = "TIC"; // Total Intensity Chromatogram
	private static final String OVERLAY_TYPE_BPC = "BPC"; // Base Peak Chromatogram
	private static final String OVERLAY_TYPE_XIC = "XIC"; // Extracted Ion Chromatogram
	private static final String OVERLAY_TYPE_SIC = "SIC"; // Selected Ion Chromatogram
	private static final String OVERLAY_TYPE_CONCATENATOR = "+";
	private static final String ESCAPE_CONCATENATOR = "\\";
	private static final String SELECTED_IONS_CONCATENATOR = " ";
	private static final String EDITOR_TAB = "_EditorTab#";
	private static final String OVERLAY_START_MARKER = "_(";
	private static final String OVERLAY_STOP_MARKER = ")";
	//
	private static final String SELECTED_SERIES_NONE = "None";
	private static final String SELECTED_IONS_DEFAULT = "18 28 32 84 207";
	private static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	private static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	private static final String SELECTED_IONS_FAME = "FAME";
	private static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	private static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	private ChromatogramChart chromatogramChart;
	private IColorScheme colorScheme;
	private Map<String, Color> usedColors;
	private Map<String, String> selectedIonsMap;
	//
	private String[] overlayTypes;
	private String[] selectedIons;
	private Combo comboOverlayType;
	private Combo comboSelectedSeries;
	private Combo comboSelectedIons;
	private Text textShiftX;
	private Combo comboScaleX;
	private Text textShiftY;
	private Combo comboScaleY;

	public ChromatogramOverlayPart() {
		colorScheme = Colors.getColorScheme(Colors.COLOR_SCHEME_PUBLICATION);
		usedColors = new HashMap<String, Color>();
		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC //
		};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED //
		};
		//
		selectedIonsMap = new HashMap<String, String>();
		selectedIonsMap.put(SELECTED_IONS_HYDROCARBONS, "57 71 85");
		selectedIonsMap.put(SELECTED_IONS_FATTY_ACIDS, "74 84");
		selectedIonsMap.put(SELECTED_IONS_FAME, "79 81");
		selectedIonsMap.put(SELECTED_IONS_SOLVENT_TAILING, "84");
		selectedIonsMap.put(SELECTED_IONS_COLUMN_BLEED, "207");
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		createButtonsToolbar(parent);
		createChromatogramChart(parent);
	}

	private void createButtonsToolbar(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(2, false));
		//
		Composite compositeLeft = new Composite(composite, SWT.NONE);
		GridData gridDataLeft = new GridData(GridData.FILL_HORIZONTAL);
		gridDataLeft.horizontalAlignment = SWT.BEGINNING;
		gridDataLeft.grabExcessHorizontalSpace = true;
		compositeLeft.setLayoutData(gridDataLeft);
		compositeLeft.setLayout(new GridLayout(11, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataRight);
		compositeRight.setLayout(new GridLayout(2, false));
		//
		createDisplayTypeCombo(compositeLeft);
		createHighlightSeriesCombo(compositeLeft);
		createTextSelectedIons(compositeLeft);
		createTextShiftX(compositeLeft);
		createComboScaleX(compositeLeft);
		createButtonLeft(compositeLeft);
		createButtonRight(compositeLeft);
		createTextShiftY(compositeLeft);
		createComboScaleY(compositeLeft);
		createButtonUp(compositeLeft);
		createButtonDown(compositeLeft);
		//
		createResetButton(compositeRight);
		createSettingsButton(compositeRight);
	}

	private void createDisplayTypeCombo(Composite parent) {

		comboOverlayType = new Combo(parent, SWT.READ_ONLY);
		comboOverlayType.setToolTipText("Select the overlay type");
		comboOverlayType.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboOverlayType.setLayoutData(gridData);
		comboOverlayType.setItems(overlayTypes);
		comboOverlayType.select(0);
		comboOverlayType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshUpdateOverlayChart();
			}
		});
	}

	private void createHighlightSeriesCombo(Composite parent) {

		comboSelectedSeries = new Combo(parent, SWT.READ_ONLY);
		comboSelectedSeries.setToolTipText("Highlight the selected series");
		comboSelectedSeries.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 350;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedSeries.setLayoutData(gridData);
		comboSelectedSeries.setItems(new String[]{SELECTED_SERIES_NONE});
		comboSelectedSeries.select(0);
		comboSelectedSeries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(comboSelectedSeries.getText().trim());
				baseChart.redraw();
			}
		});
	}

	private void createTextSelectedIons(Composite parent) {

		comboSelectedIons = new Combo(parent, SWT.NONE);
		comboSelectedIons.setToolTipText("Select the overlay ions.");
		comboSelectedIons.setItems(selectedIons);
		comboSelectedIons.setText(SELECTED_IONS_DEFAULT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 350;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedIons.setLayoutData(gridData);
		comboSelectedIons.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				refreshUpdateOverlayChart();
			}
		});
	}

	private void createTextShiftX(Composite parent) {

		textShiftX = new Text(parent, SWT.BORDER);
		textShiftX.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftX.setLayoutData(gridData);
	}

	private void createComboScaleX(Composite parent) {

		comboScaleX = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleX.setLayoutData(gridData);
	}

	private void createButtonLeft(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Left");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShift(IExtendedChart.X_AXIS) * -1.0d;
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
			}
		});
	}

	private void createButtonRight(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Right");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftX = getShift(IExtendedChart.X_AXIS);
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, shiftX, 0.0d);
				baseChart.redraw();
			}
		});
	}

	private void createTextShiftY(Composite parent) {

		textShiftY = new Text(parent, SWT.BORDER);
		textShiftY.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		textShiftY.setLayoutData(gridData);
	}

	private void createComboScaleY(Composite parent) {

		comboScaleY = new Combo(parent, SWT.READ_ONLY);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		comboScaleY.setLayoutData(gridData);
	}

	private void createButtonUp(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Up");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShift(IExtendedChart.Y_AXIS);
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
			}
		});
	}

	private void createButtonDown(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Move Down");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				double shiftY = getShift(IExtendedChart.Y_AXIS) * -1.0d;
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				baseChart.shiftSeries(selectedSeriesId, 0.0d, shiftY);
				baseChart.redraw();
			}
		});
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the Overlay");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				colorScheme.reset();
				usedColors.clear();
				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePage = new PreferencePage();
				preferencePage.setTitle("Overlay Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applyOverlaySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void applyOverlaySettings() {

		comboOverlayType.select(0);
		comboSelectedSeries.select(0);
	}

	private void createChromatogramChart(Composite parent) {

		chromatogramChart = new ChromatogramChart(parent, SWT.NONE);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setSupportDataShift(true);
		chromatogramChart.applySettings(chartSettings);
		//
		setComboAxisItems();
	}

	@Focus
	public void setFocus() {

		refreshUpdateOverlayChart();
	}

	private void refreshUpdateOverlayChart() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections(partService);
		Set<String> availableSeriesIds = new HashSet<String>();
		BaseChart baseChart = chromatogramChart.getBaseChart();
		List<Integer> ions = getSelectedIons();
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		for(int i = 0; i < chromatogramSelections.size(); i++) {
			IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String chromatogramName = chromatogram.getName() + EDITOR_TAB + (i + 1);
			/*
			 * Select which series shall be displayed.
			 */
			String[] overlayTypes = comboOverlayType.getText().trim().split(ESCAPE_CONCATENATOR + OVERLAY_TYPE_CONCATENATOR);
			for(String overlayType : overlayTypes) {
				Color color = getSeriesColor(chromatogramName);
				if(overlayType.equals(OVERLAY_TYPE_SIC)) {
					/*
					 * SIC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						for(int ion : ions) {
							String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + "-" + ion + OVERLAY_STOP_MARKER;
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								List<Integer> sic = new ArrayList<Integer>();
								sic.add(ion);
								lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, sic));
							}
						}
					}
				} else if(overlayType.equals(OVERLAY_TYPE_BPC) || overlayType.equals(OVERLAY_TYPE_XIC)) {
					/*
					 * BPC, XIC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + OVERLAY_STOP_MARKER;
						availableSeriesIds.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, ions));
						}
					}
				} else {
					/*
					 * TIC
					 */
					String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + OVERLAY_STOP_MARKER;
					availableSeriesIds.add(seriesId);
					if(!baseChart.isSeriesContained(seriesId)) {
						lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, color, ions));
					}
				}
			}
		}
		/*
		 * Add the selected series
		 */
		chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
		/*
		 * Delete non-available series.
		 */
		for(ISeries series : baseChart.getSeriesSet().getSeries()) {
			String seriesId = series.getId();
			if(!availableSeriesIds.contains(seriesId)) {
				baseChart.deleteSeries(seriesId);
			}
		}
		/*
		 * Reset the selected series selection.
		 */
		baseChart.resetSeriesSettings();
		String[] items = new String[availableSeriesIds.size() + 1];
		items[0] = SELECTED_SERIES_NONE;
		int index = 1;
		for(String seriesId : availableSeriesIds) {
			items[index++] = seriesId;
		}
		comboSelectedSeries.setItems(items);
		comboSelectedSeries.setText(SELECTED_SERIES_NONE);
	}

	private List<Integer> getSelectedIons() {

		DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();
		List<Integer> selectedIons = new ArrayList<Integer>();
		//
		String comboText = comboSelectedIons.getText().trim();
		String ionsText = "";
		if(selectedIonsMap.containsKey(comboSelectedIons.getText().trim())) {
			ionsText = selectedIonsMap.get(comboText);
		} else {
			ionsText = comboText;
		}
		//
		String[] ions = ionsText.split(SELECTED_IONS_CONCATENATOR);
		for(String ion : ions) {
			try {
				selectedIons.add(AbstractIon.getIon(decimalFormat.parse(ion).doubleValue()));
			} catch(ParseException e) {
				logger.warn(e);
			}
		}
		//
		return selectedIons;
	}

	private Color getSeriesColor(String chromatogramName) {

		Color color = usedColors.get(chromatogramName);
		if(color == null) {
			color = colorScheme.getColor();
			colorScheme.incrementColor();
			usedColors.put(chromatogramName, color);
		}
		return color;
	}

	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, String overlayType, Color color, List<Integer> ions) {

		double[] xSeries = new double[chromatogram.getNumberOfScans()];
		double[] ySeries = new double[chromatogram.getNumberOfScans()];
		LineStyle lineStyle = getLineStyle(overlayType);
		/*
		 * Get the data.
		 */
		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			/*
			 * Get the retention time and intensity.
			 */
			xSeries[index] = scan.getRetentionTime();
			ySeries[index] = getIntensity(scan, overlayType, ions);
			index++;
		}
		/*
		 * Add the series.
		 */
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setLineColor(color);
		lineSerieSettings.setLineStyle(lineStyle);
		lineSerieSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSerieSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	private double getIntensity(IScan scan, String overlayType, List<Integer> ions) {

		double intensity = 0.0d;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			/*
			 * TIC
			 */
			intensity = scan.getTotalSignal();
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			/*
			 * BPC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					intensity = ion.getAbundance();
				}
			}
		} else if(overlayType.equals(OVERLAY_TYPE_XIC) || overlayType.equals(OVERLAY_TYPE_SIC)) {
			/*
			 * XIC, SIC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				for(int ion : ions) {
					intensity += extractedIonSignal.getAbundance(ion);
				}
			}
		}
		//
		return intensity;
	}

	private LineStyle getLineStyle(String overlayType) {

		LineStyle lineStyle;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			lineStyle = LineStyle.SOLID;
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			lineStyle = LineStyle.DASH;
		} else if(overlayType.equals(OVERLAY_TYPE_XIC)) {
			lineStyle = LineStyle.DASHDOT;
		} else if(overlayType.equals(OVERLAY_TYPE_SIC)) {
			lineStyle = LineStyle.DASHDOTDOT;
		} else {
			lineStyle = LineStyle.DOT;
		}
		return lineStyle;
	}

	public void setComboAxisItems() {

		/*
		 * X Axes
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		String[] axisLabelsX = baseChart.getAxisLabels(IExtendedChart.X_AXIS);
		comboScaleX.setItems(axisLabelsX);
		if(axisLabelsX.length > 0) {
			int selectedIndex = 1; // "Minutes"
			if(selectedIndex >= 0 && selectedIndex < axisLabelsX.length) {
				comboScaleX.select(selectedIndex);
				textShiftX.setText("0.5");
			} else {
				comboScaleX.select(0); // Milliseconds
				textShiftX.setText("10000");
			}
		}
		/*
		 * Y Axes
		 */
		String[] axisLabelsY = baseChart.getAxisLabels(IExtendedChart.Y_AXIS);
		comboScaleY.setItems(axisLabelsY);
		if(axisLabelsY.length > 0) {
			int selectedIndex = 1; // "Relative Intensity [%]"
			if(selectedIndex >= 0 && selectedIndex < axisLabelsY.length) {
				comboScaleY.select(selectedIndex);
				textShiftY.setText("1.2");
			} else {
				comboScaleY.select(0); // Intensity
				textShiftY.setText("100000");
			}
		}
	}

	private double getShift(String axis) {

		double shiftValue = 0.0d;
		try {
			/*
			 * Try to calculate the primary unit.
			 */
			BaseChart baseChart = chromatogramChart.getBaseChart();
			DecimalFormat decimalFormat;
			int selectedAxis;
			//
			if(axis.equals(IExtendedChart.X_AXIS)) {
				selectedAxis = comboScaleX.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.X_AXIS, selectedAxis);
			} else {
				selectedAxis = comboScaleY.getSelectionIndex();
				decimalFormat = baseChart.getDecimalFormat(IExtendedChart.Y_AXIS, selectedAxis);
			}
			//
			double secondaryValue;
			if(axis.equals(IExtendedChart.X_AXIS)) {
				secondaryValue = decimalFormat.parse(textShiftX.getText().trim()).doubleValue();
			} else {
				secondaryValue = decimalFormat.parse(textShiftY.getText().trim()).doubleValue();
			}
			/*
			 * Convert the range on demand.
			 */
			if(selectedAxis == 0) {
				shiftValue = secondaryValue;
			} else {
				IAxisScaleConverter axisScaleConverter = baseChart.getAxisScaleConverter(axis, selectedAxis);
				shiftValue = axisScaleConverter.convertToPrimaryUnit(secondaryValue);
			}
		} catch(ParseException e) {
			System.out.println(e);
		}
		//
		return shiftValue;
	}
}
