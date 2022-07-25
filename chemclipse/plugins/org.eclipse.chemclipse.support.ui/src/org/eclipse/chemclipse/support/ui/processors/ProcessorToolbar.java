/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support for sorting / icons
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.processors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.support.ui.preferences.ProcessorToolbarPreferencePage;
import org.eclipse.chemclipse.support.ui.swt.EditorToolBar;
import org.eclipse.jface.preference.IPreferenceStore;

public class ProcessorToolbar {

	private final IProcessSupplierContext context;
	private final BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener;
	private final EditorToolBar editorToolBar;
	private EditorToolBar toolBar;
	private final Predicate<IProcessSupplier<?>> isVisible;
	private List<Processor> processors = new ArrayList<>();
	private PreferencesSupport preferencesSupport;

	public ProcessorToolbar(EditorToolBar editorToolBar, IProcessSupplierContext context, Predicate<IProcessSupplier<?>> isVisible, BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener) {

		this.editorToolBar = editorToolBar;
		this.context = context;
		this.isVisible = isVisible;
		this.executionListener = executionListener;
	}

	public void enablePreferencePage(IPreferenceStore preferenceStore, String key) {

		if(preferenceStore != null) {
			preferencesSupport = new PreferencesSupport(preferenceStore, key, context, isVisible);
			editorToolBar.addPreferencePages(() -> Collections.singleton(new ProcessorToolbarPreferencePage(preferencesSupport)), () -> update()); // Callback
			update(); // Initialize
		}
	}

	public void update() {

		/*
		 * Reload the processor list.
		 */
		processors.clear();
		if(preferencesSupport != null) {
			processors.addAll(preferencesSupport.getStoredProcessors());
		}
		/*
		 * TODO due to strange behaviour of Toolbarmanager we need to create a
		 * new toolbar everytime and clear the old one, we should check if this
		 * is a bug or we use the toolbar manager in a wrong way
		 */
		if(toolBar != null) {
			toolBar.clear();
			toolBar = null;
		}
		//
		toolBar = editorToolBar.createChild(true);
		if(!processors.isEmpty()) {
			for(Processor processor : processors) {
				if(processor != null && processor.isActive() && isVisible.test(processor.getProcessSupplier())) {
					toolBar.addAction(new EditorToolbarAction(processor, executionListener, context));
				}
			}
			toolBar.setVisible(true);
			toolBar.addSeparator();
		}
		//
		editorToolBar.update();
	}
}
