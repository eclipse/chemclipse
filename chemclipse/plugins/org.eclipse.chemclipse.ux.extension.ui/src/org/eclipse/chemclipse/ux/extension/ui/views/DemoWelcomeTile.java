/*******************************************************************************
 * Copyright (c) 2013, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add support for loading the demo chromatogram directly, refactor for using the TileDefinition
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.chemclipse.support.ui.workbench.EditorSupport;
import org.eclipse.chemclipse.support.ui.workbench.PerspectiveSupport;
import org.eclipse.chemclipse.ux.extension.ui.definitions.IWelcomeTileDefinition;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class DemoWelcomeTile implements IWelcomeTileDefinition {

	private File chromatogramFile;

	@Execute
	public void handleEvent(EModelService modelService, MApplication application, EPartService partService, PerspectiveSupport perspectiveSupport) {

		perspectiveSupport.changePerspective(WelcomeView.PERSPECTIVE_DATA_ANALYSIS);
		Executors.defaultThreadFactory().newThread(() -> {

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
				Display.getDefault().syncExec(() -> {

					try {
						if(chromatogramFile != null && chromatogramFile.exists()) {
							MPart part = createChromatogramPart(modelService);
							part.setLabel("DemoChromatogram.ocb");
							Map<String, Object> map = new HashMap<>();
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
				});
			}
		}).start();
	}

	void showEditorPart(MPart part, EModelService modelService, MApplication application, EPartService partService) {

		/*
		 * Get the editor part stack.
		 */
		MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, application);
		/*
		 * Add it to the stack and show it.
		 */
		partStack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}

	MPart createChromatogramPart(EModelService modelService) {

		/*
		 * Create the input part and prepare it.
		 */
		MPart part = modelService.createModelElement(MPart.class);
		part.setElementId("org.eclipse.chemclipse.ux.extension.xxd.ui.part.chromatogramEditorMSD");
		part.setContributionURI("bundleclass://org.eclipse.chemclipse.ux.extension.xxd.ui/org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ChromatogramEditorMSD");
		part.setIconURI(ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_CHROMATOGRAM, IApplicationImageProvider.SIZE_16x16));
		part.setCloseable(true);
		return part;
	}

	private void copyChromatogram(File file) throws IOException {

		try (InputStream stream = DemoWelcomeTile.class.getResourceAsStream("/files/DemoChromatogram.ocb")) {
			Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
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
	public String getContext() {

		return WelcomeView.WELCOME_MAIN_CONTEXT;
	}
}
