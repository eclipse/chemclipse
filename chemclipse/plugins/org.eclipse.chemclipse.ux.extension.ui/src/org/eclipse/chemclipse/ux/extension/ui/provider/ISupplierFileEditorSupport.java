/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xir.model.core.IScanXIR;
import org.eclipse.e4.core.internal.contexts.EclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

@SuppressWarnings({"restriction"})
public interface ISupplierFileEditorSupport extends ISupplierFileIdentifier {

	default boolean openEditor(final File file) {

		return openEditor(file, false);
	}

	@Deprecated
	boolean openEditor(final File file, boolean batch);

	default void openOverview(final File file) {

	}

	default void openEditor(File file, Object object, String elementId, String contributionURI, String iconURI, String tooltip) {

		openEditor(file, object, elementId, contributionURI, iconURI, tooltip, false);
	}

	@SuppressWarnings("rawtypes")
	@Deprecated
	default void openEditor(File file, Object object, String elementId, String contributionURI, String iconURI, String tooltip, boolean batch) {

		EModelService modelService = ModelSupportAddon.getModelService();
		MApplication application = ModelSupportAddon.getApplication();
		EPartService partService = ModelSupportAddon.getPartService();
		/*
		 * Fix for: "Application does not have an active window"
		 * in org.eclipse.e4.ui.internal.workbench.ApplicationPartServiceImpl
		 */
		MWindow window = application.getChildren().get(0);
		application.getContext().set(EclipseContext.ACTIVE_CHILD, window.getContext());
		/*
		 * Get the editor part stack.
		 */
		MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
		/*
		 * Create the input part and prepare it.
		 */
		MPart part = MBasicFactory.INSTANCE.createInputPart();
		part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
		part.setElementId(elementId);
		part.setContributionURI(contributionURI);
		/*
		 * File or chromatogram/mass spectra are maybe null.
		 */
		if(file == null) {
			if(object != null) {
				part.setObject(object);
				if(object instanceof IChromatogram) {
					String type = "";
					if(object instanceof IChromatogramMSD) {
						type = " [MSD]";
					} else if(object instanceof IChromatogramCSD) {
						type = " [CSD]";
					} else if(object instanceof IChromatogramWSD) {
						type = " [WSD]";
					}
					part.setLabel(((IChromatogram)object).getName() + type);
				} else if(object instanceof IMassSpectra) {
					part.setLabel(((IMassSpectra)object).getName());
				} else if(object instanceof IScanXIR) {
					part.setLabel("FTIR");
				} else if(object instanceof IMeasurement) {
					part.setLabel(((IMeasurement)object).getDataName());
				}
			} else {
				part.setObject(null);
				part.setLabel("No valid chromatogram/mass spectra data");
			}
		} else {
			/*
			 * Get the file to load via the map.
			 */
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(EditorSupport.MAP_FILE, file.getAbsolutePath());
			map.put(EditorSupport.MAP_BATCH, batch);
			part.setObject(map);
			part.setLabel(file.getName());
		}
		//
		part.setIconURI(iconURI);
		part.setTooltip(tooltip);
		part.setCloseable(true);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}
}
