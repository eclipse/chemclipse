/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - fix bug with local perspectives, refactor for using TaskTileContainer
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.workbench.PerspectiveSupport;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTile;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTileContainer;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class WelcomeView {

	public static final String WELCOME_MAIN_CONTEXT = "WelcomeView.Main";
	public static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	//
	private static final String PERSPECTIVE_PCA = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	private static final String PERSPECTIVE_LOGGING = "org.eclipse.chemclipse.logging.ui.perspective.main";
	private static final int DEFAULT_NUMBER_OF_COLUMNS = Integer.getInteger("chemclipse.welcome.columns", 4);
	@Inject
	private IEclipseContext eclipseContext;

	private class Component implements TileDefinition {

		private String perspectiveId = "";
		private Image image;
		private String section;
		private String description;

		public Component(String perspectiveId, Image image, String section, String description) {

			this.perspectiveId = perspectiveId;
			this.image = image;
			this.section = section;
			this.description = description;
		}

		@Execute
		public void execute(PerspectiveSupport perspectiveSupport) {

			if(perspectiveId != null && !"".equals(perspectiveId)) {
				perspectiveSupport.changePerspective(perspectiveId);
			}
		}

		@Override
		public String getTitle() {

			return section;
		}

		@Override
		public String getDescription() {

			return description;
		}

		@Override
		public Image getIcon() {

			return image;
		}

		@Override
		public String getContext() {

			return WELCOME_MAIN_CONTEXT;
		}
	}

	@Focus
	public void setFocus() {

	}

	@PostConstruct
	public void initializeContent(Composite parent) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		preferenceStore.setDefault(WelcomeViewExtensionHandler.PREFERENCE_MIN_TILES, DEFAULT_NUMBER_OF_COLUMNS);
		preferenceStore.setDefault(WelcomeViewExtensionHandler.PREFERENCE_ALWAYS_CHANGE_PERSPECTIVE, true);
		int cols = preferenceStore.getInt(WelcomeViewExtensionHandler.PREFERENCE_MIN_TILES);
		TaskTileContainer tileContainer = new TaskTileContainer(parent, cols, () -> eclipseContext);
		parent.setLayout(new FillLayout());
		Image imageDataAnalysis = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_DATA_ANALYSIS, IApplicationImage.SIZE_128x128);
		resizeTile(2, 2, tileContainer.addTaskTile(new Component(PERSPECTIVE_DATA_ANALYSIS, imageDataAnalysis, "Data Analysis", "This is the main perspective. Most of the work is performed here.")));
		resizeTile(cols - 3, 1, tileContainer.addTaskTile(new Component(PERSPECTIVE_PCA, null, "PCA", "Used for principal component analysis")));
		resizeTile(cols - 3, 1, tileContainer.addTaskTile(new Component(PERSPECTIVE_LOGGING, null, "Logging", "Have a look at the log files.")));
		resizeTile(cols - 2, 1, tileContainer.addTaskTile(new DemoWelcomeTile()));
		new WelcomeViewExtensionHandler(tileContainer, preferenceStore, "");
	}

	private void resizeTile(int horizontalSpan, int verticalSpan, TaskTile welcomeTile) {

		GridData gridData = (GridData)welcomeTile.getLayoutData();
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
	}
}