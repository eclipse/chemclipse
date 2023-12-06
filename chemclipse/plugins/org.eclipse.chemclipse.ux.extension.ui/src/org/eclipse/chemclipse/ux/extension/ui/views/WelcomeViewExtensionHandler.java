/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Named;

import org.eclipse.chemclipse.rcp.app.ui.dialogs.PerspectiveChooserDialog;
import org.eclipse.chemclipse.support.ui.workbench.PerspectiveSupport;
import org.eclipse.chemclipse.ux.extension.ui.Activator;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTile;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTileContainer;
import org.eclipse.chemclipse.ux.extension.ui.swt.TileSelectionDialog;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * Handles the extension tiles area
 *
 */
public class WelcomeViewExtensionHandler {

	public static final String PREFERENCE_MIN_TILES = "WelcomeViewExtensionHandler.minTiles";
	public static final String PREFERENCE_MAX_TILES = "WelcomeViewExtensionHandler.maxTiles";
	public static final String PREFERENCE_ALWAYS_CHANGE_PERSPECTIVE = "WelcomeViewExtensionHandler.changePerspective";
	//
	private static final String PREFERENCE_ADDED = "WelcomeViewExtensionHandler.addedTiles";
	private static final String PREFERENCE_REMOVED = "WelcomeViewExtensionHandler.removedTiles";
	private static final String DATA_POPUP_MENU = "WelcomeViewExtensionHandler.POPUP";
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.ux.extension.ui.welcometile";
	private static final String ATTRIBUTE_SECTION = "Section";
	private static final String ATTRIBUTE_DESCRIPTION = "Description";
	private static final String ATTRIBUTE_PERSPECTIVE_ID = "PerspectiveId";
	private static final String ATTRIBUTE_DEFAULTSHOW = "defaultShow";
	private static final Comparator<TileDefinition> EXTENSION_COMPARATOR = (c1, c2) -> c1.getTitle().compareToIgnoreCase(c2.getTitle());
	//
	private final Set<String> removedTiles;
	private final Set<String> addedTiles;
	private final Set<TileDefinition> privateTileDefinitions = new HashSet<>();
	private final TaskTileContainer tileContainer;
	private final int minTiles;
	private final int maxTiles;
	private int tiles;
	private String subcontext;
	//
	private final IPreferenceStore preferenceStore;
	private final Predicate<TileDefinition> definitionAcceptor;

	/**
	 * Constructs a {@link WelcomeViewExtensionHandler} with the given parameters
	 * 
	 * @param parent
	 *            the parent for new Tile objects
	 * @param tileContainer
	 *            the label provider to use for the selection dialog
	 */
	public WelcomeViewExtensionHandler(TaskTileContainer tileContainer, IPreferenceStore preferenceStore, String subcontext) {

		this(tileContainer, preferenceStore, subcontext, tile -> tile.matches(subcontext));
	}

	private WelcomeViewExtensionHandler(TaskTileContainer tileContainer, IPreferenceStore preferenceStore, String subcontext, Predicate<TileDefinition> definitionAcceptor) {

		this.tileContainer = tileContainer;
		this.preferenceStore = preferenceStore;
		this.subcontext = subcontext;
		this.definitionAcceptor = definitionAcceptor;
		this.minTiles = preferenceStore.getInt(PREFERENCE_MIN_TILES);
		this.maxTiles = Math.max(minTiles, preferenceStore.getInt(PREFERENCE_MAX_TILES));
		addedTiles = restoreSet(preferenceStore.getString(PREFERENCE_ADDED));
		removedTiles = restoreSet(preferenceStore.getString(PREFERENCE_REMOVED));
		initTiles();
	}

	private void initTiles() {

		for(TileDefinition tileDefinition : getAllExtensions()) {
			String id = getExtensionId(tileDefinition);
			if(addedTiles.contains(id)) {
				setTile(createTile(), tileDefinition, true);
			}
		}
		while(tiles < minTiles) {
			createTile();
		}
		updateTiles();
	}

