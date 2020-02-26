/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EditorUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanDataSupport;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("rawtypes")
public class ExtendedScanBrowseUI {

	private Composite toolbarInfo;
	private Label labelInfo;
	private ScanChartUI scanChartUI;
	//
	private Button buttonPreviousScan;
	private ComboViewer comboChromatograms;
	private Button buttonNextScan;
	//
	private int masterRetentionTime;
	//
	private final ScanDataSupport scanDataSupport = new ScanDataSupport();
	private EditorUpdateSupport editorUpdateSupport = new EditorUpdateSupport();

	public ExtendedScanBrowseUI(Composite parent) {
		initialize(parent);
	}

	public void update(IScan scan) {

		setScan(scan);
		enableReferenceScanWidgets();
	}

	private void setScan(IScan scan) {

		masterRetentionTime = (scan != null) ? scan.getRetentionTime() : 0;
		updateScan(scan);
		setComboReferenceItems(scan);
	}

	private void updateScan(IScan scan) {

		labelInfo.setText(scanDataSupport.getScanLabel(scan));
		scanChartUI.setInput(scan);
	}

	private void initialize(Composite parent) {

		Composite container = createContainer(parent);
		//
		createToolbarReferences(container);
		toolbarInfo = createToolbarInfo(container);
		createScanChart(container);
		/*
		 * No initial selection.
		 */
		PartSupport.setCompositeVisibility(toolbarInfo, true);
		setScan(null);
	}

	private Composite createToolbarReferences(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		buttonPreviousScan = createPreviousReferenceScanButton(composite);
		comboChromatograms = createReferenceCombo(composite);
		buttonNextScan = createNextReferenceScanButton(composite);
		//
		return composite;
	}

	private Button createPreviousReferenceScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Get the scan of the previous reference.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PREVIOUS_YELLOW, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectReferenceScan(-1);
			}
		});
		return button;
	}

	private ComboViewer createReferenceCombo(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				String label = "";
				if(element instanceof IChromatogramSelection) {
					IChromatogramSelection chromatogramSelection = (IChromatogramSelection)element;
					label = chromatogramSelection.getChromatogram().getName();
				}
				return label;
			}
		});
		//
		Combo combo = comboViewer.getCombo();
		combo.setToolTipText("Editor Reference(s) Scan");
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectReferenceScan(0);
			}
		});
		return comboViewer;
	}

	private Button createNextReferenceScanButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Get the scan of the next reference.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_NEXT_YELLOW, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				selectReferenceScan(1);
			}
		});
		return button;
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelInfo = new Label(composite, SWT.NONE);
		labelInfo.setText("");
		labelInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private void createScanChart(Composite parent) {

		scanChartUI = new ScanChartUI(parent, SWT.BORDER);
		scanChartUI.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void setComboReferenceItems(IScan scan) {

		List<IChromatogramSelection> chromatogramSelections = editorUpdateSupport.getChromatogramSelections();
		comboChromatograms.setInput(chromatogramSelections);
		//
		if(scan != null) {
			exitloop:
			for(int i = 0; i < chromatogramSelections.size(); i++) {
				IChromatogramSelection chromatogramSelection = chromatogramSelections.get(i);
				if(chromatogramSelection.getSelectedScan() == scan) {
					comboChromatograms.getCombo().select(i);
					break exitloop;
				}
			}
		}
		//
		enableReferenceScanWidgets();
	}

	private void enableReferenceScanWidgets() {

		Combo combo = comboChromatograms.getCombo();
		buttonPreviousScan.setEnabled(combo.getSelectionIndex() > 0);
		buttonNextScan.setEnabled(combo.getSelectionIndex() < combo.getItemCount() - 1);
	}

	private void selectReferenceScan(int moveIndex) {

		Combo combo = comboChromatograms.getCombo();
		int index = combo.getSelectionIndex() + moveIndex;
		//
		if(moveIndex < 0) {
			index = (index < 0) ? 0 : index;
			combo.select(index);
		} else if(moveIndex > 0) {
			index = (index > combo.getItemCount()) ? combo.getItemCount() : index;
			combo.select(index);
		}
		/*
		 * Update the chart and label
		 */
		IScan referenceScan = null;
		IStructuredSelection structuredSelection = comboChromatograms.getStructuredSelection();
		Object object = structuredSelection.getFirstElement();
		if(object instanceof IChromatogramSelection) {
			IChromatogramSelection chromatogramSelection = (IChromatogramSelection)object;
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int scanNumber = chromatogram.getScanNumber(masterRetentionTime);
			referenceScan = chromatogram.getScan(scanNumber);
		}
		//
		updateScan(referenceScan);
		enableReferenceScanWidgets();
	}
}
