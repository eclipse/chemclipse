/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.chemclipse.logging.support.Settings;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

@SuppressWarnings({"deprecation", "restriction"})
public class WelcomeView {

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
	 * The button for the MSD perspective is selected by default.
	 */
	private Button buttonMSDPerspective;

	@Inject
	public WelcomeView(Composite parent) {

		initializeContent(parent);
	}

	@Focus
	public void setFocus() {

		buttonMSDPerspective.setFocus();
	}

	private void initializeContent(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		/*
		 * Make the composite able to scroll.
		 */
		final ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
		scrolledComposite.setLayoutData(new GridLayout(1, true));
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Center all elements
		 */
		final Composite composite = new Composite(scrolledComposite, SWT.NONE);
		composite.setData(CSSSWTConstants.CSS_ID_KEY, CSS_ID);
		composite.setLayout(new GridLayout(1, true));
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
		composite.setLayoutData(gridData);
		createContent(composite);
		/*
		 * Set the scrolled composite content.
		 */
		scrolledComposite.setContent(composite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {

				Rectangle rectangle = scrolledComposite.getClientArea();
				scrolledComposite.setMinSize(composite.computeSize(rectangle.width, SWT.DEFAULT));
			}
		});
	}

	private void createContent(Composite parent) {

		/*
		 * Important for a transparent background
		 * of the contained components.
		 */
		parent.setBackgroundMode(SWT.INHERIT_FORCE);
		/*
		 * Create the components.
		 */
		createWelcomeText(parent);
		createPerspectivesInfo(parent);
		createRunDemoButton(parent);
		createInfo(parent);
		createPerspectivesLaunchButtons(parent);
		createAdditionalInfo(parent);
		createLibraryAndPeaksPerspectiveButtons(parent);
		createContactLink(parent);
	}

	private void createWelcomeText(Composite parent) {

		GridData gridData;
		Font welcomeFont = new Font(Display.getCurrent(), "Arial", 18, SWT.BOLD);
		Label welcome = new Label(parent, SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.verticalIndent = 50;
		welcome.setLayoutData(gridData);
		welcome.setFont(welcomeFont);
		/*
		 * Get the application name
		 */
		Properties properties = System.getProperties();
		Object object = properties.get(Settings.D_APPLICATION_NAME);
		String welcomeMessage = "Welcome";
		if(object instanceof String) {
			welcomeMessage = (String)object;
			if(welcomeMessage == null || welcomeMessage.equals("")) {
				welcomeMessage = "Welcome";
			} else {
				/*
				 * White space is not allowed in the config.ini.
				 */
				welcomeMessage = welcomeMessage.replaceAll("_", " ");
			}
		}
		//
		welcome.setText(welcomeMessage);
		welcomeFont.dispose();
	}

	private void createPerspectivesInfo(Composite parent) {

		GridData gridData;
		Image image = Activator.getDefault().getImageRegistry().get(Activator.INFO_PERSPECTIVES);
		Composite logo = new Composite(parent, SWT.NONE);
		gridData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gridData.verticalIndent = 30;
		gridData.verticalAlignment = SWT.BEGINNING;
		gridData.widthHint = image.getBounds().width;
		gridData.heightHint = image.getBounds().height;
		logo.setLayoutData(gridData);
		logo.setBackgroundImage(image);
		// IPerspectiveAndViewIds
		Label welcome = new Label(parent, SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.verticalIndent = 10;
		welcome.setLayoutData(gridData);
		welcome.setText("Use the toolbar to (1) Install Plug-ins (2) Fetch Updates (3) Switch Perspectives (4) Open Views");
	}

	private void createRunDemoButton(Composite parent) {

		Button buttonDemo = new Button(parent, SWT.PUSH);
		GridData gridData = new GridData();
		gridData.verticalIndent = 30;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		buttonDemo.setLayoutData(gridData);
		buttonDemo.setText("Run Demo");
		buttonDemo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_PEAKS_MSD);
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
								MInputPart inputPart = MBasicFactory.INSTANCE.createInputPart();
								inputPart.setElementId("org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramEditor");
								inputPart.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.msd.ui/org.eclipse.chemclipse.ux.extension.msd.ui.editors.ChromatogramEditorMSD");
								inputPart.setInputURI(file.getAbsolutePath());
								inputPart.setIconURI("platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif");
								inputPart.setLabel(file.getName());
								inputPart.setTooltip("Chromatogram - Detector Type: MSD");
								inputPart.setCloseable(true);
								/*
								 * Add it to the stack and show it.
								 */
								partStack.getChildren().add(inputPart);
								partService.showPart(inputPart, PartState.ACTIVATE);
							}
						} catch(MalformedURLException e) {
							System.out.println(e);
						}
					}
				});
			}
		});
	}

	private void createInfo(Composite parent) {

		GridData gridData;
		Label info1 = new Label(parent, SWT.WRAP | SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.TOP, true, false);
		gridData.verticalIndent = 10;
		gridData.verticalAlignment = SWT.BEGINNING;
		info1.setLayoutData(gridData);
		info1.setText("The flexible open source solution for chromatography and mass spectrometry.\r\nIt offers a variety of solutions to analyze chromatographic data.\r\nSo far, the main focus is on mass spectrometric data (MSD).");
	}

	private void createPerspectivesLaunchButtons(Composite parent) {

		GridData gridData;
		//
		buttonMSDPerspective = new Button(parent, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalIndent = 10;
		buttonMSDPerspective.setLayoutData(gridData);
		buttonMSDPerspective.setText("MSD Perspective (MS, MS/MS, ...)");
		buttonMSDPerspective.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_MSD);
			}
		});
		//
		Button buttonCSDPerspective = new Button(parent, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		buttonCSDPerspective.setLayoutData(gridData);
		buttonCSDPerspective.setText("CSD Perspective (FID, ECD, ...)");
		buttonCSDPerspective.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_CSD);
			}
		});
		//
		Button buttonWSDPerspective = new Button(parent, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		buttonWSDPerspective.setLayoutData(gridData);
		buttonWSDPerspective.setText("WSD Perspective (UV/Vis, DAD, ...)");
		buttonWSDPerspective.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_WSD);
			}
		});
	}

	private void createAdditionalInfo(Composite parent) {

		GridData gridData;
		Label info = new Label(parent, SWT.WRAP | SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.verticalIndent = 50;
		info.setLayoutData(gridData);
		info.setText("Several perspectives are offered, focused on different tasks.");
	}

	private void createLibraryAndPeaksPerspectiveButtons(Composite parent) {

		GridData gridData;
		//
		Button buttonSetLibraryPerspective = new Button(parent, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalIndent = 20;
		buttonSetLibraryPerspective.setLayoutData(gridData);
		buttonSetLibraryPerspective.setText("Mass Spectrum / Library Perspective");
		buttonSetLibraryPerspective.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_MS_LIBRARY);
			}
		});
		//
		Button buttonSetPeaksPerspective = new Button(parent, SWT.PUSH);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.CENTER;
		buttonSetPeaksPerspective.setLayoutData(gridData);
		buttonSetPeaksPerspective.setText("Peaks Perspective (MSD)");
		buttonSetPeaksPerspective.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				switchPerspective(IPerspectiveAndViewIds.PERSPECTIVE_PEAKS_MSD);
			}
		});
	}

	private void createContactLink(Composite parent) {

		GridData gridData;
		Label info = new Label(parent, SWT.WRAP | SWT.CENTER);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.verticalIndent = 50;
		info.setLayoutData(gridData);
		info.setText("If you have questions, don't hesitate to contact us.");
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