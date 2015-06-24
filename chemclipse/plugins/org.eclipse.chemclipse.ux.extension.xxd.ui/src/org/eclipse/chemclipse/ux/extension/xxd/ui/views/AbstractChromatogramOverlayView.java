/*******************************************************************************
 * Copyright (c) 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.swt.ui.support.IOffset;
import org.eclipse.chemclipse.swt.ui.support.Offset;
import org.eclipse.chemclipse.ux.extension.csd.ui.editors.ChromatogramEditorCSD;
import org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD;
import org.eclipse.chemclipse.ux.extension.ui.views.AbstractChromatogramSelectionView;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;

@SuppressWarnings("deprecation")
public abstract class AbstractChromatogramOverlayView extends AbstractChromatogramSelectionView {

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
		chromatogramSelections.add(masterChromatogramSelection);
		/*
		 * Get all open parts.
		 */
		Collection<MPart> parts = partService.getParts();
		for(MPart part : parts) {
			if(part instanceof MInputPart && (part.getElementId().equals(ChromatogramEditorMSD.ID) || part.getElementId().equals(ChromatogramEditorCSD.ID))) {
				/*
				 * Select the chromatogram editor parts only.
				 */
				Object object = part.getObject();
				if(object != null) {
					/*
					 * MSD/FID
					 */
					IChromatogramSelection selection = null;
					if(object instanceof ChromatogramEditorMSD) {
						ChromatogramEditorMSD editor = (ChromatogramEditorMSD)object;
						selection = editor.getChromatogramSelection();
					} else if(object instanceof ChromatogramEditorCSD) {
						ChromatogramEditorCSD editor = (ChromatogramEditorCSD)object;
						selection = editor.getChromatogramSelection();
					}
					/*
					 * Do not add the master chromatogram selection twice.
					 */
					if(selection != null && selection != masterChromatogramSelection) {
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

	/**
	 * Returns an offset instance.
	 * 
	 * @return {@link IOffset}import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
	 */
	public IOffset getOffset() {

		int xOffset = PreferenceSupplier.getOverlayXOffset();
		int yOffset = PreferenceSupplier.getOverlayYOffset();
		IOffset offset = new Offset(xOffset, yOffset);
		return offset;
	}
}
