/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - migrate from InputPart to Part
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.editors.IChromatogramEditor;
import org.eclipse.chemclipse.ux.extension.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.ISpectrumXIR;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Shell;

public interface ISupplierFileEditorSupport extends ISupplierFileIdentifier {

	default boolean openEditor(final File file) {

		return openEditor(file, false);
	}

	boolean openEditor(final File file, boolean batch);

	boolean openEditor(File file, ISupplier supplier);

	default void openOverview(final File file) {

	}

	default void openEditor(File file, Object object, String elementId, String contributionURI, String iconURI, String tooltip) {

		openEditor(file, object, elementId, contributionURI, iconURI, tooltip, false);
	}

	default void openEditor(File file, Object object, String elementId, String contributionURI, String iconURI, String tooltip, boolean batch) {

		EModelService modelService = Activator.getDefault().getModelService();
		MApplication application = Activator.getDefault().getApplication();
		EPartService partService = Activator.getDefault().getPartService();
		//
		if(modelService != null && application != null && partService != null) {
			/*
			 * Check if editor is already open?
			 */
			boolean openEditor = true;
			if(file != null) {
				boolean sourceIsDirectory = file.isDirectory();
				if(!PreferenceSupplier.isOpenEditorMultipleTimes()) {
					List<MPart> parts = modelService.findElements(application, null, MPart.class, null);
					if(parts != null) {
						exitloop:
						for(MPart part : parts) {
							Object editor = part.getObject();
							if(editor instanceof IChromatogramEditor chromatogramEditor) {
								File fileEditor = chromatogramEditor.getChromatogramSelection().getChromatogram().getFile();
								if(sourceIsDirectory) {
									/*
									 * Some chromatograms are stored in directories.
									 */
									if(fileEditor.getAbsolutePath().startsWith(file.getAbsolutePath())) {
										openEditor = false;
										break exitloop;
									}
								} else {
									/*
									 * Compare File
									 */
									if(fileEditor.equals(file)) {
										openEditor = false;
										break exitloop;
									}
								}
							}
						}
					}
				}
			}
			/*
			 * Try to open the editor.
			 */
			if(openEditor) {
				/*
				 * Fix for: "Application does not have an active window"
				 * in org.eclipse.e4.ui.internal.workbench.ApplicationPartServiceImpl
				 */
				MWindow window = application.getChildren().get(0);
				application.getContext().set("activeChildContext", window.getContext()); // EclipseContext.ACTIVE_CHILD
				/*
				 * Get the editor part stack.
				 */
				MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
				/*
				 * Create the input part and prepare it.
				 */
				MPart part = MBasicFactory.INSTANCE.createPart();
				part.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
				part.setElementId(elementId);
				part.setContributionURI(contributionURI);
				/*
				 * File or chromatogram/mass spectra are maybe null.
				 */
				if(file == null) {
					if(object != null) {
						part.setObject(object);
						if(object instanceof IChromatogram<?> chromatogram) {
							String type = "";
							if(object instanceof IChromatogramMSD) {
								type = " [MSD]";
							} else if(object instanceof IChromatogramCSD) {
								type = " [CSD]";
							} else if(object instanceof IChromatogramWSD) {
								type = " [WSD]";
							} else if(object instanceof IChromatogramISD) {
								type = " [ISD]";
							}
							part.setLabel(chromatogram.getName() + type);
						} else if(object instanceof IMassSpectra massSpectra) {
							part.setLabel(massSpectra.getName());
						} else if(object instanceof ISpectrumXIR) {
							part.setLabel("FTIR");
						} else if(object instanceof IMeasurement measurement) {
							part.setLabel(measurement.getDataName());
						}
					} else {
						part.setObject(null);
						part.setLabel("No valid chromatogram/mass spectra data");
					}
				} else {
					/*
					 * Get the file to load via the map.
					 */
					Map<String, Object> map = new HashMap<>();
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
				((Shell)window.getWidget()).forceFocus();
			}
		}
	}
}