	public TaskTile createTile() {

		if(tiles < maxTiles) {
			ExtensionTileDefinition definition = new ExtensionTileDefinition();
			TaskTile tile = tileContainer.addTaskTile(definition);
			Menu popupMenu = new Menu(tile);
			MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
			deleteItem.setText("Remove Shortcut");
			deleteItem.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					String extensionId = getExtensionId(definition.delegate);
					definition.delegate = null;
					removedTiles.add(extensionId);
					addedTiles.remove(extensionId);
					if(tiles > minTiles) {
						tileContainer.removeTaskTile(tile);
						tiles--;
					}
					try {
						updateTiles();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {

				}
			});
			tile.setData(DATA_POPUP_MENU, popupMenu);
			tiles++;
			return tile;
		} else {
			return null;
		}
	}

	public void addTileDefinition(TileDefinition definition) {

		privateTileDefinitions.add(definition);
		updateTiles();
	}

	public void removeTileDefinition(TileDefinition definition) {

		privateTileDefinitions.remove(definition);
		updateTiles();
	}

	private String getExtensionId(TileDefinition extension) {

		if(extension instanceof ConfigurationElementTileDefinition) {
			ConfigurationElementTileDefinition elementTileDefinition = (ConfigurationElementTileDefinition)extension;
			return elementTileDefinition.element.getContributor().getName() + "#" + elementTileDefinition.element.getAttribute(ATTRIBUTE_SECTION);
		} else if(extension != null) {
			return extension.getClass().getName();
		}
		return "";
	}

	private void updateTiles() {

		// fetch unused ones ...
		List<TileDefinition> unusedExtensions = getUnusedExtensions();
		// fetch used ones..
		List<TileDefinition> used = getUsedExtensions();
		// fill space with defaultShow tiles if there is unused space except a final 'Add shortcut' tile ....
		for(Iterator<TileDefinition> iterator = unusedExtensions.iterator(); iterator.hasNext();) {
			if(used.size() < maxTiles - 1) {
				TileDefinition defaultExtension = iterator.next();
				// fill space with ones defined as defaultShow
				if(defaultExtension.isDefaultShow(subcontext)) {
					// but only if the user has not previously explicitly removed this one!
					if(!removedTiles.contains(getExtensionId(defaultExtension))) {
						iterator.remove();
						used.add(defaultExtension);
						continue;
					}
				}
			} else {
				break;
			}
		}
		// sort them
		Collections.sort(used, EXTENSION_COMPARATOR);
		// now we can update all tiles text and status...
		boolean hasShortcut = unusedExtensions.isEmpty();
		for(TaskTile tile : tileContainer.getTiles()) {
			hasShortcut = updateTile(used, hasShortcut, tile);
		}
		// try to extend if possible
		while(!used.isEmpty() || !hasShortcut) {
			TaskTile tile = createTile();
			if(tile == null) {
				break;
			}
			hasShortcut = updateTile(used, hasShortcut, tile);
		}
		preferenceStore.setValue(PREFERENCE_ADDED, serializeSet(addedTiles));
		preferenceStore.setValue(PREFERENCE_REMOVED, serializeSet(removedTiles));
		if(preferenceStore instanceof IPersistentPreferenceStore) {
			IPersistentPreferenceStore store = (IPersistentPreferenceStore)preferenceStore;
			if(store.needsSaving()) {
				try {
					store.save();
				} catch(IOException e) {
				}
			}
		}
	}

	private boolean updateTile(List<TileDefinition> used, boolean hasShortcut, TaskTile tile) {

		TileDefinition definition = tile.getDefinition();
		if(definition instanceof ExtensionTileDefinition) {
			TileDefinition extension;
			if(used.isEmpty()) {
				extension = null;
			} else {
				extension = used.remove(0);
			}
			hasShortcut = setTile(tile, extension, hasShortcut);
		}
		return hasShortcut;
	}

	private static boolean setTile(TaskTile tile, TileDefinition extension, boolean hasShortcut) {

		if(tile == null) {
			return false;
		}
		ExtensionTileDefinition extensionTileDefinition = (ExtensionTileDefinition)tile.getDefinition();
		extensionTileDefinition.delegate = extension;
		if(extension != null) {
			tile.setMenu((Menu)tile.getData(DATA_POPUP_MENU));
			extensionTileDefinition.addshortcut = false;
		} else {
			tile.setMenu(null);
			if(!hasShortcut) {
				extensionTileDefinition.addshortcut = true;
				hasShortcut = true;
			} else {
				extensionTileDefinition.addshortcut = false;
			}
		}
		tile.updateFromDefinition();
		return hasShortcut;
	}

	private void selectExtensionForTile(ExtensionTileDefinition definition, Shell shell, PerspectiveSupport perspectiveSupport) {

		List<TileDefinition> allExtensions = getUnusedExtensions();
		// show selection dialog to the user...
		TileSelectionDialog dialog = new TileSelectionDialog(shell, allExtensions.toArray(new TileDefinition[0]), new ColumnLabelProvider() {

			Map<IConfigurationElement, Image> images = new IdentityHashMap<>();

			@Override
			public String getText(Object element) {

				ILabelProvider labelProvider = Adapters.adapt(element, ILabelProvider.class);
				if(labelProvider != null) {
					return labelProvider.getText(element);
				}
				if(element instanceof TileDefinition) {
					return ((TileDefinition)element).getTitle();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {

				if(element instanceof ConfigurationElementTileDefinition) {
					IConfigurationElement configurationElement = ((ConfigurationElementTileDefinition)element).element;
					Image image = images.get(configurationElement);
					if(image == null) {
						MPerspective perspectiveModel = perspectiveSupport.getPerspectiveModel(configurationElement.getAttribute(ATTRIBUTE_PERSPECTIVE_ID));
						if(perspectiveModel != null) {
							URI iconURI = null;
							try {
								iconURI = new URI(perspectiveModel.getIconURI());
							} catch(URISyntaxException e) {
								logger.warn(e);
							}
							if(iconURI != null) {
								try (InputStream stream = iconURI.toURL().openStream()) {
									image = new Image(shell.getDisplay(), stream);
									images.put(configurationElement, image);
								} catch(IOException e) {
									logger.warn(e);
								}
							}
						}
					}
					return image;
				}
				ILabelProvider labelProvider = Adapters.adapt(element, ILabelProvider.class);
				if(labelProvider != null) {
					return labelProvider.getImage(element);
				}
				return super.getImage(element);
			}

			@Override
			public void dispose() {

				for(Image image : images.values()) {
					image.dispose();
				}
				super.dispose();
			}
		});
		if(dialog.open() == Window.OK) {
			TileDefinition element = dialog.getSelectedElement();
			definition.delegate = element;
			definition.addshortcut = false;
			String extensionId = getExtensionId(element);
			removedTiles.remove(extensionId);
			addedTiles.add(extensionId);
			updateTiles();
		}
	}

	private List<TileDefinition> getUnusedExtensions() {

		List<TileDefinition> allExtensions = getAllExtensions();
		allExtensions.removeAll(getUsedExtensions());
		return allExtensions;
	}

	private List<TileDefinition> getUsedExtensions() {

		List<TileDefinition> usedExtensions = new ArrayList<>();
		for(TaskTile tile : tileContainer.getTiles()) {
			TileDefinition definition = tile.getDefinition();
			if(definition instanceof ExtensionTileDefinition) {
				TileDefinition extension = ((ExtensionTileDefinition)definition).delegate;
				if(extension != null) {
					usedExtensions.add(extension);
				}
			}
		}
		return usedExtensions;
	}

	private List<TileDefinition> getAllExtensions() {

		List<TileDefinition> result = new ArrayList<>();
		// first query the extension registry
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			ConfigurationElementTileDefinition definition = new ConfigurationElementTileDefinition(element);
			if(definitionAcceptor != null && definitionAcceptor.test(definition)) {
				result.add(definition);
			}
		}
		// then add dynamic services
		TileDefinition[] definitions = Activator.getDefault().getTileDefinitions();
		for(TileDefinition definition : definitions) {
			if(definitionAcceptor != null && definitionAcceptor.test(definition)) {
				result.add(definition);
			}
		}
		// now add the private ones
		result.addAll(privateTileDefinitions);
		return result;
	}

	private static String serializeSet(Set<String> set) {

		StringBuilder sb = new StringBuilder();
		for(String key : set) {
			if(sb.length() > 0) {
				sb.append('\t');
			}
			sb.append(key);
		}
		return sb.toString();
	}

	private static Set<String> restoreSet(String serializedSet) {

		HashSet<String> result = new HashSet<>();
		if(serializedSet != null && !serializedSet.trim().isEmpty()) {
			result.addAll(Arrays.asList(serializedSet.split("\t")));
		}
		return result;
	}

	private final class ConfigurationElementTileDefinition implements TileDefinition {

		private final IConfigurationElement element;

		public ConfigurationElementTileDefinition(IConfigurationElement element) {

			this.element = element;
		}

		@Override
		public String getTitle() {

			return element.getAttribute(ATTRIBUTE_SECTION);
		}

		@Override
		public String getDescription() {

			return element.getAttribute(ATTRIBUTE_DESCRIPTION);
		}

		@Override
		public boolean isDefaultShow(String subcontext) {

			return Boolean.parseBoolean(element.getAttribute(ATTRIBUTE_DEFAULTSHOW));
		}

		@Override
		public boolean equals(Object obj) {

			if(obj instanceof ConfigurationElementTileDefinition) {
				return element.equals(((ConfigurationElementTileDefinition)obj).element);
			}
			return false;
		}

		@Override
		public String getPreferredPerspective() {

			return element.getAttribute(ATTRIBUTE_PERSPECTIVE_ID);
		}

		@Override
		public String getContext() {

			return "perspective-switch," + getPreferredPerspective();
		}

		@CanExecute
		public boolean canExecute(PerspectiveSupport perspectiveSupport) {

			String perspectiveId = getPreferredPerspective();
			if(perspectiveId == null || perspectiveId.isEmpty()) {
				return false;
			} else {
				return perspectiveSupport.getPerspectiveModel(perspectiveId) != null;
			}
		}
	}

	private final class ExtensionTileDefinition implements TileDefinition {

		private boolean addshortcut;
		private TileDefinition delegate;

		@Override
		public String getTitle() {

			if(delegate != null) {
				return delegate.getTitle();
			} else if(addshortcut) {
				return "+";
			}
			return "";
		}

		@Override
		public String getDescription() {

			if(delegate != null) {
				return delegate.getDescription();
			} else if(addshortcut) {
				return "Add shortcut";
			}
			return "";
		}

		@Override
		public boolean isDefaultShow(String context) {

			if(delegate != null) {
				return delegate.isDefaultShow(context);
			}
			return false;
		}

		@Override
		public String getContext() {

			if(delegate != null) {
				return delegate.getContext();
			}
			return TileDefinition.super.getContext();
		}

		@Override
		public Image getIcon() {

			if(delegate != null) {
				return delegate.getIcon();
			}
			return null;
		}

		@Execute
		public void execute(PerspectiveSupport perspectiveSupport, @Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext context) {

			if(delegate != null) {
				String preferredPerspective = delegate.getPreferredPerspective();
				if(preferredPerspective != null && !preferredPerspective.isEmpty()) {
					if(!perspectiveSupport.getActivePerspectiveId().startsWith(preferredPerspective)) {
						if(preferredPerspective != null) {
							MPerspective perspectiveModel = perspectiveSupport.getPerspectiveModel(preferredPerspective);
							if(perspectiveModel != null) {
								boolean changePerspectiveAutomatically = (delegate instanceof ConfigurationElementTileDefinition) || preferenceStore.getBoolean(PREFERENCE_ALWAYS_CHANGE_PERSPECTIVE);
								if(!changePerspectiveAutomatically) {
									/*
									 * Ask the user for changing the perspective.
									 */
									PerspectiveChooserDialog dialog = new PerspectiveChooserDialog(shell, "Change Perspective?", "Perspectives offer the best user experience and special views to optimize the workflow. This task is associated with the " + perspectiveModel.getLabel() + " perspective do you like to change? ", preferenceStore, PREFERENCE_ALWAYS_CHANGE_PERSPECTIVE);
									changePerspectiveAutomatically = (dialog.open() == Window.OK);
								}
								/*
								 * Change perspective?
								 */
								if(changePerspectiveAutomatically) {
									perspectiveSupport.changePerspective(preferredPerspective);
								}
							}
						}
					}
				}
				ContextInjectionFactory.invoke(delegate, Execute.class, context, null);
			} else if(addshortcut) {
				selectExtensionForTile(this, shell, perspectiveSupport);
			}
		}

		@CanExecute
		public boolean canExecute(IEclipseContext context) {

			if(delegate != null) {
				Object invoke = ContextInjectionFactory.invoke(delegate, CanExecute.class, context, Boolean.TRUE);
				if(invoke instanceof Boolean) {
					return ((Boolean)invoke).booleanValue();
				}
				return true;
			}
			return addshortcut;
		}
	}
}
