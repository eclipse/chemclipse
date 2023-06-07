/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.support.PeakQuantitation;
import org.eclipse.chemclipse.model.support.PeakQuantitations;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.chemclipse.swt.ui.components.peaks.PeakQuantitationListUI;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExtendedPeakQuantitationListUI extends Composite implements IExtendedPartUI {

	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private AtomicReference<PeakQuantitationListUI> tableViewer = new AtomicReference<>();
	//
	private PeakQuantitations peakQuantitations;

	public ExtendedPeakQuantitationListUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	@Override
	public boolean setFocus() {

		tableViewer.get().getTable().setFocus();
		return true;
	}

	public void update(PeakQuantitations peakQuantitations) {

		this.peakQuantitations = peakQuantitations;
		tableViewer.get().update(peakQuantitations);
		updateLabel();
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createListUI(this);
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
		composite.setLayout(new GridLayout(1, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
	}

	private void createToolbarInfo(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		toolbarInfo.set(informationUI);
	}

	private void createListUI(Composite parent) {

		PeakQuantitationListUI peakQuantitationListUI = new PeakQuantitationListUI(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		peakQuantitationListUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		peakQuantitationListUI.getTable().addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings({"rawtypes", "unchecked"})
			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = peakQuantitationListUI.getStructuredSelection().getFirstElement();
				if(object instanceof PeakQuantitation peakQuantitation) {
					IChromatogramSelection chromatogramSelection = peakQuantitation.getChromatogramSelection();
					IPeak peak = peakQuantitation.getPeak();
					//
					if(chromatogramSelection != null) {
						if(peak != null) {
							chromatogramSelection.setSelectedPeak(peak);
							UpdateNotifierUI.update(e.display, IChemClipseEvents.TOPIC_EDITOR_CHROMATOGRAM_UPDATE, "The quanititation peak has been selected.");
							UpdateNotifierUI.update(e.display, peak);
						}
					} else if(peak != null) {
						UpdateNotifierUI.update(e.display, peak);
					}
				}
			}
		});
		//
		tableViewer.set(peakQuantitationListUI);
	}

	private void updateLabel() {

		toolbarInfo.get().setText(peakQuantitations != null ? "Entries: " + peakQuantitations.getQuantitationEntries().size() : "");
	}
}
