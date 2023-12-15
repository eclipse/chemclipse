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
 * Philip Wenig - add profile support
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.converter.PathResolver;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.logging.support.Settings;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.ICategories;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

@Component(service = {IProcessTypeSupplier.class})
public class MethodProcessTypeSupplier implements IProcessTypeSupplier, BundleTrackerCustomizer<Collection<IProcessSupplier<?>>> {

	private static final Logger logger = Logger.getLogger(MethodProcessTypeSupplier.class);
	//
	private static final String PROCESSORS_ENTRY_PATH = "/OSGI-INF/processors/";
	private static final String BUNDLE_PREFIX = "bundle:";
	private static final String SYSTEM_PREFIX = "system:";
	//
	private BundleTracker<Collection<IProcessSupplier<?>>> bundleTracker;
	private final List<IProcessSupplier<?>> systemMethods = new ArrayList<>();

	@Override
	public String getCategory() {

		return ICategories.USER_METHODS;
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL)
	public void setLogService(LogService logService) {

		// Not used
	}

	public void unsetLogService(LogService logService) {

		// Not used
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Collection<IProcessSupplier<?>> object) {

		// don't mind
		// System.out.println("MOD: " + bundle + "\t" + event);
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Collection<IProcessSupplier<?>> object) {

		// nothing to do here
		// System.out.println("REM: " + bundle + "\t" + event);
	}

	@Activate
	public void start(BundleContext bundleContext) {

		bundleTracker = new BundleTracker<>(bundleContext, Bundle.ACTIVE, this);
		bundleTracker.open();
		//
		systemMethods.addAll(parseSystemMethods());
	}

	@Deactivate
	public void stop() {

		bundleTracker.close();
	}

	@Override
	public Collection<IProcessSupplier<?>> getProcessorSuppliers() {

		List<IProcessSupplier<?>> processSupplierList = new ArrayList<>();
		//
		processSupplierList.addAll(parseUserMethods());
		addTrackedProcessMethods(processSupplierList);
		processSupplierList.addAll(systemMethods);
		//
		return processSupplierList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> IProcessSupplier<T> getSupplier(String id) {

		IProcessSupplier<T> processSupplier = IProcessTypeSupplier.super.getSupplier(id);
		if(processSupplier == null) {
			String[] split = id.split(":", 2);
			if(split.length == 2) {
				String baseId = split[0];
				for(IProcessSupplier<?> processSupplierX : getProcessorSuppliers()) {
					if(processSupplierX.getId().startsWith(baseId)) {
						if(processSupplier == null) {
							/*
							 * Set the process supplier x as active.
							 */
							processSupplier = (IProcessSupplier<T>)processSupplierX;
						} else {
							/*
							 * The id is ambiguous.
							 * To be safe we return null here instead of doing something nasty...
							 */
							return null;
						}
					}
				}
			}
		}
		//
		return processSupplier;
	}

	@Override
	public Collection<IProcessSupplier<?>> addingBundle(Bundle bundle, BundleEvent event) {

		return parseBundleMethods(bundle);
	}

	private List<IProcessSupplier<?>> parseUserMethods() {

		List<IProcessSupplier<?>> processSupplierList = new ArrayList<>();
		//
		Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
		Set<String> processSupplierIds = new HashSet<>();
		//
		for(IProcessMethod processMethod : userMethods) {
			UserMethodProcessSupplier processSupplier = new UserMethodProcessSupplier(processMethod, this);
			if(processSupplierIds.contains(processSupplier.getId())) {
				logger.warn("Duplicate id detected for method: " + processMethod.getName() + " (id: " + processSupplier.getId() + ")");
				continue;
			}
			processSupplierList.add(processSupplier);
			processSupplierIds.add(processSupplier.getId());
		}
		//
		return processSupplierList;
	}

	private List<IProcessSupplier<?>> parseBundleMethods(Bundle bundle) {

		List<IProcessSupplier<?>> processSupplierList = new ArrayList<>();
		Enumeration<URL> entries = bundle.findEntries(PROCESSORS_ENTRY_PATH, "*" + MethodConverter.FILE_EXTENSION, true);
		//
		if(entries != null) {
			while(entries.hasMoreElements()) {
				URL url = entries.nextElement();
				try {
					try (InputStream inputStream = url.openStream()) {
						/*
						 * Try to resolve the file.
						 */
						String urlPath = url.getPath().toString();
						String absolutePath = PathResolver.getAbsolutePath(bundle, urlPath);
						File sourceFile = new File(absolutePath);
						//
						String path = url.getPath().replace(PROCESSORS_ENTRY_PATH, "").replace(MethodConverter.FILE_EXTENSION, "");
						String externalForm = url.toExternalForm();
						IProcessingInfo<IProcessMethod> processingInfo = MethodConverter.load(inputStream, externalForm, null);
						IProcessMethod processMethod = processingInfo.getProcessingResult();
						if(processMethod != null) {
							/*
							 * Set the File (if available) to allow editing the profiles.
							 * The containing bundle should define in the MANIFEST.MF:
							 * Eclipse-BundleShape: dir
							 */
							if(processMethod instanceof ProcessMethod method && sourceFile.exists()) {
								method.setSourceFile(sourceFile);
							}
							processSupplierList.add(new MetaProcessorProcessSupplier(MethodProcessSupport.getID(processMethod, BUNDLE_PREFIX + bundle.getSymbolicName() + ":" + path), processMethod, this));
						}
					}
				} catch(IOException e) {
					logger.error("Failed to load the method from URL: " + url, e);
				}
			}
		}
		//
		return processSupplierList;
	}

	private List<IProcessSupplier<?>> parseSystemMethods() {

		List<IProcessSupplier<?>> processSupplierList = new ArrayList<>();
		File systemMethodFolder = Settings.getSystemMethodDirectory();
		//
		if(systemMethodFolder.isDirectory()) {
			File[] listFiles = systemMethodFolder.listFiles();
			if(listFiles != null) {
				for(File file : listFiles) {
					if(file.isFile() && file.getName().toLowerCase().endsWith(MethodConverter.FILE_EXTENSION)) {
						try {
							try (InputStream inputStream = new FileInputStream(file)) {
								IProcessingInfo<IProcessMethod> load = MethodConverter.load(inputStream, file.getAbsolutePath(), null);
								IProcessMethod processMethod = load.getProcessingResult();
								if(processMethod != null) {
									/*
									 * Set the File to allow editing the profiles.
									 */
									if(processMethod instanceof ProcessMethod method) {
										method.setSourceFile(file);
									}
									processSupplierList.add(new MetaProcessorProcessSupplier(MethodProcessSupport.getID(processMethod, SYSTEM_PREFIX + file.getName()), processMethod, this));
								}
							}
						} catch(IOException e) {
							logger.error("Failed to load the following method from the system path: " + file.getAbsolutePath(), e);
						}
					}
				}
			}
		}
		//
		return processSupplierList;
	}

	private void addTrackedProcessMethods(List<IProcessSupplier<?>> processSupplierList) {

		Collection<Collection<IProcessSupplier<?>>> values = bundleTracker.getTracked().values();
		values.forEach(processSupplierList::addAll);
	}
}