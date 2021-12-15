package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "PaymentType")
public class PaymentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TypeId")
    private int typeId;
    @Column(name = "TypeName")
    private String typeName;
    @Column(name = "ImageSrc")
    private String imageSrc;

    public PaymentType constructEntity(String typeName, String imageSrc) {
        PaymentType paymentType = new PaymentType();
        paymentType.setTypeName(typeName);
        paymentType.setImageSrc(imageSrc);
        return paymentType;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int categoryId) {
        this.typeId = categoryId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String name) {
        this.typeName = name;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageType) {
        this.imageSrc = imageType;
    }
}
