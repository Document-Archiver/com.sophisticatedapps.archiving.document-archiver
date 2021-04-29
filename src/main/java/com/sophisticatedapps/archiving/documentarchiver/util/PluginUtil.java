/*
 * Copyright 2021 by Stephan Sann (https://github.com/stephansann)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sophisticatedapps.archiving.documentarchiver.util;

import com.sophisticatedapps.archiving.documentarchiver.api.ArchiveBrowsingService;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class PluginUtil {

    private static File pluginDirectory = new File(AppDirUtil.getUserDataDir(), "plugins");

    private static ClassLoader pluginClassLoader;
    private static List<String> pluginNamesList = Collections.emptyList();
    private static ModuleLayer pluginModuleLayer;

    static {

        if (pluginDirectory.exists()) {

            try {

                File[] tmpJarFiles = pluginDirectory.listFiles((aDir, aName) -> aName.endsWith(".jar"));
                URL[] tmpJarFileURLs = new URL[Objects.requireNonNull(tmpJarFiles).length];

                for (int i = 0; i < tmpJarFiles.length; i++) {

                    tmpJarFileURLs[i] = tmpJarFiles[i].toURI().toURL();
                }

                pluginClassLoader = new URLClassLoader(tmpJarFileURLs, Thread.currentThread().getContextClassLoader());

                // Search for plugins in the plugins directory
                ModuleFinder tmpPluginsFinder = ModuleFinder.of(pluginDirectory.toPath());

                // Find all names of all found plugin modules
                pluginNamesList = tmpPluginsFinder
                        .findAll()
                        .stream()
                        .map(ModuleReference::descriptor)
                        .map(ModuleDescriptor::name)
                        .collect(Collectors.toList());

                // Create configuration that will resolve plugin modules
                // (verify that the graph of modules is correct)
                Configuration tmpPluginsConfiguration = ModuleLayer
                        .boot()
                        .configuration()
                        .resolve(tmpPluginsFinder, ModuleFinder.of(), pluginNamesList);

                // Create a module layer for plugins
                pluginModuleLayer = ModuleLayer
                        .boot()
                        .defineModulesWithOneLoader(tmpPluginsConfiguration, pluginClassLoader);

            }
            catch (Exception e) {

                throw (new RuntimeException("Could not load plugin(s): " + e.getMessage()));
            }
        }
    }

    /**
     * Private constructor.
     */
    private PluginUtil() {
    }

    public static boolean isPluginAvailable(String aPluginName) {

        return pluginNamesList.contains(aPluginName);
    }

    public static void fireArchiveBrowsingPlugin() {

        Thread.currentThread().setContextClassLoader(pluginClassLoader);
        ServiceLoader<ArchiveBrowsingService> loader =
                ServiceLoader.load(pluginModuleLayer, ArchiveBrowsingService.class);
        ArchiveBrowsingService service = loader.iterator().next();
        service.startBrowsing();
    }

}
