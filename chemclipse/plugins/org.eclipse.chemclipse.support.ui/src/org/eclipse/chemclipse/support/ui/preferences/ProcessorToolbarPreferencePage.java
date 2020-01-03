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
package org.eclipse.chemclipse.support.ui.preferences;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.swt.ControlBuilder;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ProcessorToolbarPreferencePage extends PreferencePage {

	public static final String ID_SEPARATOR = "\t";
	private static final Comparator<IProcessSupplier<?>> COMPARATOR = (o1, o2) -> o1.getName().compareTo(o2.getName());
	private final ProcessSupplierContext context;
	private ListViewer avaiableList;
	private final Set<IProcessSupplier<?>> unusedSupplier = new TreeSet<>(COMPARATOR);
	private final Set<IProcessSupplier<?>> usedSupplier = new TreeSet<>(COMPARATOR);
	private ListViewer usedList;
	private final String key;
	private final Set<String> addedIds;
	private final Set<String> removedIds;
	private final String keyExclude;
	private final Predicate<IProcessSupplier<?>> filter;

	public ProcessorToolbarPreferencePage(ProcessSupplierContext context, Predicate<IProcessSupplier<?>> filter, IPreferenceStore preferenceStore, String key) {
		this.context = context;
		this.filter = filter;
		this.key = key;
		this.keyExclude = key + ".excluded";
		preferenceStore.setDefault(keyExclude, "");
		addedIds = new HashSet<>(Arrays.asList(preferenceStore.getString(key).split(ID_SEPARATOR)));
		removedIds = new HashSet<>(Arrays.asList(preferenceStore.getString(keyExclude).split(ID_SEPARATOR)));
		noDefaultAndApplyButton();
		setDescription("Here you can configure the processors that should appear in the Toolbar as a Quick-Access item");
		setSize(new Point(400, 600));
		setPreferenceStore(preferenceStore);
		setTitle("Processor Quick-Access");
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite container = ControlBuilder.createContainer(parent, 2, true);
		Composite left = createContainer(container, 2);
		avaiableList = createList(left);
		createToolbar(left);
		for(IProcessSupplier<?> supplier : context.getSupplier(filter)) {
			if(addedIds.contains(supplier.getId())) {
				usedSupplier.add(supplier);
			} else {
				unusedSupplier.add(supplier);
			}
		}
		avaiableList.setInput(unusedSupplier);
		usedList = createList(container);
		usedList.setInput(usedSupplier);
		return container;
	}

	@Override
	public boolean performOk() {

		getPreferenceStore().setValue(key, toString(addedIds));
		getPreferenceStore().setValue(keyExclude, toString(removedIds));
		return true;
	}

	private static String toString(Set<String> set) {

		StringBuilder sb = new StringBuilder();
		for(String id : set) {
			if(sb.length() > 0) {
				sb.append(ID_SEPARATOR);
			}
			sb.append(id);
		}
		return sb.toString();
	}

	private void createToolbar(Composite left) {

		ToolBar toolBar = new ToolBar(left, SWT.VERTICAL | SWT.FLAT);
		ToolItem addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_FORWARD, IApplicationImage.SIZE_16x16));
		ToolItem removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_BACKWARD, IApplicationImage.SIZE_16x16));
		addItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(Object selected : avaiableList.getStructuredSelection().toArray()) {
					if(selected instanceof IProcessSupplier<?>) {
						IProcessSupplier<?> supplier = (IProcessSupplier<?>)selected;
						if(unusedSupplier.remove(supplier)) {
							usedSupplier.add(supplier);
							addedIds.add(supplier.getId());
							removedIds.remove(supplier.getId());
						}
					}
					avaiableList.refresh();
					usedList.refresh();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		removeItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(Object selected : usedList.getStructuredSelection().toArray()) {
					if(selected instanceof IProcessSupplier<?>) {
						IProcessSupplier<?> supplier = (IProcessSupplier<?>)selected;
						if(usedSupplier.remove(supplier)) {
							unusedSupplier.add(supplier);
							removedIds.add(supplier.getId());
							addedIds.remove(supplier.getId());
						}
					}
				}
				avaiableList.refresh();
				usedList.refresh();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
	}

	private ListViewer createList(Composite parent) {

		ListViewer viewer = new ListViewer(parent);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IProcessSupplier<?>) {
					return ((IProcessSupplier<?>)element).getName();
				}
				return super.getText(element);
			}
		});
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 350;
		viewer.getControl().setLayoutData(data);
		return viewer;
	}
}
