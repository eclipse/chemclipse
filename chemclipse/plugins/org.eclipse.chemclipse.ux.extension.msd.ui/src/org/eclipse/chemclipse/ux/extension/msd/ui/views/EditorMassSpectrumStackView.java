/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.swt.ui.components.massspectrum.MassSpectraStackUI;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractChromatogramSelectionView;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class EditorMassSpectrumStackView extends AbstractChromatogramSelectionView {

	@Inject
	private Composite parent;
	private EPartService partService;
	private MassSpectraStackUI massSpectraStackUI;
	private IMassSpectra massSpectra;

	@Inject
	public EditorMassSpectrumStackView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.partService = partService;
		massSpectra = new MassSpectra();
	}

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		massSpectraStackUI = new MassSpectraStackUI(parent, SWT.NONE);
	}

	@Focus
	public void setFocus() {

		massSpectraStackUI.update(massSpectra, false);
		massSpectraStackUI.setFocus();
	}

	public IMassSpectra getMassSpectra(IChromatogramSelectionMSD masterChromatogramSelection) {

		/*
		 * Add the master selection.
		 */
		IScanMSD masterScanMSD = masterChromatogramSelection.getSelectedScan();
		int masterRetentionTime = masterScanMSD.getRetentionTime();
		IMassSpectra massSpectra = new MassSpectra();
		massSpectra.addMassSpectrum(masterScanMSD);
		/*
		 * Get all open parts.
		 */
		Collection<MPart> parts = partService.getParts();
		for(MPart part : parts) {
			if(part.getElementId().equals(ChromatogramEditorMSD.ID)) {
				/*
				 * Select the chromatogram editor parts only.
				 */
				Object object = part.getObject();
				if(object != null) {
					/*
					 * MSD
					 */
					IScanMSD scan = null;
					if(object instanceof ChromatogramEditorMSD) {
						ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
						IChromatogramSelectionMSD chromatogramSelectionMSD = editor.getChromatogramSelection();
						IChromatogramMSD chromatogramMSD = chromatogramSelectionMSD.getChromatogramMSD();
						if(chromatogramMSD != null) {
							int scanNumber = chromatogramMSD.getScanNumber(masterRetentionTime);
							IScan selection = chromatogramMSD.getScan(scanNumber);
							if(selection instanceof IScanMSD) {
								scan = (IScanMSD)selection;
							}
						}
					}
					/*
					 * Do not add the master scan selection twice.
					 */
					if(scan != null && scan != masterScanMSD) {
						massSpectra.addMassSpectrum(scan);
					}
				}
			}
		}
		//
		return massSpectra;
	}

	@PreDestroy
	private void preDestroy() {

		unsubscribe();
	}

	@Override
	public void update(IChromatogramSelection chromatogramSelection, boolean forceReload) {

		if(chromatogramSelection != null && chromatogramSelection instanceof IChromatogramSelectionMSD) {
			massSpectra = getMassSpectra((IChromatogramSelectionMSD)chromatogramSelection);
			massSpectraStackUI.update(massSpectra, forceReload);
		} else {
			massSpectraStackUI.update(new MassSpectra(), forceReload);
		}
	}
}