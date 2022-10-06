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
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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
 * This implements a lazy {@link File}based TreeContent. Care must be taken when using this provider:
 * <ul>
 * <li>The Treeviewer must be constructed with the Virtual Flag:
 * <br>
 * <code>TreeViewer treeViewer = new TreeViewer(parent, &lt;other options&gt; | SWT.VIRTUAL);<br>
 * treeViewer.setUseHashlookup(true);</code></li>
 * <li>The Input must be of type File[] (or null)</li>
 * </ul>
 * 
 * @author Christoph Läubrich
 *
 */
public class LazyFileExplorerContentProvider implements ILazyTreeContentProvider, FileFilter {

	public static final int MAX_CACHE_SIZE = Integer.parseInt(System.getProperty("fileexplorer.max_cache_size", "1000"));
	//
	private static final File[] NO_CHILD = new File[0];
	private TreeViewer viewer;
	private File[] roots;
	private final Map<File, File[]> cache = new FileCache<>();

	@Override
	public void updateElement(Object parent, int index) {

		if(viewer == null) {
			return;
		}
		Object child = null;
		if(parent instanceof Object[] objects) {
			if(index < objects.length) {
				child = objects[index];
			}
		}
		if(parent instanceof File file) {
			File[] childs = getChilds(file);
			if(index < childs.length) {
				child = childs[index];
			}
		}
		if(child instanceof File file) {
			viewer.replace(parent, index, child);
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

		if(newViewer instanceof TreeViewer treeViewer) {
			if((treeViewer.getTree().getStyle() & SWT.VIRTUAL) == 0) {
				throw new IllegalArgumentException("Treeviewer must be constructed with SWT.VIRTUAL flag!");
			}
			if(newInput instanceof File file) {
				// convert single file input to array
				newInput = new File[]{file};
			}
			if(newInput == null || newInput instanceof File[]) {
				roots = (File[])newInput;
			} else {
				throw new IllegalArgumentException("Input must be an Array of Files");
			}
			viewer = treeViewer;
		} else {
			throw new IllegalArgumentException("Viewer must be a TreeViewer!");
		}
	}

	public void refresh(File file) {

		cache.remove(file);
		if(viewer != null) {
			viewer.refresh(file);
		}
	}

	@Override
	public void dispose() {

		cache.clear();
		ILazyTreeContentProvider.super.dispose();
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {

		if(viewer == null) {
			return;
		}
		if(element instanceof File file) {
			int childs = getChilds(file).length;
			if(currentChildCount != childs) {
				viewer.setChildCount(element, childs);
			}
			return;
		}
		if(element instanceof Object[] objects) {
			int childs = objects.length;
			if(currentChildCount != childs) {
				viewer.setChildCount(element, childs);
			}
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

		if(element instanceof File file) {
			File parentFile = file.getParentFile();
			if(parentFile == null) {
				return roots;
			}
			if(roots != null) {
				// we must scan for "non-root-roots"...
				String realPath = getRealPath(file);
				for(File root : roots) {
					if(realPath.equals(getRealPath(root))) {
						return roots;
					}
				}
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

	private static String getRealPath(File file) {

		try {
			return file.getCanonicalPath();
		} catch(IOException e) {
			return file.getAbsolutePath();
		}
	}

	protected static final class FileCache<T> extends LinkedHashMap<File, T> {

		private static final long serialVersionUID = -5978638803700871374L;

		public FileCache() {

			super(MAX_CACHE_SIZE, 0.75f, true);
		}

		@Override
		public boolean removeEldestEntry(Map.Entry<File, T> eldest) {

			return size() > MAX_CACHE_SIZE;
		}
	}
}
