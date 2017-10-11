/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.File;
import java.net.URL;

import javax.inject.Inject;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings("restriction")
public class WelcomeView {

	private static final String PERSPECTIVE_DATA_ANALYSIS = "org.eclipse.chemclipse.ux.extension.xxd.ui.perspective.main";
	private static final String PERSPECTIVE_QUANTITATION = "org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.perspective";
	private static final String PERSPECTIVE_LOGGING = "org.eclipse.chemclipse.logging.ui.perspective.main";
	private static final String PERSPECTIVE_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.perspective.main";
	private static final String PERSPECTIVE_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.perspective.main";
	private static final String PERSPECTIVE_WSD = "org.eclipse.chemclipse.ux.extension.wsd.ui.perspective.main";
	//
	private static final String CSS_ID = "org-eclipse-chemclipse-ux-extension-ui-views-welcomeview-background";
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
	private Composite compositeDataAnalysis;

	private interface ISelectionHandler {

		public void handleEvent();
	}

	private class Component1 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_DATA_ANALYSIS);
		}
	}

	private class Component2 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_QUANTITATION);
		}
	}

	private class Component3 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_LOGGING);
		}
	}

	private class Component4 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_DATA_ANALYSIS);
			Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {

					try {
						URL url = new URL(Platform.getInstallLocation().getURL() + "DemoChromatogram.ocb");
						File file = new File(url.getFile());
						if(file.exists()) {
							/*
							 * Get the editor part stack.
							 */
							MPartStack partStack = (MPartStack)modelService.find("org.eclipse.e4.primaryDataStack", application);
							/*
							 * Create the input part and prepare it.
							 */
							MPart part = MBasicFactory.INSTANCE.createInputPart();
							part.setElementId("org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramEditor");
							part.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD");
							part.setObject(file.getAbsolutePath());
							part.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif");
							part.setLabel(file.getName());
							part.setTooltip("Chromatogram - Detector Type: MSD");
							part.setCloseable(true);
							/*
							 * Add it to the stack and show it.
							 */
							partStack.getChildren().add(part);
							partService.showPart(part, PartState.ACTIVATE);
						}
					} catch(Exception e) {
						System.out.println(e);
					}
				}
			});
		}
	}

	private class Component5 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_MSD);
		}
	}

	private class Component6 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_CSD);
		}
	}

	private class Component7 implements ISelectionHandler {

		@Override
		public void handleEvent() {

			switchPerspective(PERSPECTIVE_WSD);
		}
	}

	@Inject
	public WelcomeView(Composite parent) {
		initializeContent(parent);
	}

	@Focus
	public void setFocus() {

		compositeDataAnalysis.setFocus();
	}

	private void initializeContent(Composite parent) {

		parent.setLayout(new FillLayout());
		//
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setData(CSSSWTConstants.CSS_ID_KEY, CSS_ID);
		composite.setLayout(new GridLayout(4, false));
		//
		createContent(composite);
	}

	private void createContent(Composite parent) {

		/*
		 * Important for a transparent background
		 * of the contained components.
		 */
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		//
		Image imageDataAnalysis = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_DATA_ANALYSIS, IApplicationImage.SIZE_128x128);
		Image imageQuant = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_QUANT, IApplicationImage.SIZE_128x128);
		Image imageLogging = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_LOGGING, IApplicationImage.SIZE_128x128);
		Image imageDemo = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_DEMO, IApplicationImage.SIZE_128x128);
		Image imageMSD = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_MSD, IApplicationImage.SIZE_128x128);
		Image imageCSD = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_CSD, IApplicationImage.SIZE_128x128);
		Image imageWSD = ApplicationImageFactory.getInstance().getImage(IApplicationImage.PICTOGRAM_WSD, IApplicationImage.SIZE_128x128);
		//
		Color color1 = Colors.getColor(74, 142, 142);
		Color color2 = Colors.getColor(151, 189, 189);
		Color color3 = Colors.getColor(204, 222, 222);
		//
		compositeDataAnalysis = createComposite(parent, new Component1(), "Data Analysis", imageDataAnalysis, color2, 2, 2);
		createComposite(parent, new Component2(), "Quantitation", imageQuant, color3, 1, 1);
		createComposite(parent, new Component3(), "Logging", imageLogging, color3, 1, 1);
		createComposite(parent, new Component4(), "Demo", imageDemo, color2, 2, 1);
		createComposite(parent, new Component5(), "MSD", imageMSD, color1, 2, 1);
		createComposite(parent, new Component6(), "CSD", imageCSD, color1, 1, 1);
		createComposite(parent, new Component7(), "WSD", imageWSD, color1, 1, 1);
	}

	private Composite createComposite(Composite parent, ISelectionHandler selectionHandler, String tooltip, Image image, Color color, int horizontalSpan, int verticalSpan) {

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setToolTipText(tooltip);
		composite.setBackground(color);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = horizontalSpan;
		gridData.verticalSpan = verticalSpan;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, true));
		composite.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				selectionHandler.handleEvent();
			}
		});
		composite.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				selectionHandler.handleEvent();
			}
		});
		//
		Label labelImage = new Label(composite, SWT.CENTER);
		labelImage.setToolTipText(tooltip);
		labelImage.setImage(image);
		labelImage.setLayoutData(getGridDataImage());
		labelImage.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {

				selectionHandler.handleEvent();
			}
		});
		labelImage.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				selectionHandler.handleEvent();
			}
		});
		//
		return composite;
	}

	private GridData getGridDataImage() {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;
		return gridData;
	}

	/**
	 * The method is available in the org.eclipse.chemclipse.rcp.app.ui package too,
	 * but it can't be referenced, because app.ui depends on the logging
	 * package.
	 * Furthermore, this is only a workaround, because setting an initial
	 * perspective doesn't work.
	 */
	private void switchPerspective(String perspectiveId) {

		MUIElement element = modelService.find(perspectiveId, application);
		if(element instanceof MPerspective) {
			MPerspective perspective = (MPerspective)element;
			partService.switchPerspective(perspective);
			if(eventBroker != null) {
				eventBroker.send(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, perspective.getLabel());
			}
		}
	}
}