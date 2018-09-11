package com.aleksey.combatradar.gui;

import com.aleksey.combatradar.config.GroupType;
import com.aleksey.combatradar.config.RadarConfig;
import com.aleksey.combatradar.config.RadarEntityInfo;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mumfrey.liteloader.gl.GL.*;

/**
 * @author Aleksey Terzi
 */
public class GuiEntityScreen extends GuiScreen {
    private static class EntityGroup {
        public GroupType groupType;
        public List<RadarEntityInfo> entities;
        public List<Integer> listColTextWidth;

        public int getColWidth(int colIndex) {
            return ICON_WIDTH + listColTextWidth.get(colIndex) + BUTTON_WIDTH + 25;
        }

        public int getIconAndTextWidth(int colIndex) {
            return ICON_WIDTH + listColTextWidth.get(colIndex) + 1;
        }

        public int getTotalWidth() {
            int totalWidth = 0;

            for(int i = 0; i < listColTextWidth.size(); i++)
                totalWidth += getColWidth(i);

            return totalWidth;
        }

        public EntityGroup(GroupType groupType) {
            this.groupType = groupType;
            this.entities = new ArrayList<RadarEntityInfo>();
            this.listColTextWidth = new ArrayList<Integer>();
        }
    }

    private static final int BUTTON_ID_NEUTRAL = 1;
    private static final int BUTTON_ID_AGGRESSIVE = 2;
    private static final int BUTTON_ID_OTHER = 3;
    private static final int BUTTON_ID_ENABLE = 4;
    private static final int BUTTON_ID_DONE = 5;
    private static final int BUTTON_ID_FIRSTICON = 100;

    private static final int MAX_ENTITIES_PER_COL = 8;
    private static final int ICON_WIDTH = 12;
    private static final int LINE_HEIGHT = 16;
    private static final int BUTTON_WIDTH = 24;

    private static GroupType _activeGroupType = GroupType.Neutral;

    private RadarConfig _config;
    private GuiScreen _parent;
    private GuiButton _enableButton;

    private Map<GroupType, EntityGroup> _groups;
    private int _titleTop;
    private int _buttonTop;
    private int _iconTop, _iconLeft;
    private EntityGroup _activeGroup;
    private String _groupName;

    public GuiEntityScreen(GuiScreen parent, RadarConfig config) {
        _parent = parent;
        _config = config;
    }

    @Override
    public void initGui() {
        _titleTop = this.height / 4 - 40;
        _buttonTop = this.height - this.height / 4 - 10;
        _iconTop = _titleTop + 16 + (this.height - (this.height - _buttonTop) - _titleTop - 16 - MAX_ENTITIES_PER_COL * LINE_HEIGHT) / 2;

        createEntityGroups();
        showGroup(_activeGroupType);
    }

    private void createEntityGroups() {
        _groups = new HashMap<GroupType, EntityGroup>();

        for(RadarEntityInfo info : _config.getEntities()) {
            EntityGroup group = _groups.get(info.getGroupType());

            if(group == null)
                _groups.put(info.getGroupType(), group = new EntityGroup(info.getGroupType()));

            int colIndex = group.entities.size() / MAX_ENTITIES_PER_COL;
            int textWidth = this.fontRenderer.getStringWidth(info.getName());

            if(group.listColTextWidth.size() <= colIndex)
                group.listColTextWidth.add(textWidth);
            else if(group.listColTextWidth.get(colIndex) < textWidth)
                group.listColTextWidth.set(colIndex, textWidth);

            group.entities.add(info);
        }
    }

