/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.PeakQuantitations;
import org.eclipse.chemclipse.model.support.PeakQuantitationsExtractor;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swtchart.extensions.preferences.PreferencePage;

public class ExtendedChromatogramStatisticsUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<KeyValueListUI> tableControl = new AtomicReference<>();
	//
	private IChromatogramSelection<?, ?> chromatogramSelection = null;
	private PeakQuantitationsExtractor peakQuantitationsExtractor = new PeakQuantitationsExtractor();
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish();

	public ExtendedChromatogramStatisticsUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(IChromatogramSelection<?, ?> chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		updateInput();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createTable(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createTable(Composite parent) {

		KeyValueListUI keyValueListUI = new KeyValueListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = keyValueListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		tableControl.set(keyValueListUI);
	}

	private void createSettingsButton(Composite parent) {

		createSettingsButton(parent, Arrays.asList(PreferencePage.class), new ISettingsHandler() {

			@Override
			public void apply(Display display) {

				applySettings();
			}
		});
	}

	private void applySettings() {

		updateInput();
	}

	private void updateInput() {

		String info = "--";
		Map<String, String> dataMap = new LinkedHashMap<>();
		if(chromatogramSelection != null) {
			/*
			 * Map
			 */
			addTimeData(chromatogramSelection, dataMap);
			addScanData(chromatogramSelection, dataMap);
			addPeakData(chromatogramSelection, dataMap);
			//
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			info = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
		}
		//
		toolbarInfo.get().setText(info);
		tableControl.get().setInput(dataMap);
	}

	private void addTimeData(IChromatogramSelection<?, ?> chromatogramSelection, Map<String, String> dataMap) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		dataMap.put("Start Total [min]", decimalFormat.format(chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		dataMap.put("Stop Total [min]", decimalFormat.format(chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		dataMap.put("Start Selection [min]", decimalFormat.format(chromatogramSelection.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		dataMap.put("Stop Selection [min]", decimalFormat.format(chromatogramSelection.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
	}

	private void addScanData(IChromatogramSelection<?, ?> chromatogramSelection, Map<String, String> dataMap) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int scanStart = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int scanStop = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		dataMap.put("Scans Total", Integer.toString(chromatogram.getNumberOfScans()));
		dataMap.put("Scans Delay [ms]", Integer.toString(chromatogram.getScanDelay()));
		dataMap.put("Scan Interval [ms]", Integer.toString(chromatogram.getScanInterval()));
		dataMap.put("Scans Selection", Integer.toString(scanStop - scanStart + 1));
		dataMap.put("Scan Selection Start", Integer.toString(scanStart));
		dataMap.put("Scan Selection Stop", Integer.toString(scanStop));
	}

	private void addPeakData(IChromatogramSelection<?, ?> chromatogramSelection, Map<String, String> dataMap) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> peaks = ChromatogramDataSupport.extractPeaks(chromatogramSelection);
		/*
		 * Peak Area
		 */
		dataMap.put("Peaks Total", Integer.toString(chromatogram.getNumberOfPeaks()));
		dataMap.put("Peak Area Sum Total", Double.toString(getSummedPeakArea(chromatogram.getPeaks())));
		dataMap.put("Peaks Selection", Integer.toString(peaks.size()));
		dataMap.put("Peak Area Sum Selection", Double.toString(getSummedPeakArea(peaks)));
		/*
		 * Quantitations
		 */
		PeakQuantitations peakQuantitations = peakQuantitationsExtractor.extract(peaks, chromatogramSelection);
		Map<String, Double> summedQuantitations = peakQuantitations.getSummedQuantitations();
		List<String> entries = new ArrayList<>(summedQuantitations.keySet());
		Collections.sort(entries);
		for(String entry : entries) {
			dataMap.put(entry, Double.toString(summedQuantitations.get(entry)));
		}
	}

	private double getSummedPeakArea(List<? extends IPeak> peaks) {

		double peakArea = 0.0d;
		for(IPeak peak : peaks) {
			peakArea += peak.getIntegratedArea();
		}
		//
		return peakArea;
	}
}
