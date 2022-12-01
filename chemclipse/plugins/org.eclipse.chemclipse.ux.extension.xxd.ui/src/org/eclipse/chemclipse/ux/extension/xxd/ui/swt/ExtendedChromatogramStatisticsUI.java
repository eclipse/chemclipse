/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
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
	@SuppressWarnings("unused")
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
			 * Chromatogram
			 */
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			info = ChromatogramDataSupport.getChromatogramLabel(chromatogram);
			/*
			 * Map
			 */
			dataMap.put("Start (Minutes)", decimalFormat.format(chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			dataMap.put("Stop (Minutes)", decimalFormat.format(chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			dataMap.put("Scans", Integer.toString(chromatogram.getNumberOfScans()));
			dataMap.put("Peaks", Integer.toString(chromatogram.getNumberOfPeaks()));
			addPeakData(chromatogramSelection, dataMap);
		}
		//
		toolbarInfo.get().setText(info);
		tableControl.get().setInput(dataMap);
	}

	private void addPeakData(IChromatogramSelection<?, ?> chromatogramSelection, Map<String, String> dataMap) {

		List<? extends IPeak> peaks = ChromatogramDataSupport.extractPeaks(chromatogramSelection);
		/*
		 * Peak Area
		 */
		double peakArea = 0.0d;
		for(IPeak peak : peaks) {
			peakArea += peak.getIntegratedArea();
		}
		dataMap.put("Peak Area", Double.toString(peakArea));
		/*
		 * Quantitations
		 */
		// PeakQuantitations peakQuantitations = peakQuantitationsExtractor.extract(peaks, chromatogramSelection);
		// List<String> titles = peakQuantitations.getTitles();
		// for(int i = 0; i < titles.size(); i++) {
		// String title = titles.get(i);
		// double quantitation = 0.0d;
		// for(PeakQuantitation peakQuantitation : peakQuantitations.getQuantitationEntries()) {
		// quantitation += peakQuantitation.getConcentrations().get(i);
		// }
		// dataMap.put(title, Double.toString(quantitation));
		// }
	}
}