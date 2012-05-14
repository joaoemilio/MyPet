/*
 * Copyright (C) 2011-2012 Keyle
 *
 * This file is part of MyPet
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
 * along with MyPet. If not, see <http://www.gnu.org/licenses/>.
 */

package de.Keyle.MyPet.entity.pathfinder;

import de.Keyle.MyPet.entity.types.EntityMyPet;
import de.Keyle.MyPet.entity.types.MyPet;
import de.Keyle.MyPet.skill.skills.Behavior;
import de.Keyle.MyPet.util.MyPetUtil;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PathfinderGoalTarget;
import org.bukkit.entity.Player;

import java.util.List;

public class PathfinderGoalAggressiveTarget extends PathfinderGoalTarget
{
    private MyPet MPet;
    private EntityMyPet wolf;
    private EntityLiving target;
    private float range;

    public PathfinderGoalAggressiveTarget(MyPet MPet, float range)
    {
        super(MPet.Pet.getHandle(), 32.0F, false);
        this.wolf = MPet.Pet.getHandle();
        this.MPet = MPet;
        this.range = range;
    }

    public boolean a()
    {
        if (MPet.getSkillSystem().hasSkill("Behavior"))
        {
            Behavior behavior = (Behavior) MPet.getSkillSystem().getSkill("Behavior");
            if (behavior.getLevel() > 0)
            {
                if (behavior.getBehavior() == Behavior.BehaviorState.Friendly)
                {
                    return false;
                }
                else if (behavior.getBehavior() == Behavior.BehaviorState.Aggressive && !MPet.isSitting())
                {
                    if (target == null || !target.isAlive())
                    {
                        List list = this.wolf.world.a(EntityLiving.class, this.wolf.boundingBox.grow((double) this.range, 4.0D, (double) this.range));

                        for (Object aList : list)
                        {
                            Entity entity = (Entity) aList;
                            EntityLiving entityliving = (EntityLiving) entity;

                            if (wolf.am().canSee(entityliving) && entityliving != wolf)
                            {
                                if (entityliving instanceof EntityPlayer)
                                {
                                    String playerName = ((EntityPlayer) entityliving).name;
                                    if (!MyPetUtil.getOfflinePlayer(playerName).isOnline())
                                    {
                                        continue;
                                    }
                                    Player target = MyPetUtil.getOfflinePlayer(playerName).getPlayer();

                                    if (target == MPet.getOwner())
                                    {
                                        continue;
                                    }
                                    if (!MyPetUtil.canHurtFactions(MPet.getOwner().getPlayer(), target))
                                    {
                                        continue;
                                    }
                                    if (!MyPetUtil.canHurtTowny(MPet.getOwner().getPlayer(), target))
                                    {
                                        continue;
                                    }
                                    if (!MyPetUtil.canHurtWorldGuard(target))
                                    {
                                        continue;
                                    }
                                }
                                this.target = entityliving;
                                return true;
                            }
                        }
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void c()
    {
        wolf.b(this.target);
        super.c();
    }
}