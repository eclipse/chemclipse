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
 * Christoph Läubrich - remove ModelAddon dependency
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.rcp.app.ui.handlers.PerspectiveSwitchHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

public class CreatePcaEvaluation {

	private static final String EDITOR_ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.part.pcaeditor";
	public static final String PCA_PERSPECTIVE = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.perspective";
	public static final String PCA_EDITOR_STACK = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.stackId.pcaeditorStack";
	public static final String PCA_CREATE_NEW_EDITOR = "CREATE_NEW_EDITOR";

	public static void createPart(ISamplesPCA<?, ?> samples, IEclipseContext context, String title) {

		switchPespective();
		OpenSnippetHandler.openSnippet(EDITOR_ID, context, PCA_EDITOR_STACK, (eclipseContext, part) -> {
			eclipseContext.set(ISamplesPCA.class, samples);
			if(title != null) {
				part.setLabel(title);
			}
			return null;
		});
	}

	@Inject
	@Optional
	public void createNewEditor(@UIEventTopic(PCA_CREATE_NEW_EDITOR) Samples samples, IEclipseContext context) {

		createPart(samples, context, null);
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part, IEclipseContext context) {

		switchPespective();
	}

	private static void switchPespective() {

		if(!PerspectiveSwitchHandler.isActivePerspective(PCA_PERSPECTIVE)) {
			PerspectiveSwitchHandler.focusPerspectiveAndView(PCA_PERSPECTIVE, new ArrayList<>());
		}
	}
}
