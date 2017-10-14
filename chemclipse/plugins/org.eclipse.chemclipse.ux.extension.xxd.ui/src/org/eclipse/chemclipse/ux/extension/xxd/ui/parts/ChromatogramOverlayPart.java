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
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePage;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.BaseChart;
import org.eclipse.eavp.service.swtchart.core.IAxisScaleConverter;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.IExtendedChart;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.ISeriesModificationListener;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesStatusAdapter;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
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
	private static final String OVERLAY_TYPE_TSC = "TSC"; // Total Substracted Chromatogram
	// private static final String OVERLAY_TYPE_SRM = "SRM"; // Single Reaction Monitoring
	// private static final String OVERLAY_TYPE_MRM = "MRM"; // Single Reaction Monitoring
	//
	private static final String DERIVATIVE_NONE = "--";
	private static final String DERIVATIVE_FIRST = "1st";
	private static final String DERIVATIVE_SECOND = "2nd";
	private static final String DERIVATIVE_THIRD = "3rd";
	//
	private static final String OVERLAY_TYPE_CONCATENATOR = "+";
	private static final String ESCAPE_CONCATENATOR = "\\";
	private static final String SELECTED_IONS_CONCATENATOR = " ";
	private static final String EDITOR_TAB = "_EditorTab#";
	private static final String OVERLAY_START_MARKER = "_(";
	private static final String OVERLAY_STOP_MARKER = ")";
	private static final String DELIMITER_ION_DERIVATIVE = ",";
	//
	private static final String SELECTED_IONS_DEFAULT = "18 28 32 84 207";
	private static final String SELECTED_IONS_HYDROCARBONS = "Hydrocarbons";
	private static final String SELECTED_IONS_FATTY_ACIDS = "Fatty Acids";
	private static final String SELECTED_IONS_FAME = "FAME";
	private static final String SELECTED_IONS_SOLVENT_TAILING = "Solvent Tailing";
	private static final String SELECTED_IONS_COLUMN_BLEED = "Column Bleed";
	//
	private static final String DISPLAY_MODUS_NORMAL = "Normal";
	private static final String DISPLAY_MODUS_MIRRORED = "Mirrored";
	//
	private IColorScheme colorSchemeNormal;
	private Map<String, Color> usedColorsNormal;
	private IColorScheme colorSchemeSIC;
	private Map<String, Color> usedColorsSIC;
	//
	private Map<String, String> selectedIonsMap;
	private Set<String> mirroredSeries;
	//
	private ChromatogramChart chromatogramChart;
	//
	private Composite compositeToolbar;
	private Composite compositeType;
	private Composite compositeSelectedIons;
	private Composite compositeShift;
	private Composite compositeStatus;
	//
	private String[] overlayTypes;
	private String[] derivativeTypes;
	private String[] selectedIons;
	private String[] displayModi;
	private Combo comboOverlayType;
	private Combo comboDerivativeType;
	private Combo comboSelectedSeries;
	private Combo comboDisplayModus;
	private Combo comboSelectedIons;
	private Text textShiftX;
	private Combo comboScaleX;
	private Button buttonShiftLeft;
	private Button buttonShiftRight;
	private Text textShiftY;
	private Combo comboScaleY;
	private Button buttonShiftUp;
	private Button buttonShiftDown;
	private Label labelDataStatus;
	//
	private LineStyle lineStyleTIC;
	private LineStyle lineStyleBPC;
	private LineStyle lineStyleXIC;
	private LineStyle lineStyleSIC;
	private LineStyle lineStyleTSC;
	private LineStyle lineStyleDefault;

	public ChromatogramOverlayPart() {
		/*
		 * Colors
		 */
		usedColorsNormal = new HashMap<String, Color>();
		usedColorsSIC = new HashMap<String, Color>();
		applyUserSettings();
		resetColorMaps();
		//
		overlayTypes = new String[]{//
				OVERLAY_TYPE_TIC, //
				OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TSC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_BPC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_XIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_SIC, //
				OVERLAY_TYPE_TIC + OVERLAY_TYPE_CONCATENATOR + OVERLAY_TYPE_TSC};
		//
		derivativeTypes = new String[]{//
				DERIVATIVE_NONE, //
				DERIVATIVE_FIRST, //
				DERIVATIVE_SECOND, //
				DERIVATIVE_THIRD};
		//
		selectedIons = new String[]{//
				SELECTED_IONS_HYDROCARBONS, //
				SELECTED_IONS_FATTY_ACIDS, //
				SELECTED_IONS_FAME, //
				SELECTED_IONS_SOLVENT_TAILING, //
				SELECTED_IONS_COLUMN_BLEED //
		};
		//
		displayModi = new String[]{//
				DISPLAY_MODUS_NORMAL, //
				DISPLAY_MODUS_MIRRORED //
		};
		//
		selectedIonsMap = new HashMap<String, String>();
		selectedIonsMap.put(SELECTED_IONS_HYDROCARBONS, "57 71 85");
		selectedIonsMap.put(SELECTED_IONS_FATTY_ACIDS, "74 84");
		selectedIonsMap.put(SELECTED_IONS_FAME, "79 81");
		selectedIonsMap.put(SELECTED_IONS_SOLVENT_TAILING, "84");
		selectedIonsMap.put(SELECTED_IONS_COLUMN_BLEED, "207");
		//
		mirroredSeries = new HashSet<String>();
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		createButtonsToolbar(parent);
		createChromatogramChart(parent);
		//
		modifyToolbarComposites();
	}

	private void createButtonsToolbar(Composite parent) {

		compositeToolbar = new Composite(parent, SWT.NONE);
		compositeToolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeToolbar.setLayout(new GridLayout(3, false));
		/*
		 * 1st row
		 */
		compositeType = new Composite(compositeToolbar, SWT.NONE);
		GridData gridDataType = new GridData(GridData.FILL_HORIZONTAL);
		gridDataType.grabExcessHorizontalSpace = true;
		compositeType.setLayoutData(gridDataType);
		compositeType.setLayout(new GridLayout(3, false));
		//
		createOverlayTypeCombo(compositeType);
		createDerivativeTypeCombo(compositeType);
		createSelectedSeriesCombo(compositeType);
		//
		compositeSelectedIons = new Composite(compositeToolbar, SWT.NONE);
		compositeSelectedIons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		compositeSelectedIons.setLayout(new GridLayout(1, false));
		//
		createSelectedIonsCombo(compositeSelectedIons);
		//
		compositeStatus = new Composite(compositeToolbar, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		compositeStatus.setLayoutData(gridDataStatus);
		compositeStatus.setLayout(new GridLayout(4, false));
		//
		createDataStatusLabel(compositeStatus);
		createToggleChartLegendButton(compositeStatus);
		createResetButton(compositeStatus);
		createSettingsButton(compositeStatus);
		/*
		 * 2nd row
		 */
		compositeShift = new Composite(compositeToolbar, SWT.NONE);
		GridData gridDataShift = new GridData(GridData.FILL_HORIZONTAL);
		gridDataShift.horizontalSpan = 3;
		compositeShift.setLayoutData(gridDataShift);
		compositeShift.setLayout(new GridLayout(9, false));
		//
		createDisplayModusCombo(compositeShift);
		createTextShiftX(compositeShift);
		createComboScaleX(compositeShift);
		createButtonLeft(compositeShift);
		createButtonRight(compositeShift);
		createTextShiftY(compositeShift);
		createComboScaleY(compositeShift);
		createButtonUp(compositeShift);
		createButtonDown(compositeShift);
	}

	private void createOverlayTypeCombo(Composite parent) {

		comboOverlayType = new Combo(parent, SWT.READ_ONLY);
		comboOverlayType.setToolTipText("Select the overlay type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboOverlayType.setLayoutData(gridData);
		comboOverlayType.setItems(overlayTypes);
		comboOverlayType.select(0);
		comboOverlayType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				modifyToolbarComposites();
				refreshUpdateOverlayChart();
			}
		});
	}

	private void createDerivativeTypeCombo(Composite parent) {

		comboDerivativeType = new Combo(parent, SWT.READ_ONLY);
		comboDerivativeType.setToolTipText("Select the derivative type");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 60;
		gridData.grabExcessHorizontalSpace = true;
		comboDerivativeType.setLayoutData(gridData);
		comboDerivativeType.setItems(derivativeTypes);
		comboDerivativeType.select(0);
		comboDerivativeType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				String derivativeType = comboDerivativeType.getText();
				if(DERIVATIVE_NONE.equals(derivativeType)) {
					if(comboDisplayModus.getText().equals(DISPLAY_MODUS_NORMAL)) {
						chartSettings.getRangeRestriction().setZeroY(false);
					}
				} else {
					chartSettings.getRangeRestriction().setZeroY(false);
				}
				chromatogramChart.applySettings(chartSettings);
				refreshUpdateOverlayChart();
			}
		});
	}

	private void createSelectedSeriesCombo(Composite parent) {

		comboSelectedSeries = new Combo(parent, SWT.READ_ONLY);
		comboSelectedSeries.setToolTipText("Highlight the selected series");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 250;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedSeries.setLayoutData(gridData);
		comboSelectedSeries.setItems(new String[]{BaseChart.SELECTED_SERIES_NONE});
		comboSelectedSeries.select(0);
		comboSelectedSeries.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String selectedSeriesId = comboSelectedSeries.getText().trim();
				BaseChart baseChart = chromatogramChart.getBaseChart();
				baseChart.resetSeriesSettings();
				baseChart.selectSeries(selectedSeriesId);
				baseChart.redraw();
				modifyToolbarComposites();
			}
		});
	}

	private void createDisplayModusCombo(Composite parent) {

		comboDisplayModus = new Combo(parent, SWT.READ_ONLY);
		comboDisplayModus.setToolTipText("Select the display modus.");
		comboDisplayModus.setItems(displayModi);
		comboDisplayModus.setText(DISPLAY_MODUS_NORMAL);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboDisplayModus.setLayoutData(gridData);
		comboDisplayModus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				BaseChart baseChart = chromatogramChart.getBaseChart();
				String displayModus = comboDisplayModus.getText().trim();
				String selectedSeriesId = comboSelectedSeries.getText().trim();
				String derivativeType = comboDerivativeType.getText();
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				//
				if(displayModus.equals(DISPLAY_MODUS_MIRRORED)) {
					/*
					 * Mirror
					 */
					chartSettings.getRangeRestriction().setZeroY(false);
					//
					if(!mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeries(selectedSeriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.add(selectedSeriesId);
					}
				} else {
					/*
					 * Normal
					 */
					if(mirroredSeries.contains(selectedSeriesId)) {
						baseChart.multiplySeries(selectedSeriesId, IExtendedChart.Y_AXIS, -1.0d);
						mirroredSeries.remove(selectedSeriesId);
					}
					//
					if(mirroredSeries.size() == 0 && DERIVATIVE_NONE.equals(derivativeType)) {
						chartSettings.getRangeRestriction().setZeroY(true);
					} else {
						chartSettings.getRangeRestriction().setZeroY(false);
					}
				}
				//
				chromatogramChart.applySettings(chartSettings);
				chromatogramChart.adjustRange(true);
				chromatogramChart.redraw();
			}
		});
	}

	private void createSelectedIonsCombo(Composite parent) {

		comboSelectedIons = new Combo(parent, SWT.NONE);
		comboSelectedIons.setToolTipText("Select the overlay ions.");
		comboSelectedIons.setItems(selectedIons);
		comboSelectedIons.setText(SELECTED_IONS_DEFAULT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.minimumWidth = 150;
		gridData.grabExcessHorizontalSpace = true;
		comboSelectedIons.setLayoutData(gridData);
		comboSelectedIons.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.deleteSeries();
				refreshUpdateOverlayChart();
			}
		});
		comboSelectedIons.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					chromatogramChart.deleteSeries();
					refreshUpdateOverlayChart();
				}
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

		buttonShiftLeft = new Button(parent, SWT.PUSH);
		buttonShiftLeft.setToolTipText("Move Left");
		buttonShiftLeft.setText("");
		buttonShiftLeft.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		buttonShiftLeft.addSelectionListener(new SelectionAdapter() {

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

		buttonShiftRight = new Button(parent, SWT.PUSH);
		buttonShiftRight.setToolTipText("Move Right");
		buttonShiftRight.setText("");
		buttonShiftRight.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		buttonShiftRight.addSelectionListener(new SelectionAdapter() {

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

		buttonShiftUp = new Button(parent, SWT.PUSH);
		buttonShiftUp.setToolTipText("Move Up");
		buttonShiftUp.setText("");
		buttonShiftUp.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		buttonShiftUp.addSelectionListener(new SelectionAdapter() {

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

		buttonShiftDown = new Button(parent, SWT.PUSH);
		buttonShiftDown.setToolTipText("Move Down");
		buttonShiftDown.setText("");
		buttonShiftDown.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		buttonShiftDown.addSelectionListener(new SelectionAdapter() {

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

	private void createDataStatusLabel(Composite parent) {

		labelDataStatus = new Label(parent, SWT.NONE);
		labelDataStatus.setToolTipText("Indicates whether the data has been modified or not.");
		labelDataStatus.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 120;
		labelDataStatus.setLayoutData(gridData);
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
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

				applyOverlaySettings();
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
						applyUserSettings();
						applyOverlaySettings();
					} catch(Exception e1) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void modifyToolbarComposites() {

		/*
		 * Overlay Type
		 */
		String overlayType = comboOverlayType.getText().trim();
		boolean enableOverlayType = (overlayType.contains(OVERLAY_TYPE_XIC) || overlayType.contains(OVERLAY_TYPE_SIC) || overlayType.contains(OVERLAY_TYPE_TSC));
		comboSelectedIons.setEnabled(enableOverlayType);
		/*
		 * Selected Series
		 */
		String selectedSeries = comboSelectedSeries.getText().trim();
		boolean visibleSelectedSeries = !selectedSeries.equals(BaseChart.SELECTED_SERIES_NONE);
		comboDisplayModus.setVisible(visibleSelectedSeries);
		textShiftX.setVisible(visibleSelectedSeries);
		comboScaleX.setVisible(visibleSelectedSeries);
		buttonShiftLeft.setVisible(visibleSelectedSeries);
		buttonShiftRight.setVisible(visibleSelectedSeries);
		textShiftY.setVisible(visibleSelectedSeries);
		comboScaleY.setVisible(visibleSelectedSeries);
		buttonShiftUp.setVisible(visibleSelectedSeries);
		buttonShiftDown.setVisible(visibleSelectedSeries);
		GridData gridDataCompositeShift = (GridData)compositeShift.getLayoutData();
		gridDataCompositeShift.exclude = !visibleSelectedSeries;
		/*
		 * Update the layout
		 */
		Composite parent = compositeToolbar.getParent();
		parent.layout(true);
		parent.redraw();
	}

	private void modifyDataStatusLabel() {

		if(chromatogramChart.getBaseChart().isDataShifted()) {
			labelDataStatus.setText("Shifted Data");
			labelDataStatus.setBackground(Colors.YELLOW);
		} else {
			labelDataStatus.setText("");
			labelDataStatus.setBackground(null);
		}
	}

	private void applyOverlaySettings() {

		resetColorMaps();
		chromatogramChart.deleteSeries();
		refreshUpdateOverlayChart();
		//
		modifyToolbarComposites();
		modifyDataStatusLabel();
	}

	private void applyUserSettings() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_NORMAL));
		colorSchemeSIC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_OVERLAY_SIC));
		//
		lineStyleTIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_TIC));
		lineStyleBPC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_BPC));
		lineStyleXIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_XIC));
		lineStyleSIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_SIC));
		lineStyleTSC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_TSC));
		lineStyleDefault = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_OVERLAY_DEFAULT));
	}

	private void resetColorMaps() {

		colorSchemeNormal.reset();
		usedColorsNormal.clear();
		colorSchemeSIC.reset();
		usedColorsSIC.clear();
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
		chartSettings.setShowRangeSelectorInitially(false);
		chartSettings.setSupportDataShift(true);
		chromatogramChart.applySettings(chartSettings);
		//
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addSeriesModificationListener(new ISeriesModificationListener() {

			@Override
			public void handleSeriesModificationEvent() {

				modifyDataStatusLabel();
			}
		});
		//
		baseChart.addSeriesStatusListener(new SeriesStatusAdapter() {

			@Override
			public void handleSeriesSelectionEvent(String seriedId) {

				comboSelectedSeries.setText(seriedId);
				modifyToolbarComposites();
			}
		});
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
		comboDisplayModus.setText(DISPLAY_MODUS_NORMAL);
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
			String derivativeType = comboDerivativeType.getText().trim();
			//
			for(String overlayType : overlayTypes) {
				if(overlayType.equals(OVERLAY_TYPE_SIC)) {
					/*
					 * SIC
					 */
					if(chromatogram instanceof IChromatogramMSD) {
						for(int ion : ions) {
							//
							String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + DELIMITER_ION_DERIVATIVE + derivativeType + DELIMITER_ION_DERIVATIVE + ion + OVERLAY_STOP_MARKER;
							Color color = getSeriesColor(seriesId, overlayType);
							//
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								List<Integer> sic = new ArrayList<Integer>();
								sic.add(ion);
								lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, sic));
							}
						}
					}
				} else {
					//
					String seriesId = chromatogramName + OVERLAY_START_MARKER + overlayType + DELIMITER_ION_DERIVATIVE + derivativeType + OVERLAY_STOP_MARKER;
					Color color = getSeriesColor(chromatogramName, overlayType);
					//
					if(overlayType.equals(OVERLAY_TYPE_BPC) || overlayType.equals(OVERLAY_TYPE_XIC) || overlayType.equals(OVERLAY_TYPE_TSC)) {
						/*
						 * BPC, XIC, TSC
						 */
						if(chromatogram instanceof IChromatogramMSD) {
							availableSeriesIds.add(seriesId);
							if(!baseChart.isSeriesContained(seriesId)) {
								lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions));
							}
						}
					} else {
						/*
						 * TIC
						 */
						availableSeriesIds.add(seriesId);
						if(!baseChart.isSeriesContained(seriesId)) {
							lineSeriesDataList.add(getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, ions));
						}
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
		items[0] = BaseChart.SELECTED_SERIES_NONE;
		int index = 1;
		for(String seriesId : availableSeriesIds) {
			items[index++] = seriesId;
		}
		//
		comboSelectedSeries.setItems(items);
		comboSelectedSeries.setText(BaseChart.SELECTED_SERIES_NONE);
		//
		modifyDataStatusLabel();
		chromatogramChart.adjustRange(true);
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

	private Color getSeriesColor(String seriesId, String overlayType) {

		Color color;
		if(OVERLAY_TYPE_SIC.equals(overlayType)) {
			/*
			 * SIC
			 */
			color = usedColorsSIC.get(seriesId);
			if(color == null) {
				color = colorSchemeSIC.getColor();
				colorSchemeSIC.incrementColor();
				usedColorsSIC.put(seriesId, color);
			}
		} else {
			/*
			 * Normal
			 */
			color = usedColorsNormal.get(seriesId);
			if(color == null) {
				color = colorSchemeNormal.getColor();
				colorSchemeNormal.incrementColor();
				usedColorsNormal.put(seriesId, color);
			}
		}
		return color;
	}

	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, String overlayType, String derivativeType, Color color, List<Integer> ions) {

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
			ySeries[index] = getIntensity(scan, overlayType, derivativeType, ions);
			index++;
		}
		/*
		 * Calculate a derivative?
		 */
		int derivatives = getNumberOfDerivatives(derivativeType);
		for(int i = 1; i <= derivatives; i++) {
			ySeries = calculateDerivate(ySeries);
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

	private double getIntensity(IScan scan, String overlayType, String derivativeType, List<Integer> ions) {

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
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			/*
			 * TSC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				for(int ion : ions) {
					intensity -= extractedIonSignal.getAbundance(ion);
				}
			}
		}
		//
		return intensity;
	}

	private int getNumberOfDerivatives(String derivativeType) {

		int derivatives;
		switch(derivativeType) {
			case DERIVATIVE_FIRST:
				derivatives = 1;
				break;
			case DERIVATIVE_SECOND:
				derivatives = 2;
				break;
			case DERIVATIVE_THIRD:
				derivatives = 3;
				break;
			default:
				derivatives = 0;
				break;
		}
		return derivatives;
	}

	private double[] calculateDerivate(double[] values) {

		int size = values.length;
		double[] derivative = new double[size];
		for(int i = 1; i < size; i++) {
			derivative[i] = values[i] - values[i - 1];
		}
		return derivative;
	}

	private LineStyle getLineStyle(String overlayType) {

		LineStyle lineStyle;
		if(overlayType.equals(OVERLAY_TYPE_TIC)) {
			lineStyle = lineStyleTIC;
		} else if(overlayType.equals(OVERLAY_TYPE_BPC)) {
			lineStyle = lineStyleBPC;
		} else if(overlayType.equals(OVERLAY_TYPE_XIC)) {
			lineStyle = lineStyleXIC;
		} else if(overlayType.equals(OVERLAY_TYPE_SIC)) {
			lineStyle = lineStyleSIC;
		} else if(overlayType.equals(OVERLAY_TYPE_TSC)) {
			lineStyle = lineStyleTSC;
		} else {
			lineStyle = lineStyleDefault;
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
