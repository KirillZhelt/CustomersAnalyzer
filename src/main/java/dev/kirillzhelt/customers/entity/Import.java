package dev.kirillzhelt.customers.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

public class Import {

    @Id
    private Integer id;

    private boolean visible = true;

    public Import() {}

    public Import(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return "Import{" +
            "id=" + id +
            '}';
    }
}
