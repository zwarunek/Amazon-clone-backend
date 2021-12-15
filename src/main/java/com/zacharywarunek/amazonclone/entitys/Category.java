package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "Category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CID")
    private int categoryId;
    @Column(name = "Name")
    private String name;

    public Category constructEntity(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
