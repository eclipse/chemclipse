/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring Windows local file support
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.internal.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.SupplierContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

// DISABLED FOR NOW @Component(service = {SupplierContext.class})
public class EclipseSupplierFileIdentifier implements SupplierContext {

	private Collection<ISupplier> list;

	@Override
	public Collection<ISupplier> getSupplier() {

		if(list == null) {
			ArrayList<ISupplier> items = new ArrayList<ISupplier>();
			IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
			IFileEditorMapping[] editorMappings = editorRegistry.getFileEditorMappings();
			for(IFileEditorMapping editorMapping : editorMappings) {
				IEditorDescriptor[] editors = editorMapping.getEditors();
				for(IEditorDescriptor editorDescriptor : editors) {
					if(editorDescriptor.isOpenExternal()) {
						/*
						 * Skip external editors..
						 */
						continue;
					}
					items.add(new EclipseEditorSupplier(editorMapping, editorDescriptor));
				}
			}
			list = Collections.unmodifiableCollection(items);
		}
		return list;
	}

	private static class EclipseEditorSupplier implements ISupplier {

		private final IFileEditorMapping editorMapping;
		private final IEditorDescriptor editorDescriptor;

		public EclipseEditorSupplier(IFileEditorMapping editorMapping, IEditorDescriptor editorDescriptor) {

			this.editorMapping = editorMapping;
			this.editorDescriptor = editorDescriptor;
		}

		@Override
		public String getId() {

			return editorDescriptor.getId();
		}

		@Override
		public String getDescription() {

			return editorDescriptor.getLabel();
		}

		@Override
		public String getFilterName() {

			return editorDescriptor.getLabel();
		}

		@Override
		public String getFileExtension() {

			return editorMapping.getExtension();
		}

		@Override
		public String getFileName() {

			return editorMapping.getName();
		}

		@Override
		public String getDirectoryExtension() {

			return "";
		}

		@Override
		public boolean isExportable() {

			return false;
		}

		@Override
		public boolean isImportable() {

			return true;
		}

		@Override
		public boolean isMatchMagicNumber(File file) {

			try {
				IEditorDescriptor descriptor = IDE.getEditorDescriptor(file.getName(), false, false);
				boolean matches = descriptor != null && getId().equals(descriptor.getId());
				return matches;
			} catch(PartInitException | OperationCanceledException e) {
				return false;
			}
		}

		@Override
		public String toString() {

			return getFilterName();
		}

		@Execute
		public boolean open(File file) {

			IFileStore fileStore = EFS.getLocalFileSystem().fromLocalFile(file);
			if(fileStore != null) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if(page != null) {
					try {
						page.openEditor(new FileStoreEditorInput(fileStore), getId());
						return true;
					} catch(PartInitException e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}
	}
}