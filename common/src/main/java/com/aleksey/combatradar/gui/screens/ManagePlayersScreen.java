package com.aleksey.combatradar.gui.screens;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.network.chat.TextComponent;

import java.awt.*;
import java.util.List;

/**
 * @author Aleksey Terzi
 */
public class ManagePlayersScreen extends Screen {
    private static final int SLOT_HEIGHT = 16;

    private class PlayerListItem extends AbstractSelectionList.Entry<PlayerListItem> {
        private final String _playerName;

        public String getPlayerName() {
            return _playerName;
        }

        public PlayerListItem(String playerName) {
            _playerName = playerName;
        }

        @Override
        // WTF??? Why Y and X are in wrong places???????
        public void render(PoseStack poseStack, int itemIndex, int y, int x, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_) {
            ManagePlayersScreen.this.font.drawShadow(poseStack, _playerName, x + 1, y + 1, Color.WHITE.getRGB());
        }

        @Override
        public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
            ManagePlayersScreen.this._playerListContainer.setSelected(this);
            ManagePlayersScreen.this._deleteButton.active = true;

            return true;
        }
    }

    private class PlayerListContainer extends AbstractSelectionList<PlayerListItem> {
        public PlayerListContainer() {
            super(ManagePlayersScreen.this.minecraft, ManagePlayersScreen.this.width, ManagePlayersScreen.this.height, 32, ManagePlayersScreen.this.height - 73, SLOT_HEIGHT);
        }

        @Override
        public void updateNarration(NarrationElementOutput p_169042_) { }
    }

    private static PlayerType _activePlayerType = PlayerType.Ally;

    private RadarConfig _config;
    private Screen _parent;
    private Button _allyButton;
    private Button _enemyButton;
    private Button _deleteButton;
    private PlayerListContainer _playerListContainer;

    public ManagePlayersScreen(Screen parent, RadarConfig config) {
        super(TextComponent.EMPTY);
        _parent = parent;
        _config = config;
    }

    @Override
    public void init() {
        int x = this.width / 2 - 100;
        int y = this.height - 72;

        addRenderableWidget(_playerListContainer = new PlayerListContainer());

        addRenderableWidget(_allyButton = new Button(x, y, 100, 20, new TextComponent("Allies"),
                btn -> loadPlayers(PlayerType.Ally)));

        addRenderableWidget(_enemyButton = new Button(x + 101, y, 100, 20, new TextComponent("Enemies"),
                btn -> loadPlayers(PlayerType.Enemy)));

        y += 24;
        addRenderableWidget(new Button(x, y, 100, 20, new TextComponent("Add Player"),
                btn -> this.minecraft.setScreen(new AddPlayerScreen(this, _config, _activePlayerType))));

        addRenderableWidget(_deleteButton = new Button(x + 101, y, 100, 20, new TextComponent("Delete Player"),
                btn -> deletePlayer()));

        y += 24;
        addRenderableWidget(new Button(x, y, 200, 20, new TextComponent("Done"),
                btn -> this.minecraft.setScreen(_parent)));

        loadPlayers(_activePlayerType);
    }

    private void loadPlayers(PlayerType playerType) {
        _activePlayerType = playerType;

        _playerListContainer.children().clear();

        List<String> players = _config.getPlayers(playerType);
        for (String playerName : players)
            _playerListContainer.children().add(new PlayerListItem(playerName));

        _allyButton.active = playerType != PlayerType.Ally;
        _enemyButton.active = playerType != PlayerType.Enemy;
        _deleteButton.active = false;
    }

    private void deletePlayer() {
        String playerName = _playerListContainer.getSelected().getPlayerName();

        _config.setPlayerType(playerName, PlayerType.Neutral);
        _config.save();

        loadPlayers(_activePlayerType);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(poseStack, mouseX, mouseY, partialTicks);

        this.drawCenteredString(poseStack, this.font, "Manage Players", this.width / 2, 20, Color.WHITE.getRGB());
    }
}