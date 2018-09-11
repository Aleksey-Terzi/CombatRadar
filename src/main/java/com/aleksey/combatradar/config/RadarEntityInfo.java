package com.aleksey.combatradar.config;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.util.Comparator;

/**
 * @author Aleksey Terzi
 */
public class RadarEntityInfo {
    public static class EntityComparator implements Comparator<RadarEntityInfo> {
        @Override
        public int compare(RadarEntityInfo i1, RadarEntityInfo i2) {
            return i1._name.compareTo(i2._name);
        }
    }

    private int _id;
    private Class<? extends Entity> _entityClass;
    private String _name;
    private ResourceLocation _icon;
    private GroupType _groupType;
    private boolean _enabled;

    public int getId() { return _id; }
    public void setId(int value) { _id = value; }

    public Class<? extends Entity> getEntityClass() { return _entityClass; }
    public String getName() { return _name; }
    public ResourceLocation getIcon() { return _icon; }
    public GroupType getGroupType() { return _groupType; }

    public boolean getEnabled() { return _enabled; }
    public void setEnabled(boolean value) { _enabled = value; }

    public RadarEntityInfo(Class<? extends Entity> entityClass, String name, String iconPath, GroupType groupType) {
        _entityClass = entityClass;
        _name = name;
        _icon = new ResourceLocation("combatradar", iconPath);
        _groupType = groupType;
        _enabled = true;
    }
}
