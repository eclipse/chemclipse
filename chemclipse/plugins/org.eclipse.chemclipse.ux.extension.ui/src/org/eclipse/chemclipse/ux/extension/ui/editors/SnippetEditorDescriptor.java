/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.editors;

import java.io.File;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface SnippetEditorDescriptor extends EditorDescriptor {

	/**
	 * 
	 * @return the ID of the editors
	 */
	String getEditorSnippetId();

	@Execute
	default boolean openEditor(File file, ISupplier supplier, IEclipseContext eclipseContext) {

		OpenSnippetHandler.openSnippet(getEditorSnippetId(), eclipseContext, new BiFunction<IEclipseContext, MPart, Runnable>() {

			@Override
			public Runnable apply(IEclipseContext context, MPart part) {

				part.setLabel(file.getName());
				context.set(File.class, file);
				context.set(ISupplier.class, supplier);
				return null;
			}
		});
		return true;
	}
}
