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

import de.Keyle.MyPet.api.Configuration;
import de.Keyle.MyPet.api.util.hooks.PluginHookName;
import de.Keyle.MyPet.api.util.hooks.types.PlayerVersusEntityHook;
import de.Keyle.MyPet.api.util.hooks.types.PlayerVersusPlayerHook;
import de.Keyle.MyPet.util.PluginHook;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

@PluginHookName("GriefPrevention")
public class GriefPreventionHook extends PluginHook implements PlayerVersusEntityHook, PlayerVersusPlayerHook {

    protected GriefPrevention griefPrevention;

    @Override
    public boolean onEnable() {
        if (Configuration.Hooks.USE_GriefPrevention) {
            griefPrevention = GriefPrevention.instance;
            return griefPrevention != null;
        }
        return false;
    }

    @Override
    public boolean canHurt(Player attacker, Entity defender) {
        try {
            if (griefPrevention.pvpRulesApply(attacker.getWorld())) {
                if (attacker != defender) {
                    DataStore dataStore = griefPrevention.dataStore;

                    PlayerData defenderData = dataStore.getPlayerData(defender.getUniqueId());
                    PlayerData attackerData = dataStore.getPlayerData(attacker.getUniqueId());
                    if (griefPrevention.config_pvp_protectFreshSpawns) {
                        if (defenderData.pvpImmune || attackerData.pvpImmune) {
                            return false;
                        }
                    }
                    if ((griefPrevention.config_pvp_noCombatInPlayerLandClaims) || (griefPrevention.config_pvp_noCombatInAdminLandClaims)) {
                        Claim attackerClaim = dataStore.getClaimAt(attacker.getLocation(), false, attackerData.lastClaim);
                        if (!attackerData.ignoreClaims) {
                            if ((attackerClaim != null) && (!attackerData.inPvpCombat())) {
                                if (attackerClaim.isAdminClaim() && attackerClaim.parent == null && griefPrevention.config_pvp_noCombatInAdminLandClaims) {
                                    return false;
                                }
                                if (attackerClaim.isAdminClaim() && attackerClaim.parent != null && griefPrevention.config_pvp_noCombatInAdminSubdivisions) {
                                    return false;
                                }
                                if (!attackerClaim.isAdminClaim() && griefPrevention.config_pvp_noCombatInPlayerLandClaims) {
                                    return false;
                                }
                            }
                            Claim defenderClaim = dataStore.getClaimAt(defender.getLocation(), false, defenderData.lastClaim);
                            if (defenderClaim != null && !defenderData.inPvpCombat()) {
                                if (defenderClaim.isAdminClaim() && defenderClaim.parent == null && griefPrevention.config_pvp_noCombatInAdminLandClaims) {
                                    return false;
                                }
                                if (defenderClaim.isAdminClaim() && defenderClaim.parent != null && griefPrevention.config_pvp_noCombatInAdminSubdivisions) {
                                    return false;
                                }
                                if (!defenderClaim.isAdminClaim() && griefPrevention.config_pvp_noCombatInPlayerLandClaims) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return true;
    }

    @Override
    public boolean canHurt(Player attacker, Player defender) {
        return canHurt(attacker, (Entity) defender);
    }
}