/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.addons;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.activator.ContextAddon;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.GroupHandler;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Display;

public class PerspectiveSupport {

	private static final Logger logger = Logger.getLogger(PerspectiveSupport.class);
	/*
	 * This flag is used to show a set of parts initially
	 * in the Data Analysis perspective.
	 */
	private static final String DATA_ANALYSIS_PERSPECTIVE_LABEL = "<Data Analysis (Main)>";
	private static final String TOOLBAR_ID = "org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar.dataanalysis";
	private static boolean activatePartsInitially = true;

	@PostConstruct
	public void postConstruct() {

		logger.info("PerspectiveSupport has been activated.");
		handleToolbarUpdates(Activator.getDefault().getDataUpdateSupport());
	}

	private void handleToolbarUpdates(DataUpdateSupport dataUpdateSupport) {

		dataUpdateSupport.add(new IDataUpdateListener() {

			@Override
			public void update(String topic, List<Object> objects) {

				if(topic.equals(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE)) {
					Object object = objects.get(0);
					if(object instanceof String) {
						String label = (String)object;
						if(DATA_ANALYSIS_PERSPECTIVE_LABEL.equals(label)) {
							/*
							 * Show parts initially.
							 */
							enableToolBar(true);
							if(activatePartsInitially) {
								GroupHandler.activateReferencedParts();
								activatePartsInitially = false;
							}
						} else {
							enableToolBar(false);
						}
						GroupHandler.updateGroupHandlerMenu();
					}
				} else if(topic.equals(IChemClipseEvents.TOPIC_PART_CLOSED)) {
					Object object = objects.get(0);
					logger.info("Part has been closed: " + object);
					GroupHandler.updateGroupHandlerMenu();
				}
			}
		});
	}

	private static void enableToolBar(boolean show) {

		EModelService modelService = ContextAddon.getModelService();
		MApplication application = ContextAddon.getApplication();
		//
		if(modelService != null && application != null) {
			Display display = Display.getDefault();
			display.asyncExec(new Runnable() {

				@Override
				public void run() {

					MToolBar toolBar = PartSupport.getToolBar(TOOLBAR_ID, modelService, application);
					toolBar.setToBeRendered(show);
					toolBar.setVisible(show);
				}
			});
		}
	}
}
