/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for loading the demo chromatogram directly, refactor for using the TileDefinition
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PerspectiveSupport;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.graphics.Image;

public class DemoWelcomeTile implements TileDefinition {

	private File chromatogramFile;

	@Execute
	public void handleEvent(EModelService modelService, MApplication application, EPartService partService, PerspectiveSupport perspectiveSupport) {

		perspectiveSupport.changePerspective(WelcomeView.PERSPECTIVE_DATA_ANALYSIS);
		Executors.defaultThreadFactory().newThread(new Runnable() {

			@Override
			public void run() {

				synchronized(DemoWelcomeTile.this) {
					/*
					 * Try to create the chromatogram.
					 */
					if(chromatogramFile == null) {
						try {
							File tempFile = File.createTempFile("DemoChromatogram", ".ocb");
							tempFile.deleteOnExit();
							copyChromatogram(tempFile);
							chromatogramFile = tempFile;
						} catch(Exception e) {
							e.printStackTrace();
							return;
						}
					}
					/*
					 * Open the chromatogram Editor.
					 */
					DisplayUtils.getDisplay().syncExec(new Runnable() {

						@Override
						public void run() {

							try {
								if(chromatogramFile != null && chromatogramFile.exists()) {
									MPart part = createChromatogramPart();
									part.setLabel("DemoChromatogram.ocb");
									Map<String, Object> map = new HashMap<String, Object>();
									map.put(EditorSupport.MAP_FILE, chromatogramFile.getAbsolutePath());
									map.put(EditorSupport.MAP_BATCH, false);
									part.setObject(map);
									part.setTooltip("Demo Chromatogram (MSD)");
									showEditorPart(part, modelService, application, partService);
								}
							} catch(Exception e) {
								e.printStackTrace();
								return;
							}
						}
					});
				}
			}
		}).start();
	}

	void showEditorPart(MPart part, EModelService modelService, MApplication application, EPartService partService) {

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

	private void copyChromatogram(File file) throws IOException {

		try (InputStream stream = DemoWelcomeTile.class.getResourceAsStream("/files/DemoChromatogram.ocb")) {
			try (FileOutputStream fout = new FileOutputStream(file)) {
				byte[] buffer = new byte[1024];
				int read;
				while((read = stream.read(buffer)) > -1) {
					fout.write(buffer, 0, read);
				}
				fout.flush();
			}
		}
	}

	@Override
	public String getTitle() {

		return "Demo";
	}

	@Override
	public String getDescription() {

		return "Load a demo chromatogram.";
	}

	@Override
	public Image getIcon() {

		return null;
	}

	@Override
	public boolean isDefaultShow() {

		return false;
	}

	@Override
	public String getContext() {

		return WelcomeView.WELCOME_MAIN_CONTEXT;
	}
}
