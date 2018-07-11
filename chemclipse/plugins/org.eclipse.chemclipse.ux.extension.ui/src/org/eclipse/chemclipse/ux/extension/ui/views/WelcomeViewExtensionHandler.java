/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.views;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.ux.extension.ui.swt.ExtensionsTileSelectionDialog;
import org.eclipse.chemclipse.ux.extension.ui.swt.ISelectionHandler;
import org.eclipse.chemclipse.ux.extension.ui.swt.TaskTile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Handles the extension tiles area
 * 
 * @author Christoph Läubrich
 *
 */
public class WelcomeViewExtensionHandler {

	private static final String DATA_TILE_INDEX = "WelcomeViewExtensionHandler.INDEX";
	private static final String DATA_POPUP_MENU = "WelcomeViewExtensionHandler.POPUP";
	private static final String DATA_EXTENSION = "WelcomeViewExtensionHandler.EXTENSION";
	private static final String DATA_PREV = "WelcomeViewExtensionHandler.PREV";
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.ux.extension.ui.welcometile";
	private static final String ATTRIBUTE_SECTION = "Section";
	private static final String ATTRIBUTE_DESCRIPTION = "Description";
	private static final String ATTRIBUTE_PERSPECTIVE_ID = "PerspectiveId";
	private static final String ATTRIBUTE_DEFAULTSHOW = "defaultShow";
	private static final int MAX_EXTENSION_TILES = 4;
	private static final String PREFERENCE_ADDED = "addedTiles";
	private static final String PREFERENCE_REMOVED = "removedTiles";
	private static final Comparator<IConfigurationElement> EXTENSION_COMPARATOR = new Comparator<IConfigurationElement>() {

		@Override
		public int compare(IConfigurationElement c1, IConfigurationElement c2) {

			return c1.getAttribute(ATTRIBUTE_SECTION).compareTo(c2.getAttribute(ATTRIBUTE_SECTION));
		}
	};
	private final Collection<TaskTile> tiles = new ArrayList<>();
	private final Set<String> removedTiles;
	private final Set<String> addedTiles;
	private final WelcomeView welcomeView;
	private final Preferences preferences;

	public WelcomeViewExtensionHandler(Composite parent, WelcomeView welcomeView) {
		preferences = InstanceScope.INSTANCE.getNode(WelcomeView.class.getName());
		addedTiles = restoreSet(preferences.get(PREFERENCE_ADDED, ""));
		removedTiles = restoreSet(preferences.get(PREFERENCE_REMOVED, ""));
		this.welcomeView = welcomeView;
		initTiles(parent);
		updateTiles();
	}

	public void initTiles(Composite parent) {

		List<IConfigurationElement> extensions = getAllExtensions();
		TaskTile tilePrev = null;
		for(int i = 0; i < MAX_EXTENSION_TILES; i++) {
			TaskTile tile = createTile(parent);
			tile.setData(DATA_TILE_INDEX, i);
			tile.setData(DATA_PREV, tilePrev);
			tilePrev = tile;
			// add initial extension
			for(Iterator<IConfigurationElement> iterator = extensions.iterator(); iterator.hasNext();) {
				IConfigurationElement extension = (IConfigurationElement)iterator.next();
				if(addedTiles.contains(getExtensionId(extension))) {
					tile.setData(DATA_EXTENSION, extension);
					iterator.remove();
					break;
				}
			}
		}
	}

	public TaskTile createTile(Composite parent) {

		TaskTile tile = new TaskTile(parent, SWT.NONE);
		tile.setSelectionHandler(new ISelectionHandler() {

			@Override
			public void handleEvent() {

				tileSelected(tile);
			}
		});
		welcomeView.addWelcomeTile(tile);
		Menu popupMenu = new Menu(tile);
		MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
		deleteItem.setText("Remove Shortcut");
		deleteItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				String extensionId = getExtensionId((IConfigurationElement)tile.getData(DATA_EXTENSION));
				removedTiles.add(extensionId);
				addedTiles.remove(extensionId);
				tile.setData(DATA_EXTENSION, null);
				updateTiles();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		tile.setData(DATA_POPUP_MENU, popupMenu);
		tiles.add(tile);
		return tile;
	}

	protected String getExtensionId(IConfigurationElement extension) {

		if(extension != null) {
			return extension.getContributor().getName() + "#" + extension.getAttribute(ATTRIBUTE_SECTION);
		}
		return "";
	}

