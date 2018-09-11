package com.aleksey.combatradar.config;

import net.minecraft.entity.player.EntityPlayer;

/**
 * @author Aleksey Terzi
 */
public class PlayerRadarEntityInfo extends RadarEntityInfo {
    private PlayerType _playerType;

    public PlayerType getPlayerType() {
        return _playerType;
    }

    public PlayerRadarEntityInfo(PlayerType playerType, String name, String iconPath, GroupType groupType) {
        super(EntityPlayer.class, name, iconPath, groupType);
        _playerType = playerType;
    }
}
