package es.uva.locomotion.model;

import java.util.Objects;

public class Module extends IssuableAbs implements Comparable<Module> {
    private String name;

    public Module(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Module module = (Module) o;
        return name.equals(module.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                (invalidReason != null ? ("invalidReason='" + invalidReason + '\'') : "") +
                '}';
    }

    @Override
    public int compareTo(Module o) {
        return name.compareTo(o.getName());
    }
}
