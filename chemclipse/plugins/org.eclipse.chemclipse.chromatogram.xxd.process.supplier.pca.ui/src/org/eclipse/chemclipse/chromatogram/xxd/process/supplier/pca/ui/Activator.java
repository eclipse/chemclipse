/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	public static final String ICON_NORM_1NORM = "ICON_NORM_1NORM"; // $NON-NLS-1$
	public static final String ICON_NORM_2NORM = "ICON_NORM_2NORM"; // $NON-NLS-1$
	public static final String ICON_NORM_INFNORM = "ICON_NORM_INFNORM"; // $NON-NLS-1$
	public static final String ICON_CENTER_MEAN = "ICON_CENTER_MEAN"; // $NON-NLS-1$
	public static final String ICON_CENTER_MEDIAN = "ICON_CENTER_MEDIAN"; // $NON-NLS-1$
	public static final String ICON_DEVIATION = "ICON_DEVIATION"; // $NON-NLS-1$
	public static final String ICON_NORM_CENTER = "ICON_NORM_CENTER"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_AUTO = "ICON_NORM_SCALE_AUTO"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_LEVEL = "ICON_NORM_SCALE_LEVEL"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_MAX2 = "ICON_NORM_SCALE_MAX2"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_MAX = "ICON_NORM_SCALE_MAX"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_PARETO = "ICON_NORM_SCALE_PARETO"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_RANGE2 = "ICON_NORM_SCALE_RANGE2"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_RANGE = "ICON_NORM_SCALE_RANGE"; // $NON-NLS-1$
	public static final String ICON_NORM_SCALE_VAST = "ICON_NORM_SCALE_VAST"; // $NON-NLS-1$
	public static final String ICON_NORM_TRANS = "ICON_NORM_TRANS"; // $NON-NLS-1$
	public static final String ICON_NORM_TRANS_LOG = "ICON_NORM_TRANS_LOG"; // $NON-NLS-1$
	public static final String ICON_NORM_TRANS_NONE = "ICON_NORM_TRANS_NONE"; // $NON-NLS-1$
	public static final String ICON_NORM_TRANS_POWER = "ICON_NORM_TRANS_POWER"; // $NON-NLS-1$
	//
	public static final String TOPIC_PCA_EVALUATION_LOAD = "pca/evaluation/load";
	public static final String TOPIC_PCA_EVALUATION_CLEAR = "pca/evaluation/clear";
	//
	private static Activator plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {

		return plugin;
	}

	/**
	 * The constructor
	 */
	public Activator() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStore(PreferenceSupplier.INSTANCE());
		initializeImageRegistry(getImageHashMap());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		super.stop(context);
	}

	public IEventBroker getEventBroker() {

		BundleContext bundleContext = getBundle().getBundleContext();
		IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
		eclipseContext.set(Logger.class, null);
		return eclipseContext.get(IEventBroker.class);
	}

	private Map<String, String> getImageHashMap() {

		Map<String, String> imageHashMap = new HashMap<String, String>();
		//
		imageHashMap.put(ICON_NORM_1NORM, "icons/1norm.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_2NORM, "icons/2norm.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_INFNORM, "icons/infnorm.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_CENTER_MEAN, "icons/center_mean.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_CENTER_MEDIAN, "icons/center_median.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_DEVIATION, "icons/deviation.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_CENTER, "icons/norm_center.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_AUTO, "icons/norm_scal_auto.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_LEVEL, "icons/norm_scal_level.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_MAX2, "icons/norm_scal_max2.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_MAX, "icons/norm_scal_max.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_PARETO, "icons/norm_scal_pareto.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_RANGE2, "icons/norm_scal_range2.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_RANGE, "icons/norm_scal_range.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_SCALE_VAST, "icons/norm_scal_vast.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_TRANS, "icons/norm_trans.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_TRANS_LOG, "icons/trans_log.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_TRANS_NONE, "icons/trans_none.jpg"); // $NON-NLS-1$
		imageHashMap.put(ICON_NORM_TRANS_POWER, "icons/trans_power.jpg"); // $NON-NLS-1$
		//
		return imageHashMap;
	}
}
