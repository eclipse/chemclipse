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
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

/**
 * This implements a lazy {@link File}based TreeContent.
 * 
 * @author Christoph Läubrich
 *
 */
public class LazyFileExplorerContentProvider implements ILazyTreeContentProvider, FileFilter {

	private static final File[] NO_CHILD = new File[0];
	private TreeViewer viewer;
	private Object[] roots;
	private static final int MAX_CACHE_SIZE = 100;
	private final Map<File, File[]> cache = new LinkedHashMap<File, File[]>(MAX_CACHE_SIZE, 0.75f, true) {

		private static final long serialVersionUID = -5978638803700871374L;

		@Override
		public boolean removeEldestEntry(Map.Entry<File, File[]> eldest) {

			return size() > MAX_CACHE_SIZE;
		}
	};

	@Override
	public void updateElement(Object parent, int index) {

		if(viewer == null) {
			return;
		}
		Object child = null;
		if(parent instanceof Object[]) {
			Object[] objects = (Object[])parent;
			if(index < objects.length) {
				child = objects[index];
			}
		}
		if(parent instanceof File) {
			File[] childs = getChilds((File)parent);
			if(index < childs.length) {
				child = childs[index];
			}
		}
		if(child instanceof File) {
			viewer.replace(parent, index, child);
			File file = (File)child;
			if(file.isDirectory()) {
				viewer.setHasChildren(file, true);
				// remove the file from the cache to allow updates to the underlying file system
				cache.remove(file);
			} else {
				viewer.setChildCount(file, 0);
			}
		}
	}

	@Override
	public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) {

		if(newViewer instanceof TreeViewer) {
			TreeViewer treeViewer = (TreeViewer)newViewer;
			if((treeViewer.getTree().getStyle() & SWT.VIRTUAL) == 0) {
				throw new IllegalArgumentException("Treeviewer must be constructed with SWT.VIRTUAL flag!");
			}
			if(newInput == null || newInput instanceof Object[]) {
				roots = (Object[])newInput;
			} else {
				throw new IllegalArgumentException("Input must be an Array");
			}
			viewer = treeViewer;
		} else {
			throw new IllegalArgumentException("Viewer must be a TreeViewer!");
		}
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {

		if(viewer == null) {
			return;
		}
		if(element instanceof File) {
			File file = (File)element;
			int childs = getChilds(file).length;
			if(currentChildCount != childs) {
				viewer.setChildCount(element, childs);
			}
			return;
		}
		if(element instanceof Object[]) {
			Object[] objects = (Object[])element;
			int childs = objects.length;
			if(currentChildCount != childs) {
				viewer.setChildCount(element, childs);
			}
			return;
		}
	}

	private File[] getChilds(File file) {

		if(file.isDirectory()) {
			File[] cached = cache.get(file);
			if(cached != null) {
				return cached;
			}
			Display display = viewer.getControl().getDisplay();
			BusyIndicator.showWhile(display, new Runnable() {

				@Override
				public void run() {

					File[] childs = file.listFiles(LazyFileExplorerContentProvider.this);
					// null check is required here, e.g. if the directory can't be read/accessed list returns null
					if(childs != null) {
						Arrays.sort(childs);
						cache.put(file, childs);
					} else {
						cache.put(file, NO_CHILD);
					}
				}
			});
			return cache.get(file);
		}
		return NO_CHILD;
	}

	@Override
	public Object getParent(Object element) {

		if(element instanceof File) {
			File file = (File)element;
			File parentFile = file.getParentFile();
			if(parentFile == null) {
				return roots;
			}
			return parentFile;
		}
		return null;
	}

	/**
	 * The default implementation accepts any file/folder that is not hidden and does not start with a dot, subclasses may override to provide additional behavior
	 */
	@Override
	public boolean accept(File file) {

		return !file.isHidden() && !file.getName().startsWith(".") && file.canRead();
	}
}
