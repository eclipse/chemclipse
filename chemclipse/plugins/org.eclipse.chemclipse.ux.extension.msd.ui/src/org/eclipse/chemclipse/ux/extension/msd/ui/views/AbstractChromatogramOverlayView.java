/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.preferences.PreferenceSupplier;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

public abstract class AbstractChromatogramOverlayView extends AbstractChromatogramSelectionMSDView {

	private EPartService partService;

	public AbstractChromatogramOverlayView(MPart part, EPartService partService, IEventBroker eventBroker) {
		super(part, partService, eventBroker);
		this.partService = partService;
	}

	/**
	 * Returns a list of chromatogram selections.
	 * 
	 * @return List<IChromatogramSelection>
	 */
	public List<IChromatogramSelection> getChromatogramSelections(IChromatogramSelection masterChromatogramSelection) {

		/*
		 * Add the master selection.
		 */
		List<IChromatogramSelection> chromatogramSelections = new ArrayList<IChromatogramSelection>();
		if(masterChromatogramSelection.isOverlaySelected()) {
			chromatogramSelections.add(masterChromatogramSelection);
		}
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
				if(object != null && object instanceof ChromatogramEditorMSD) {
					ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
					IChromatogramSelectionMSD selection = editor.getChromatogramSelection();
					/*
					 * Do not add the master chromatogram selection twice.
					 */
					if(selection != masterChromatogramSelection) {
						if(selection.isOverlaySelected()) {
							chromatogramSelections.add(selection);
						}
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

	/**
	 * Returns an offset instance.
	 * 
	 * @return {@link IOffset}
	 */
	public IOffset getOffset() {

		int xOffset = PreferenceSupplier.getOverlayXOffset();
		int yOffset = PreferenceSupplier.getOverlayYOffset();
		IOffset offset = new Offset(xOffset, yOffset);
		return offset;
	}
}
