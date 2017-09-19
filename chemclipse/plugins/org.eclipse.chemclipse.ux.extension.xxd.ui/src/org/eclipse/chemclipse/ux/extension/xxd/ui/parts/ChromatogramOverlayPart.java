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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.csd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.wsd.ui.editors.ChromatogramEditorWSD;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.eavp.service.swtchart.core.IChartSettings;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.customcharts.ChromatogramChart;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineChart;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ChromatogramOverlayPart {

	@Inject
	private EPartService partService;
	private ChromatogramChart chromatogramChart;
	private IColorScheme colorScheme;

	public ChromatogramOverlayPart() {
		colorScheme = Colors.getColorScheme(Colors.COLOR_SCHEME_PUBLICATION);
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
		compositeLeft.setLayoutData(gridDataLeft);
		compositeLeft.setLayout(new GridLayout(2, false));
		//
		Composite compositeRight = new Composite(composite, SWT.NONE);
		GridData gridDataRight = new GridData(GridData.FILL_HORIZONTAL);
		gridDataRight.horizontalAlignment = SWT.END;
		compositeRight.setLayoutData(gridDataRight);
		compositeRight.setLayout(new GridLayout(2, false));
		//
		createDisplayTypeCombo(compositeLeft);
		createHighlightSeriesCombo(compositeLeft);
		createResetButton(compositeRight);
		createSettingsButton(compositeRight);
	}

	private void createDisplayTypeCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.PUSH);
		combo.setToolTipText("Set the display type");
		combo.setText("");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setItems(new String[]{"TIC", "TIC+BPC", "Mirrored"});
		combo.select(0);
	}

	private void createHighlightSeriesCombo(Composite parent) {

		Combo combo = new Combo(parent, SWT.PUSH);
		combo.setToolTipText("Highlight the selected series");
		combo.setText("");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setItems(new String[]{"002.D", "003.D"});
		combo.select(0);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the display");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

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

			}
		});
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
		chromatogramChart.applySettings(chartSettings);
	}

	@Focus
	public void setFocus() {

		List<IChromatogramSelection> chromatogramSelections = getChromatogramSelections();
		for(IChromatogramSelection chromatogramSelection : chromatogramSelections) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			String seriesId = chromatogram.getName();
			/*
			 * TIC
			 */
			String seriesIdTic = seriesId + "TIC";
			if(!chromatogramChart.getBaseChart().isSeriesContained(seriesIdTic)) {
				addDataSeries(chromatogram, seriesIdTic, false);
			}
			/*
			 * BPC
			 */
			String seriesIdBpc = seriesId + "BPC";
			if(!chromatogramChart.getBaseChart().isSeriesContained(seriesIdBpc)) {
				addDataSeries(chromatogram, seriesIdBpc, true);
			}
		}
	}

	private void addDataSeries(IChromatogram chromatogram, String seriesId, boolean bpc) {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		double[] xSeries = new double[chromatogram.getNumberOfScans()];
		double[] ySeries = new double[chromatogram.getNumberOfScans()];
		int index = 0;
		for(IScan scan : chromatogram.getScans()) {
			xSeries[index] = scan.getRetentionTime();
			if(bpc && scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					ySeries[index] = ion.getAbundance();
				} else {
					ySeries[index] = scan.getTotalSignal();
				}
			} else {
				ySeries[index] = scan.getTotalSignal();
			}
			index++;
		}
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSerieSettings = lineSeriesData.getLineSeriesSettings();
		lineSerieSettings.setLineColor(colorScheme.getColor());
		colorScheme.incrementColor();
		lineSerieSettings.setEnableArea(false);
		lineSeriesDataList.add(lineSeriesData);
		chromatogramChart.addSeriesData(lineSeriesDataList, LineChart.MEDIUM_COMPRESSION);
	}

	private List<IChromatogramSelection> getChromatogramSelections() {

		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		/*
		 * Get all open chromatogram parts.
		 */
		Collection<MPart> parts = partService.getParts();
		for(MPart part : parts) {
			if(isChromatogramEditor(part)) {
				Object object = part.getObject();
				if(object != null) {
					/*
					 * MSD/CSD/WSD
					 */
					IChromatogramSelection selection = null;
					if(object instanceof ChromatogramEditorMSD) {
						ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
						selection = editor.getChromatogramSelection();
					} else if(object instanceof ChromatogramEditorCSD) {
						ChromatogramEditorCSD editor = (ChromatogramEditorCSD)object;
						selection = editor.getChromatogramSelection();
					} else if(object instanceof ChromatogramEditorWSD) {
						ChromatogramEditorWSD editor = (ChromatogramEditorWSD)object;
						selection = editor.getChromatogramSelection();
					}
					//
					if(selection != null) {
						chromatogramSelections.add(selection);
					}
				}
			}
		}
		/*
		 * If the window was null and there was no open editor, the list will
		 * contains 0 elements.
		 */
		return chromatogramSelections;
	}

	private boolean isChromatogramEditor(MPart part) {

		return (part.getElementId().equals(ChromatogramEditorMSD.ID) || //
				part.getElementId().equals(ChromatogramEditorCSD.ID) || //
				part.getElementId().equals(ChromatogramEditorWSD.ID));
	}
}
