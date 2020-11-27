/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.util.function.BiFunction;

import javax.inject.Named;

import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategoryGroup;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.DataTypeTypeSelectionWizard;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

public class CreateProcessMethodHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext eclipseContext) {

		/*
		 * Update the context to PartServiceImpl.
		 */
		Activator.getDefault().updateEPartService(eclipseContext.get(EPartService.class));
		//
		DataCategoryGroup group = DataTypeTypeSelectionWizard.open(shell, "Please choose the desired categories to create a new method for", Activator.getDefault().getPreferenceStore());
		OpenSnippetHandler.openSnippet(ProcessMethodEditor.SNIPPET_ID, eclipseContext, new BiFunction<IEclipseContext, MPart, Runnable>() {

			@Override
			public Runnable apply(IEclipseContext context, MPart part) {

				context.set(IProcessMethod.class, new ProcessMethod(group.getDataCategories()));
				return null;
			}
		});
	}
}