package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;

@Entity
@Table(name = "PaymentType")
public class PaymentType {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "TypeId")
    private int typeId;
    @Column(name = "TypeName")
    private String typeName;

    public PaymentType constructEntity(String typeName) {
        PaymentType paymentType = new PaymentType();
        paymentType.setTypeName(typeName);
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
}
