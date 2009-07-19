// Copyright (c) 2009 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.debug.ui.launcher;

import java.io.IOException;

import org.chromium.debug.core.model.DebugTargetImpl;
import org.chromium.debug.ui.ChromiumDebugUIPlugin;
import org.chromium.debug.ui.DialogBasedTabSelector;
import org.chromium.debug.ui.PluginUtil;
import org.chromium.sdk.Browser;
import org.chromium.sdk.BrowserFactory;
import org.chromium.sdk.UnsupportedVersionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

/**
 * A launch configuration delegate for the JavaScript debugging.
 */
public class LaunchType implements ILaunchConfigurationDelegate {

  /** Launch configuration attribute (debug port). */
  public static final String CHROMIUM_DEBUG_PORT = "debug_port"; //$NON-NLS-1$

  /** Launch configuration attribute (target project name). */
  public static final String CHROMIUM_DEBUG_PROJECT_NAME = "debug_project_name"; //$NON-NLS-1$

  public void launch(ILaunchConfiguration config, String mode, ILaunch launch,
      IProgressMonitor monitor) throws CoreException {
    // Chromium JavaScript launch is only supported for debugging.
    if (mode.equals(ILaunchManager.DEBUG_MODE)) {
      int port =
          config.getAttribute(LaunchType.CHROMIUM_DEBUG_PORT,
              PluginVariablesUtil.getValueAsInt(PluginVariablesUtil.DEFAULT_PORT));
      String projectName =
          config.getAttribute(LaunchType.CHROMIUM_DEBUG_PROJECT_NAME,
              PluginVariablesUtil.getValue(PluginVariablesUtil.DEFAULT_PROJECT_NAME));

      Browser browser = BrowserFactory.getInstance().create(port);
      try {
        browser.connect();
      } catch (UnsupportedVersionException e) {
        throw newCoreException(e);
      } catch (IOException e) {
        throw newCoreException(e);
      }
      DebugTargetImpl target = new DebugTargetImpl(launch, browser);
      try {
        boolean attached = target.attach(
            projectName,
            new DialogBasedTabSelector(),
            new Runnable() {
              public void run() {
                PluginUtil.openProjectExplorerView();
              }
            },
            monitor);
        if (!attached) {
          terminateTarget(target);
        }
      } catch (CoreException e) {
        terminateTarget(target);
        throw e;
      } catch (RuntimeException e) {
        terminateTarget(target);
        throw e;
      } finally {
        launch.addDebugTarget(target);
        monitor.done();
      }
    }
  }

  private static void terminateTarget(DebugTargetImpl target) {
    target.setDisconnected(true);
    target.fireTerminateEvent();
  }

  private static CoreException newCoreException(Exception e) {
    return new CoreException(
        new Status(Status.ERROR, ChromiumDebugUIPlugin.PLUGIN_ID,
            "Failed to connect to the remote browser", e)); //$NON-NLS-1$
  }

}