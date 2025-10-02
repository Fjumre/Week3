package app.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    @Column(name= "pId", nullable= false, unique= true)
    private int pId;

    @Column(name= "name", unique = true, nullable= false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name= "mfr", nullable= false)
    private Manufacturer mfr;

    @Enumerated(EnumType.STRING)
    @Column(name= "type", nullable= false)
    private ProductType type;

    @Column(name= "calories", nullable= false)
    private int calories;

    @Column(name= "protein", nullable= false)
    private int protein;

    @Column(name= "fat", nullable= false)
    private int fat;

    @Column(name= "sodium", nullable= false)
    private int sodium;

    @Column(name= "fiber", nullable= false)
    private float fiber;

    @Column(name= "carbo", nullable= false)
    private float carbo;

    @Column(name= "sugars", nullable= false)
    private int sugars;

    @Column(name= "potass", nullable= false)
    private int potass;

    @Column(name= "vitamins", nullable= false)
    private int vitamins;

    @Column(name= "shelf", nullable= false)
    private int shelf;

    @Column(name= "weight", nullable= false)
    private float weight;

    @Column(name= "cups", nullable= false)
    private float cups;

    @Column(name= "rating", nullable= false)
    private float rating;


}