    private void showGroup(GroupType groupType) {
        _activeGroupType = groupType;
        _activeGroup = _groups.get(groupType);
        _iconLeft = (this.width - _activeGroup.getTotalWidth() + 25) / 2;

        this.buttonList.clear();

        int y = _buttonTop;
        int x = this.width / 2 - 100;

        GuiButton neutralButton, aggressiveButton, otherButton;

        this.buttonList.add(neutralButton = new GuiButton(BUTTON_ID_NEUTRAL, x, y, 66, 20, "Neutral"));
        this.buttonList.add(aggressiveButton = new GuiButton(BUTTON_ID_AGGRESSIVE, x + 66 + 1, y, 66, 20, "Agressive"));
        this.buttonList.add(otherButton = new GuiButton(BUTTON_ID_OTHER, x + 66 + 1 + 66 + 1, y, 66, 20, "Other"));

        switch(groupType) {
            case Neutral:
                neutralButton.enabled = false;
                _groupName = "Neutral";
                break;
            case Aggressive:
                aggressiveButton.enabled = false;
                _groupName = "Aggressive";
                break;
            case Other:
                otherButton.enabled = false;
                _groupName = "Other";
                break;
        }

        y += 24;
        this.buttonList.add(_enableButton = new GuiButton(BUTTON_ID_ENABLE, x, y, 200, 20, getEnableButtonText()));
        y += 24;
        this.buttonList.add(new GuiButton(BUTTON_ID_DONE, x, y, 200, 20, "Done"));

        addIconButtons();
    }

    private String getEnableButtonText() {
        return _groupName + " Entities: " + (_config.isGroupEnabled(_activeGroupType) ? "On" : "Off");
    }

    private void addIconButtons() {
        int colIndex = 0;
        int rowIndex = 0;
        int colX = _iconLeft;
        int buttonX = colX + _activeGroup.getIconAndTextWidth(colIndex);
        int buttonY = _iconTop - 4;
        int buttonHeight = LINE_HEIGHT - 1;

        for(RadarEntityInfo info : _activeGroup.entities) {
            if(rowIndex == MAX_ENTITIES_PER_COL) {
                colX += _activeGroup.getColWidth(colIndex);

                colIndex++;
                rowIndex = 0;

                buttonX = colX + _activeGroup.getIconAndTextWidth(colIndex);
                buttonY = _iconTop - 4;
            }

            String buttonText = info.getEnabled() ? "on": "off";
            this.buttonList.add(new GuiButton(BUTTON_ID_FIRSTICON + info.getId(), buttonX, buttonY, BUTTON_WIDTH, buttonHeight, buttonText));

            buttonY += LINE_HEIGHT;

            rowIndex++;
        }
    }

    @Override
    public void onGuiClosed() {
        _config.save();
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if(!guiButton.enabled)
            return;

        switch(guiButton.id) {
            case BUTTON_ID_NEUTRAL:
                showGroup(GroupType.Neutral);
                break;
            case BUTTON_ID_AGGRESSIVE:
                showGroup(GroupType.Aggressive);
                break;
            case BUTTON_ID_OTHER:
                showGroup(GroupType.Other);
                break;
            case BUTTON_ID_ENABLE:
                _config.setGroupEnabled(_activeGroupType, !_config.isGroupEnabled(_activeGroupType));
                _config.save();
                _enableButton.displayString = getEnableButtonText();
                break;
            case BUTTON_ID_DONE:
                mc.displayGuiScreen(_parent);
                break;
            default:
                RadarEntityInfo info = _config.getEntities().get(guiButton.id - BUTTON_ID_FIRSTICON);
                info.setEnabled(!info.getEnabled());
                _config.save();
                guiButton.displayString = info.getEnabled() ? "on": "off";
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        drawCenteredString(this.fontRenderer, "Radar Entities", this.width / 2, _titleTop, Color.WHITE.getRGB());
        renderIcons();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void renderIcons() {
        int colIndex = 0;
        int rowIndex = 0;
        int x = _iconLeft;
        int y = _iconTop;

        for(RadarEntityInfo info : _activeGroup.entities) {
            if(rowIndex == MAX_ENTITIES_PER_COL) {
                x += _activeGroup.getColWidth(colIndex);
                y = _iconTop;

                colIndex++;
                rowIndex = 0;
            }

            renderIcon(x, y + 4, info);

            Color color = info.getEnabled() ? Color.WHITE : Color.DARK_GRAY;
            this.fontRenderer.drawStringWithShadow(info.getName(), x + ICON_WIDTH, y, color.getRGB());

            y += LINE_HEIGHT;

            rowIndex++;
        }
    }

    private void renderIcon(float x, float y, RadarEntityInfo info) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(0.6f, 0.6f, 0.6f);
        glColor4f(1f, 1f, 1f, 1f);

        mc.getTextureManager().bindTexture(info.getIcon());

        Gui.drawModalRectWithCustomSizedTexture(-8, -8, 0, 0, 16, 16, 16, 16);

        glPopMatrix();
    }
}