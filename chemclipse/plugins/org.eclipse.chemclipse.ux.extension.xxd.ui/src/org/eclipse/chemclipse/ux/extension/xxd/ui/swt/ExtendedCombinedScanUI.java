/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.support.CalculationType;
import org.eclipse.chemclipse.msd.model.support.FilterSupport;
import org.eclipse.chemclipse.msd.swt.ui.support.DatabaseFileSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageSubtract;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

@SuppressWarnings("rawtypes")
public class ExtendedCombinedScanUI extends Composite implements IExtendedPartUI {

	private static final Logger logger = Logger.getLogger(ExtendedCombinedScanUI.class);
	//
	private static final int INDEX_CHART = 0;
	private static final int INDEX_TABLE = 1;
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private TabFolder tabFolder;
	private ScanChartUI scanChartUI;
	private ScanTableUI scanTableUI;
	//
	private IChromatogramSelectionMSD chromatogramSelection = null;
	private IScanMSD scanMSD = null;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public ExtendedCombinedScanUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public boolean setFocus() {

		updateScan();
		return true;
	}

	public void update(Object object) {

		chromatogramSelection = null;
		scanMSD = null;
		//
		if(object instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
			this.chromatogramSelection = chromatogramSelectionMSD;
			boolean useNormalize = PreferenceSupplier.isUseNormalizedScan();
			CalculationType calculationType = PreferenceSupplier.getCalculationType();
			scanMSD = FilterSupport.getCombinedMassSpectrum(chromatogramSelectionMSD, null, useNormalize, calculationType);
		}
		//
		toolbarInfo.get().setText(getCombinedRangeInfo(object));
		updateScan();
	}

	private void updateScan() {

		updateScanData();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createScanTabFolderSection(this);
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
		composite.setLayout(new GridLayout(3, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createSaveButton(composite);
		createSettingsButton(composite);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createScanTabFolderSection(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(1, true));
		//
		tabFolder = new TabFolder(composite, SWT.BOTTOM);
		tabFolder.setBackground(Colors.WHITE);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		tabFolder.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				updateScanData();
			}
		});
		//
		createScanChart(tabFolder);
		createScanTable(tabFolder);
	}

	private void createScanChart(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Chart");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		scanChartUI = new ScanChartUI(composite, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createScanTable(TabFolder tabFolder) {

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Table");
		Composite composite = new Composite(tabFolder, SWT.NONE);
		composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(1, true));
		tabItem.setControl(composite);
		//
		scanTableUI = new ScanTableUI(composite, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		scanTableUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private Button createSaveButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Save the combined scan.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SAVE_AS, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					if(scanMSD != null) {
						DatabaseFileSupport.saveMassSpectrum(DisplayUtils.getShell(), scanMSD, "CombinedScan");
					}
				} catch(NoConverterAvailableException e1) {
					logger.warn(e1);
				}
			}
		});
		return button;
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePageScans.class, PreferencePageSubtract.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		update(chromatogramSelection);
	}

	private void updateScanData() {

		switch(tabFolder.getSelectionIndex()) {
			case INDEX_CHART:
				if(scanChartUI != null) {
					scanChartUI.setInput(scanMSD);
				}
				break;
			case INDEX_TABLE:
				if(scanTableUI != null) {
					scanTableUI.setInput(scanMSD);
				}
				break;
		}
	}

	private String getCombinedRangeInfo(Object object) {

		StringBuilder builder = new StringBuilder();
		if(object instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)object;
			int startRetentionTime = chromatogramSelectionMSD.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelectionMSD.getStopRetentionTime();
			IChromatogram chromatogram = chromatogramSelectionMSD.getChromatogram();
			builder.append("Scan range: ");
			builder.append(chromatogram.getScanNumber(startRetentionTime));
			builder.append("–");
			builder.append(chromatogram.getScanNumber(stopRetentionTime));
			builder.append(" | RT range: ");
			builder.append(decimalFormat.format((double)startRetentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append("–");
			builder.append(decimalFormat.format((double)stopRetentionTime / IChromatogram.MINUTE_CORRELATION_FACTOR));
			builder.append(" | Calculation Type: ");
			builder.append(PreferenceSupplier.getCalculationType().toString());
		} else {
			builder.append("No chromatogram selected.");
		}
		return builder.toString();
	}
}
