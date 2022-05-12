package com.aleksey.combatradar.config;

import net.minecraft.world.entity.player.Player;

/**
 * @author Aleksey Terzi
 */
public class PlayerRadarEntityInfo extends RadarEntityInfo {
    private PlayerType _playerType;

    public PlayerType getPlayerType() {
        return _playerType;
    }

    public PlayerRadarEntityInfo(PlayerType playerType, String name, String iconPath, GroupType groupType) {
        super(Player.class.getCanonicalName() + "." + playerType, name, iconPath, groupType);
        _playerType = playerType;
    }
}
