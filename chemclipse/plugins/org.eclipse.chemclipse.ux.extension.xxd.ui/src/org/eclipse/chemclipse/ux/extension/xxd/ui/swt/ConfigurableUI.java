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

/**
 * UIs can implement this interface to indicate that they are configurable,
 * there is no restriction on this config object, but it is recommended that each UI defines a config interface
 * that extends "well-known" interfaces like {@link ToolbarConfig} so it is possible to configure UIs in a generic way for example with a generic toolbar handler.
 * 
 * Besides that, of course the interface can define as many special settings that can only be used from special handlers
 * 
 * @author Christoph Läubrich
 *
 * @param <T>
 */
public interface ConfigurableUI<T> {

	T getConfig();
}
