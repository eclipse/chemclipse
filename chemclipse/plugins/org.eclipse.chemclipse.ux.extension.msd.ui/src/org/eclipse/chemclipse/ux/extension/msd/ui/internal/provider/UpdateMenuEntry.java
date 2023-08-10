/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.swt.IMassSpectrumChart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class UpdateMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	@Override
	public String getCategory() {

		return ICategories.MASS_SPECTRUM;
	}

	@Override
	public String getName() {

		return "Update";
	}

	@Override
	public Image getIcon() {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_REFRESH, IApplicationImageProvider.SIZE_16x16);
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		if(scrollableChart instanceof IMassSpectrumChart massSpectrumChart) {
			massSpectrumChart.update();
		}
	}
}
