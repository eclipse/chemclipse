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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A {@link IStructuredContentProvider} that uses the <a href="https://www.eclipse.org/articles/article.php?file=Article-Adapters/index.html">Adapter-Pattern</a>
 * 
 * @author Christoph Läubrich
 *
 */
public class ProxyStructuredContentProvider implements ITreeContentProvider {

	private IStructuredContentProvider proxy;
	private Viewer viewer;
	private Object newInput;

	@Override
	public Object[] getElements(Object inputElement) {

		if(proxy != null) {
			Object[] elements = proxy.getElements(inputElement);
			if(elements != null) {
				return elements;
			}
		}
		return new Object[0];
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		this.viewer = viewer;
		this.newInput = newInput;
	}

	public void setProxy(IStructuredContentProvider proxy) {

		if(this.proxy != null && viewer != null && newInput != null) {
			this.proxy.inputChanged(viewer, newInput, null);
		}
		this.proxy = proxy;
		if(proxy != null && viewer != null && newInput != null) {
			proxy.inputChanged(viewer, null, newInput);
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if(proxy instanceof ITreeContentProvider) {
			return ((ITreeContentProvider)proxy).getChildren(parentElement);
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {

		if(proxy instanceof ITreeContentProvider) {
			return ((ITreeContentProvider)proxy).getParent(element);
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if(proxy instanceof ITreeContentProvider) {
			return ((ITreeContentProvider)proxy).hasChildren(element);
		}
		return false;
	}
}
