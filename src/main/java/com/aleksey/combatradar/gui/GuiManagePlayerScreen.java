package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.PlayerType;
import com.aleksey.combatradar.config.RadarConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * @author Aleksey Terzi
 */
public class GuiManagePlayerScreen extends GuiScreen {
    private static final int BUTTON_ID_ALLIES = 0;
    private static final int BUTTON_ID_ENEMIES = 1;
    private static final int BUTTON_ID_ADD = 2;
    private static final int BUTTON_ID_DELETE = 3;
    private static final int BUTTON_ID_DONE = 100;

    private static final int SLOT_HEIGHT = 16;

    private class PlayerListContainer extends GuiSlot {
        public PlayerListContainer() {
            super(GuiManagePlayerScreen.this.mc, GuiManagePlayerScreen.this.width, GuiManagePlayerScreen.this.height, 32, GuiManagePlayerScreen.this.height - 73, SLOT_HEIGHT);
        }

        @Override
        protected int getSize() {
            return GuiManagePlayerScreen.this._players.size();
        }

        @Override
        protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
            GuiManagePlayerScreen.this._playerIndex = slotIndex;

            boolean isValidSlot = slotIndex >= 0 && slotIndex < getSize();

            GuiManagePlayerScreen.this._deleteButton.enabled = isValidSlot;
        }

        @Override
        protected boolean isSelected(int slotIndex) {
            return slotIndex == GuiManagePlayerScreen.this._playerIndex;
        }

        @Override
        protected int getContentHeight() {
            return getSize() * SLOT_HEIGHT;
        }

        @Override
        protected void drawBackground() {
            GuiManagePlayerScreen.this.drawDefaultBackground();
        }

        @Override
        protected void drawSlot(int entryId, int par2, int par3, int par4, int par5, int par6, float par7) {
            String playerName = GuiManagePlayerScreen.this._players.get(entryId);
            GuiManagePlayerScreen.this.drawString(mc.fontRenderer, playerName, par2 + 1, par3 + 1, Color.WHITE.getRGB());
        }
    }

    private static PlayerType _activePlayerType = PlayerType.Ally;

    private RadarConfig _config;
    private GuiScreen _parent;
    private GuiButton _allyButton;
    private GuiButton _enemyButton;
    private GuiButton _deleteButton;
    private PlayerListContainer _playerListContainer;

    private List<String> _players;
    private int _playerIndex = -1;

    public GuiManagePlayerScreen(GuiScreen parent, RadarConfig config) {
        _parent = parent;
        _config = config;
    }

    @Override
    public void initGui() {
        int x = this.width / 2 - 100;
        int y = this.height - 72;

        this.buttonList.clear();
        this.buttonList.add(_allyButton = new GuiButton(BUTTON_ID_ALLIES, x, y, 100, 20, "Allies"));
        this.buttonList.add(_enemyButton = new GuiButton(BUTTON_ID_ENEMIES, x + 101, y, 100, 20, "Enemies"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_ADD, x, y, 100, 20, "Add Player"));
        this.buttonList.add(_deleteButton = new GuiButton(BUTTON_ID_DELETE, x + 101, y, 100, 20, "Delete Player"));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, x, y, 200, 20, "Done"));

        _playerListContainer = new PlayerListContainer();
        _playerListContainer.registerScrollButtons(101, 102);

        showPlayers(_activePlayerType);
    }

    private void showPlayers(PlayerType playerType) {
        _activePlayerType = playerType;

        _players = _config.getPlayers(playerType);
        _playerIndex = _players.size() > 0 ? 0 : -1;

        _allyButton.enabled = playerType != PlayerType.Ally;
        _enemyButton.enabled = playerType != PlayerType.Enemy;
        _deleteButton.enabled = _playerIndex >= 0;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        _playerListContainer.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)	 {
        if(!guiButton.enabled)
            return;

        switch(guiButton.id) {
            case BUTTON_ID_ALLIES:
                showPlayers(PlayerType.Ally);
                break;
            case BUTTON_ID_ENEMIES:
                showPlayers(PlayerType.Enemy);
                break;
            case BUTTON_ID_ADD:
                mc.displayGuiScreen(new GuiAddPlayerScreen(this, _config, _activePlayerType));
                break;
            case BUTTON_ID_DELETE:
                _config.setPlayerType(_players.get(_playerIndex), PlayerType.Neutral);
                _config.save();
                showPlayers(_activePlayerType);
                break;
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawBackground(0);
        _playerListContainer.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, "Manage Players", this.width / 2, 20, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}