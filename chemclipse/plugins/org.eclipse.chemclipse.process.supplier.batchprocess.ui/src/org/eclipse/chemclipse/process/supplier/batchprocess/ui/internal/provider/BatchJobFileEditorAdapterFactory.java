/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Aleksandar Kurtakov - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.ui.internal.provider;

import org.eclipse.chemclipse.process.supplier.batchprocess.ui.provider.BatchJobFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.ui.editors.EditorDescriptor;
import org.eclipse.chemclipse.ux.extension.ui.provider.ISupplierFileEditorSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors.BatchJobFileSupplier;
import org.eclipse.core.runtime.IAdapterFactory;

public class BatchJobFileEditorAdapterFactory implements IAdapterFactory {

	private static final BatchJobFileEditorSupport EDITOR_SUPPORT = new BatchJobFileEditorSupport();

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adaptableObject instanceof BatchJobFileSupplier && (adapterType == ISupplierFileEditorSupport.class || adapterType == EditorDescriptor.class)) {
			return adapterType.cast(EDITOR_SUPPORT);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{ISupplierFileEditorSupport.class, EditorDescriptor.class};
	}
}
