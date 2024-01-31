/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.editors;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.supplier.AbstractProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierFactory;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.osgi.service.component.annotations.Component;

import jakarta.inject.Inject;

@Component(service = {IProcessTypeSupplier.class})
public class EditorProcessTypeSupplier implements IProcessTypeSupplier {

	@Override
	public String getCategory() {

		return ICategories.USER_INTERFACE;
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		return Collections.singleton(new OpenEditorSupplier(this));
	}

	private static final class OpenEditorSupplier extends AbstractProcessSupplier<Void> implements IChromatogramSelectionProcessSupplier<Void> {

		@Inject
		IEclipseContext eclipseContext;
		@Inject
		UISynchronize ui;

		public OpenEditorSupplier(IProcessTypeSupplier parent) {

			super("org.eclipse.chemclipse.ux.extension.xxd.ui.editors.EditorProcessTypeSupplier.OpenEditorSupplier", "Open Editor", "Opens an editor with the given dataset", null, parent, DataCategory.MSD, DataCategory.CSD, DataCategory.WSD);
		}

		@Override
		public IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, Void processSettings, ProcessExecutionContext context) {

			if(eclipseContext == null) {
				context.addErrorMessage(getName(), "This supplier requires ContextInjection to work properly");
			} else {
				ui.asyncExec(new Runnable() {

					@Override
					public void run() {

						IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
						if(chromatogram instanceof IChromatogramMSD) {
							new SupplierEditorSupport(DataType.MSD, () -> eclipseContext).openEditor(chromatogram);
						} else if(chromatogram instanceof IChromatogramWSD) {
							new SupplierEditorSupport(DataType.WSD, () -> eclipseContext).openEditor(chromatogram);
						} else if(chromatogram instanceof IChromatogramCSD) {
							new SupplierEditorSupport(DataType.CSD, () -> eclipseContext).openEditor(chromatogram);
						}
					}
				});
			}
			return chromatogramSelection;
		}

		@ProcessSupplierFactory
		public OpenEditorSupplier createInjectionInstance() {

			return new OpenEditorSupplier(getTypeSupplier());
		}
	}
}
