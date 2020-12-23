package es.uva.locomotion.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ModulesList {

    private List<String> modules;

    public ModulesList() {
        modules = new ArrayList<>();
    }

    public List<String> getModules() {
        List<String> mod = new ArrayList<>(modules);
        mod.sort(Comparator.comparing(String::toString));
        return mod;
    }

    public void add(String module) {
        modules.add(module);
    }

    public boolean contains(String module) {
        return modules.contains(module);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModulesList that = (ModulesList) o;
        return Objects.equals(modules, that.modules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modules);
    }
}
