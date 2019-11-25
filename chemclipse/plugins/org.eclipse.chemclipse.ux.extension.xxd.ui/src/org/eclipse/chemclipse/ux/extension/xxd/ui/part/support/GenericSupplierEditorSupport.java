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
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.io.File;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public class GenericSupplierEditorSupport implements ISupplierFileEditorSupport {

	private final Supplier<IEclipseContext> contextSupplier;
	private final ISupplierFileIdentifier fileIdentifier;

	public GenericSupplierEditorSupport(ISupplierFileIdentifier fileIdentifier, Supplier<IEclipseContext> contextSupplier) {
		this.fileIdentifier = fileIdentifier;
		this.contextSupplier = contextSupplier;
	}

	@Override
	public String getType() {

		return "Generic";
	}

	@Override
	public boolean isSupplierFile(File file) {

		return false;
	}

	@Override
	public Collection<ISupplier> getSupplier(File file) {

		return fileIdentifier.getSupplier(file);
	}

	@Override
	public Collection<ISupplier> getSupplier() {

		return fileIdentifier.getSupplier();
	}

	@Override
	public boolean openEditor(File file, boolean batch) {

		return false;
	}

	@Override
	public boolean openEditor(File file, ISupplier supplier) {

		EditorDescriptor descriptor = Adapters.adapt(supplier, EditorDescriptor.class);
		if(descriptor != null) {
			OpenSnippetHandler.openSnippet(descriptor.getEditorId(), contextSupplier.get(), new BiFunction<IEclipseContext, MPart, Runnable>() {

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
		return false;
	}
}
