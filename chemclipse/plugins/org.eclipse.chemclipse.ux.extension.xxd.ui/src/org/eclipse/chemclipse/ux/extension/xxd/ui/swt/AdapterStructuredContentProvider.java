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

import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A {@link IStructuredContentProvider} that uses the <a href="https://www.eclipse.org/articles/article.php?file=Article-Adapters/index.html">Adapter-Pattern</a>
 * 
 * @author Christoph Läubrich
 *
 */
public class AdapterStructuredContentProvider implements IStructuredContentProvider {

	private static final AdapterStructuredContentProvider PROVIDER = new AdapterStructuredContentProvider();

	@Override
	public Object[] getElements(Object inputElement) {

		IStructuredContentProvider provider = Adapters.adapt(inputElement, IStructuredContentProvider.class);
		if(provider != null) {
			return provider.getElements(provider);
		}
		return new Object[0];
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		IStructuredContentProvider oldProvider = Adapters.adapt(oldInput, IStructuredContentProvider.class);
		IStructuredContentProvider newProvider = Adapters.adapt(newInput, IStructuredContentProvider.class);
		if(oldProvider != null) {
			oldProvider.inputChanged(viewer, oldInput, newInput);
		}
		if(newProvider != null && newProvider != oldProvider) {
			newProvider.inputChanged(viewer, oldInput, newInput);
		}
	}

	/**
	 * 
	 * @return a static instance of this provider, since no internal state is kept, instances of this provider can safely be shared across threads
	 */
	public static IContentProvider getInstance() {

		return PROVIDER;
	}
}
