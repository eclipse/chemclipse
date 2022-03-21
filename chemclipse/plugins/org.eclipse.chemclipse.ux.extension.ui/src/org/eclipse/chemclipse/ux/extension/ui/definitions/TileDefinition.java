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
 * Philip Wenig - improved perspective dialog
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.definitions;

import org.eclipse.swt.graphics.Image;

/**
 * Definition of a tile, a small graphical item to allow quick access to a given function
 *
 */
public interface TileDefinition {

	/**
	 * 
	 * @return the tile of the tile, required
	 */
	String getTitle();

	/**
	 * 
	 * @return the description of the tile, returns <code>null</code> if there is no description
	 */
	default String getDescription() {

		return null;
	}

	/**
	 * 
	 * @return the icon of the tile or <code>null</code> if no icon is desired
	 */
	default Image getIcon() {

		return null;
	}

	default boolean isDefaultShow(String subContext) {

		return false;
	}

	/**
	 * Describes the context in which this tile should be shown, this is only a hint and might be implementation specific or ignored at all,
	 * the default implementation assumes that a comma separated list of names is returned.
	 * If an implementation overrides {@link #getContext()} it should also override {@link #matches(String)} if it does not follow these assumption
	 * 
	 * @return the context
	 */
	default String getContext() {

		String preferredPerspective = getPreferredPerspective();
		if(preferredPerspective != null) {
			return preferredPerspective;
		}
		return "";
	}

	default boolean matches(String subContext) {

		if(subContext == null || subContext.isEmpty()) {
			return true;
		}
		String context = getContext();
		if(context != null) {
			String[] split = context.split(",");
			for(String string : split) {
				if(subContext.equals(string.trim())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * A tile can proclaim that it wants to be executed in a a given perspective, if the perspective is really switched might depend on the implementor
	 * 
	 * @return the preferred perspective for this tile or <code>null</code> if there is no preference
	 */
	default String getPreferredPerspective() {

		return null;
	}
}