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
package org.eclipse.chemclipse.support.ui.swt;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.preferences.ProcessorToolbarPreferencePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

public class ProcessorToolbar {

	private static final int MAX_TEXT_LENGTH = 10;
	private final ProcessSupplierContext context;
	private final BiConsumer<IProcessSupplier<?>, ProcessSupplierContext> executionListener;
	private final EditorToolBar editorToolBar;
	private EditorToolBar toolBar;
	private final Predicate<IProcessSupplier<?>> isVisible;
	private String[] ids;

	public ProcessorToolbar(EditorToolBar editorToolBar, ProcessSupplierContext context, Predicate<IProcessSupplier<?>> isVisible, BiConsumer<IProcessSupplier<?>, ProcessSupplierContext> executionListener) {
		this.editorToolBar = editorToolBar;
		this.context = context;
		this.isVisible = isVisible;
		this.executionListener = executionListener;
	}

	private static String shortenName(String name) {

		if(name.length() > MAX_TEXT_LENGTH) {
			return name.substring(0, MAX_TEXT_LENGTH - 1) + '…';
		}
		return name;
	}

	public void enablePreferencePage(IPreferenceStore preferenceStore, String key) {

		if(preferenceStore != null) {
			preferenceStore.setDefault(key, "");
			editorToolBar.addPreferencePages(() -> Collections.singleton(new ProcessorToolbarPreferencePage(context, isVisible, preferenceStore, key)), () -> setProcessorIds(preferenceStore.getString(key).split(ProcessorToolbarPreferencePage.ID_SEPARATOR)));
			setProcessorIds(preferenceStore.getString(key).split(ProcessorToolbarPreferencePage.ID_SEPARATOR));
		}
	}

	public void setProcessorIds(String... ids) {

		this.ids = ids;
		update();
	}

	private final class EditorToolbarAction extends Action {

		private final IProcessSupplier<?> supplier;

		private EditorToolbarAction(IProcessSupplier<?> supplier) {
			super(shortenName(supplier.getName()));
			this.supplier = supplier;
			StringBuilder sb = new StringBuilder(supplier.getName());
			String description = supplier.getDescription();
			if(description != null && !description.isEmpty()) {
				sb.append(": ");
				sb.append(description);
			}
			setToolTipText(sb.toString());
			setId(supplier.getId());
		}

		@Override
		public ImageDescriptor getImageDescriptor() {

			return ApplicationImageFactory.getInstance().getIcon(IApplicationImage.IMAGE_EXECUTE_EXTENSION);
		}

		@Override
		public void run() {

			executionListener.accept(supplier, context);
		}
	}

	public void update() {

		// TODO due to strange behaviour of Toolbarmanager we need to create a new toolbar everytime and clear the old one, we should check if this is a bug or we use the toolbar manager in a wrong way
		if(toolBar != null) {
			toolBar.clear();
			toolBar = null;
		}
		toolBar = editorToolBar.createChild(true);
		if(this.ids != null) {
			if(ids.length > 0) {
				for(String id : ids) {
					IProcessSupplier<?> supplier = context.getSupplier(id);
					if(supplier != null && isVisible.test(supplier)) {
						toolBar.addAction(new EditorToolbarAction(supplier));
					}
				}
				toolBar.setVisible(true);
				toolBar.addSeparator();
			}
		}
		editorToolBar.update();
	}
}
