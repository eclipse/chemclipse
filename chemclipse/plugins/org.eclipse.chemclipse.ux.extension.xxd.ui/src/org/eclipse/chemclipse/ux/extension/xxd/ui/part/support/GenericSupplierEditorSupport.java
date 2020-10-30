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
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import java.io.File;
import java.util.Collection;
import java.util.function.Supplier;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;

public class GenericSupplierEditorSupport implements ISupplierFileEditorSupport {

	private static final Object NO_EXECUTE_METHOD = new Object();
	//
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

		IEclipseContext eclipseContext = contextSupplier.get();
		IEclipseContext parameterContext = EclipseContextFactory.create();
		try {
			parameterContext.set(File.class, file);
			parameterContext.set(ISupplier.class, supplier);
			Object[] executables = {Adapters.adapt(supplier, EditorDescriptor.class), supplier};
			for(Object executable : executables) {
				if(executable == null) {
					continue;
				}
				Object invoke = ContextInjectionFactory.invoke(executable, Execute.class, eclipseContext, parameterContext, NO_EXECUTE_METHOD);
				if(NO_EXECUTE_METHOD != invoke) {
					if(invoke instanceof Boolean) {
						return ((Boolean)invoke).booleanValue();
					}
					return true;
				}
			}
		} finally {
			parameterContext.dispose();
		}
		return false;
	}
}
