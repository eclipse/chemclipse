/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - fix bug with local perspectives
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTile;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class WelcomeView {

	public static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	private static final String PERSPECTIVE_PCA = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	private static final String PERSPECTIVE_LOGGING = "org.eclipse.chemclipse.logging.ui.perspective.main";
	private static final int NUMBER_OF_COLUMNS = Integer.getInteger("chemclipse.welcome.columns", 4);
	private static final boolean EXTENSIONS_ONLY = Boolean.getBoolean("chemclipse.welcome.simple");
	//
	// private static final String CSS_ID = "org-eclipse-chemclipse-ux-extension-ui-views-welcomeview-background";
	/*
	 * Context and services
	 */
	@Inject
	private MApplication application;
	@Inject
	private EModelService modelService;
	@Inject
	private EPartService partService;
	@Inject
	private IEventBroker eventBroker;
	/*
	 * Main
	 */
	private List<TaskTile> welcomeTiles;

	private class Component implements ISelectionHandler {

		private String perspectiveId = "";

		public Component(String perspectiveId) {
			this.perspectiveId = perspectiveId;
		}

		@Override
		public void handleEvent() {

			if(perspectiveId != null && !"".equals(perspectiveId)) {
				switchPerspective(perspectiveId);
			}
		}
	}

	@Inject
	public WelcomeView(Composite parent) {
		welcomeTiles = new ArrayList<TaskTile>();
		initializeContent(parent);
	}

	@Focus
	public void setFocus() {

	}

	private void initializeContent(Composite parent) {

		parent.setLayout(new FillLayout());
		//
		Composite composite = new Composite(parent, SWT.NONE);
		// composite.setData(CSSSWTConstants.CSS_ID_KEY, CSS_ID);
		// composite.setBackground(Colors.WHITE);
		composite.setLayout(new GridLayout(NUMBER_OF_COLUMNS, false));
		/*
		 * Important for a transparent background
		 * of the contained components.
		 */
		composite.setBackgroundMode(SWT.INHERIT_FORCE);
		if(!EXTENSIONS_ONLY) {
			/*
			 * Default Tiles
			 */
			Image imageDataAnalysis = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_DATA_ANALYSIS, IApplicationImage.SIZE_128x128);
			initializeTile(new TaskTile(composite, TaskTile.HIGHLIGHT), 2, 2, new Component(PERSPECTIVE_DATA_ANALYSIS), imageDataAnalysis, "Data Analysis", "This is the main perspective. Most of the work is performed here.");
			initializeTile(new TaskTile(composite, TaskTile.HIGHLIGHT), 1, 1, new Component(PERSPECTIVE_PCA), null, "PCA", "Used for principal component analysis");
			initializeTile(new TaskTile(composite, TaskTile.HIGHLIGHT), 1, 1, new Component(PERSPECTIVE_LOGGING), null, "Logging", "Have a look at the log files.");
			initializeTile(new TaskTile(composite, TaskTile.HIGHLIGHT), 2, 1, new DemoWelcomeTile(this), null, "Demo", "Load a demo chromatogram.");
		}
		/*
		 * Registered Tiles
		 */
		new WelcomeViewExtensionHandler(composite, this);
	}

	private void initializeTile(TaskTile welcomeTile, int horizontalSpan, int verticalSpan, ISelectionHandler selectionHandler, Image image, String section, String description) {

		welcomeTile.setSelectionHandler(selectionHandler);
		welcomeTile.setContent(image, section, description);
		addWelcomeTile(welcomeTile);
		GridData gridData = (GridData)welcomeTile.getLayoutData();
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
	}

	void addWelcomeTile(TaskTile welcomeTile) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		welcomeTile.setLayoutData(gridData);
		welcomeTile.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {

				highlightComposite(welcomeTile);
			}
		});
		welcomeTiles.add(welcomeTile);
	}

	private void highlightComposite(TaskTile welcomeTileHighlight) {

		for(TaskTile welcomeTile : welcomeTiles) {
			if(welcomeTile == welcomeTileHighlight) {
				welcomeTile.setActive();
			} else {
				welcomeTile.setInactive();
			}
		}
	}

	/**
	 * The method is available in the org.eclipse.chemclipse.rcp.app.ui package too,
	 * but it can't be referenced, because app.ui depends on the logging
	 * package.
	 * Furthermore, this is only a workaround, because setting an initial
	 * perspective doesn't work.
	 */
	void switchPerspective(String perspectiveId) {

		MPerspective model = getPerspectiveModel(perspectiveId);
		if(model != null) {
			partService.switchPerspective(model);
			if(eventBroker != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, model.getLabel());
			}
		}
	}

	MPart createChromatogramPart() {

		/*
		 * Create the input part and prepare it.
		 */
		MPart part = MBasicFactory.INSTANCE.createInputPart();
		part.setElementId("org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramEditorMSD");
		part.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorMSD");
		part.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif");
		part.setCloseable(true);
		return part;
	}

	void showEditorPart(MPart part) {

		/*
		 * Get the editor part stack.
		 */
		MPartStack partStack = (MPartStack)modelService.find("org.eclipse.e4.primaryDataStack", application);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}

	MPerspective getPerspectiveModel(String perspectiveId) {

		if(perspectiveId != null) {
			List<MPerspective> elements = modelService.findElements(application, null, MPerspective.class, null);
			if(elements != null && !elements.isEmpty()) {
				for(MPerspective perspective : elements) {
					String elementId = perspective.getElementId();
					String elementLabel = perspective.getLabel();
					if(perspectiveId.equals(elementId) || elementId.equals(perspectiveId + "." + elementLabel)) {
						return perspective;
					}
				}
			}
		}
		return null;
	}
}