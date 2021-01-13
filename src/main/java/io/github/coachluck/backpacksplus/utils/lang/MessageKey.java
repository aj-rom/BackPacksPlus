/*
 *     File: MessageKey.java
 *     Last Modified: 1/12/21, 12:51 PM
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

package io.github.coachluck.backpacksplus.utils.lang;

import lombok.Getter;

public enum MessageKey {

    /** General Section **/
    PERMISSION_COMMAND("General.Permission"),
    PERMISSION_CRAFT("General.CraftPerm"),
    PERMISSION_RENAME("General.NoRenamePerm"),
    PERMISSION_USE("General.Use"),
    OFFLINE_PLAYER("General.Offline-Player", "%player%"),
    RELOAD("General.Reload"),
    INCORRECT_ARGS("General.BadArgs"),
    NO_CONSOLE("General.Console"),
    NOT_FOUND("General.NotFound", "%backpack%"),
    OVER_LIMIT("General.OverLimit", "%removed%", "%limit%"),

    /** BackPack Section **/
    BACKPACK_CRAFT("BackPack.OnCraft", "%backpack%"),
    BACKPACK_GIVE("BackPack.OnGive", "%player%", "%amt%", "%backpack%"),
    BACKPACK_RECEIVE("BackPack.OnReceive", "%amt%", "%backpack%"),
    BACKPACK_RECIPE_HEADER("BackPack.Recipe-View.Header"),
    BACKPACK_RECIPE_BODY("BackPack.Recipe-View.Body", "%backpack%"),
    BACKPACK_RECIPE_FOOTER("BackPack.Recipe-View.Footer"),

    /** Help Section **/
    HELP("Help");

    @Getter
    private final String key;

    @Getter
    private final String[] tags;

    MessageKey(String key, String... tags) {
        this.key = key;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return key;
    }
}
