/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods, support for init with different datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.updates.IUpdateListener;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.MethodListLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public class MethodTreeViewer extends TreeViewer {

	private final TreeViewerColumn[] columns = new TreeViewerColumn[MethodListLabelProvider.TITLES.length];
	//
	private IProcessSupplierContext processingSupport;
	private BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier;
	private AtomicReference<ProcessMethodToolbar> toolbarButtons;
	//
	private IUpdateListener updateListener = null;

	public MethodTreeViewer(Composite parent, int style) {

		super(parent, style);
	}

	public void createControl(IProcessSupplierContext processingSupport, BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier, final AtomicReference<ProcessMethodToolbar> toolbarButtons, boolean enableTooltips) {

		this.processingSupport = processingSupport;
		this.preferencesSupplier = preferencesSupplier;
		this.toolbarButtons = toolbarButtons;
		//
		setUseHashlookup(true);
		setExpandPreCheckFilters(true);
		getTree().setLinesVisible(true);
		getTree().setHeaderVisible(true);
		//
		enableTooltips(enableTooltips);
		createTree();
	}

	public void setUpdateListener(IUpdateListener updateListener) {

		this.updateListener = updateListener;
	}

	private void enableTooltips(boolean enableTooltips) {

		if(enableTooltips) {
			ColumnViewerToolTipSupport.enableFor(this, ToolTip.NO_RECREATE);
		}
	}

	private void createTree() {

		addColumns();
		setLabelProvider(new MethodListLabelProvider(processingSupport, preferencesSupplier));
		setContentProvider();
		getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		registerEventListeners();
	}

	private void addColumns() {

		for(int i = 0; i < MethodListLabelProvider.TITLES.length; i++) {
			columns[i] = createColumn(this, MethodListLabelProvider.TITLES[i], MethodListLabelProvider.BOUNDS[i], null);
		}
	}

	private void setContentProvider() {

		setContentProvider(new ITreeContentProvider() {

			@Override
			public boolean hasChildren(Object element) {

				if(element instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)element;
					IProcessSupplier<?> supplier = processingSupport.getSupplier(entry.getProcessorId());
					if(supplier instanceof ProcessEntryContainer) {
						return ((ProcessEntryContainer)supplier).getNumberOfEntries() > 0;
					}
				}
				//
				if(element instanceof ProcessEntryContainer) {
					return ((ProcessEntryContainer)element).getNumberOfEntries() > 0;
				}
				//
				return false;
			}

			@Override
			public Object getParent(Object element) {

				if(element instanceof IProcessEntry) {
					return ((IProcessEntry)element).getParent();
				}
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {

				if(inputElement instanceof ProcessEntryContainer) {
					ProcessEntryContainer container = (ProcessEntryContainer)inputElement;
					return entryList(container, false);
				}
				//
				if(inputElement instanceof Object[]) {
					return (Object[])inputElement;
				}
				//
				return new Object[0];
			}

			private Object[] entryList(Iterable<? extends IProcessEntry> iterable, boolean detatch) {

				List<Object> list = new ArrayList<>();
				if(detatch) {
					iterable.forEach(new Consumer<IProcessEntry>() {

						@Override
						public void accept(IProcessEntry entry) {

							list.add(new ProcessEntry(entry, null));
						}
					});
				} else {
					iterable.forEach(list::add);
				}
				return list.toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {

				if(parentElement instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)parentElement;
					IProcessSupplier<?> supplier = processingSupport.getSupplier(entry.getProcessorId());
					if(supplier instanceof ProcessEntryContainer) {
						return entryList((ProcessEntryContainer)supplier, true);
					}
				}
				//
				if(parentElement instanceof ProcessEntryContainer) {
					return entryList((ProcessEntryContainer)parentElement, false);
				}
				//
				return new Object[0];
			}
		});
	}

	private void registerEventListeners() {

		addSelectionChangedListener(event -> toolbarButtons.get().updateTableButtons());
		//
		addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				if(preferencesSupplier == null) {
					return;
				}
				//
				Object firstElement = getStructuredSelection().getFirstElement();
				if(firstElement instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)firstElement;
					if(toolbarButtons.get().modifyProcessEntry(getControl().getShell(), entry, IProcessEntry.getContext(entry, processingSupport), true)) {
						fireUpdate();
					}
				}
			}
		});
		//
		getTree().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.stateMask == SWT.MOD1 && e.keyCode == 'c') {
					toolbarButtons.get().copyToClipboard(e.display);
				}
			}
		});
	}

	private void fireUpdate() {

		if(updateListener != null) {
			updateListener.update();
		}
	}
}