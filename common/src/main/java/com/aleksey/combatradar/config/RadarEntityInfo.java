package com.aleksey.combatradar.config;

import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, ResourceLocation> _entities;
    private ResourceLocation _defaultIcon;
    private String _name;
    private GroupType _groupType;
    private boolean _enabled;

    public String getName() { return _name; }
    public GroupType getGroupType() { return _groupType; }

    public ResourceLocation getIcon(Entity entity)
    {
        if (entity == null || _entities.size() == 1)
            return _defaultIcon;

        var entityClassName = entity.getClass().getCanonicalName();

        return _entities.get(entityClassName);
    }

    public boolean getEnabled() { return _enabled; }
    public void setEnabled(boolean value) { _enabled = value; }

    public RadarEntityInfo(Class<? extends Entity> entityClass, String name, String iconPath, GroupType groupType) {
        this(entityClass.getCanonicalName(), name, iconPath, groupType);
    }

    protected RadarEntityInfo(String entityClass, String name, String iconPath, GroupType groupType) {
        _name = name;
        _groupType = groupType;
        _enabled = true;

        _entities = new HashMap<>();
        _entities.put(entityClass, _defaultIcon = new ResourceLocation("combatradar", iconPath));
    }

    public RadarEntityInfo addEntity(Class<? extends Entity> entityClass, String iconPath) {
        var icon = iconPath != null ? new ResourceLocation("combatradar", iconPath) : null;
        _entities.put(entityClass.getCanonicalName(), icon);
        return this;
    }

    public void addToMap(Map<String, RadarEntityInfo> map) {
        for (var entityClass : _entities.keySet())
            map.put(entityClass, this);
    }
}
