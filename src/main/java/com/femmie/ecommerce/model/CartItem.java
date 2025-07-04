package com.femmie.ecommerce.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void setTotalPrice() {
        if (unitPrice != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}
