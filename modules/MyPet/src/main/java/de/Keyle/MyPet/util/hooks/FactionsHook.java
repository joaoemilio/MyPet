/*
 * This file is part of MyPet
 *
 * Copyright © 2011-2016 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.util.hooks;

import com.massivecraft.factions.engine.EngineMain;
import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.util.hooks.PluginHookName;
import de.Keyle.MyPet.api.util.hooks.types.PlayerVersusPlayerHook;
import de.Keyle.MyPet.util.PluginHook;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@PluginHookName("Factions")
public class FactionsHook extends PluginHook implements PlayerVersusPlayerHook {

    protected EngineMain engineMain;

    @Override
    public boolean onEnable() {
        if (Configuration.Hooks.USE_Factions) {
            engineMain = EngineMain.get();
            return true;
        }
        return false;
    }

    @Override
    public boolean canHurt(Player attacker, Player defender) {
        try {
            EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(attacker, defender, EntityDamageEvent.DamageCause.CUSTOM, 0.);
            return engineMain.canCombatDamageHappen(sub, false);
        } catch (Throwable ignored) {
        }
        return true;
    }
}