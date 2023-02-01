/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;

public interface IPartHandler {

	boolean isPartStackAssigned();

	boolean isPartVisible();

	void action(boolean show);

	default String getName() {

		return "";
	}

	PartStackReference getPartStackReference();

	default String getIconURI() {

		return ApplicationImageFactory.getInstance().getURI(IApplicationImage.IMAGE_TAG, IApplicationImageProvider.SIZE_16x16);
	}

	void toggleVisibility();
}