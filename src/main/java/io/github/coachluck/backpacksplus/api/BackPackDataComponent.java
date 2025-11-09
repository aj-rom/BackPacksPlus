package io.github.coachluck.backpacksplus.api;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackPackDataComponent implements CustomModelDataComponent {

    public static BackPackDataComponent simpleData(double data) {
        BackPackDataComponent inst = new BackPackDataComponent();
        inst.setFloats(List.of((float) data));
        return inst;
    }

    public static BackPackDataComponent defaultInstance() {
        return simpleData(0);
    }

    @Getter
    @Setter
    public List<Color> colors = List.of();

    @Getter
    @Setter
    public List<Boolean> flags = List.of();

    @Getter
    @Setter
    public List<Float> floats;

    @Getter
    @Setter
    public List<String> strings = List.of();

    @Override
    @NonNull
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("colors", colors.stream().map(Color::serialize));
        map.put("flags", flags);
        map.put("floats", floats);
        map.put("strings", strings);
        return map;
    }
}
