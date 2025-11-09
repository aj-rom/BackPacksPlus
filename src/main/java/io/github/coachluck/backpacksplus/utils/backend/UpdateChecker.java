/*
 *     File: UpdateChecker.java
 *     Last Modified: 9/4/20, 4:55 PM
 *     Project: BackPacksPlus
 *     Copyright (C) 2020 CoachL_ck
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.coachluck.backpacksplus.utils.backend;

import io.github.coachluck.backpacksplus.BackPacksPlus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public final class UpdateChecker {

    private static final String resourceId = "82612";

    public static void getVersion(final Consumer<String> consumer) {
        BackPacksPlus.runTaskAsynchronously(() -> {
            try {
                URI uri = new URI("https", "api.spigotmc.org", "/legacy/update.php", "resource=" + resourceId);
                URL resourceUrl = uri.toURL();
                InputStream inputStream = resourceUrl.openStream();
                Scanner scanner = new Scanner(inputStream);
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException | URISyntaxException exception) {
                ChatUtil.logMsg("&cFailed to check for updates: &e" + exception.getMessage());
            }
        });
    }

    // Attempt to extract integer values from a versioned string
    private static int parseVersionString(String version) {
        String reformatted = version.replaceAll("\\.", "");
        try {
            return Integer.parseInt(reformatted);
        } catch (NumberFormatException exception) {
            ChatUtil.logMsg("&cCannot parse version: &e" + reformatted);
            return -1;
        }
    }

    // Initiate the update task
    public static void checkForUpdate() {
        int currentVersion = parseVersionString(BackPacksPlus.getInstance().getDescription().getVersion());
        getVersion( newVersionString -> {
            int newVersion = parseVersionString(newVersionString);
            if (currentVersion >= newVersion) {
                ChatUtil.logMsg("&aYou are running the latest version.");
                return;
            }

            ChatUtil.logMsg("&aThere is a new update available. &ehttps://www.spigotmc.org/resources/b.82612/");
        });
    }
}
