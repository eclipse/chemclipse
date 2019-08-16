/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *
 *******************************************************************************/

package org.eclipse.chemclipse.ux.fx.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LazyLoadingThreads {

	private static final ExecutorService exe = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public static ExecutorService getExecutorService() {
		return exe;
	}

}
