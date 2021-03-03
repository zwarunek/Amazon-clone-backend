package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;

@Entity
@Table(name = "ProductImages")
public class ProductImages {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "PID")
    private int ProductId;
    @Column(name = "Image")
    private String image;

    public ProductImages constructEntity(int productId, String image) {
        ProductImages productImages = new ProductImages();
        productImages.setProductId(productId);
        productImages.setImage(image);
        return productImages;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int categoryId) {
        this.ProductId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String name) {
        this.image = name;
    }
}