	private void updateTiles() {

		// fetch unused ones ...
		List<IConfigurationElement> unusedExtensions = getUnusedExtensions();
		// fetch used ones..
		List<IConfigurationElement> used = getUsedExtensions();
		// fill space with defaultShow tiles if there is unused space except a final 'Add shortcut' tile ....
		for(Iterator<IConfigurationElement> iterator = unusedExtensions.iterator(); iterator.hasNext();) {
			if(used.size() < MAX_EXTENSION_TILES - 1) {
				IConfigurationElement defaultExtension = (IConfigurationElement)iterator.next();
				// fill space with ones defined as defaultShow
				if(Boolean.parseBoolean(defaultExtension.getAttribute(ATTRIBUTE_DEFAULTSHOW))) {
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
		for(TaskTile welcomeTile : tiles) {
			IConfigurationElement extension;
			if(used.isEmpty()) {
				extension = null;
			} else {
				extension = used.remove(0);
			}
			welcomeTile.setData(DATA_EXTENSION, extension);
			if(extension != null) {
				// a tile with an extension!
				String attribute = extension.getAttribute(ATTRIBUTE_PERSPECTIVE_ID);
				if(attribute != null && !attribute.isEmpty()) {
					welcomeTile.updateStyle(TaskTile.HIGHLIGHT);
				} else {
					welcomeTile.updateStyle(SWT.NONE);
				}
				welcomeTile.setContent(null, extension.getAttribute(ATTRIBUTE_SECTION), extension.getAttribute(ATTRIBUTE_DESCRIPTION));
				welcomeTile.setMenu((Menu)welcomeTile.getData(DATA_POPUP_MENU));
			} else {
				welcomeTile.setMenu(null);
				if(isLastEmptyTile(welcomeTile) && !unusedExtensions.isEmpty()) {
					welcomeTile.updateStyle(TaskTile.HIGHLIGHT | TaskTile.LARGE_TITLE);
					welcomeTile.setContent(null, "+", "Add shortcut");
				} else {
					// clear empty ones...
					welcomeTile.updateStyle(0);
					welcomeTile.setContent(null, "", "");
				}
			}
		}
		preferences.put(PREFERENCE_ADDED, serializeSet(addedTiles));
		preferences.put(PREFERENCE_REMOVED, serializeSet(removedTiles));
		try {
			preferences.flush();
		} catch(BackingStoreException e) {
			// we can't store then :-(
		}
	}

	private void tileSelected(TaskTile tile) {

		IConfigurationElement configurationElement = getExtension(tile);
		if(configurationElement != null) {
			welcomeView.switchPerspective(configurationElement.getAttribute(ATTRIBUTE_PERSPECTIVE_ID));
		} else if(isLastEmptyTile(tile)) {
			selectExtensionForTile(tile);
		}
	}

	private void selectExtensionForTile(TaskTile tile) {

		List<IConfigurationElement> allExtensions = getUnusedExtensions();
		// show selection dialog to the user...
		ExtensionsTileSelectionDialog dialog = new ExtensionsTileSelectionDialog(tile.getShell(), allExtensions.toArray(new IConfigurationElement[0]), new ColumnLabelProvider() {

			Map<IConfigurationElement, Image> images = new IdentityHashMap<>();

			@Override
			public String getText(Object element) {

				if(element instanceof IConfigurationElement) {
					return ((IConfigurationElement)element).getAttribute(ATTRIBUTE_SECTION);
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {

				if(element instanceof IConfigurationElement) {
					IConfigurationElement configurationElement = (IConfigurationElement)element;
					Image image = images.get(configurationElement);
					if(image == null) {
						MPerspective perspectiveModel = welcomeView.getPerspectiveModel(configurationElement.getAttribute(ATTRIBUTE_PERSPECTIVE_ID));
						if(perspectiveModel != null) {
							String iconURI = perspectiveModel.getIconURI();
							if(iconURI != null) {
								try {
									try (InputStream stream = new URL(iconURI).openStream()) {
										image = new Image(tile.getDisplay(), stream);
										images.put(configurationElement, image);
									}
								} catch(Exception e) {
									// can't load icon then
								}
							}
						}
					}
					return image;
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
			IConfigurationElement element = dialog.getSelectedElement();
			tile.setData(DATA_EXTENSION, element);
			String extensionId = getExtensionId(element);
			removedTiles.remove(extensionId);
			addedTiles.add(extensionId);
			updateTiles();
		}
	}

	public List<IConfigurationElement> getUnusedExtensions() {

		List<IConfigurationElement> allExtensions = getAllExtensions();
		// remove all used extension
		for(Iterator<IConfigurationElement> iterator = allExtensions.iterator(); iterator.hasNext();) {
			IConfigurationElement element = (IConfigurationElement)iterator.next();
			for(TaskTile welcomeTile : tiles) {
				if(element.equals(getExtension(welcomeTile))) {
					iterator.remove();
					break;
				}
			}
		}
		return allExtensions;
	}

	public List<IConfigurationElement> getUsedExtensions() {

		List<IConfigurationElement> usedExtensions = new ArrayList<>();
		for(TaskTile welcomeTile : tiles) {
			IConfigurationElement extension = getExtension(welcomeTile);
			if(extension != null) {
				usedExtensions.add(extension);
			}
		}
		return usedExtensions;
	}

	public List<IConfigurationElement> getAllExtensions() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		Arrays.sort(elements, EXTENSION_COMPARATOR);
		return new ArrayList<>(Arrays.asList(elements));
	}

	private static IConfigurationElement getExtension(TaskTile tile) {

		return (IConfigurationElement)tile.getData(DATA_EXTENSION);
	}

	private static boolean isLastEmptyTile(TaskTile tile) {

		if(tile.getData(DATA_EXTENSION) == null) {
			TaskTile prevTile = (TaskTile)tile.getData(DATA_PREV);
			if(prevTile == null) {
				// this is the first tile!
				return true;
			}
			return getExtension(prevTile) != null;
		}
		return false;
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
}
