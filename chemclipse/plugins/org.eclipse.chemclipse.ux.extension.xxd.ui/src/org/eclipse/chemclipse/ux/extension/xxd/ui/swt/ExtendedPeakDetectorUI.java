/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ChromatogramSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePagePeaks;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExtendedPeakDetectorUI {

	/*
	 * Detection Types
	 */
	private static final String DETECTION_TYPE_BASELINE = "DETECTION_TYPE_BASELINE";
	private static final String DETECTION_TYPE_SCAN = "DETECTION_TYPE_SCAN";
	private static final String DETECTION_TYPE_SCAN_BB = DETECTION_TYPE_SCAN + "_BB";
	private static final String DETECTION_TYPE_SCAN_VV = DETECTION_TYPE_SCAN + "_VV";
	private static final String DETECTION_TYPE_NONE = "";
	//
	private static final char KEY_BASELINE = 'd';
	private static final char KEY_BB = 'b';
	private static final char KEY_VV = 'v';
	/*
	 * Detection Box
	 */
	private static final String DETECTION_BOX_LEFT = "DETECTION_BOX_LEFT";
	private static final String DETECTION_BOX_RIGHT = "DETECTION_BOX_RIGHT";
	private static final String DETECTION_BOX_NONE = "DETECTION_BOX_NONE";
	//
	private static final int BOX_SNAP_MARKER_WINDOW = 4;
	private static final int BOX_MAX_DELTA = 1;
	//
	private Composite toolbarInfo;
	private Label labelChromatogram;
	private Composite toolbarDetect;
	private PeakChartUI peakChartUI;
	//
	private IChromatogramSelection chromatogramSelection;
	//
	private Shell shell = Display.getDefault().getActiveShell();
	//
	private Cursor defaultCursor;
	private String detectionType = DETECTION_TYPE_NONE;
	private String detectionBox = DETECTION_BOX_NONE;

	@Inject
	public ExtendedPeakDetectorUI(Composite parent) {
		initialize(parent);
	}

	@Focus
	public void setFocus() {

		updateChromatogram();
	}

	public void update(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		IChromatogram chromatogram = null;
		if(chromatogramSelection != null) {
			chromatogram = chromatogramSelection.getChromatogram();
		}
		labelChromatogram.setText(ChromatogramSupport.getChromatogramLabel(chromatogram));
		updateChromatogram();
	}

	private void updateChromatogram() {

		peakChartUI.setInput(null);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarInfo = createToolbarInfo(parent);
		toolbarDetect = createToolbarDetect(parent);
		createPeakChart(parent);
		//
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		PartSupport.setCompositeVisibility(toolbarDetect, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridDataStatus = new GridData(GridData.FILL_HORIZONTAL);
		gridDataStatus.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridDataStatus);
		composite.setLayout(new GridLayout(4, false));
		//
		createButtonToggleToolbarInfo(composite);
		createButtonToggleToolbarDetect(composite);
		createToggleChartLegendButton(composite);
		createSettingsButton(composite);
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		composite.setVisible(false);
		//
		labelChromatogram = new Label(composite, SWT.NONE);
		labelChromatogram.setText("");
		labelChromatogram.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarDetect(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		composite.setVisible(false);
		//
		createDetectionTypeButton(composite, "Reset", IApplicationImage.IMAGE_RESET, DETECTION_TYPE_NONE);
		createDetectionTypeButton(composite, "Detection Modus (Baseline) [Key:" + KEY_BASELINE + "]", IApplicationImage.IMAGE_DETECTION_TYPE_BASELINE, DETECTION_TYPE_BASELINE);
		createDetectionTypeButton(composite, "Detection Modus (BB) [Key:" + KEY_BB + "]", IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_BB, DETECTION_TYPE_SCAN_BB);
		createDetectionTypeButton(composite, "Detection Modus (VV) [Key:" + KEY_VV + "]", IApplicationImage.IMAGE_DETECTION_TYPE_SCAN_VV, DETECTION_TYPE_SCAN_VV);
		createAddPeakButton(composite);
		//
		return composite;
	}

	private Button createDetectionTypeButton(Composite parent, String tooltip, String image, String detectionType) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(tooltip);
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				setDetectionType(detectionType);
				if(detectionType.equals(DETECTION_TYPE_NONE)) {
					reset();
				}
			}
		});
		return button;
	}

	private Button createAddPeakButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the manually detected peak.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				// TODO Add
			}
		});
		return button;
	}

	private void setDetectionType(String detectionType) {

		setDefaultCursor();
		resetBaselinePeakSelection();
		resetScanPeakSelection();
		//
		this.detectionType = detectionType;
		if(detectionType.equals(DETECTION_TYPE_BASELINE)) {
			this.detectionBox = DETECTION_BOX_NONE;
		}
		//
		if(detectionType.equals(DETECTION_TYPE_NONE)) {
			setDefaultCursor();
		} else {
			setCursor(SWT.CURSOR_CROSS);
		}
	}

	private void setCursor(int cursorId) {

		peakChartUI.setCursor(Display.getCurrent().getSystemCursor(cursorId));
	}

	private void setDefaultCursor() {

		peakChartUI.setCursor(defaultCursor);
	}

	private void resetBaselinePeakSelection() {

		detectionType = DETECTION_TYPE_NONE;
		// baselineSelectionPaintListener.reset();
		// resetSelectedRange();
		peakChartUI.redraw();
	}

	private void resetScanPeakSelection() {

		// resetSelectedRange();
		detectionType = DETECTION_TYPE_NONE;
		setDefaultCursor();
		// scanSelectionPaintListener.reset();
		peakChartUI.redraw();
	}

	private Button createButtonToggleToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createButtonToggleToolbarDetect(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the detector toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarDetect);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EDIT, IApplicationImage.SIZE_16x16));
				}
			}
		});
	}

	private void createToggleChartLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				peakChartUI.toggleSeriesLegendVisibility();
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

				IPreferencePage preferencePage = new PreferencePagePeaks();
				preferencePage.setTitle("Chromatogram Settings");
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(shell, preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == PreferenceDialog.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(shell, "Settings", "Something has gone wrong to apply the chart settings.");
					}
				}
			}
		});
	}

	private void createPeakChart(Composite parent) {

		peakChartUI = new PeakChartUI(parent, SWT.BORDER);
		peakChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = peakChartUI.getChartSettings();
		chartSettings.setCreateMenu(true);
		peakChartUI.applySettings(chartSettings);
		//
		defaultCursor = peakChartUI.getCursor();
	}

	private void reset() {

		updateChromatogram();
	}

	private void applySettings() {

		updateChromatogram();
	}
}
