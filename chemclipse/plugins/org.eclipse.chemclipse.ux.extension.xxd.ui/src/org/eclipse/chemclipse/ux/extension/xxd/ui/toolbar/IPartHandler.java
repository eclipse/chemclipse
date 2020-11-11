/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.toolbar;

public interface IPartHandler {

	boolean isPartStackAssigned();

	boolean isPartVisible();

	void action(boolean show);

	default String getName() {

		return "";
	}

	PartStackReference getPartStackReference();

	default String getIconURI() {

		return "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/tag.gif";
	}

	void toggleVisibility();
}
