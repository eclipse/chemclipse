/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * A {@link ITableLabelProvider} that uses a Proxy to supply labels
 */
public class ProxyTableLabelProvider implements ITableLabelProvider, IAdaptable, ITableColorProvider, ITableFontProvider {

	private ITableLabelProvider proxy;
	private Set<ILabelProviderListener> listeners = new LinkedHashSet<>();

	@Override
	public void addListener(ILabelProviderListener listener) {

		listeners.add(listener);
	}

	@Override
	public void dispose() {

		proxy = null;
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {

		if(proxy != null) {
			return proxy.isLabelProperty(element, property);
		}
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

		listeners.remove(listener);
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(proxy != null) {
			return proxy.getColumnImage(element, columnIndex);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if(proxy != null) {
			return proxy.getColumnText(element, columnIndex);
		} else {
			return null;
		}
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {

		if(proxy instanceof ITableColorProvider tableColorProvider) {
			return tableColorProvider.getForeground(element, columnIndex);
		} else {
			return null;
		}
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {

		if(proxy instanceof ITableColorProvider tableColorProvider) {
			return tableColorProvider.getBackground(element, columnIndex);
		} else {
			return null;
		}
	}

	@Override
	public Font getFont(Object element, int columnIndex) {

		if(proxy instanceof ITableFontProvider tableFontProvider) {
			return tableFontProvider.getFont(element, columnIndex);
		}
		return null;
	}

	/**
	 * Updates the provider to the one that can be adapted from the given object
	 * 
	 * @param item
	 */
	public void setProxy(ITableLabelProvider proxy) {

		this.proxy = proxy;
		LabelProviderChangedEvent event = new LabelProviderChangedEvent(this);
		for(ILabelProviderListener listener : listeners) {
			listener.labelProviderChanged(event);
		}
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {

		return Adapters.adapt(proxy, adapter);
	}
}