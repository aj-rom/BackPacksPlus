/*
 *     File: Reflector.java
 *     Last Modified: 1/14/21, 10:30 PM
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

package io.github.coachluck.backpacksplus.utils.multiversion;

import lombok.Getter;
import org.bukkit.Bukkit;

public class Reflector {

    @Getter
    private static final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    @Getter
    private final MultiVersionUtil multiVersionUtil;

    public Reflector()
    {
        if(serverVersion.startsWith("v1_14"))
            multiVersionUtil = new MultiVersionUtil_1_14();

        else
            multiVersionUtil = new MultiVersionUtil_1_15();
    }
}
