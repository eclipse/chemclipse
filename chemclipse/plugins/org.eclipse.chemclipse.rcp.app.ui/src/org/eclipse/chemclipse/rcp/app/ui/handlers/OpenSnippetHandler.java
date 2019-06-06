/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Named;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MInputPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

/**
 * This Handler allows to open a E4 Snippet as an editor, the command must be called with a the {@link OpenSnippetHandler#SNIPPT_PARAMETER} to identify the snippet.
 * It also contains a static {@link OpenSnippetHandler#openSnippet(String, EPartService, MApplication, EModelService)} method to open snippet programmatically.
 * Even though any snippet can be handled by this method, only MParts can be really opened! If you you use a Snippet that is not a {@link MPart}, {@link MInputPart} or {@link MCompositePart} you must take further actions to make it visible
 * 
 * @author Christoph Läubrich
 *
 */
@SuppressWarnings("deprecation")
public class OpenSnippetHandler {

	private static final String SNIPPT_PARAMETER = "org.eclipse.chemclipse.rcp.app.ui.commandparameter.opensnippet.snippetid";

	@Execute
	public void execute(@Named(SNIPPT_PARAMETER) String snippetId, EPartService partService, MApplication application, EModelService modelService) {

		openSnippet(snippetId, partService, application, modelService);
	}

	/**
	 * Open the given snippet
	 * 
	 * @param snippetId
	 * @param partService
	 * @param application
	 * @param modelService
	 * @return the created element
	 */
	public static MUIElement openSnippet(String snippetId, EPartService partService, MApplication application, EModelService modelService) {

		return openSnippet(snippetId, modelService, application, partService, null);
	}

	/**
	 * Clone and open the given snippet
	 * 
	 * @param snippetId
	 *            the snippet id to use
	 * @param modelService
	 *            the modelservice to query, take care that this is a recent, injected and not static cached one or you will get "Application has no active Window" in certain circumstances!
	 * @param application
	 *            the {@link MSnippetContainer} to clone the snippet from, you would most likely use the {@link MApplication} here. For special cases it would be possible to NOT pass the {@link MApplication} here but a wrapper, then the part won't be added to the editors partstack
	 * @param partService
	 *            the partservice to use to open the part, or <code>null</code> if opening is not desired
	 * @param initializer
	 *            optional initializer that is called after the snippet is cloned, this allows special initialization, e.g. putting data into the TransientData map to pass parameters to the part, be aware that the initializer is called before the part is constructed, so no widget or instance will be avaiable, if this is required one must use the returned object
	 * @return the created object
	 */
	public static MUIElement openSnippet(String snippetId, EModelService modelService, MSnippetContainer application, EPartService partService, Consumer<MUIElement> initializer) {

		MUIElement snippet = modelService.cloneSnippet(application, snippetId, null);
		if(snippet != null) {
			snippet.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
			snippet.setElementId(snippetId + "." + UUID.randomUUID().toString());
			if(initializer != null) {
				initializer.accept(snippet);
			}
			if(application instanceof MUIElement) {
				if(snippet instanceof MStackElement) {
					final MPartStack partStack = (MPartStack)modelService.find(IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, (MUIElement)application);
					partStack.getChildren().add((MStackElement)snippet);
				}
			}
			if(partService != null) {
				if(snippet instanceof MPart) {
					MPart part = (MPart)snippet;
					part.setCloseable(true);
					partService.showPart(part, PartState.ACTIVATE);
				}
			}
		}
		return snippet;
	}

	@CanExecute
	public boolean canExecute(@Named(SNIPPT_PARAMETER) String snippetId, MApplication application, EModelService modelService) {

		return modelService.findSnippet(application, snippetId) != null;
	}
}